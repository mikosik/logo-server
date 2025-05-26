package com.mikosik.logoserver.analyse;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.Thread.State.TERMINATED;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertThrows;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class AsyncDocumentHandlerTest {
  BlockingQueue<Consumer<DocumentHandler>> queue;
  AsyncDocumentHandler handler;

  @BeforeEach
  public void beforeEach() {
    queue = new LinkedBlockingQueue<>();
    handler = new AsyncDocumentHandler("uri", queue);
  }

  @AfterEach
  public void afterEach() {
    handler.shutdown();
  }

  @Nested
  class Multithreading {
    @Test
    void shutdownStopsBackgroundThread() throws InterruptedException {
      var backgroundThreadHolder = new AtomicReference<Thread>();
      queue.put((documentHandler) -> backgroundThreadHolder.set(Thread.currentThread()));
      await().until(() -> backgroundThreadHolder.get() != null);
      var thread = backgroundThreadHolder.get();

      handler.shutdown();

      await().until(() -> thread.getState() == TERMINATED);
    }

    @Test
    void runtimeExceptionDoesNotStopBackgroundThread() throws InterruptedException {
      var backgroundThreadHolder = new AtomicReference<Thread>();
      var future = new CompletableFuture<Void>();
      queue.put((documentHandler) -> {
        backgroundThreadHolder.set(Thread.currentThread());
        throw new RuntimeException("test exception");
      });
      await().until(() -> backgroundThreadHolder.get() != null);

      queue.put((documentHandler) -> future.complete(null));

      await().until(future::isDone);
    }
  }

  @Nested
  class SemanticTokensFull {
    @Test
    void returnsTokens() {
      handler = new AsyncDocumentHandler("uri");
      handler.setText("forward 10");

      var future = handler.semanticTokensFull();

      await().until(future::isDone);
      assertThat(future.join().getData()).isEqualTo(List.of(0, 0, 7, 0, 0, 0, 8, 2, 2, 0));
    }

    @Test
    void failsWhenNoDocumentProvided() {
      handler = new AsyncDocumentHandler("uri");

      var future = handler.semanticTokensFull();

      await().until(future::isDone);
      assertThat(future.isCompletedExceptionally()).isTrue();
    }

    @Test
    void addingTaskFailsAfterShutdown() {
      handler.shutdown();

      assertThrows(IllegalStateException.class, () -> handler.semanticTokensFull());
    }
  }
}
