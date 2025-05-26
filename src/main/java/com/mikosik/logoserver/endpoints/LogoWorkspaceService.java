package com.mikosik.logoserver.endpoints;

import org.eclipse.lsp4j.DidChangeConfigurationParams;
import org.eclipse.lsp4j.DidChangeWatchedFilesParams;
import org.eclipse.lsp4j.services.WorkspaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoWorkspaceService implements WorkspaceService {
  private static final Logger logger = LoggerFactory.getLogger(LogoWorkspaceService.class);

  @Override
  public void didChangeConfiguration(DidChangeConfigurationParams params) {
    logger.info("Received didChangeConfiguration");
  }

  @Override
  public void didChangeWatchedFiles(DidChangeWatchedFilesParams params) {
    logger.info("Received didChangeWatchedFiles");
  }
}
