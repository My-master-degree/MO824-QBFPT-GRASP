/**
 * 
 */
package metaheuristics.grasp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public abstract class ReactiveGRASP<E> {

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
	protected double[] alphas;
	
	protected double[] alphasSolutionsCostsSum;
	
	protected int[] alphasSolutionsNumber;
	
	protected double[] alphasProbabilities;	

	protected int minNuberOfSolutionsPerAlpha;
	
	protected Boolean readyToBalanceAlphasProbabilities;
	
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
	 * {@link #alphas} greedyness-randomness parameter.
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
	public ReactiveGRASP(Evaluator<E> objFunction, double[] alphas, Integer iterations, int minNuberOfSolutionsPerAlpha) {
		this.ObjFunction = objFunction;
		this.alphas = alphas;
		this.alphasSolutionsCostsSum = new double[alphas.length];
		this.alphasSolutionsNumber = new int[alphas.length];
		this.alphasProbabilities = new double[alphas.length];
		for (int i = 0; i < alphasProbabilities.length; i++) {
			this.alphasProbabilities[i] = (double) 1/alphasProbabilities.length;
		}
		this.iterations = iterations;		
		this.minNuberOfSolutionsPerAlpha = minNuberOfSolutionsPerAlpha;
		this.readyToBalanceAlphasProbabilities = false;
	}
	
//	protected Double getAlphaAverageCost(i) {
//		Double totalCost = 0;
//		
//	}
	
	protected void updateAlphaProbability(int i) {
//		Double qs = 0d;
//		for (int j = 0; j < this.alphas.length; j++) {
//			qs += this.alphasAverageCost[i]
//		}
//		this.alphasProbabilities[i] = (this.incumbentCost/this.alphasAverageCost[i])/
	}
	
	public void balanceAlphasProbabilities() {		
//		check if each alpha has the minNuberOfSolutionsPerAlpha
		boolean ready = true;
		if (!this.readyToBalanceAlphasProbabilities) {								
			for (int i = 0; i < this.alphasSolutionsNumber.length; i++) {
				if (this.alphasSolutionsNumber[i] < this.minNuberOfSolutionsPerAlpha) {
					ready = false;
					break;
				}
			}
		}
		this.readyToBalanceAlphasProbabilities = ready;
//		get value
		if (this.readyToBalanceAlphasProbabilities) {
//			System.out.println("now balance!!!!");
			double[] qs = new double[alphas.length];
			double dSum = 0;
//			calculate q	
//			System.out.println("Quoeficients values");
			for (int i = 0; i < alphas.length; i++) {
				if (this.incumbentCost == 0) {
					qs[i] = (1 + Math.abs(this.incumbentCost))/(1 + Math.abs(this.alphasSolutionsCostsSum[i]/this.alphasSolutionsNumber[i]));
				}else {
					qs[i] = Math.abs(this.incumbentCost)/Math.abs(this.alphasSolutionsCostsSum[i]/this.alphasSolutionsNumber[i]);
				}					
				dSum += qs[i];
//				System.out.print(qs[i]+", ");
			}			
//			System.out.println();
			for (int i = 0; i < alphas.length; i++) {
				this.alphasProbabilities[i] = qs[i]/dSum;
			}			
		}
	}
	
	public Integer getAlphaIndex(double prob) {
		double count = 0d;
		
		
		
//		System.out.println("Trying to find "+prob);
//		System.out.println("Probabilities values:");
//		for (int i = 0; i < alphasProbabilities.length; i++) {
//			System.out.print(alphasProbabilities[i] + ",");
//		}
//		System.out.println();
		
		
		
		
		for (int i = 0; i < alphas.length; i++) {
			count += alphasProbabilities[i];
			if (prob <= count) {
				return i;				
			}			
		}
		return null;
	}
	
	
	
	/**
	 * The GRASP constructive heuristic, which is responsible for building a
	 * feasible solution by selecting in a greedy-random fashion, candidate
	 * elements to enter the solution.
	 * 
	 * @return A feasible solution to the problem being minimized.
	 */
	public Solution<E> constructiveHeuristic() {

		CL = makeCL();
		RCL = makeRCL();
		incumbentSol = createEmptySol();
		incumbentCost = Double.POSITIVE_INFINITY;		
		Integer alphaIndex = this.getAlphaIndex(this.rng.nextDouble());
		Double alpha = this.alphas[alphaIndex];		
		/* Main loop, which repeats until the stopping criteria is reached. */
		while (!constructiveStopCriteria()) {

			updateCL();
			if (CL.size() == 0) {
				break;
			}
			double maxCost = Double.NEGATIVE_INFINITY, minCost = Double.POSITIVE_INFINITY;
			incumbentCost = ObjFunction.evaluate(incumbentSol);			

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
				}
			}
//			System.out.println("Incumbet cost is "+incumbentCost);
//			System.out.println("RCL itens between "+minCost+" and "+alpha * (maxCost - minCost));
			/* Choose a candidate randomly from the RCL */
			int rndIndex = rng.nextInt(RCL.size());
			E inCand = RCL.get(rndIndex);
			CL.remove(inCand);
			incumbentSol.add(inCand);
			ObjFunction.evaluate(incumbentSol);
			RCL.clear();
		}
		this.alphasSolutionsCostsSum[alphaIndex] += incumbentCost;
		this.alphasSolutionsNumber[alphaIndex]++;
		this.balanceAlphasProbabilities();
		return incumbentSol;
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
		for (int i = 0; i < iterations; i++) {
			constructiveHeuristic();
			localSearch();
			if (bestSol.cost > incumbentSol.cost) {
				bestSol = new Solution<E>(incumbentSol);
				if (verbose)
					System.out.println("(Iter. " + i + ") BestSol = " + bestSol);
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
