package problems.qbf.solvers;

import java.io.IOException;
import java.util.ArrayList;

import metaheuristics.grasp.PopGRASP;
import problems.qbf.QBFPT_Inverse;
import solutions.Solution;

public class Pop_GRASP_QBFPT extends PopGRASP<Integer> {


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
	public Pop_GRASP_QBFPT(Double alpha, Integer iterations, String filename) throws IOException {
		super(new QBFPT_Inverse(filename), alpha, iterations);
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

		// do nothing since all elements off the solution are viable candidates.

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
//		best improvement
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
//		first improvement
		// Evaluate insertions
		do {
			updateCL();
			boolean used = false;
			for (Integer candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
				if (deltaCost < incumbentSol.cost) {
					incumbentSol.add(candIn);
					CL.remove(candIn);
					ObjFunction.evaluate(incumbentSol);
					used = true;
					break;
				}
			}		
			if (!used)
				break;
		} while (true);
		// Evaluate removals
		do {
			updateCL();
			boolean used = false;			
			for (Integer candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
				if (deltaCost < incumbentSol.cost) {
					incumbentSol.remove(candOut);
					CL.add(candOut);
					ObjFunction.evaluate(incumbentSol);
					used = true;
				}
			}
			if (!used)
				break;
		} while (true);
		// Evaluate exchanges
		do {
			updateCL();
			boolean used = false;
			for (Integer candIn : CL) {
				boolean used1 = false;
				for (Integer candOut : incumbentSol) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
					if (deltaCost < minDeltaCost) {
						incumbentSol.remove(candOut);
						CL.add(candOut);
						incumbentSol.add(candIn);
						CL.remove(candIn);
						ObjFunction.evaluate(incumbentSol);
						used = true;
						used1 = true;
						break;
					}
				}
				if (!used1)
					break;			
			}
			if (!used)
				break;			
		} while (true);
		
		return null;
	}

	/**
	 * A main method used for testing the GRASP metaheuristic.
	 * 
	 */
	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		Pop_GRASP_QBF grasp = new Pop_GRASP_QBF(0.05, 1000, "instances/qbf080");
		Solution<Integer> bestSol = grasp.solve();
		System.out.println("maxVal = " + bestSol);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Time = "+(double)totalTime/(double)1000+" seg");

	}



}
