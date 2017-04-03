package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.jetbrains.adapter.SymtabUtils;
import org.antlr.jetbrains.adapter.psi.AntlrPsiNode;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSubtree extends AntlrPsiNode implements ScopeNode {

    private final SampleParserDefinition parserDefinition;

    public BlockSubtree(@NotNull ASTNode node, SampleParserDefinition parserDefinition) {
        super(node);
        this.parserDefinition = parserDefinition;
    }

    @Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        return SymtabUtils.resolve(this, parserDefinition.PSI_ELEMENT_TYPE_FACTORY,
                element, "/block/vardef/ID");
    }
}
