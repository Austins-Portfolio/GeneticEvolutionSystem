package ges.system;

public class GeneticEvolutionEngine {

	private Population population;
	private int populationFitness;
	private FitnessFunction ff;
	private SelectionFunction sf;
	private PopulateFunction pf;
	private MutationFunction mf;
	
	private int generationCount = 0;
	
	public GeneticEvolutionEngine(FitnessFunction ff, SelectionFunction sf, PopulateFunction pf, MutationFunction mf, int geneticMin, int geneticMax, int mutationChance, int outOf, int geneCount, int targetPopulationSize) {
		this.ff = ff;
		this.sf = sf;
		this.pf = pf;
		this.mf = mf;
		population = new Population(geneticMin, geneticMax, mutationChance, outOf, geneCount, targetPopulationSize);
		population.initializePopulation();
	}
	
	public void step() {
		fitness(ff);
		selection(sf);
		populate(pf);
		mutate(mf);
		
		generationCount++;
	}
	
	public void fitness(FitnessFunction ff) {
		populationFitness = ff.specimensFitness(population);
	}
	
	public void selection(SelectionFunction sf) {
		sf.selectSpecimens(population);
	}
	
	public void populate(PopulateFunction pf) {
		pf.populateSpecimens(population);
	}
	
	public void mutate(MutationFunction mf) {
		mf.mutateSpecimens(population);
	}
	
	public int getGenerationCount() {
		return generationCount;
	}
	
	public int getPopulationSize() {
		return population.getPopulationSize();
	}
	
	public int getTargetPopulationSize() {
		return population.getTargetPopulationSize();
	}
	
	public int getPopulationFitness() {
		return populationFitness;
	}
	
	public Population getPopulation() {
		return population;
	}
	
}
