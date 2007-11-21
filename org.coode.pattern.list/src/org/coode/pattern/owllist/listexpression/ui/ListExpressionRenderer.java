package org.coode.pattern.owllist.listexpression.ui;

import org.protege.editor.owl.ui.renderer.OWLObjectRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRenderer;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionVocabulary;
import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.ui.AbstractPatternRenderer;

import java.util.Set;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Oct 2, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class ListExpressionRenderer extends AbstractPatternRenderer<ListExpression> {

    private OWLDescription filler;

    public ListExpressionRenderer() {
    }

    // constructor only used for testing
    public ListExpressionRenderer(OWLEditorKit eKit) {
        init(eKit);
    }

    public String render(ListExpression list) {

        OWLObjectRenderer objRen = getOWLEditorKit().getOWLModelManager().getOWLObjectRenderer();
        OWLEntityRenderer entityRen = getOWLEditorKit().getOWLModelManager().getOWLEntityRenderer();

        StringBuilder sb = new StringBuilder();

        if (list.isNegated()){
            sb.append(ListExpressionVocabulary.NOT);
        }

        sb.append(ListExpressionVocabulary.LIST_OPEN);

        for (OWLDescription element : list) {
            if (element == null){
                sb.append(ListExpressionVocabulary.ANYTHING);
            }
            else if (element instanceof OWLObjectUnionOf){
                sb.append(render((OWLObjectUnionOf)element, objRen, entityRen));
            }
            else if (isOnlyElement(element)){
                sb.append(objRen.render(getOnlyElement(element), entityRen)).append(ListExpressionVocabulary.ONLY);
            }
            else{
                sb.append(objRen.render(element, entityRen));
            }
            sb.append(ListExpressionVocabulary.LIST_ELEMENT_SEPARATOR);
        }

        if (!list.isEmpty()) {
            sb.delete(sb.length() - ListExpressionVocabulary.LIST_ELEMENT_SEPARATOR.length(), sb.length());
            sb.append(ListExpressionVocabulary.LIST_CLOSE);
        }

        return sb.toString();
    }

    private OWLObject getOnlyElement(OWLDescription element) {
        return filler;
    }

    private boolean isOnlyElement(OWLDescription element) {
        boolean isOnly = false;

        if (element instanceof OWLObjectIntersectionOf){
            final Set<OWLDescription> ops = ((OWLObjectIntersectionOf) element).getOperands();
            if (ops.size() == 2){
                OWLDescription filler1 = null;
                OWLDescription filler2 = null;
                for (OWLDescription op : ops){
                    if (op instanceof OWLObjectAllRestriction){
                        if (filler1 == null){
                            filler1 = ((OWLObjectAllRestriction)op).getFiller();
                        }
                        else{
                            filler2 = ((OWLObjectAllRestriction)op).getFiller();
                        }
                    }
                }
                if (filler1 != null && filler2 != null){
                    if (filler1 instanceof OWLObjectAllRestriction){
                        if (((OWLObjectAllRestriction)filler1).getFiller().equals(filler2)){
                            isOnly = true;
                            filler = filler2;
                        }
                    }
                    if (!isOnly && filler2 instanceof OWLObjectAllRestriction){
                        if (((OWLObjectAllRestriction)filler2).getFiller().equals(filler1)){
                            isOnly = true;
                            filler = filler1;
                        }
                    }
                }
            }
        }
        return isOnly;
    }

    private StringBuilder render(OWLObjectUnionOf unionOf, OWLObjectRenderer objRen, OWLEntityRenderer entityRen) {
        StringBuilder unionStr = new StringBuilder(ListExpressionVocabulary.UNION_OPEN);
        for (OWLDescription op : unionOf.getOperands()){
            if (unionStr.length()!=ListExpressionVocabulary.UNION_OPEN.length()){
                unionStr.append(ListExpressionVocabulary.UNION_SEPARATOR);
            }
            unionStr.append(objRen.render(op, entityRen));
        }
        unionStr.append(ListExpressionVocabulary.UNION_CLOSE);
        return unionStr;
    }
}