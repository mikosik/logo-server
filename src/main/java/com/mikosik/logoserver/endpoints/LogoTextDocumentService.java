package com.mikosik.logoserver.endpoints;

import com.mikosik.logoserver.analyse.AsyncDocumentHandler;
import com.mikosik.logoserver.analyse.DocumentHandlerManager;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.DeclarationParams;
import org.eclipse.lsp4j.DidChangeTextDocumentParams;
import org.eclipse.lsp4j.DidCloseTextDocumentParams;
import org.eclipse.lsp4j.DidOpenTextDocumentParams;
import org.eclipse.lsp4j.DidSaveTextDocumentParams;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.LocationLink;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.SemanticTokens;
import org.eclipse.lsp4j.SemanticTokensParams;
import org.eclipse.lsp4j.jsonrpc.messages.Either;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoTextDocumentService implements TextDocumentService {
  private static final Logger logger = LoggerFactory.getLogger(LogoTextDocumentService.class);
  private LanguageClient client;
  private final DocumentHandlerManager documentHandlerManager;

  public LogoTextDocumentService() {
    this.documentHandlerManager = new DocumentHandlerManager();
  }

  public void setClient(LanguageClient client) {
    this.client = client;
  }

  @Override
  public void didOpen(DidOpenTextDocumentParams params) {
    var uri = params.getTextDocument().getUri();
    logger.info("Received didOpen for {}", uri);
    var handler = documentHandlerManager.handlerFor(uri);
    setText(uri, handler, params.getTextDocument().getText());
  }

  @Override
  public void didChange(DidChangeTextDocumentParams params) {
    var uri = params.getTextDocument().getUri();
    logger.info("Received didChange for {}", uri);

    var changes = params.getContentChanges();
    if (changes.size() != 1 || changes.get(0).getRange() != null) {
      logger.error("Server does not support partial updates.");
    } else {
      var handler = documentHandlerManager.handlerFor(uri);
      setText(uri, handler, changes.get(0).getText());
    }
  }

  private void setText(String uri, AsyncDocumentHandler handler, String text) {
    handler.setText(text).thenAccept(diagnostics -> {
      if (client != null) {
        client.publishDiagnostics(new PublishDiagnosticsParams(uri, diagnostics));
      }
    });
  }

  @Override
  public CompletableFuture<SemanticTokens> semanticTokensFull(SemanticTokensParams params) {
    var uri = params.getTextDocument().getUri();
    logger.info("Received semanticTokensFull for {}", uri);
    return documentHandlerManager.handlerFor(uri).semanticTokensFull();
  }

  @Override
  public CompletableFuture<Either<List<? extends Location>, List<? extends LocationLink>>>
      declaration(DeclarationParams params) {
    var uri = params.getTextDocument().getUri();
    logger.info("Received declaration for {}", uri);
    var position = params.getPosition();
    return documentHandlerManager
        .handlerFor(uri)
        .declaration(position.getLine(), position.getCharacter())
        .thenApply(Either::forLeft);
  }

  @Override
  public void didSave(DidSaveTextDocumentParams params) {
    logger.info("Received didSave for {}", params.getTextDocument().getUri());
  }

  @Override
  public void didClose(DidCloseTextDocumentParams params) {
    var uri = params.getTextDocument().getUri();
    logger.info("Received didClose for {}", uri);
    documentHandlerManager.scheduleForRemoval(uri);
  }
}
