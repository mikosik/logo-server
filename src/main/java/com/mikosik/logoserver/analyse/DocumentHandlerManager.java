package com.mikosik.logoserver.analyse;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides and handles lifecycle of {@link AsyncDocumentHandler} instances.
 * Creates instances on demand or reuses cached instance if already created for the given URI.
 * Allows scheduling {@link AsyncDocumentHandler} for removal in which case it will be shutdown
 * after delay of 5 minutes. The actual shutdown is triggered during execution of public methods
 * if shutdown delay has been reached.
 */
public class DocumentHandlerManager {
  private static final Logger logger = LoggerFactory.getLogger(DocumentHandlerManager.class);
  static final Duration SHUTDOWN_DELAY = Duration.ofMinutes(5);
  private final HashMap<String, AsyncDocumentHandler> documentHandlers;
  private final Function<String, AsyncDocumentHandler> factory;
  private final TreeMap<String, Recyclable> recyclable = new TreeMap<>();
  private final Supplier<Instant> currentInstantSupplier;

  public DocumentHandlerManager() {
    this(AsyncDocumentHandler::new, Instant::now);
  }

  DocumentHandlerManager(
      Function<String, AsyncDocumentHandler> factory, Supplier<Instant> currentInstantSupplier) {
    this.currentInstantSupplier = currentInstantSupplier;
    this.documentHandlers = new HashMap<>();
    this.factory = factory;
  }

  public AsyncDocumentHandler handlerFor(String uri) {
    var result = documentHandlers.computeIfAbsent(uri, (k) -> recycleOrCreate(uri));
    cleanUp();
    return result;
  }

  private AsyncDocumentHandler recycleOrCreate(String uri) {
    var recycled = recyclable.remove(uri);
    if (recycled == null) {
      return factory.apply(uri);
    } else {
      return recycled.asyncDocumentHandler;
    }
  }

  public void scheduleForRemoval(String uri) {
    cleanUp();
    var handler = documentHandlers.remove(uri);
    if (handler == null) {
      logger.warn("Ignoring request to remove handler for {} as it does not exist.", uri);
    } else {
      recyclable.put(
          uri, new Recyclable(handler, currentInstantSupplier.get().plus(SHUTDOWN_DELAY)));
    }
  }

  // visible for testing
  void cleanUp() {
    var now = currentInstantSupplier.get();
    var iterator = recyclable.entrySet().iterator();
    while (iterator.hasNext()) {
      var entry = iterator.next();
      var value = entry.getValue();
      if (!now.isBefore(value.shutdownTime)) {
        value.asyncDocumentHandler.shutdown();
        iterator.remove();
      } else {
        break;
      }
    }
  }

  private record Recyclable(AsyncDocumentHandler asyncDocumentHandler, Instant shutdownTime)
      implements Comparable<Recyclable> {
    @Override
    public int compareTo(Recyclable that) {
      return this.shutdownTime.compareTo(that.shutdownTime);
    }
  }
}
