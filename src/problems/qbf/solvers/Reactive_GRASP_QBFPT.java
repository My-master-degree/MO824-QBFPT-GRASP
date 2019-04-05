package problems.qbf.solvers;

import java.awt.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import metaheuristics.grasp.AbstractGRASP;
import metaheuristics.grasp.ReactiveGRASP;
import problems.Evaluator;
import problems.qbf.QBFPT_Inverse;
import problems.qbf.QBF_Inverse;
import solutions.Solution;



/**
 * Metaheuristic GRASP (Greedy Randomized Adaptive Search Procedure) for
 * obtaining an optimal solution to a QBF (Quadractive Binary Function --
 * {@link #QuadracticBinaryFunction}). Since by default this GRASP considers
 * minimization problems, an inverse QBF function is adopted.
 * 
 * @author ccavellucci, fusberti
 */
public class Reactive_GRASP_QBFPT extends ReactiveGRASP<Integer> {
	
	/**
	 * Constructor for the GRASP_QBF class. An inverse QBF objective function is
	 * passed as argument for the superclass constructor.
	 * 
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 * @param filename
	 *            Name of the file for which the objective function parameters
	 *            should be read.
	 * @throws IOException
	 *             necessary for I/O operations.
	 */
	public Reactive_GRASP_QBFPT(double[] alphas, Integer iterations, int minNuberOfSolutionsPerAlpha, String filename) throws IOException {
		super(new QBFPT_Inverse(filename), alphas, iterations, minNuberOfSolutionsPerAlpha);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeCL()
	 */
	@Override
	public ArrayList<Integer> makeCL() {

		ArrayList<Integer> _CL = new ArrayList<Integer>();
		for (int i = 0; i < ObjFunction.getDomainSize(); i++) {
			Integer cand = new Integer(i);
			_CL.add(cand);
		}

		return _CL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#makeRCL()
	 */
	@Override
	public ArrayList<Integer> makeRCL() {

		ArrayList<Integer> _RCL = new ArrayList<Integer>();

		return _RCL;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see grasp.abstracts.AbstractGRASP#updateCL()
	 */
	@Override
	public void updateCL() {
		QBFPT_Inverse QBFPT_Inverse = (QBFPT_Inverse) super.ObjFunction;
		Integer[][] triples = QBFPT_Inverse.triples;
//		mount filled triples
		Map<Integer[], Integer> filledTriples = new HashMap<Integer[], Integer> ();			
		for (int i = 0; i < super.incumbentSol.size(); i++) {
			Integer item = super.incumbentSol.get(i);
			for (int j = 0; j < triples.length; j++) {				
				Integer tripleCount = filledTriples.get(triples[j]);						
				Boolean tripleCountNull = tripleCount == null,
						candidateMatch = triples[j][0].equals(item) || triples[j][1].equals(item) || triples[j][2].equals(item);
//				System.out.println("\tTriple ("+triples[j][0]+", "+triples[j][1]+","+triples[j][2]+")");
				if (candidateMatch) 
					if (tripleCountNull)
						filledTriples.put(triples[j], 1);
					else if (tripleCount == 1) 
						filledTriples.put(triples[j], 2);
					else
						break;
//					System.out.println("\tCandidate "+item+" passed in triple ("+triples[j][0]+", "+triples[j][1]+","+triples[j][2]+")");													
			}
		}
//		mount cl
		ArrayList<Integer> oldCL = super.CL; 
		super.CL = new ArrayList<Integer>();
		for (int i = 0; i < oldCL.size(); i++) {
			Boolean candidateIn = true;
			Integer item = oldCL.get(i);
			for (Integer[] triple : filledTriples.keySet()) {
				Integer tripleCount = filledTriples.get(triple);
				if (tripleCount != null && tripleCount == 2 && (triple[0].equals(item) || triple[1].equals(item) || triple[2].equals(item))) {
//					System.out.println("\tCandidate "+item+" break in triple ("+triple[0]+", "+triple[1]+","+triple[2]+") that already has "+tripleCount);
					candidateIn = false;
					break;
				}
			}
			if (candidateIn)
				super.CL.add(item);
		}
//		System.out.println(super.CL);
		
		
	}

	/**
	 * {@inheritDoc}
	 * 
	 * This createEmptySol instantiates an empty solution and it attributes a
	 * zero cost, since it is known that a QBF solution with all variables set
	 * to zero has also zero cost.
	 */
	@Override
	public Solution<Integer> createEmptySol() {
		Solution<Integer> sol = new Solution<Integer>();
		sol.cost = 0.0;
		return sol;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * The local search operator developed for the QBF objective function is
	 * composed by the neighborhood moves Insertion, Removal and 2-Exchange.
	 */
	@Override
	public Solution<Integer> localSearch() {

		Double minDeltaCost;
		Integer bestCandIn = null, bestCandOut = null;

		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			updateCL();
				
			// Evaluate insertions
			for (Integer candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;
				}
			}
			// Evaluate removals
			for (Integer candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = null;
					bestCandOut = candOut;
				}
			}
			// Evaluate exchanges
			for (Integer candIn : CL) {
				for (Integer candOut : incumbentSol) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
					if (deltaCost < minDeltaCost) {
						minDeltaCost = deltaCost;
						bestCandIn = candIn;
						bestCandOut = candOut;
					}
				}
			}
			// Implement the best move, if it reduces the solution cost.
			if (minDeltaCost < -Double.MIN_VALUE) {
				if (bestCandOut != null) {
					incumbentSol.remove(bestCandOut);
					CL.add(bestCandOut);
				}
				if (bestCandIn != null) {
					incumbentSol.add(bestCandIn);
					CL.remove(bestCandIn);
				}
				ObjFunction.evaluate(incumbentSol);
			}
		} while (minDeltaCost < -Double.MIN_VALUE);

		return null;
	}

	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {
		int minNumberSolutionsPerAlpha = 5;
		double[] alphas = new double[10];
		for (int i = 0; i < alphas.length; i++) {
			alphas[i] = ((double) 1/alphas.length)*(i + 1);
		}
		
		
		long startTime = System.currentTimeMillis();		
		Reactive_GRASP_QBFPT grasp = new Reactive_GRASP_QBFPT(alphas, 100000, minNumberSolutionsPerAlpha, "instances/qbf020");
		Solution<Integer> bestSol = grasp.solve();
		
		
//		QBFPT_Inverse QBFPT_Inverse = (QBFPT_Inverse) grasp.ObjFunction;
//		Integer[][] triples = QBFPT_Inverse.triples;
//		for (int i = 0; i < triples.length; i++) {
//			for (int j = 0; j < triples[i].length; j++) {
//				System.out.print(triples[i][j]+", ");
//			}
//			System.out.println();
//		}
		
		
		
		
		System.out.println("maxVal = " + bestSol);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");				
		
		
				
//		Map<Integer[], Integer> filledTriples = new HashMap<Integer[], Integer> ();			
//		for (int i = 0; i < grasp.incumbentSol.size(); i++) {
//			Integer item = grasp.incumbentSol.get(i);
//			for (int j = 0; j < triples.length; j++) {				
//				Integer tripleCount = filledTriples.get(triples[j]);						
//				Boolean tripleCountNull = tripleCount == null,
//						candidateMatch = triples[j][0].equals(item) || triples[j][1].equals(item) || triples[j][2].equals(item);
////				System.out.println("\tTriple ("+triples[j][0]+", "+triples[j][1]+","+triples[j][2]+")");
//				if (candidateMatch) 
//					if (tripleCountNull)
//						filledTriples.put(triples[j], 1);
//					else if (tripleCount == 1) 
//						filledTriples.put(triples[j], 2);
//					else
//						break;
////					System.out.println("\tCandidate "+item+" passed in triple ("+triples[j][0]+", "+triples[j][1]+","+triples[j][2]+")");													
//			}
//		}
//		
//		for (Integer[] triple : filledTriples.keySet()) {
//			Integer tripleCount = filledTriples.get(triple);
//			if (tripleCount != null && tripleCount == 2 && (triple[0].equals(item) || triple[1].equals(item) || triple[2].equals(item))) {
//				System.out.println("\tCandidate "+item+" break in triple ("+triple[0]+", "+triple[1]+","+triple[2]+") that already has "+tripleCount);
//			}
//		}
	}

}
