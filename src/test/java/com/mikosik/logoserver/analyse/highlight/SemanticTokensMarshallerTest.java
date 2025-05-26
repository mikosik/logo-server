package com.mikosik.logoserver.analyse.highlight;

import static com.google.common.truth.Truth.assertThat;
import static com.mikosik.logoserver.analyse.base.TokenModifier.DECLARATION;
import static com.mikosik.logoserver.analyse.base.TokenModifier.DEFINITION;
import static com.mikosik.logoserver.analyse.base.TokenType.KEYWORD;
import static com.mikosik.logoserver.analyse.base.TokenType.NUMBER;

import java.util.List;
import org.junit.jupiter.api.Test;

public class SemanticTokensMarshallerTest {
  @Test
  public void addSingleToken() {
    var semanticTokens =
        new SemanticTokensMarshaller().add(0, 5, 10, KEYWORD, DECLARATION).build();
    assertThat(semanticTokens).isEqualTo(List.of(0, 5, 10, 0, 1));
  }

  @Test
  public void addMultipleTokens() {
    var semanticTokens = new SemanticTokensMarshaller()
        .add(0, 5, 10, KEYWORD, DECLARATION)
        .add(1, 7, 9, NUMBER, DEFINITION)
        .build();
    assertThat(semanticTokens).isEqualTo(List.of(0, 5, 10, 0, 1, 1, 7, 9, 2, 2));
  }

  @Test
  public void addTokensOnSameLine() {
    var semanticTokens = new SemanticTokensMarshaller()
        .add(0, 5, 3, KEYWORD, DECLARATION)
        .add(0, 10, 5, NUMBER, DEFINITION)
        .build();
    assertThat(semanticTokens).isEqualTo(List.of(0, 5, 3, 0, 1, 0, 5, 5, 2, 2));
  }
}
