/**
 *
 * Copyright  Mikel Egana Aranguren 
 * The FileReader.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The FileReader.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 *
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 *
 */
package uk.ac.manchester.gong.opl.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;


public class OPLFileReader extends OPLReader {

    private String oplFilePath;

    public OPLFileReader(String oplFilePath) throws IOException {
        super(new BufferedReader(new FileReader(oplFilePath)));
        this.oplFilePath = oplFilePath;
    }
}
