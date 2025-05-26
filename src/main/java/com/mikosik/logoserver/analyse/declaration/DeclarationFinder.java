package com.mikosik.logoserver.analyse.declaration;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.binarySearch;
import static java.util.Comparator.comparing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.mikosik.logoserver.analyse.base.Ranges;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Range;

/**
 * For a given location in the document, it finds all variable or procedure declarations whose names
 * are equal to referenced name at given position.
 */
public class DeclarationFinder {
  private final ImmutableList<Token> sortedTokens;
  private final Declarations declarations;

  public DeclarationFinder(ImmutableList<Token> sortedTokens, Declarations declarations) {
    this.sortedTokens = sortedTokens;
    this.declarations = declarations;
  }

  public ImmutableList<Range> findDeclarationsOf(int line, int characterAtLine) {
    var token = tokenAt(line, characterAtLine);
    if (token != null) {
      var text = token.getText();
      if (isVariableReference(text)) {
        return getReferences(text.substring(1), declarations.variables());
      } else {
        return getReferences(text, declarations.procedures());
      }
    } else {
      return ImmutableList.of();
    }
  }

  private static boolean isVariableReference(String text) {
    return text.startsWith(":");
  }

  private ImmutableList<Range> getReferences(String name, ImmutableMultimap<String, Range> list) {
    return list.get(name).stream().map(Ranges::newRange).collect(toImmutableList());
  }

  private Token tokenAt(int lineZeroBased, int charPositionInLine) {
    int line = lineZeroBased + 1;
    var commonToken = new CommonToken(0);
    commonToken.setLine(line);
    commonToken.setCharPositionInLine(charPositionInLine);
    var positionComparator = comparing(Token::getLine).thenComparing(Token::getCharPositionInLine);
    var index = binarySearch(sortedTokens, commonToken, positionComparator);
    if (0 <= index && index < sortedTokens.size()) {
      return sortedTokens.get(index);
    }
    var insertionPoint = -index - 1;
    if (insertionPoint == 0) {
      return null;
    }
    var token = sortedTokens.get(insertionPoint - 1);
    if (token.getLine() == line
        && charPositionInLine <= token.getCharPositionInLine() + token.getText().length()) {
      return token;
    }
    return null;
  }
}
