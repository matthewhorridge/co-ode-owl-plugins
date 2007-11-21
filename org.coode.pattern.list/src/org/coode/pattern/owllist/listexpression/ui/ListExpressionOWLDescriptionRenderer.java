package org.coode.pattern.owllist.listexpression.ui;

import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLObjectAllRestriction;
import org.coode.pattern.owllist.listexpression.ListExpressionDescriptor;
import org.coode.pattern.owllist.listexpression.ListExpression;

import java.util.List;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 15, 2007<br><br>
 */
public class ListExpressionOWLDescriptionRenderer {

    private ListExpressionDescriptor descriptor;


    public ListExpressionOWLDescriptionRenderer(ListExpressionDescriptor descriptor) {
        this.descriptor = descriptor;
    }

    public OWLDescription render(ListExpression list, OWLDataFactory df) {
        OWLDescription descr;
        if (list.isOnly()){
            descr = onlyToOWL(list.getOnly(), df);
        }
        else{
            if (list.isStartOpen()){ // handle the "exact match" or "ends with" union in the pattern
                Set<OWLDescription> union = new HashSet<OWLDescription>();
                union.add(toOWL(list, list.subList(1, list.size()), df));
                union.add(toOWL(list, list, df));
                descr = df.getOWLObjectUnionOf(union);
            }
            else{
                descr = toOWL(list, list, df);
            }
            if (list.isNegated()){ // only generate the not at the start of the expression
                descr = df.getOWLObjectComplementOf(descr);
            }
        }
        return descr;
    }

    /** Are lists always terminated??
     * (A, B, ...) is this:
     * - A next B
     * - A next B followedBy EmptyList
     * The first subsumes the second and is how we've looked at this up to now (ie the paper allows unterminated)
     * The second might allow simplification - a list is always terminated by EmptyList
     */
    private OWLDescription toOWL(ListExpression pattern, List<OWLDescription> elements, OWLDataFactory df) {
        if (elements.isEmpty()){
            return descriptor.getDefaultEmptyListClass();
        }
        else{
            OWLDescription descr = null;
            OWLDescription element = elements.get(0);

            List<OWLDescription> rest = null;
            if (elements.size() > 1){
                rest = elements.subList(1, elements.size());
            }
            else if (element != null){ // as long as the last element is not "..."
                rest = Collections.EMPTY_LIST; // will cause the list to be terminated
            }

            if (element == null){
                if (rest != null){
                    descr = df.getOWLObjectSomeRestriction(pattern.getFollowedByProperty(), toOWL(pattern, rest, df));
                }
            }
            else{
                descr = df.getOWLObjectSomeRestriction(pattern.getContentsProperty(), element);
                if (rest != null){
                    if (rest.size()>1 && rest.get(0) == null){ // treat the following ... as a followedBy
                        OWLDescription restOWL = toOWL(pattern, rest.subList(1, rest.size()), df);
                        if (restOWL != null){
                            Set<OWLDescription> restrs = new HashSet<OWLDescription>();
                            restrs.add(descr);
                            restrs.add(df.getOWLObjectSomeRestriction(pattern.getFollowedByProperty(), restOWL));
                            descr = df.getOWLObjectIntersectionOf(restrs);
                        }
                    }
                    else{
                        OWLDescription restOWL = toOWL(pattern, rest, df);
                        if (restOWL != null){
                            Set<OWLDescription> restrs = new HashSet<OWLDescription>();
                            restrs.add(descr);
                            restrs.add(df.getOWLObjectSomeRestriction(pattern.getNextProperty(), restOWL));
                            descr = df.getOWLObjectIntersectionOf(restrs);
                        }
                    }
                }
            }
            return descr;
        }
    }

    private OWLDescription onlyToOWL(OWLDescription descr, OWLDataFactory df) {
        Set<OWLDescription> and = new HashSet<OWLDescription>();
        final OWLObjectAllRestriction hasContentsOnly =
                df.getOWLObjectAllRestriction(descriptor.getDefaultContentsProperty(), descr);
        and.add(hasContentsOnly);
        and.add(df.getOWLObjectAllRestriction(descriptor.getDefaultFollowedByProperty(), hasContentsOnly));
        return df.getOWLObjectIntersectionOf(and);
    }
}
