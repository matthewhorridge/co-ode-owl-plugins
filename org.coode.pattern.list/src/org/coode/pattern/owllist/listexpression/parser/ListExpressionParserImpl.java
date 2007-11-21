package org.coode.pattern.owllist.listexpression.parser;

import org.coode.pattern.owllist.listexpression.ListExpression;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.listexpression.parser.ListExpressionParser;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLDescriptionParser;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.*;

import java.util.*;

/**
 * Author: Nick Drummond<br>
 * nick.drummond@cs.manchester.ac.uk<br>
 * http://www.cs.man.ac.uk/~drummond<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 8, 2006<br><br>
 * <p/>
 * code made available under Mozilla Public License (http://www.mozilla.org/MPL/MPL-1.1.html)<br>
 * copyright 2006, The University of Manchester<br>
 */
public class ListExpressionParserImpl implements ListExpressionParser {

    private OWLModelManager mngr;
    private OWLDescriptionParser p;
    private ListExpressionDescriptor descr;

    public ListExpressionParserImpl(ListExpressionDescriptor descr, OWLModelManager mngr) {
        this.mngr = mngr;
        this.descr = descr;
    }

    public boolean isWellFormed(String expression) throws OWLExpressionParserException {
        return createOWLPattern(expression) != null;
    }

    public OWLDescription createOWLDescription(String expression) throws OWLExpressionParserException {
        return createOWLPattern(expression).toOWL(mngr.getActiveOntology(), mngr.getOWLDataFactory());
    }

    // for testing purposes only
    public List<OWLDescription> createList(String expression) throws OWLExpressionParserException {
        List<OWLDescription> list = new ArrayList<OWLDescription>();
        if (expression.startsWith(ListExpressionVocabulary.NOT)){
            expression = expression.substring(3);
        }
        if (expression.startsWith(ListExpressionVocabulary.LIST_OPEN) &&
            expression.endsWith(ListExpressionVocabulary.LIST_CLOSE)){// @@TODO should be more strict matching brackets
            String e = expression.substring(ListExpressionVocabulary.LIST_OPEN.length(),
                                            expression.length() - ListExpressionVocabulary.LIST_CLOSE.length());
            return getValues(e, ListExpressionVocabulary.LIST_OPEN.length());
        }
//        else{
//            Set<String> expectedSymbols = new HashSet<String>();
//            expectedSymbols.add("(");
//            expectedSymbols.add(")");
//            throw new OWLExpressionParserException("Expected list expression to start and end with brackets",
//                                                   0,
//                                                   1,
//                                                   isClassParser(),
//                                                   isObjectPropertyParser(),
//                                                   isDatatypePropertyParser(),
//                                                   isIndividualParser(),
//                                                   isDatatypeParser(),
//                                                   expectedSymbols);
//        }
        return list;
    }

    public ListExpression createOWLPattern(String expression) throws OWLExpressionParserException {
        ListExpression l = null;
        boolean negated = false;
        p = mngr.getOWLDescriptionParser();
        if (expression.startsWith(ListExpressionVocabulary.NOT)){
            negated = true;
            expression = expression.substring(ListExpressionVocabulary.NOT.length());
        }
        if (expression.startsWith(ListExpressionVocabulary.LIST_OPEN) &&
            expression.endsWith(ListExpressionVocabulary.LIST_CLOSE)){// @@TODO should be more strict matching brackets
            l = new ListExpression(descr);
            l.setNegated(negated);
            String e = expression.substring(ListExpressionVocabulary.LIST_OPEN.length(),
                                            expression.length() - ListExpressionVocabulary.LIST_CLOSE.length());
            if (e.endsWith(ListExpressionVocabulary.ONLY)){
                if (e.contains(ListExpressionVocabulary.LIST_ELEMENT_SEPARATOR)){
                    throw new OWLExpressionParserException("Can only use * in a single element list",
                                                           ListExpressionVocabulary.LIST_OPEN.length() + e.length()-1,
                                                           ListExpressionVocabulary.LIST_OPEN.length() + e.length()-1,
                                                           false, false, false, false, false, Collections.EMPTY_SET);
                }
                OWLDescription descr = getOWLExpression(e.substring(0, e.length()-ListExpressionVocabulary.ONLY.length()),
                                                        ListExpressionVocabulary.LIST_OPEN.length());
                l.setOnly(descr);
            }
            else{
                l.addAll(getValues(e, ListExpressionVocabulary.LIST_OPEN.length()));
            }
        }
//        else{
//            Set<String> expectedSymbols = new HashSet<String>();
//            expectedSymbols.add("(");
//            expectedSymbols.add(")");
//            throw new OWLExpressionParserException("Expected list expression to start and end with brackets",
//                                                   0,
//                                                   1,
//                                                   isClassParser(),
//                                                   isObjectPropertyParser(),
//                                                   isDatatypePropertyParser(),
//                                                   isIndividualParser(),
//                                                   isDatatypeParser(),
//                                                   expectedSymbols);
//        }
        return l;
    }

    private List<OWLDescription> getValues(String expression, int startIndex) throws OWLExpressionParserException {
        List<OWLDescription> values = new ArrayList<OWLDescription>();
        if (expression.length() > 0) {
            String[] strings = expression.split(ListExpressionVocabulary.LIST_ELEMENT_SEPARATOR);
            for (String string : strings) {
                if (string.equals(ListExpressionVocabulary.ANYTHING)){
                    if (values.isEmpty()){
                        values.add(null);
                    }
                    else if (values.get(values.size()-1) != null){
                        values.add(null);
                    }
                }
                else if (string.startsWith(ListExpressionVocabulary.UNION_OPEN) &&
                         string.endsWith(ListExpressionVocabulary.UNION_CLOSE)){
                    values.add(getOWLUnion(string, startIndex));
                }
                else{
                    OWLDescription descr = getOWLExpression(string, startIndex);
                    values.add(descr);
                }
                startIndex += string.length() + ListExpressionVocabulary.LIST_ELEMENT_SEPARATOR.length();
            }
        }
        return values;
    }

    private OWLDescription getOWLUnion(String string, int startIndex) throws OWLExpressionParserException {
        String union = string.substring(ListExpressionVocabulary.UNION_OPEN.length(),
                                        string.length() - ListExpressionVocabulary.UNION_CLOSE.length());
        int unionElementStartIndex = startIndex + ListExpressionVocabulary.UNION_OPEN.length();
        Set<OWLDescription> unionElements = new HashSet<OWLDescription>();

        for (String element : union.split(ListExpressionVocabulary.UNION_SEPARATOR_ESCAPED)){
            unionElements.add(getOWLExpression(element, unionElementStartIndex));
            unionElementStartIndex += element.length() + ListExpressionVocabulary.UNION_OPEN.length();
        }

        return mngr.getOWLDataFactory().getOWLObjectUnionOf(unionElements);
    }

    private OWLDescription getOWLExpression(String string, int tokenStartIndex) throws OWLExpressionParserException {
        try{
            return p.createOWLDescription(string);
        }
        catch(OWLExpressionParserException e){
            throw new OWLExpressionParserException(e.getMessage(),
                                                   tokenStartIndex,
                                                   tokenStartIndex + 1,
                                                   isClassParser(),
                                                   isObjectPropertyParser(),
                                                   isDatatypePropertyParser(),
                                                   isIndividualParser(),
                                                   isDatatypeParser(),
                                                   new HashSet<String>());
        }
    }

    protected boolean isDatatypeParser() {
        return false;
    }

    protected boolean isObjectPropertyParser() {
        return true;
    }

    protected boolean isDatatypePropertyParser() {
        return false;
    }

    protected boolean isIndividualParser() {
        return false;
    }

    protected boolean isClassParser() {
        return true;
    }
}