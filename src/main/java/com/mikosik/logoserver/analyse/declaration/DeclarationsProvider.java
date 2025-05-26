package com.mikosik.logoserver.analyse.declaration;

import static com.mikosik.logoserver.analyse.base.Ranges.newRange;

import com.google.common.collect.ImmutableMultimap;
import com.mikosik.logoserver.analyse.parser.antlr.LogoBaseListener;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.DefineContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.DocumentContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.LocalmakeContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.MakevarContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.NamevarContext;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.ToContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.eclipse.lsp4j.Range;

/**
 * Provides variable and procedure declarations from a given parse tree.
 */
public class DeclarationsProvider {
  public Declarations declarationsFrom(DocumentContext parseTree) {
    var listener = new Listener();
    new ParseTreeWalker().walk(listener, parseTree);
    return listener.build();
  }

  private static class Listener extends LogoBaseListener {
    private final ImmutableMultimap.Builder<String, Range> variables = ImmutableMultimap.builder();
    private final ImmutableMultimap.Builder<String, Range> procedures = ImmutableMultimap.builder();

    @Override
    public void enterMakevar(MakevarContext makevarContext) {
      addVariableDeclaration(makevarContext.WORD());
    }

    @Override
    public void enterNamevar(NamevarContext namevarContext) {
      addVariableDeclaration(namevarContext.WORD());
    }

    @Override
    public void enterLocalmake(LocalmakeContext localmakeContext) {
      addVariableDeclaration(localmakeContext.WORD());
    }

    private void addVariableDeclaration(TerminalNode word) {
      Token symbol = word.getSymbol();
      // remove leading double quotes `"` from variable name declaration
      var name = symbol.getText().substring(1);
      var value = newRange(symbol);
      variables.put(name, value);
    }

    @Override
    public void enterTo(ToContext toContext) {
      procedures.put(toContext.NAME().getText(), newRange(toContext.NAME().getSymbol()));
    }

    @Override
    public void enterDefine(DefineContext defineContext) {
      // remove leading double quotes `"` from procedure name declaration
      var name = defineContext.WORD().getText().substring(1);
      procedures.put(name, newRange(defineContext.WORD().getSymbol()));
    }

    public Declarations build() {
      return new Declarations(variables.build(), procedures.build());
    }
  }
}
