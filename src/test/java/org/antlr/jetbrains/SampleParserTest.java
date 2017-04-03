package org.antlr.jetbrains;

import org.antlr.jetbrains.sample.parser.SampleLanguageLexer;
import org.antlr.jetbrains.sample.parser.SampleLanguageParser;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.junit.Assert;
import org.junit.Test;

/**
 * Basic tests to ensure adapter works.
 *
 * @author Kostiantyn Shchepanovskyi
 */
public class SampleParserTest {

    private static final ErrorListener ERROR_LISTENER = new ErrorListener();

    @Test
    public void varDefinitionAfterFunction() throws Exception {
        String input = "func x(){} var a = 9";
        SampleLanguageParser.ScriptContext scriptContext = parseScript(input);
        Assert.assertNotNull(scriptContext);
    }

    private SampleLanguageParser.ScriptContext parseScript(String input) {
        CharStream stream = CharStreams.fromString(input);
        SampleLanguageLexer lexer = new SampleLanguageLexer(stream);
        lexer.addErrorListener(ERROR_LISTENER);
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        SampleLanguageParser parser = new SampleLanguageParser(tokenStream);
        parser.addErrorListener(ERROR_LISTENER);
        return parser.script();
    }

    private static class ErrorListener extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
            throw new IllegalStateException("Can not parse line " + line + " position " + charPositionInLine + ": " + msg, e);
        }
    }
}
