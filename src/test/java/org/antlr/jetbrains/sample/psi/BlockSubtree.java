package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.jetbrains.adapter.SymtabUtils;
import org.antlr.jetbrains.adapter.psi.ANTLRPsiNode;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSubtree extends ANTLRPsiNode implements ScopeNode {

	private final SampleParserDefinition parserDefinition;

	public BlockSubtree(@NotNull ASTNode node, SampleParserDefinition parserDefinition) {
		super(node);
		this.parserDefinition = parserDefinition;
	}

	@Nullable
	@Override
	public PsiElement resolve(PsiNamedElement element) {
//		System.out.println(getClass().getSimpleName()+
//		                   ".resolve("+element.getName()+
//		                   " at "+Integer.toHexString(element.hashCode())+")");

		return SymtabUtils.resolve(this, parserDefinition.psiElementTypeFactory,
				element, "/block/vardef/ID");
	}
}
