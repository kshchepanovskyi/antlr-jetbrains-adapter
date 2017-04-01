package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;

public class ArgdefSubtree extends VardefSubtree {

	private final SampleParserDefinition parserDefinition;

	public ArgdefSubtree(@NotNull ASTNode node, SampleParserDefinition parserDefinition) {
		super(node, parserDefinition);
		this.parserDefinition = parserDefinition;
	}
}
