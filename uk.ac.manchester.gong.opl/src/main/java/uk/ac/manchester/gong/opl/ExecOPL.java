/**
 * 
 * Copyright Mikel Egana Aranguren 
 * The ExecOPL.java software is free software and is licensed under the terms of the 
 * GNU General Public License (GPL) as published by the Free Software Foundation; 
 * either version 2 of the License, or (at your option) any later version. The ExecOPL.java 
 * software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GPL for more details; a copy of the GPL is included with this product. 
 * 
 * For more info:
 * mikel.eganaaranguren@cs.manchester.ac.uk
 * http://www.gong.manchester.ac.uk
 * 
 */
package uk.ac.manchester.gong.opl;

import java.io.IOException;


public class ExecOPL {

	public static void main(String[] args) {
		OPLCore oplcore = new OPLCore(args [0], args[1]);
        try {
            oplcore.work();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

