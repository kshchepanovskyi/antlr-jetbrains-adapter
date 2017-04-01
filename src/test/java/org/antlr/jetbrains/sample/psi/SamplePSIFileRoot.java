package org.antlr.jetbrains.sample.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.antlr.jetbrains.adapter.SymtabUtils;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.antlr.jetbrains.sample.Icons;
import org.antlr.jetbrains.sample.SampleFileType;
import org.antlr.jetbrains.sample.SampleLanguage;
import org.antlr.jetbrains.sample.SampleParserDefinition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class SamplePSIFileRoot extends PsiFileBase implements ScopeNode {

	private final SampleParserDefinition parserDefinition;

	public SamplePSIFileRoot(@NotNull FileViewProvider viewProvider, SampleParserDefinition parserDefinition) {
		super(viewProvider, SampleLanguage.INSTANCE);
		this.parserDefinition = parserDefinition;
	}

    @NotNull
    @Override
    public FileType getFileType() {
        return SampleFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Sample Language file";
    }

    @Override
    public Icon getIcon(int flags) {
        return Icons.SAMPLE_ICON;
    }

	/** Return null since a file scope has no enclosing scope. It is
	 *  not itself in a scope.
	 */
	@Override
	public ScopeNode getContext() {
		return null;
	}

	@Nullable
	@Override
	public PsiElement resolve(PsiNamedElement element) {
//		System.out.println(getClass().getSimpleName()+
//		                   ".resolve("+element.getName()+
//		                   " at "+Integer.toHexString(element.hashCode())+")");
		if ( element.getParent() instanceof CallSubtree ) {
			return SymtabUtils.resolve(this, parserDefinition.psiElementTypeFactory,
					element, "/script/function/ID");
		}
		return SymtabUtils.resolve(this, parserDefinition.psiElementTypeFactory,
				element, "/script/vardef/ID");
	}
}
