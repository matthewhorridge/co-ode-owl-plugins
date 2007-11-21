package uk.ac.manchester.gong.opl.io;

import java.util.List;
import java.util.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.net.URI;
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
 * Date: Oct 18, 2007<br><br>
 */
public class OPLReader {

    private List<String> nsDeclarations = new ArrayList<String>();
    private List<String> instructions = new ArrayList<String>();

    public OPLReader(BufferedReader in) throws IOException {
        read(in);
    }

    private void read (BufferedReader in) throws IOException {
        String str;
        while ((str = in.readLine()) != null) {
            str = str.trim();
            if (str.length() > 0){
                if(str.startsWith("SELECT")){
                    instructions.add(str);
                }
                else if (str.startsWith("ADD") || str.startsWith("REMOVE")){
                    String instruction = instructions.get(instructions.size()-1);
                    instructions.remove(instructions.size()-1);
                    instruction += str;
                    instructions.add(instruction);
                }
                else if((str.length()>0) && !str.startsWith("#")){
                    nsDeclarations.add(str);
                }
            }
        }
        in.close();
    }

    public List<String> getNSDeclarations(){
        return nsDeclarations;
    }

    public Map<String, URI> getNSMappings(){
        Map<String, URI> ns2uri = new HashMap<String, URI>();
        for (String nsMapping : nsDeclarations){
            String [] ontology_meta = nsMapping.split(" ");
            if (ontology_meta.length == 2){
                URI logicalURI = URI.create(ontology_meta[1]);
                String NS = ontology_meta[0];
                ns2uri.put(NS,logicalURI);
            }
        }
        return ns2uri;
    }

    public List<String> getInstructions (){
        return instructions;
    }
}
