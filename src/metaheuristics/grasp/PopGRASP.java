/**
 * 
 */
package metaheuristics.grasp;

import java.util.ArrayList;
import java.util.Random;

import problems.Evaluator;
import solutions.Solution;

/**
 * Abstract class for metaheuristic GRASP (Greedy Randomized Adaptive Search
 * Procedure). It consider a minimization problem.
 * 
 * @author ccavellucci, fusberti
 * @param <E>
 *            Generic type of the element which composes the solution.
 */
public abstract class PopGRASP<E> {

	/**
	 * flag that indicates whether the code should print more information on
	 * screen
	 */
	public static boolean verbose = true;

	/**
	 * a random number generator
	 */
	static Random rng = new Random(0);

	/**
	 * the objective function being optimized
	 */
	protected Evaluator<E> ObjFunction;

	/**
	 * the GRASP greediness-randomness parameter
	 */
	protected Double alpha;

	/**
	 * the best solution cost
	 */
	protected Double bestCost;

	/**
	 * the incumbent solution cost
	 */
	protected Double incumbentCost;

	/**
	 * the best solution
	 */
	protected Solution<E> bestSol;

	/**
	 * the incumbent solution
	 */
	protected Solution<E> incumbentSol;

	/**
	 * the number of iterations the GRASP main loop executes.
	 */
	protected Integer iterations;

	/**
	 * the Candidate List of elements to enter the solution.
	 */
	protected ArrayList<E> CL;

	/**
	 * the Restricted Candidate List of elements to enter the solution.
	 */
	protected ArrayList<E> RCL;

	/**
	 * Creates the Candidate List, which is an ArrayList of candidate elements
	 * that can enter a solution.
	 * 
	 * @return The Candidate List.
	 */
	public abstract ArrayList<E> makeCL();

	/**
	 * Creates the Restricted Candidate List, which is an ArrayList of the best
	 * candidate elements that can enter a solution. The best candidates are
	 * defined through a quality threshold, delimited by the GRASP
	 * {@link #alpha} greedyness-randomness parameter.
	 * 
	 * @return The Restricted Candidate List.
	 */
	public abstract ArrayList<E> makeRCL();

	/**
	 * Updates the Candidate List according to the incumbent solution
	 * {@link #incumbentSol}. In other words, this method is responsible for
	 * updating which elements are still viable to take part into the solution.
	 */
	public abstract void updateCL();

	/**
	 * Creates a new solution which is empty, i.e., does not contain any
	 * element.
	 * 
	 * @return An empty solution.
	 */
	public abstract Solution<E> createEmptySol();

	/**
	 * The GRASP local search phase is responsible for repeatedly applying a
	 * neighborhood operation while the solution is getting improved, i.e.,
	 * until a local optimum is attained.
	 * 
	 * @return An local optimum solution.
	 */
	public abstract Solution<E> localSearch();

	/**
	 * Constructor for the AbstractGRASP class.
	 * 
	 * @param objFunction
	 *            The objective function being minimized.
	 * @param alpha
	 *            The GRASP greediness-randomness parameter (within the range
	 *            [0,1])
	 * @param iterations
	 *            The number of iterations which the GRASP will be executed.
	 */
	public PopGRASP(Evaluator<E> objFunction, Double alpha, Integer iterations) {
		this.ObjFunction = objFunction;
		this.alpha = alpha;
		this.iterations = iterations;
	}
	
	/**
	 * The GRASP constructive heuristic, which is responsible for building a
	 * feasible solution by selecting in a greedy-random fashion, candidate
	 * elements to enter the solution.
	 * 
	 * @return A feasible solution to the problem being minimized.
	 */
	public Solution<E> constructiveHeuristic() {

		System.out.println("Iniciate Construction");
		CL = makeCL();
		RCL = makeRCL();
		incumbentSol = createEmptySol();
		incumbentCost = Double.POSITIVE_INFINITY;
		int count = 0;

		/* Main loop, which repeats until the stopping criteria is reached. */
		while (!constructiveStopCriteria()) {

			double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
			incumbentCost = ObjFunction.evaluate(incumbentSol);
			updateCL();

			/*
			 * Explore all candidate elements to enter the solution, saving the
			 * highest and lowest cost variation achieved by the candidates.
			 */
			for (E c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
				if (deltaCost < minCost)
					minCost = deltaCost;
				if (deltaCost > maxCost)
					maxCost = deltaCost;
			}

			/*
			 * Among all candidates, insert into the RCL those with the highest
			 * performance using parameter alpha as threshold.
			 */
			for (E c : CL) {
				Double deltaCost = ObjFunction.evaluateInsertionCost(c, incumbentSol);
				if (deltaCost <= minCost + alpha * (maxCost - minCost)) {
					RCL.add(c);
					count++;
				}
			}

			//Aqui
			/* Choose a candidate randomly from the RCL */
			int rndIndex = rng.nextInt(RCL.size());
			System.out.println("RCL: "+RCL.toString());
			E inCand = RCL.get(rndIndex);
			CL.remove(inCand);
			incumbentSol.add(inCand);
			ObjFunction.evaluate(incumbentSol);
			System.out.println("Solution: "+incumbentSol);
			if(count > 4) {
				System.out.println("Pré: "+incumbentSol);
				consc_localsearch();
				count = 0;
				System.out.println("pos: "+incumbentSol);
			}
			
			
			
			RCL.clear();
			System.out.println("RCL Clear");

		}
		System.out.println("Incumbent Solution: "+incumbentSol);
		return incumbentSol;
	}

	private Solution<E> consc_localsearch() {
		Double minDeltaCost;
		E bestCandIn = null, bestCandOut = null;
		System.out.println("iniciate consc_localsearch\n________________________________________________________________________________");
		do {
			minDeltaCost = Double.POSITIVE_INFINITY;
			updateCL();
				
			// Evaluate insertions
			/*
			for (E candIn : CL) {
				double deltaCost = ObjFunction.evaluateInsertionCost(candIn, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = candIn;
					bestCandOut = null;
				}
			}*/
			// Evaluate removals
			for (E candOut : incumbentSol) {
				double deltaCost = ObjFunction.evaluateRemovalCost(candOut, incumbentSol);
				if (deltaCost < minDeltaCost) {
					minDeltaCost = deltaCost;
					bestCandIn = null;
					bestCandOut = candOut;
				}
			}
			// Evaluate exchanges
			/*
			for (E candIn : CL) {
				for (E candOut : incumbentSol) {
					double deltaCost = ObjFunction.evaluateExchangeCost(candIn, candOut, incumbentSol);
					if (deltaCost < minDeltaCost) {
						minDeltaCost = deltaCost;
						bestCandIn = candIn;
						bestCandOut = candOut;
					}
				}
			}*/
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
		
		System.out.println("end concs_localsearch\n________________________________________________________________________________");
		return null;
	}
	
	
	
	/**
	 * The GRASP mainframe. It consists of a loop, in which each iteration goes
	 * through the constructive heuristic and local search. The best solution is
	 * returned as result.
	 * 
	 * @return The best feasible solution obtained throughout all iterations.
	 */
	public Solution<E> solve() {
		bestSol = createEmptySol();
		long startTime = System.currentTimeMillis();
		long maxDurationInMilliseconds = 30 * 60 * 1000;
		boolean intime = true;
		for (int i = 0; i < iterations && intime ; i++) {
		

			
			
				constructiveHeuristic();
				localSearch();
				if (bestSol.cost > incumbentSol.cost) {
					bestSol = new Solution<E>(incumbentSol);
					//System.out.println("BestSol = "+bestSol);
					System.out.println("(Iter. " + i + ") BestSol = " + bestSol);
					if (verbose) {
						System.out.println("(Iter. " + i + ") BestSol = " + bestSol);
						
					}
				}
				
				if(System.currentTimeMillis() > startTime + maxDurationInMilliseconds) {
					intime = false;
				}
			
		}

		return bestSol;	
	}

	/**
	 * A standard stopping criteria for the constructive heuristic is to repeat
	 * until the incumbent solution improves by inserting a new candidate
	 * element.
	 * 
	 * @return true if the criteria is met.
	 */
	public Boolean constructiveStopCriteria() {
		return (incumbentCost > incumbentSol.cost) ? false : true;
	}

}
