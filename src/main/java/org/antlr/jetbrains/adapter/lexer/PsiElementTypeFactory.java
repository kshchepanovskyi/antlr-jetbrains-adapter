package org.antlr.jetbrains.adapter.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.misc.Utils;
import org.jetbrains.annotations.NotNull;

/**
 * The factory that maps all tokens and rule names into
 * IElementType objects: {@link TokenIElementType} and {@link RuleIElementType}.
 */
public class PsiElementTypeFactory {

    private final List<TokenIElementType> tokenIElementTypes;
    private final List<RuleIElementType> ruleIElementTypes;
    private final Map<String, Integer> tokenNames;
    private final Map<String, Integer> ruleNames;
    private final TokenIElementType eofIElementType;

    private PsiElementTypeFactory(Language language, Parser parser) {
        Vocabulary vocabulary = parser.getVocabulary();
        String[] ruleNames = parser.getRuleNames();
        tokenIElementTypes = createTokenIElementTypes(language, vocabulary);
        ruleIElementTypes = createRuleIElementTypes(language, ruleNames);
        tokenNames = createTokenTypeMap(vocabulary);
        this.ruleNames = createRuleIndexMap(ruleNames);
        eofIElementType = new TokenIElementType(Token.EOF, "EOF", language);
    }

    public static PsiElementTypeFactory create(Language language, Parser parser) {
        return new PsiElementTypeFactory(language, parser);
    }

    public TokenIElementType getEofElementType() {
        return eofIElementType;
    }

    public List<TokenIElementType> getTokenIElementTypes() {
        return tokenIElementTypes;
    }

    public List<RuleIElementType> getRuleIElementTypes() {
        return ruleIElementTypes;
    }

    public Map<String, Integer> getRuleNameToIndexMap() {
        return ruleNames;
    }

    public Map<String, Integer> getTokenNameToTypeMap() {
        return tokenNames;
    }

    /**
     * Get a map from token names to token types.
     */
    private Map<String, Integer> createTokenTypeMap(Vocabulary vocabulary) {
        return IntStream.rangeClosed(0, vocabulary.getMaxTokenType())
                .boxed()
                .collect(Collectors.toMap(
                        vocabulary::getDisplayName,
                        Function.identity()));
    }

    /**
     * Get a map from rule names to rule indexes.
     */
    private Map<String, Integer> createRuleIndexMap(String[] ruleNames) {
        return Utils.toMap(ruleNames);
    }

    @NotNull
    private List<TokenIElementType> createTokenIElementTypes(Language language, Vocabulary vocabulary) {
        return IntStream.rangeClosed(0, vocabulary.getMaxTokenType())
                .boxed()
                .map(i -> {
                    String name = vocabulary.getDisplayName(i);
                    return new TokenIElementType(i, name, language);
                })
                .collect(Collectors.toList());
    }

    @NotNull
    private List<RuleIElementType> createRuleIElementTypes(Language language, String[] ruleNames) {
        List<RuleIElementType> result;
        RuleIElementType[] elementTypes = new RuleIElementType[ruleNames.length];
        for (int i = 0; i < ruleNames.length; i++) {
            elementTypes[i] = new RuleIElementType(i, ruleNames[i], language);
        }
        result = Collections.unmodifiableList(Arrays.asList(elementTypes));
        return result;
    }

    /**
     * Create token set from given tokens.
     */
    public TokenSet createTokenSet(int... types) {
        List<TokenIElementType> tokenIElementTypes = getTokenIElementTypes();
        IElementType[] elementTypes = new IElementType[types.length];
        for (int i = 0; i < types.length; i++) {
            if (types[i] == Token.EOF) {
                elementTypes[i] = getEofElementType();
            } else {
                elementTypes[i] = tokenIElementTypes.get(types[i]);
            }
        }
        return TokenSet.create(elementTypes);
    }
}
