package uk.ac.manchester.gong.opl;

import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.protege.editor.owl.model.OWLModelManager;
import org.apache.log4j.Logger;
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
 * Date: Oct 17, 2007<br><br>
 */
public class ReasonerFactory {

    private static Logger logger = Logger.getLogger(ReasonerFactory.class);

    private static OWLModelManager mngr;

    public static void setOWLModelManager(OWLModelManager modelMngr){
        mngr = modelMngr;
    }

    public static OWLReasoner createReasoner(OWLOntologyManager man){
        OWLReasoner r = mngr.getOWLReasonerManager().getCurrentReasoner();
        try {
            if (!r.isClassified()){
                r.classify();
            }
        }
        catch (OWLReasonerException e) {
            logger.error(e);
        }
        return r;
    }

//    public static OWLReasoner createReasoner(OWLOntologyManager man){
//        try {
//            // The following code is a little overly complicated.  The reason for using
//            // reflection to create an instance of pellet is so that there is no compile time
//            // dependency (since the pellet libraries aren't contained in the OWL API repository).
//            // Normally, one would simply create an instance using the following incantation:
//            //
//            //     OWLReasoner reasoner = new Reasoner()
//            //
//            // Where the full class name for Reasoner is org.mindswap.pellet.owlapi.Reasoner
//            //
//            // Pellet requires the Pellet libraries  (pellet.jar, aterm-java-x.x.jar) and the
//            // XSD libraries that are bundled with pellet: xsdlib.jar and relaxngDatatype.jar
//            String reasonerClassName = "org.mindswap.pellet.owlapi.Reasoner";
//            Class reasonerClass = Class.forName(reasonerClassName);
//            Constructor<OWLReasoner> con = reasonerClass.getConstructor(OWLOntologyManager.class);
//            OWLReasoner r = con.newInstance(man);
//            r.loadOntologies(man.getNSDeclarations());
//        }
//        catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//        catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//        catch (InvocationTargetException e) {
//            throw new RuntimeException(e);
//        }
//        catch (InstantiationException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
