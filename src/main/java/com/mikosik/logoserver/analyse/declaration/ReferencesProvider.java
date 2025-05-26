package com.mikosik.logoserver.analyse.declaration;

import com.google.common.collect.ImmutableList;
import com.mikosik.logoserver.analyse.parser.antlr.LogoBaseListener;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.CallContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.DocumentContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.ThingshortContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Finds all variable and procedure references in a given parse tree.
 */
public class ReferencesProvider {
  public ImmutableList<Token> referencesFrom(DocumentContext parseTree) {
    var listener = new Listener();
    new ParseTreeWalker().walk(listener, parseTree);
    return listener.build();
  }

  private static class Listener extends LogoBaseListener {
    private final ImmutableList.Builder<Token> builder = ImmutableList.builder();

    @Override
    public void enterThingshort(ThingshortContext thingshortContext) {
      builder.add(thingshortContext.COLON_NAME().getSymbol());
    }

    @Override
    public void enterCall(CallContext callContext) {
      builder.add(callContext.NAME().getSymbol());
    }

    public ImmutableList<Token> build() {
      return builder.build();
    }
  }
}
