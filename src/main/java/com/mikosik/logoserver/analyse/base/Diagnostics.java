package com.mikosik.logoserver.analyse.base;

import static com.mikosik.logoserver.analyse.base.Ranges.newRange;

import org.eclipse.lsp4j.Diagnostic;

/**
 * Helper methods for creating LSP Diagnostics objects.
 */
public class Diagnostics {
  public static Diagnostic newDiagnostic(Diagnostic diagnostic) {
    return new Diagnostic(
        newRange(diagnostic.getRange()),
        diagnostic.getMessage(),
        diagnostic.getSeverity(),
        diagnostic.getSource());
  }
}
