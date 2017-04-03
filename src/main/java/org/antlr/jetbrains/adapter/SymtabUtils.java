package org.antlr.jetbrains.adapter;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiNamedElement;
import java.util.Collection;
import org.antlr.jetbrains.adapter.lexer.PsiElementTypeFactory;
import org.antlr.jetbrains.adapter.psi.ScopeNode;
import org.antlr.jetbrains.adapter.psi.Trees;
import org.antlr.jetbrains.adapter.xpath.XPath;

public class SymtabUtils {

    /**
     * Return the root of a def subtree chosen from among the
     * matches from xpathToIDNodes that matches namedElement's text.
     * Assumption: ID nodes are direct children of def subtree roots.
     */
    public static PsiElement resolve(ScopeNode scope,
                                     PsiElementTypeFactory psiElementTypeFactory,
                                     PsiNamedElement namedElement,
                                     String xpathToIdNodes) {
        Collection<? extends PsiElement> defIdNodes =
                XPath.findAll(psiElementTypeFactory, scope, xpathToIdNodes);
        String id = namedElement.getName();
        PsiElement idNode = Trees.toMap(defIdNodes).get(id); // Find identifier node of variable definition
        if (idNode != null) {
            return idNode.getParent(); // return the def subtree root
        }

        // If not found, ask the enclosing scope/context to resolve.
        // That might lead back to this method, but probably with a
        // different xpathToIDNodes (which is why I don't call this method
        // directly).
        ScopeNode context = scope.getContext();
        if (context != null) {
            return context.resolve(namedElement);
        }
        // must be top scope; no resolution for element
        return null;
    }

    /**
     * Get {@link ScopeNode} for a given element.
     */
    public static ScopeNode getContextFor(PsiElement element) {
        PsiElement parent = element.getParent();
        if (parent instanceof ScopeNode) {
            return (ScopeNode) parent;
        }
        if (parent instanceof PsiErrorElement) {
            return null;
        }
        return (ScopeNode) parent.getContext();
    }
}
