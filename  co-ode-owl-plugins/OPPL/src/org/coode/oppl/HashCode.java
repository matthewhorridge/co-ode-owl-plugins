/**
 * Copyright (C) 2008, University of Manchester
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
package org.coode.oppl;

import org.coode.oppl.variablemansyntax.Variable;
import org.coode.oppl.variablemansyntax.bindingtree.Assignment;

/**
 * @author Luigi Iannone
 * 
 */
public class HashCode {
	// static boolean isPrime(int n) {
	// // 2 is the smallest prime
	// if (n <= 2) {
	// return n == 2;
	// }
	// // even numbers other than 2 are not prime
	// if (n % 2 == 0) {
	// return false;
	// }
	// // check odd divisors from 3
	// // to the square root of n
	// for (int i = 3, end = (int) Math.sqrt(n); i <= end; i += 2) {
	// if (n % i == 0) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// // find the smallest prime >= n
	// static int getPrime(int n) {
	// while (!isPrime(n)) {
	// n++;
	// }
	// return n;
	// }
	public static int hashCode(Assignment assignment) {
		int owlObjectHashCode = assignment.getAssignment().hashCode();
		Integer toReturn = assignment.getAssignedVariable().hashCode();
		return 3 * toReturn * owlObjectHashCode;
	}

	public static int hashCode(Variable variable) {
		return 5 * variable.getName().hashCode();
	}
}
