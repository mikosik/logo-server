package com.mikosik.logoserver.analyse.parser;

import com.google.common.collect.ImmutableList;
import com.mikosik.logoserver.analyse.parser.antlr.LogoParser.DocumentContext;
import org.eclipse.lsp4j.Diagnostic;

/**
 * Parse tree of document together with diagnostics.
 */
public record ParsedDocument(DocumentContext parseTree, ImmutableList<Diagnostic> diagnostics) {}
