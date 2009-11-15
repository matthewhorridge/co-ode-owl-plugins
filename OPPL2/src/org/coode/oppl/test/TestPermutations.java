package org.coode.oppl.test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.coode.oppl.match.CollectionPermutation;

public class TestPermutations extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testPermutations() {
		List<?> list = Arrays.asList("a", "b", "c", "d", "e", "f", "h", "i");
		Set<?> allPermutations = CollectionPermutation.getAllPermutations(list);
		System.out.println(allPermutations);
		System.out.println(allPermutations.size());
	}
}
