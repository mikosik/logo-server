package com.mikosik.logoserver.analyse.declaration;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMultimap;
import com.mikosik.logoserver.analyse.parser.Parser;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;
import org.junit.jupiter.api.Test;

public class DeclarationsProviderTest {
  @Test
  void returnsDeclarationsForMakeCommand() {
    var code = "make \"x 100";

    var declarations = declarationsFrom(code);

    var variables = mapOf("x", new Range(new Position(0, 5), new Position(0, 7)));
    assertThat(declarations).isEqualTo(new Declarations(variables, mapOf()));
  }

  @Test
  void returnsDeclarationsForNameCommand() {
    var code = "name 100 \"x";

    var declarations = declarationsFrom(code);

    var variables = mapOf("x", new Range(new Position(0, 9), new Position(0, 11)));
    assertThat(declarations).isEqualTo(new Declarations(variables, mapOf()));
  }

  @Test
  void returnsDeclarationsForLocalmakeCommand() {
    var code = "localmake \"y 200";

    var declarations = declarationsFrom(code);

    var variables = mapOf("y", new Range(new Position(0, 10), new Position(0, 12)));
    assertThat(declarations).isEqualTo(new Declarations(variables, mapOf()));
  }

  @Test
  void returnsDeclarationsForDefineCommand() {
    var code = "define \"square [[size] [repeat 4 [forward :size right 90]]]";

    var declarations = declarationsFrom(code);

    var procedures = mapOf("square", new Range(new Position(0, 7), new Position(0, 14)));
    assertThat(declarations).isEqualTo(new Declarations(mapOf(), procedures));
  }

  @Test
  void returnsDeclarationsForToCommand() {
    var code =
        """
        to square :size
          repeat 4 [forward :size right 90]
        end""";

    var declarations = declarationsFrom(code);

    var procedures = mapOf("square", new Range(new Position(0, 3), new Position(0, 9)));
    assertThat(declarations).isEqualTo(new Declarations(mapOf(), procedures));
  }

  @Test
  void returnsDeclarationsForMultipleCommands() {
    var code =
        """
        make "x 100
        localmake "y 200
        to square :size
          repeat 4 [forward :size right 90]
        end""";

    var declarations = declarationsFrom(code);

    var variables = mapOf(
        "x",
        new Range(new Position(0, 5), new Position(0, 7)),
        "y",
        new Range(new Position(1, 10), new Position(1, 12)));
    var procedures = mapOf("square", new Range(new Position(2, 3), new Position(2, 9)));
    assertThat(declarations).isEqualTo(new Declarations(variables, procedures));
  }

  private static Declarations declarationsFrom(String document) {
    var parseTree = new Parser().parse(document).parseTree();
    return new DeclarationsProvider().declarationsFrom(parseTree);
  }

  private static ImmutableMultimap<String, Range> mapOf() {
    return ImmutableMultimap.of();
  }

  private static ImmutableMultimap<String, Range> mapOf(String key, Range value) {
    return ImmutableMultimap.of(key, value);
  }

  private static ImmutableMultimap<String, Range> mapOf(
      String key1, Range value1, String key2, Range value2) {
    return ImmutableMultimap.of(key1, value1, key2, value2);
  }
}
