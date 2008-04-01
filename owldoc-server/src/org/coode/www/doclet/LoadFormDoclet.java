/*
* Copyright (C) 2007, University of Manchester
*/
package org.coode.www.doclet;

import org.coode.html.doclet.AbstractHTMLDoclet;
import org.coode.www.OWLDocServerConstants;

import java.io.PrintWriter;
import java.net.URL;

/**
 * Author: Nick Drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jan 25, 2008<br><br>
 */
public class LoadFormDoclet extends AbstractHTMLDoclet {

    private static final String ID = "doclet.load";

    protected void renderHeader(URL pageURL, PrintWriter out) {
        renderBoxStart("Load Ontologies", out);

        out.println("    <form style='float: left;  width: 50%;' id='specify' method='POST' action='.' target='_top' >\n" +
                    "        <label for='uri-spec'><h3 style='margin-bottom: 0;'>Specify the physical location of your ontology:</h3></label><br />\n" +
                    "        <input id='" + OWLDocServerConstants.LOAD_ONTOLOGIES_INPUT_ID +
                    "' name='" + OWLDocServerConstants.PARAM_URI + "' type='text' style='width:80%; margin-top: 0;' />\n" +
                    "        <input name='action' type='submit' value='load' />\n" +
                    "    </form>\n" +
                    "    <form style='float: right;  width: 50%;' method='POST' action='.' target='_top' >\n" +
                    "        <label for='uri-bookmark'><h3 style='margin-bottom: 0;'>Or select a bookmark from below:</h3></label><br />\n" +
                    "        <select id='uri-bookmark' name='uri' style='width:80%; margin-top: 0;'>\n" +
                    "            <option value='http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl'>pizza (http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl)</option>\n" +
                    "            <option value='http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine'>wine (http://www.w3.org/TR/2003/PR-owl-guide-20031209/wine)</option>\n" +
                    "            <option value='http://www.cs.man.ac.uk/~horrocks/ISWC2003/Tutorial/people+pets.owl.rdf'>people+pets (http://www.cs.man.ac.uk/~horrocks/ISWC2003/Tutorial/people+pets.owl.rdf)</option>\n" +
                    "            <option value='http://www.mygrid.org.uk/ontology'>mygrid (http://www.mygrid.org.uk/ontology)</option>\n" +
                    "        </select>\n" +
                    "        <input name='action' type='submit' value='load' />\n" +
                    "    </form>" +
                    "    <!--form method='post' enctype='multipart/form-data' action='.'>\n" +
                    "      <p class='instructions'>Upload an ontology:</p>\n" +
                    "      <p><label title='Choose a Local File to Upload and Validate' for='uploaded_file'>File:</label>\n" +
                    "        <input type='file' id='uploaded_file' name='uploaded_file' size='30' /></p>\n" +
                    "        <input name='action' type='submit' value='load' />\n" +
                    "    </form-->");
    }

    protected void renderFooter(URL pageURL, PrintWriter out) {
        renderBoxEnd("Load Ontologies", out);
    }

    public String getID() {
        return ID;
    }
}
