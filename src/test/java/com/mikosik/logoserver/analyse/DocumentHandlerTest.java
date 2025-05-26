package com.mikosik.logoserver.analyse;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DocumentHandlerTest {
  @Nested
  class SemanticTokensFull {
    @Test
    void throwsExceptionWhenNoDocumentProvided() {
      var documentHandler = new DocumentHandler("uri");
      assertThrows(IllegalStateException.class, documentHandler::semanticTokensFull);
    }

    @Test
    void returnsTokens() {
      var documentHandler = new DocumentHandler("uri");
      documentHandler.setText("forward 10");
      assertThat(documentHandler.semanticTokensFull())
          .isEqualTo(List.of(0, 0, 7, 0, 0, 0, 8, 2, 2, 0));
    }
  }
}
