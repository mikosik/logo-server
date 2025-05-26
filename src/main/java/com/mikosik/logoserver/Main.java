package com.mikosik.logoserver;

import static org.eclipse.lsp4j.launch.LSPLauncher.createServerLauncher;

import com.mikosik.logoserver.endpoints.LogoServer;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String[] args) throws InterruptedException, ExecutionException {
    configureLogging();

    logger.info("Starting LOGO Language Server");
    var logoServer = new LogoServer();
    var launcher = createServerLauncher(logoServer, System.in, System.out);
    logoServer.setClient(launcher.getRemoteProxy());
    var future = launcher.startListening();
    logger.info("LOGO Language Server started");
    future.get();
  }

  private static void configureLogging() {
    try (InputStream is = Main.class.getClassLoader().getResourceAsStream("logging.properties")) {
      if (is != null) {
        LogManager.getLogManager().readConfiguration(is);
      } else {
        logger.error("Could not find logging.properties file");
      }
    } catch (IOException e) {
      logger.error("Error loading logging configuration: {}", e.getMessage());
    }
  }
}
