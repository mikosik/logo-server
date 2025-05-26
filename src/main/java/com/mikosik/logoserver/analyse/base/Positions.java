package com.mikosik.logoserver.analyse.base;

import org.eclipse.lsp4j.Position;

/**
 * Helper methods for creating LSP Position objects.
 */
public class Positions {
  public static Position newPosition(Position start) {
    return new Position(start.getLine(), start.getCharacter());
  }
}
