package com.mikosik.logoserver.analyse.base;

import static com.mikosik.logoserver.analyse.base.Positions.newPosition;

import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

/**
 * Utility methods for converting ANTLR tokens to LSP Ranges.
 */
public class Ranges {
  public static Range newRange(Token token) {
    var firstChar = token.getCharPositionInLine();
    var length = token.getText().length();
    return newRange(token, firstChar, length);
  }

  private static Range newRange(Token token, int firstCharIndex, int length) {
    var line = token.getLine();
    return newRange(line, firstCharIndex, length);
  }

  public static Range newRange(int line, int firstCharIndex, int length) {
    var lineZeroBased = line - 1;
    var lastChar = firstCharIndex + length;
    return new Range(
        new Position(lineZeroBased, firstCharIndex), new Position(lineZeroBased, lastChar));
  }

  public static Range newRange(Range range) {
    return new Range(newPosition(range.getStart()), newPosition(range.getEnd()));
  }
}
