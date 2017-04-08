package org.antlr.jetbrains.sample;

import com.intellij.lang.ASTNode;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import org.antlr.jetbrains.adapter.lexer.AntlrLexerAdapter;
import org.antlr.jetbrains.adapter.lexer.PsiElementTypeFactory;
import org.antlr.jetbrains.adapter.lexer.RuleIElementType;
import org.antlr.jetbrains.adapter.lexer.TokenIElementType;
import org.antlr.jetbrains.adapter.parser.AntlrParserAdapter;
import org.antlr.jetbrains.adapter.psi.AntlrPsiNode;
import org.antlr.jetbrains.sample.parser.SampleLanguageLexer;
import org.antlr.jetbrains.sample.parser.SampleLanguageParser;
import org.antlr.jetbrains.sample.psi.ArgdefSubtree;
import org.antlr.jetbrains.sample.psi.BlockSubtree;
import org.antlr.jetbrains.sample.psi.CallSubtree;
import org.antlr.jetbrains.sample.psi.FunctionSubtree;
import org.antlr.jetbrains.sample.psi.SamplePsiFileRoot;
import org.antlr.jetbrains.sample.psi.VardefSubtree;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jetbrains.annotations.NotNull;

public class SampleParserDefinition implements ParserDefinition {
    public static final IFileElementType FILE =
            new IFileElementType(SampleLanguage.INSTANCE);

    public static final PsiElementTypeFactory PSI_ELEMENT_TYPE_FACTORY = PsiElementTypeFactory.create(SampleLanguage.INSTANCE, new SampleLanguageParser(null));

    public static final TokenIElementType ID = PSI_ELEMENT_TYPE_FACTORY.getTokenIElementTypes()
            .get(SampleLanguageLexer.ID);

    public static final TokenSet COMMENTS =
            PSI_ELEMENT_TYPE_FACTORY.createTokenSet(
                    SampleLanguageLexer.COMMENT,
                    SampleLanguageLexer.LINE_COMMENT);

    public static final TokenSet WHITESPACE =
            PSI_ELEMENT_TYPE_FACTORY.createTokenSet(
                    SampleLanguageLexer.WS);

    public static final TokenSet STRING =
            PSI_ELEMENT_TYPE_FACTORY.createTokenSet(
                    SampleLanguageLexer.STRING);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        SampleLanguageLexer lexer = new SampleLanguageLexer(null);
        return new AntlrLexerAdapter(SampleLanguage.INSTANCE, lexer, PSI_ELEMENT_TYPE_FACTORY);
    }

    @Override
    @NotNull
    public PsiParser createParser(final Project project) {
        final SampleLanguageParser parser = new SampleLanguageParser(null);
        return new AntlrParserAdapter(SampleLanguage.INSTANCE, parser, PSI_ELEMENT_TYPE_FACTORY) {
            @Override
            protected ParseTree parse(Parser parser, IElementType root) {
                // start rule depends on root passed in; sometimes we want to create an ID node etc...
                if (root instanceof IFileElementType) {
                    return ((SampleLanguageParser) parser).script();
                }
                // let's hope it's an ID as needed by "rename function"
                return ((SampleLanguageParser) parser).primary();
            }
        };
    }

    /**
     * "Tokens of those types are automatically skipped by PsiBuilder."
     */
    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITESPACE;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRING;
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    /**
     * What is the IFileElementType of the root parse tree node? It
     * is called from {@link #createFile(FileViewProvider)} at least.
     */
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    /**
     * Create the root of your PSI tree (a PsiFile).
     * <p>
     * From IntelliJ IDEA Architectural Overview:
     * "A PSI (Program Structure Interface) file is the root of a structure
     * representing the contents of a file as a hierarchy of elements
     * in a particular programming language."
     * <p>
     * PsiFile is to be distinguished from a FileASTNode, which is a parse
     * tree node that eventually becomes a PsiFile. From PsiFile, we can get
     * it back via: {@link PsiFile#getNode}.
     */
    @Override
    public PsiFile createFile(FileViewProvider viewProvider) {
        return new SamplePsiFileRoot(viewProvider, this);
    }

    /**
     * Convert from *NON-LEAF* parse node (AST they call it)
     * to PSI node. Leaves are created in the AST factory.
     * Rename re-factoring can cause this to be
     * called on a TokenIElementType since we want to rename ID nodes.
     * In that case, this method is called to create the root node
     * but with ID type. Kind of strange, but we can simply create a
     * ASTWrapperPsiElement to make everything work correctly.
     * <p>
     * RuleIElementType.  Ah! It's that ID is the root
     * IElementType requested to parse, which means that the root
     * node returned from parsetree->PSI conversion.  But, it
     * must be a CompositeElement! The adaptor calls
     * rootMarker.done(root) to finish off the PSI conversion.
     * See {@link AntlrParserAdapter#parse(IElementType root,
     * PsiBuilder)}
     * <p>
     * If you don't care to distinguish PSI nodes by type, it is
     * sufficient to create a {@link AntlrPsiNode} around
     * the parse tree node
     */
    @NotNull
    public PsiElement createElement(ASTNode node) {
        IElementType elType = node.getElementType();
        if (elType instanceof TokenIElementType) {
            return new AntlrPsiNode(node);
        }
        if (!(elType instanceof RuleIElementType)) {
            return new AntlrPsiNode(node);
        }
        RuleIElementType ruleElType = (RuleIElementType) elType;
        switch (ruleElType.getRuleIndex()) {
            case SampleLanguageParser.RULE_function:
                return new FunctionSubtree(node, this);
            case SampleLanguageParser.RULE_vardef:
                return new VardefSubtree(node, this);
            case SampleLanguageParser.RULE_formal_arg:
                return new ArgdefSubtree(node, this);
            case SampleLanguageParser.RULE_block:
                return new BlockSubtree(node, this);
            case SampleLanguageParser.RULE_call_expr:
                return new CallSubtree(node);
            default:
                return new AntlrPsiNode(node);
        }
    }
}
