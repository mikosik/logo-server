package com.mikosik.logoserver.analyse.highlight;

import com.google.common.collect.ImmutableList;
import com.mikosik.logoserver.analyse.base.TokenModifier;
import com.mikosik.logoserver.analyse.base.TokenType;

/**
 * Builder for SemanticTokens using relative positioning as specified in the LSP.
 * Semantic tokens encode information about syntax elements such as keywords, numbers,
 * and their positions, using a format compliant with the LSP for textDocument/semanticTokens*
 * requests.
 * Each token is encoded as 5 integers:
 * - deltaLine: line number relative to the previous token
 * - deltaStartChar: character position relative to the start of the line if deltaLine > 0,
 *                  or relative to the previous token's character position if on the same line
 * - length: the length of the token
 * - tokenType: the type of the token
 * - tokenModifiers: bit flags for token modifiers
 */
public class SemanticTokensMarshaller {
  private final ImmutableList.Builder<Integer> data = ImmutableList.builder();
  private int previousLine = 0;
  private int previousStartChar = 0;

  public SemanticTokensMarshaller add(
      int line, int startChar, int length, TokenType tokenType, TokenModifier... tokenModifiers) {
    int deltaLine = line - previousLine;
    int deltaStartChar = (deltaLine == 0) ? startChar - previousStartChar : startChar;

    data.add(deltaLine);
    data.add(deltaStartChar);
    data.add(length);
    data.add(tokenType.ordinal());
    data.add(buildTokenModifierMask(tokenModifiers));

    previousLine = line;
    previousStartChar = startChar;

    return this;
  }

  static int buildTokenModifierMask(TokenModifier... tokenModifiers) {
    var result = 0;
    for (var tokenModifier : tokenModifiers) {
      result = result | (1 << tokenModifier.ordinal());
    }
    return result;
  }

  public ImmutableList<Integer> build() {
    return data.build();
  }
}
