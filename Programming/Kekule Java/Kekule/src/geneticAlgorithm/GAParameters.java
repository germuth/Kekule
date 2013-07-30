package geneticAlgorithm;

public class GAParameters {
	/**
	 * The Amount of Graphs which are in the population each Iteration of the 
	 * Genetic Algorithm. Higher is normally more accurate, however, takes
	 * longer
	 */
	private static int populationSize = 1000;
	/**
	 * The Amount of iterations the genetic algorithm will perform until stopping.
	 * More Iterations move the entire population closer to the optimal result, but
	 * again takes longer.
	 */
	private static int iterations = 100;
	/**
	 * The amount of graphs which are taken from the previous population
	 * to the next population based only on health. Basically, this means
	 * the top 90 of each generation are passed to the next generation. 
	 */
	public static int eliteNumber = 140;
	/**
	 * The amount of graphs which are taken from the previous population
	 * randomly to the next population. In this case, 90 are taken from
	 * the best of last generation, and 10 are randomly taken for genetic
	 * diversity.
	 */
	public static int randomNumber = 60;
	/**
	 * The amount of graphs generated from a population by simple mutation. 
	 * In our case, 100 graphs are initially taken in, and these graphs 
	 * are mutated to give 200 additional graphs.
	 */
	private static int mutantNumber = 400;
	/**
	 * The amount of graphs generated from crossover. In our case, out of the 100
	 * starting elites, two are chosen at random 200 to crossover and create a new
	 * graph. The start elite + mutantNumber + crossOverNumber = 500, the 
	 * entire population size.
	 */
	private static int crossoverNumber = 400; 
	/**
	 * The amount of graphs with maximum fitness that are required before the
	 * genetic algorithm will terminate. If this number is never reached, it
	 * will terminate when the number of iterations is reached
	 */
	private static int minimumGraphsRequired = 3;
	/**
	 * 
	 */
	private static boolean calculateFitnessEvery2nd = false;
	/**
	 * 
	 */
	private static int numNodesFrom = 0;
	/**
	 * 
	 */
	private static int numNodesTo = 20;
	/**
	 * 
	 */
	private static int numEdgesFrom = 0;
	/**
	 * 
	 */
	private static int numEdgesTo = 25;
	/**
	 * 
	 */
	private static int fitnessThreshold = 0;
	/**
	 * 
	 */
	private static double addNodeChance = 0.20;
	/**
	 * 
	 */
	private static double removeNodeChance = 0.20;
	/**
	 * 
	 */
	private static double addEdgeChance = 0.20;
	/**
	 * 
	 */
	private static double removeEdgeChance = 0.20;
	/**
	 * 
	 */
	private static double extendPortsChance = 0.05;
	
	public static int getPopulationSize() {
		return populationSize;
	}
	public static void setPopulationSize(int populationSize) {
		GAParameters.populationSize = populationSize;
	}
	public static int getIterations() {
		return iterations;
	}
	public static void setIterations(int iterations) {
		GAParameters.iterations = iterations;
	}
	public static int getEliteNumber() {
		return eliteNumber;
	}
	public static void setEliteNumber(int eliteNumber) {
		GAParameters.eliteNumber = eliteNumber;
	}
	public static int getRandomNumber() {
		return randomNumber;
	}
	public static void setRandomNumber(int randomNumber) {
		GAParameters.randomNumber = randomNumber;
	}
	public static int getMutantNumber() {
		return mutantNumber;
	}
	public static void setMutantNumber(int mutantNumber) {
		GAParameters.mutantNumber = mutantNumber;
	}
	public static int getCrossoverNumber() {
		return crossoverNumber;
	}
	public static void setCrossoverNumber(int crossoverNumber) {
		GAParameters.crossoverNumber = crossoverNumber;
	}
	public static int getMinimumGraphsRequired() {
		return minimumGraphsRequired;
	}
	public static void setMinimumGraphsRequired(int minimumGraphsRequired) {
		GAParameters.minimumGraphsRequired = minimumGraphsRequired;
	}
	public static boolean isCalculateFitnessEvery2nd() {
		return calculateFitnessEvery2nd;
	}
	public static void setCalculateFitnessEvery2nd(boolean calculateFitnessEvery2nd) {
		GAParameters.calculateFitnessEvery2nd = calculateFitnessEvery2nd;
	}
	public static int getNumNodesFrom() {
		return numNodesFrom;
	}
	public static void setNumNodesFrom(int numNodesFrom) {
		GAParameters.numNodesFrom = numNodesFrom;
	}
	public static int getNumNodesTo() {
		return numNodesTo;
	}
	public static void setNumNodesTo(int numNodesTo) {
		GAParameters.numNodesTo = numNodesTo;
	}
	public static int getNumEdgesFrom() {
		return numEdgesFrom;
	}
	public static void setNumEdgesFrom(int numEdgesFrom) {
		GAParameters.numEdgesFrom = numEdgesFrom;
	}
	public static int getNumEdgesTo() {
		return numEdgesTo;
	}
	public static void setNumEdgesTo(int numEdgesTo) {
		GAParameters.numEdgesTo = numEdgesTo;
	}
	public static int getFitnessThreshold() {
		return fitnessThreshold;
	}
	public static void setFitnessThreshold(int fitnessThreshold) {
		GAParameters.fitnessThreshold = fitnessThreshold;
	}
	public static double getAddNodeChance() {
		return addNodeChance;
	}
	public static void setAddNodeChance(double addNodeChance) {
		GAParameters.addNodeChance = addNodeChance;
	}
	public static double getRemoveNodeChance() {
		return removeNodeChance;
	}
	public static void setRemoveNodeChance(double removeNodeChance) {
		GAParameters.removeNodeChance = removeNodeChance;
	}
	public static double getAddEdgeChance() {
		return addEdgeChance;
	}
	public static void setAddEdgeChance(double addEdgeChance) {
		GAParameters.addEdgeChance = addEdgeChance;
	}
	public static double getRemoveEdgeChance() {
		return removeEdgeChance;
	}
	public static void setRemoveEdgeChance(double removeEdgeChance) {
		GAParameters.removeEdgeChance = removeEdgeChance;
	}
	public static double getExtendPortsChance() {
		return extendPortsChance;
	}
	public static void setExtendPortsChance(double extendPortsChance) {
		GAParameters.extendPortsChance = extendPortsChance;
	}
}
