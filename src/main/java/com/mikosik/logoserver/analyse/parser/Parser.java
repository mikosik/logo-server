package com.mikosik.logoserver.analyse.parser;

import static com.mikosik.logoserver.analyse.base.Ranges.newRange;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mikosik.logoserver.analyse.parser.antlr.LogoLexer;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser;
import java.util.Locale;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticSeverity;

/**
 * Antlr based Logo files parser.
 */
public class Parser {
  public ParsedDocument parse(String document) {
    var errorListener = new LogoErrorListener();
    var logoLexer = new LogoLexer(CharStreams.fromString(document.toLowerCase(Locale.ROOT)));
    logoLexer.removeErrorListeners();
    logoLexer.addErrorListener(errorListener);

    var logoParser = new LogoParser(new CommonTokenStream(logoLexer));
    logoParser.removeErrorListeners();
    logoParser.addErrorListener(errorListener);

    return new ParsedDocument(logoParser.document(), errorListener.diagnostics.build());
  }

  public static class LogoErrorListener extends BaseErrorListener {
    private final Builder<Diagnostic> diagnostics = ImmutableList.builder();

    @Override
    public void syntaxError(
        Recognizer<?, ?> recognizer,
        Object offendingSymbol,
        int line,
        int charPositionInLine,
        String msg,
        RecognitionException e) {
      var length = lengthOf(offendingSymbol);
      var range = newRange(line, charPositionInLine, length);
      diagnostics.add(new Diagnostic(range, msg, DiagnosticSeverity.Error, null));
    }

    private static int lengthOf(Object offendingSymbol) {
      if (offendingSymbol instanceof Token token && token.getType() != Token.EOF) {
        return token.getText().length();
      }
      return 0;
    }
  }
}
