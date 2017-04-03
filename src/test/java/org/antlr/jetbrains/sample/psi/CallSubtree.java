package org.antlr.jetbrains.sample.psi;

import com.intellij.lang.ASTNode;
import org.antlr.jetbrains.adapter.psi.AntlrPsiNode;
import org.jetbrains.annotations.NotNull;

public class CallSubtree extends AntlrPsiNode {
    public CallSubtree(@NotNull ASTNode node) {
        super(node);
    }
}
