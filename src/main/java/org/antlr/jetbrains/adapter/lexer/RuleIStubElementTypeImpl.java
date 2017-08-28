package org.antlr.jetbrains.adapter.lexer;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of [RuleIElementType] based on [IStubElementType].
 */
public abstract class RuleIStubElementTypeImpl<StubT extends StubElement, PsiT extends PsiElement> extends
        IStubElementType<StubT, PsiT> implements RuleIElementType {

    private final int ruleIndex;

    public RuleIStubElementTypeImpl(int ruleIndex,
            @NotNull @NonNls String debugName,
            @Nullable Language language) {
        super(debugName, language);
        this.ruleIndex = ruleIndex;
    }

    public int getRuleIndex() {
        return ruleIndex;
    }
}
