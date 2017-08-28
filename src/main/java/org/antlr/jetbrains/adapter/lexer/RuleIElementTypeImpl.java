package org.antlr.jetbrains.adapter.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of [RuleIElementType] based on [IElementType].
 */
public class RuleIElementTypeImpl extends IElementType implements RuleIElementType {

    private final int ruleIndex;

    public RuleIElementTypeImpl(int ruleIndex,
            @NotNull @NonNls String debugName,
            @Nullable Language language) {
        super(debugName, language);
        this.ruleIndex = ruleIndex;
    }

    public int getRuleIndex() {
        return ruleIndex;
    }
}
