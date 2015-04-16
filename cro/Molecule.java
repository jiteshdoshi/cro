package cro;



public class Molecule {

	int numHit; // The total number of hits.
	int minHit; // The index of the last local optimum hit.
	int[] molS; // The molecular structure this molecule holds.
	int[] t1, t2; //  Two temporary structures.
	double PE, KE; //The potential and kinetic energy this molecule holds.
	double localMin; // The local minimum this molecule previously reached.
		
}
