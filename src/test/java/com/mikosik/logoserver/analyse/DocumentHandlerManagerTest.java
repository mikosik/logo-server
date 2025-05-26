package com.mikosik.logoserver.analyse;

import static com.google.common.truth.Truth.assertThat;
import static com.mikosik.logoserver.analyse.DocumentHandlerManager.SHUTDOWN_DELAY;
import static java.time.Instant.EPOCH;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DocumentHandlerManagerTest {
  private Function<String, AsyncDocumentHandler> factory;
  private DocumentHandlerManager manager;
  private AsyncDocumentHandler handler;
  private AtomicReference<Instant> clock;

  @BeforeEach
  public void before() {
    this.factory = mock();
    this.handler = mock();
    this.clock = new AtomicReference<>(EPOCH);
    this.manager = new DocumentHandlerManager(factory, clock::get);
  }

  @Test
  void handlerForReusesAlreadyCreatedInstance() {
    when(factory.apply("uri")).thenReturn(handler);

    var handler1 = manager.handlerFor("uri");
    var handler2 = manager.handlerFor("uri");

    assertThat(handler1).isSameInstanceAs(handler2);
  }

  @Test
  void handlerForRecyclesHandlerScheduledForShutdown() {
    var newHandler = mock(AsyncDocumentHandler.class);
    when(factory.apply("uri")).thenReturn(handler).thenReturn(newHandler);

    var handler1 = manager.handlerFor("uri");
    manager.scheduleForRemoval("uri");
    var handler2 = manager.handlerFor("uri");

    assertThat(handler1).isSameInstanceAs(handler2);
  }

  @Test
  void handlerIsShutdownAfterShutdownDelay() {
    var newHandler1 = mock(AsyncDocumentHandler.class);
    var newHandler2 = mock(AsyncDocumentHandler.class);
    when(factory.apply("uri")).thenReturn(handler).thenReturn(newHandler1).thenReturn(newHandler2);

    var handler1 = manager.handlerFor("uri");
    manager.scheduleForRemoval("uri");
    clock.set(EPOCH.plus(SHUTDOWN_DELAY));
    manager.cleanUp();
    var handler2 = manager.handlerFor("uri");

    assertThat(handler1).isNotSameInstanceAs(handler2);
  }
}
