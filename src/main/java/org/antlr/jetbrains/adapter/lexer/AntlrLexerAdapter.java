package org.antlr.jetbrains.adapter.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.IntStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.jetbrains.annotations.Nullable;

/**
 * This is the adaptor class for implementations of {@link
 * com.intellij.lexer.Lexer} backed by an ANTLR 4 lexer. It supports
 * any ANTLR 4 lexer that does not store extra information for use in
 * custom actions. For lexers that do not store custom state information, this
 * default implementation is sufficient. Otherwise, subclass and override:
 * {#getInitialState} and {#getLexerState}.
 * <p>
 * Intellij lexers need to track state as they must be able to
 * restart lexing in the middle of the input buffer. From
 * <a href="http://www.jetbrains.org/intellij/sdk/docs/reference_guide/custom_language_support/implementing_lexer.html">Intellij
 * doc</a>:
 * <p>
 * "A lexer that can be used incrementally may need to return its
 * state, which means the context corresponding to each position in a
 * file. For example, a Java lexer could have separate states for top
 * level context, comment context and string literal context. An
 * important requirement for a syntax highlighting lexer is that its
 * state must be represented by a single integer number returned from
 * Lexer.getState(). That state will be passed to the Lexer.start()
 * method, along with the start offset of the fragment to process,
 * when lexing is resumed from the middle of a file."
 * <p>
 * This implementation supports single- as well as multi-mode lexers.
 *
 * @author Sam Harwell
 */
public class AntlrLexerAdapter extends com.intellij.lexer.LexerBase {
    /**
     * Gets the {@link Language} supported by this lexer. This
     * value is passed to {@link PsiElementTypeFactory} to ensure the
     * correct collection of {@link IElementType} is used for
     * assigning element types to tokens in {@link #getTokenType}.
     */
    private final Language language;

    /**
     * This field caches the collection of element types returned
     * by {@link PsiElementTypeFactory#getTokenIElementTypes} for
     * optimum efficiency of the {@link #getTokenType} method.
     */
    private final List<? extends IElementType> tokenElementTypes;

    /**
     * This is the backing field for {@link #getLexer()}.
     */
    private final Lexer lexer;

    /**
     * Provides a map from a {@code State} object &rarr; state
     * index tracked by IntelliJ. This field provides for an
     * efficient implementation of {@link #getState}.
     */
    private final Map<AntlrLexerState, Integer> stateCacheMap = new HashMap<>();

    /**
     * Provides a map from a state index tracked by IntelliJ
     * &rarr; {@code AntlrLexerState} object describing the ANTLR lexer
     * state. This field provides for an efficient implementation
     * of {@link #toLexerState}.
     */
    private final List<AntlrLexerState> stateCache = new ArrayList<>();

    /**
     * Caches the {@code buffer} provided in the call to {@link
     * #start}, as required for implementing {@link
     * #getBufferSequence}.
     */
    private CharSequence buffer;

    /**
     * Caches the {@code endOffset} provided in the call to {@link
     * #start}, as required for implementing {@link
     * #getBufferEnd}.
     */
    private int endOffset;

    /**
     * This field tracks the "exposed" lexer state, which differs
     * from the actual current state of the lexer returned by
     * {@link #getLexer()} by one token.
     * <p>
     * <p>Due to the way IntelliJ requests token information, the
     * ANTLR {@link Lexer} is always positioned one token past the
     * token whose information is returned by calls to {@link
     * #getTokenType}, {@link #getTokenType}, etc. When {@link
     * #getState} is called, IntelliJ expects a state which is
     * able to reproduce the {@link #currentToken}, but the ANTLR
     * lexer has already moved past it. This field is assigned
     * based in {@link #advance} based on the lexer state
     * <em>before</em> the current token, after which {@link
     * Lexer#nextToken} can be called to obtain {@link
     * #currentToken}.</p>
     */
    private AntlrLexerState currentState;

    /**
     * This field tracks the "exposed" lexer token. This is the
     * result of the most recent call to {@link Lexer#nextToken}
     * on the underlying ANTLR lexer, and is the source of
     * information for {@link #getTokenStart}, {@link
     * #getTokenType}, etc.
     *
     * @see #currentState
     */
    private Token currentToken;

    /**
     * Constructs a new instance of {@link AntlrLexerAdapter} with
     * the specified {@link Language} and underlying ANTLR {@link
     * Lexer}.
     *
     * @param language The language.
     * @param lexer The underlying ANTLR lexer.
     */
    public AntlrLexerAdapter(Language language, Lexer lexer, PsiElementTypeFactory psiElementTypeFactory) {
        this.language = language;
        this.tokenElementTypes = psiElementTypeFactory.getTokenIElementTypes();
        this.lexer = lexer;
    }

    /**
     * Gets the ANTLR {@link Lexer} used for actual tokenization of the input.
     *
     * @return the ANTLR {@link Lexer} instance
     */
    protected Lexer getLexer() {
        return lexer;
    }

    /**
     * Gets the {@link Token} object providing information for
     * calls to {@link #getTokenStart}, {@link #getTokenType},
     * etc.
     *
     * @return The current {@link Token} instance.
     */
    protected Token getCurrentToken() {
        return currentToken;
    }

    @Override
    public void start(CharSequence buffer, int startOffset, int endOffset, int initialState) {
        this.buffer = buffer;
        this.endOffset = endOffset;

        CharStream in = new CharSequenceCharStream(buffer, endOffset, IntStream.UNKNOWN_SOURCE_NAME);
        in.seek(startOffset);

        AntlrLexerState state;
        if (startOffset == 0 && initialState == 0) {
            state = getInitialState();
        } else {
            state = toLexerState(initialState);
        }

        applyLexerState(in, state);
        advance();
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        return getTokenType(currentToken.getType());
    }

    @Nullable
    private IElementType getTokenType(int antlrTokenType) {
        if (antlrTokenType == Token.EOF) {
            // return null when lexing is finished
            return null;
        }

        return tokenElementTypes.get(antlrTokenType);
    }

    @Override
    public void advance() {
        currentState = getLexerState(lexer);
        currentToken = lexer.nextToken();
    }

    @Override
    public int getState() {
        AntlrLexerState state = currentState != null ? currentState : getInitialState();
        Integer existing = stateCacheMap.get(state);
        if (existing == null) {
            existing = stateCache.size();
            stateCache.add(state);
            stateCacheMap.put(state, existing);
        }

        return existing;
    }

    @Override
    public int getTokenStart() {
        return currentToken.getStartIndex();
    }

    @Override
    public int getTokenEnd() {
        return currentToken.getStopIndex() + 1;
    }

    @Override
    public CharSequence getBufferSequence() {
        return buffer;
    }

    @Override
    public int getBufferEnd() {
        return endOffset;
    }

    /**
     * Update the current lexer to use the specified {@code input}
     * stream starting in the specified {@code state}.
     * <p>
     * <p>The current lexer may be obtained by calling {@link
     * #getLexer}. The default implementation calls {@link
     * Lexer#setInputStream} to set the input stream, followed by
     * {@link AntlrLexerState#apply} to initialize the state of
     * the lexer.</p>
     *
     * @param input The new input stream for the lexer.
     * @param state A {@code AntlrLexerState} instance containing the starting state for the lexer.
     */
    protected void applyLexerState(CharStream input, AntlrLexerState state) {
        lexer.setInputStream(input);
        state.apply(lexer);
    }

    /**
     * Get the initial {@code AntlrLexerState} of the lexer.
     *
     * @return a {@code AntlrLexerState} instance representing the state of
     * the lexer at the beginning of an input.
     */
    protected AntlrLexerState getInitialState() {
        return new AntlrLexerState(Lexer.DEFAULT_MODE, null);
    }

    /**
     * Get a {@code AntlrLexerState} instance representing the current state
     * of the specified lexer.
     *
     * @param lexer The lexer.
     *
     * @return A {@code AntlrLexerState} instance containing the current state of the lexer.
     */
    protected AntlrLexerState getLexerState(Lexer lexer) {
        if (lexer._modeStack.isEmpty()) {
            return new AntlrLexerState(lexer._mode, null);
        }

        return new AntlrLexerState(lexer._mode, lexer._modeStack);
    }

    /**
     * Gets the {@code AntlrLexerState} corresponding to the specified IntelliJ {@code state}.
     *
     * @param state The lexer state provided by IntelliJ.
     *
     * @return The {@code AntlrLexerState} instance corresponding to the specified state.
     */
    protected AntlrLexerState toLexerState(int state) {
        return stateCache.get(state);
    }
}
