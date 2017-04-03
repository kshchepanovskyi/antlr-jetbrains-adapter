package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.jetbrains.adapter.SymtabUtils;
import org.antlr.jetbrains.adapter.psi.IdentifierDefSubtree;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A subtree associated with a function definition.
 * Its scope is the set of arguments.
 */
public class FunctionSubtree extends IdentifierDefSubtree implements ScopeNode {

    private final SampleParserDefinition parserDefinition;

    public FunctionSubtree(@NotNull ASTNode node, SampleParserDefinition parserDefinition) {
        super(node, parserDefinition.ID);
        this.parserDefinition = parserDefinition;
    }

    @Nullable
    @Override
    public PsiElement resolve(PsiNamedElement element) {
        return SymtabUtils.resolve(this, parserDefinition.PSI_ELEMENT_TYPE_FACTORY,
                element, "/script/function/ID");
    }
}
