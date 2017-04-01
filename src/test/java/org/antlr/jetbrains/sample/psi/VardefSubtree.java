package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adapter.psi.IdentifierDefSubtree;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;

public class VardefSubtree extends IdentifierDefSubtree {

	private final SampleParserDefinition parserDefinition;

	public VardefSubtree(@NotNull ASTNode node, SampleParserDefinition parserDefinition) {
		super(node, parserDefinition.ID);
		this.parserDefinition = parserDefinition;
	}
}
