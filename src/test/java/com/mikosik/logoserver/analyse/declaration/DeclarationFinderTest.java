package com.mikosik.logoserver.analyse.declaration;

import static com.google.common.truth.Truth.assertThat;

import com.mikosik.logoserver.analyse.parser.Parser;
import java.util.List;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DeclarationFinderTest {
  @Nested
  class FindDeclarationOfVariable {
    @Test
    void ofMakeCommand() {
      var document = """
          make "x 100
          make "y 200
          show :x""";
      testFindDeclarations(
          document, 2, 6, List.of(new Range(new Position(0, 5), new Position(0, 7))));
    }

    @Test
    void ofMakeCommandWithDifferentCase() {
      var document = """
          make "abcd 100
          show :AbcD""";
      testFindDeclarations(
          document, 1, 5, List.of(new Range(new Position(0, 5), new Position(0, 10))));
    }

    @Test
    void ofNameCommand() {
      var document = """
          name 100 "x
          name 200 "y
          show :x""";
      testFindDeclarations(
          document, 2, 6, List.of(new Range(new Position(0, 9), new Position(0, 11))));
    }

    @Test
    void ofNameCommandWithDifferentCase() {
      var document = """
          name 100 "abcd
          show :AbcD""";
      testFindDeclarations(
          document, 1, 5, List.of(new Range(new Position(0, 9), new Position(0, 14))));
    }

    @Test
    void ofLocalmakeCommand() {
      var document = """
          localmake "x 100
          localmake "y 200
          show :x""";
      testFindDeclarations(
          document, 2, 6, List.of(new Range(new Position(0, 10), new Position(0, 12))));
    }

    @Test
    void ofLocalmakeCommandWithDifferentCase() {
      var document = """
          localmake "abcd 100
          show :AbcD""";
      testFindDeclarations(
          document, 1, 5, List.of(new Range(new Position(0, 10), new Position(0, 15))));
    }

    @Test
    void withMultipleVariables() {
      var document =
          """
          make "x 100
          make "x 100
          make "x 100
          name 100 "x
          show :x""";
      testFindDeclarations(
          document,
          4,
          5,
          List.of(
              new Range(new Position(0, 5), new Position(0, 7)),
              new Range(new Position(1, 5), new Position(1, 7)),
              new Range(new Position(2, 5), new Position(2, 7)),
              new Range(new Position(3, 9), new Position(3, 11))));
    }

    @Test
    void whenNotExists() {
      var document = "show :x";
      testFindDeclarations(document, 0, 6, List.of());
    }

    @Test
    void inRepeatBlock() {
      var document = """
          repeat 4 [make "x 100 show :x]""";
      testFindDeclarations(
          document, 0, 29, List.of(new Range(new Position(0, 15), new Position(0, 17))));
    }
  }

  @Nested
  class FindDeclarationOfProcedure {
    @Test
    void singleToDeclaration() {
      var document =
          """
          to square
            forward 100
          end
          square""";
      testFindDeclarations(
          document, 3, 2, List.of(new Range(new Position(0, 3), new Position(0, 9))));
    }

    @Test
    void singleToDeclarationWithDifferentCase() {
      var document =
          """
          to square
            forward 100
          end
          SQUARE""";
      testFindDeclarations(
          document, 3, 2, List.of(new Range(new Position(0, 3), new Position(0, 9))));
    }

    @Test
    void multipleToDeclarations() {
      var document =
          """
          to square
            forward 100
          end
          to square
            forward 200
          end
          square""";
      testFindDeclarations(
          document,
          6,
          2,
          List.of(
              new Range(new Position(0, 3), new Position(0, 9)),
              new Range(new Position(3, 3), new Position(3, 9))));
    }

    @Test
    void whenNotExists() {
      var document = "square";
      testFindDeclarations(document, 0, 6, List.of());
    }

    @Test
    void inNestedBlock() {
      var document = """
          repeat 4 [to square forward 100 end square]""";
      testFindDeclarations(
          document, 0, 36, List.of(new Range(new Position(0, 13), new Position(0, 19))));
    }

    @Test
    void singleDefineDeclaration() {
      var document = """
          define "square [[] [forward 100]]
          square""";
      testFindDeclarations(
          document, 1, 2, List.of(new Range(new Position(0, 7), new Position(0, 14))));
    }

    @Test
    void singleDefineDeclarationWithDifferentCase() {
      var document = """
          define "square [[] [forward 100]]
          SQUARE""";
      testFindDeclarations(
          document, 1, 2, List.of(new Range(new Position(0, 7), new Position(0, 14))));
    }

    @Test
    void multipleDefineDeclarations() {
      var document =
          """
          define "square [[] [forward 100]]
          define "square [[] [forward 200]]
          square""";
      testFindDeclarations(
          document,
          2,
          2,
          List.of(
              new Range(new Position(0, 7), new Position(0, 14)),
              new Range(new Position(1, 7), new Position(1, 14))));
    }

    @Test
    void defineDeclarationInNestedBlock() {
      var document = """
          repeat 4 [define "square [[] [forward 100]] square]""";
      testFindDeclarations(
          document, 0, 45, List.of(new Range(new Position(0, 17), new Position(0, 24))));
    }
  }

  private void testFindDeclarations(
      String document, int line, int characterAtLine, List<Range> expected) {
    var declarationFinder = declarationFinder(document);
    var declarations = declarationFinder.findDeclarationsOf(line, characterAtLine);
    assertThat(declarations).isEqualTo(expected);
  }

  private DeclarationFinder declarationFinder(String document) {
    var parsedDocument = new Parser().parse(document);
    var parseTree = parsedDocument.parseTree();
    var references = new ReferencesProvider().referencesFrom(parseTree);
    var declarations = new DeclarationsProvider().declarationsFrom(parseTree);
    return new DeclarationFinder(references, declarations);
  }
}
