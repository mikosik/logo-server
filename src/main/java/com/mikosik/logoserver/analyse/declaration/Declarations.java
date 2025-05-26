package com.mikosik.logoserver.analyse.declaration;

import com.google.common.collect.ImmutableMultimap;
import org.eclipse.lsp4j.Range;

/**
 * Holds declarations of variables and procedures as maps from name to range.
 */
public record Declarations(
    ImmutableMultimap<String, Range> variables, ImmutableMultimap<String, Range> procedures) {}
