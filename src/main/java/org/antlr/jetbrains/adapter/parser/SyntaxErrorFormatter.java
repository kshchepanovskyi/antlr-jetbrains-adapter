package org.antlr.jetbrains.adapter.parser;

/**
 * Error message formatter for syntax errors.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public interface SyntaxErrorFormatter {

    String formatMessage(SyntaxError error);
}
