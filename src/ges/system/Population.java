package ges.system;

import java.util.ArrayList;
import ges.utils.NumberUtils;

public class Population {
	
	private int geneticMin, geneticMax, mutationChance, outOf, geneCount, targetPopulationSize;
	private ArrayList<Specimen> geneticSpecimens = new ArrayList<Specimen>();
	
	public Population(int geneticMin, int geneticMax, int mutationChance, int outOf, int geneCount, int targetPopulationSize) {
		this.geneticMin = geneticMin;
		this.geneticMax = geneticMax;
		this.mutationChance = mutationChance;
		this.outOf = outOf;
		this.geneCount = geneCount;
		this.targetPopulationSize = targetPopulationSize;
	}
	
	public Specimen generateGeneticSpecimen(int length) {
		int[] genes = new int[length];
		for(int i = 0; i < length; i++) {
			genes[i] = NumberUtils.integerInRange(geneticMin, geneticMax);
		}
		return new Specimen(genes);
	}
	
	public void initializePopulation() {
		geneticSpecimens.clear();
		for(int i = 0; i < targetPopulationSize; i++) {
			geneticSpecimens.add(generateGeneticSpecimen(geneCount));
		}
	}
	
	public int getTargetPopulationSize() {
		return targetPopulationSize;
	}
	
	public int getPopulationSize() {
		return geneticSpecimens.size();
	}
	
	public int getGeneCount() {
		return geneCount;
	}
	
	public int getGeneticMin() {
		return geneticMin;
	}
	
	public int getGeneticMax() {
		return geneticMax;
	}
	
	public int getMutationChance() {
		return mutationChance;
	}
	
	public int getOutOf() {
		return outOf;
	}
	
	public ArrayList<Specimen> getGeneticSpecimens(){
		return geneticSpecimens;
	}
	
	public void setGeneticSpecimens(ArrayList<Specimen> geneticSpecimens) {
		this.geneticSpecimens = geneticSpecimens;
	}
	
}
