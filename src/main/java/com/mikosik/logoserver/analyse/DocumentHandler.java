package com.mikosik.logoserver.analyse;

import static com.google.common.collect.ImmutableList.toImmutableList;

import com.google.common.collect.ImmutableList;
import com.mikosik.logoserver.analyse.base.Diagnostics;
import com.mikosik.logoserver.analyse.declaration.DeclarationFinder;
import com.mikosik.logoserver.analyse.declaration.DeclarationsProvider;
import com.mikosik.logoserver.analyse.declaration.ReferencesProvider;
import com.mikosik.logoserver.analyse.highlight.SemanticTokensProvider;
import com.mikosik.logoserver.analyse.parser.ParsedDocument;
import com.mikosik.logoserver.analyse.parser.Parser;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.Location;

/**
 * Provides semantic tokens, diagnostics and declarations for a given document.
 */
public class DocumentHandler {
  private final String uri;
  private final Parser parser;
  private final SemanticTokensProvider semanticTokensProvider;
  private final DeclarationsProvider declarationsProvider;
  private final ReferencesProvider referencesProvider;
  private ParsedDocument parsedDocument;

  public DocumentHandler(String uri) {
    this.uri = uri;
    this.parser = new Parser();
    this.semanticTokensProvider = new SemanticTokensProvider();
    this.declarationsProvider = new DeclarationsProvider();
    this.referencesProvider = new ReferencesProvider();
    this.parsedDocument = null;
  }

  public void setText(String text) {
    parsedDocument = parser.parse(text);
  }

  public ImmutableList<Diagnostic> getDiagnostics() {
    checkState();
    return parsedDocument.diagnostics().stream()
        .map(Diagnostics::newDiagnostic)
        .collect(ImmutableList.toImmutableList());
  }

  public ImmutableList<Integer> semanticTokensFull() {
    checkState();
    return semanticTokensProvider.semanticTokensOf(parsedDocument.parseTree());
  }

  public ImmutableList<Location> declaration(int line, int characterAtLine) {
    checkState();
    var declarationFinder = createDeclarationFinder();
    return declarationFinder.findDeclarationsOf(line, characterAtLine).stream()
        .map(r -> new Location(uri, r))
        .collect(toImmutableList());
  }

  private DeclarationFinder createDeclarationFinder() {
    var references = referencesProvider.referencesFrom(parsedDocument.parseTree());
    var declarations = declarationsProvider.declarationsFrom(parsedDocument.parseTree());
    return new DeclarationFinder(references, declarations);
  }

  private void checkState() {
    if (parsedDocument == null) {
      throw new IllegalStateException("Document has not been opened yet.");
    }
  }
}
