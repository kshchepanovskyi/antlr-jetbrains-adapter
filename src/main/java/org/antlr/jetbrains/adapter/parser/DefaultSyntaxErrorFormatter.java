package org.antlr.jetbrains.adapter.parser;

/**
 * Default error message formatter.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class DefaultSyntaxErrorFormatter implements SyntaxErrorFormatter {

    @Override
    public String formatMessage(SyntaxError error) {
        return error.getMessage();
    }
}
