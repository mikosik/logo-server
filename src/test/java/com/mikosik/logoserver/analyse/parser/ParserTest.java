package com.mikosik.logoserver.analyse.parser;

import static com.google.common.truth.Truth.assertThat;
import static org.eclipse.lsp4j.DiagnosticSeverity.Error;

import java.util.List;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

public class ParserTest {
  @Test
  void returnsDiagnosticForMissingClosingBracket() {
    var code = "repeat 4 [forward 10";

    var diagnostics = new Parser().parse(code).diagnostics();

    assertThat(diagnostics)
        .isEqualTo(List.of(new Diagnostic(
            new Range(new Position(0, 20), new Position(0, 20)),
            "extraneous input '<EOF>' expecting {'forward', 'fd', 'back', 'bk', 'left', 'lt', 'right', 'rt', 'home', 'setx', 'sety', 'setxy', 'set', ']', 'setheading', 'seth', 'arc', 'ellipse', 'showturtle', 'st', 'hideturtle', 'ht', 'clean', 'clearscreen', 'cs', 'fill', 'filled', 'label', 'print', 'show', 'setlabelheight', 'wrap', 'window', 'fence', 'penup', 'pu', 'pendown', 'pd', 'setcolor', 'setpencolor', 'setwidth', 'setpensize', 'changeshape', 'csh', 'to', 'define', 'make', 'name', 'localmake', 'repeat', 'for', 'repcount', 'if', 'ifelse', 'test', 'iftrue', 'iffalse', 'wait', 'bye', 'dotimes', 'do.while', 'while', 'do.until', 'until', NAME}",
            Error,
            null)));
  }

  @Test
  void returnsDiagnosticForMissingOpeningBracket() {
    var code = "repeat 4 forward 10]";

    var diagnostics = new Parser().parse(code).diagnostics();

    var diagnostic = new Diagnostic(
        new Range(new Position(0, 9), new Position(0, 16)),
        "missing '[' at 'forward'",
        Error,
        null);
    assertThat(diagnostics).isEqualTo(List.of(diagnostic));
  }

  @Test
  void returnsDiagnosticForMissingEndKeyword() {
    var code = """
        to square :size
          repeat 4 [forward :size right 90]
        """;

    var diagnostics = new Parser().parse(code).diagnostics();

    var diagnostic = new Diagnostic(
        new Range(new Position(2, 0), new Position(2, 0)),
        "extraneous input '<EOF>' expecting {'forward', 'fd', 'back', 'bk', 'left', 'lt', 'right', 'rt', 'home', 'setx', 'sety', 'setxy', 'set', 'setheading', 'seth', 'arc', 'ellipse', 'showturtle', 'st', 'hideturtle', 'ht', 'clean', 'clearscreen', 'cs', 'fill', 'filled', 'label', 'print', 'show', 'setlabelheight', 'wrap', 'window', 'fence', 'penup', 'pu', 'pendown', 'pd', 'setcolor', 'setpencolor', 'setwidth', 'setpensize', 'changeshape', 'csh', 'to', 'end', 'define', 'make', 'name', 'localmake', 'repeat', 'for', 'repcount', 'if', 'ifelse', 'test', 'iftrue', 'iffalse', 'wait', 'bye', 'dotimes', 'do.while', 'while', 'do.until', 'until', NAME}",
        Error,
        null);
    assertThat(diagnostics).isEqualTo(List.of(diagnostic));
  }

  @Test
  void returnsDiagnosticForMissingArgumentInCommand() {
    var code = "forward";

    var parsedDocument = new Parser().parse(code);

    var diagnostic = new Diagnostic(
        new Range(new Position(0, 7), new Position(0, 7)),
        "mismatched input '<EOF>' expecting {'pos', '[', 'xcor', 'ycor', 'heading', 'towards', 'shownp', 'shown?', 'labelsize', 'pendownp', 'pendown?', 'pencolor', 'pc', 'pensize', 'def', 'thing', '(', 'first', 'butfirst', 'last', 'butlast', 'item', 'pick', 'sum', 'minus', 'random', 'modulo', 'power', 'wordp', 'word?', 'listp', 'list?', 'arrayp', 'array?', 'numberp', 'number?', 'emptyp', 'empty?', 'equalp', 'equal?', 'notequalp', 'notequal?', 'beforep', 'before?', 'substringp', 'substring?', COLON_NAME, WORD, NUMBER}",
        Error,
        null);

    assertThat(parsedDocument.diagnostics()).isEqualTo(List.of(diagnostic));
  }

  @Test
  void returnsNoDiagnosticsForValidCode() {
    var code =
        """
        forward 10
        repeat 4 [forward 10 right 90]
        to square :size
          repeat 4 [forward :size right 90]
        end
        """;

    var diagnostics = new Parser().parse(code).diagnostics();

    assertThat(diagnostics).isEmpty();
  }
}
