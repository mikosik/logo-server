package com.mikosik.logoserver.analyse;

import static java.util.concurrent.TimeUnit.HOURS;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.SemanticTokens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides asynchronous API for {@link DocumentHandler} functionality.
 */
public class AsyncDocumentHandler {
  private static final Logger logger = LoggerFactory.getLogger(AsyncDocumentHandler.class);

  private final BlockingQueue<Consumer<DocumentHandler>> queue;
  private final Thread workerThread;
  private boolean running;

  public AsyncDocumentHandler(String uri) {
    this(uri, new LinkedBlockingQueue<>());
  }

  // visible for testing
  AsyncDocumentHandler(String uri, BlockingQueue<Consumer<DocumentHandler>> queue) {
    this.queue = queue;
    this.workerThread = workerThread(uri, queue);
    this.running = true;
  }

  void shutdown() {
    workerThread.interrupt();
    running = false;
  }

  public CompletableFuture<List<Diagnostic>> setText(String text) {
    var future = new CompletableFuture<List<Diagnostic>>();
    enqueue((documentHandler) -> {
      documentHandler.setText(text);
      future.complete(documentHandler.getDiagnostics());
    });
    return future;
  }

  public CompletableFuture<SemanticTokens> semanticTokensFull() {
    var future = new CompletableFuture<SemanticTokens>();
    enqueue((documentHandler) -> {
      try {
        future.complete(new SemanticTokens(documentHandler.semanticTokensFull()));
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    });
    return future;
  }

  public CompletableFuture<List<Location>> declaration(int line, int characterAtLine) {
    var future = new CompletableFuture<List<Location>>();
    enqueue((documentHandler) -> {
      try {
        future.complete(documentHandler.declaration(line, characterAtLine));
      } catch (Exception e) {
        future.completeExceptionally(e);
      }
    });
    return future;
  }

  private void enqueue(Consumer<DocumentHandler> task) {
    if (running) {
      try {
        queue.put(task);
      } catch (InterruptedException e) {
        logger.info("Interrupted while enqueuing task.");
        Thread.currentThread().interrupt();
      }
    } else {
      throw new IllegalStateException("AsynDocumentHandler is shutdown");
    }
  }

  /**
   * Creates and starts a working thread holding a DocumentHandler instance confined to that thread.
   * The thread polls tasks (Consumers) from a given queue and executes them by passing
   * the DocumentHandler instance. To shut down the thread, call its interrupt() method.
   */
  private Thread workerThread(String uri, BlockingQueue<Consumer<DocumentHandler>> queue) {
    return Thread.ofVirtual().start(() -> {
      logger.info("Thread started for {}.", uri);
      var documentHandler = new DocumentHandler(uri);
      while (true) {
        try {
          var task = queue.poll(Long.MAX_VALUE, HOURS);
          task.accept(documentHandler);
        } catch (InterruptedException e) {
          logger.info("Interrupted. Shutting down thread for {}.", uri);
          return;
        } catch (RuntimeException e) {
          logger.error("Unexpected exception in thread for {}.", uri, e);
        }
      }
    });
  }
}
