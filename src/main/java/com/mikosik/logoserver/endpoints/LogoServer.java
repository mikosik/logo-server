package com.mikosik.logoserver.endpoints;

import static java.util.Arrays.stream;
import static java.util.concurrent.CompletableFuture.completedFuture;

import com.mikosik.logoserver.analyse.base.TokenModifier;
import com.mikosik.logoserver.analyse.base.TokenType;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.eclipse.lsp4j.InitializeParams;
import org.eclipse.lsp4j.InitializeResult;
import org.eclipse.lsp4j.SemanticTokensLegend;
import org.eclipse.lsp4j.SemanticTokensWithRegistrationOptions;
import org.eclipse.lsp4j.ServerCapabilities;
import org.eclipse.lsp4j.TextDocumentSyncKind;
import org.eclipse.lsp4j.services.LanguageClient;
import org.eclipse.lsp4j.services.LanguageServer;
import org.eclipse.lsp4j.services.TextDocumentService;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LSP language server for LOGO language.
 */
public class LogoServer implements LanguageServer {
  private static final Logger logger = LoggerFactory.getLogger(LogoServer.class);

  private final WorkspaceService workspaceService = new LogoWorkspaceService();
  private final LogoTextDocumentService textDocumentService = new LogoTextDocumentService();

  @Override
  public CompletableFuture<InitializeResult> initialize(InitializeParams initializeParams) {
    logger.info("initialization requested");
    InitializeResult result = new InitializeResult(serverCapabilities());
    return completedFuture(result);
  }

  private static ServerCapabilities serverCapabilities() {
    var capabilities = new ServerCapabilities();
    var semanticTokensOptions = semanticTokensOptions();
    capabilities.setSemanticTokensProvider(semanticTokensOptions);
    capabilities.setTextDocumentSync(TextDocumentSyncKind.Full);
    capabilities.setDeclarationProvider(true);
    return capabilities;
  }

  public void setClient(LanguageClient client) {
    this.textDocumentService.setClient(client);
  }

  private static SemanticTokensWithRegistrationOptions semanticTokensOptions() {
    var semanticTokensOptions = new SemanticTokensWithRegistrationOptions();
    semanticTokensOptions.setLegend(
        new SemanticTokensLegend(enumNames(TokenType.values()), enumNames(TokenModifier.values())));
    semanticTokensOptions.setFull(true);
    semanticTokensOptions.setRange(false);
    return semanticTokensOptions;
  }

  private static List<String> enumNames(Enum<?>[] values) {
    return stream(values).map(Enum::name).map(String::toLowerCase).toList();
  }

  @Override
  public CompletableFuture<Object> shutdown() {
    logger.info("shutdown requested");
    return completedFuture(null);
  }

  @Override
  public void exit() {
    logger.info("exit requested");
  }

  @Override
  public TextDocumentService getTextDocumentService() {
    logger.info("TextDocumentService requested");
    return textDocumentService;
  }

  @Override
  public WorkspaceService getWorkspaceService() {
    logger.info("WorkspaceService requested");
    return workspaceService;
  }
}
