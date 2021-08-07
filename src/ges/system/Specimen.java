package ges.system;

public class Specimen {

	private int[] genes = null;
	private int fitnessScore;
	
	public Specimen(int[] genes) {
		this.genes = genes;
	}
	
	public Specimen(int[] genes, int fitnessScore) {
		this.genes = genes;
		this.fitnessScore = fitnessScore;
	}
	
	public int[] getGenes() {
		return genes;
	}

	public void setGenes(int[] genes) {
		this.genes = genes;
	}

	public int getFitnessScore() {
		return fitnessScore;
	}

	public void setFitnessScore(int fitnessScore) {
		this.fitnessScore = fitnessScore;
	}
	
}
