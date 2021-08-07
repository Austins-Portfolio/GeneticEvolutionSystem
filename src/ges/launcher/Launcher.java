package ges.launcher;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import ges.system.FitnessFunction;
import ges.system.GeneticEvolutionEngine;
import ges.system.MutationFunction;
import ges.system.PopulateFunction;
import ges.system.Population;
import ges.system.SelectionFunction;
import ges.system.Specimen;
import ges.utils.ArrayUtils;
import ges.utils.NumberUtils;

public class Launcher {

	public static void main(String[] args) {
//		sequenceDemo(4*3, 0, 255);
//		imageDemo();
//		imageDemo2();
//		imageDemo3();
		imageDemo4();
	}
	
	public static void imageDemo() {

		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(null);
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(jfc.getSelectedFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//int[] source = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		int[] source = new int[image.getWidth()*image.getHeight()*3];
		int loc = 0;
		for(int i = 0; i < image.getWidth()*image.getHeight(); i++) {
			int x = i % image.getWidth();
			int y = i / image.getHeight(); 
			Color c = new Color(image.getRGB(x, y));
			source[loc] = c.getRed();
			source[loc+1] = c.getGreen();
			source[loc+2] = c.getBlue();
			loc+=3;
		}
		
		FitnessFunction ff = new FitnessFunction() {

			@Override
			public int specimenFitness(Specimen specimen) {
				
				int fitness = 0;
				
				for(int i = 0; i < source.length; i++) {
					fitness+= Math.abs(source[i]-specimen.getGenes()[i]);
				}
				return fitness;
			}

			@Override
			public int specimensFitness(Population population) {
				
				int populationFitnessScore = 0;
				
				if(source.length == population.getGeneticSpecimens().get(0).getGenes().length) {
					
					for(int i = 0; i < population.getGeneticSpecimens().size(); i++) {
						population.getGeneticSpecimens().get(i).setFitnessScore(specimenFitness(population.getGeneticSpecimens().get(i)));
						populationFitnessScore += population.getGeneticSpecimens().get(i).getFitnessScore();
					}
				}
				
				return populationFitnessScore;
			}
			
		};
		
		int selectionSize = 10;
		
		SelectionFunction sf = new SelectionFunction() {

			@Override
			public void selectSpecimens(Population population) {
				ArrayList<Specimen> specimens = population.getGeneticSpecimens();
				for(int i = 0; i < specimens.size(); i++) {
					for(int k = i; k < specimens.size(); k++) {
						if(specimens.get(k).getFitnessScore() >specimens.get(i).getFitnessScore()) {
							Collections.swap(specimens, k, i);
						}
					}
				}
				
				Collections.reverse(specimens);
				
				ArrayList<Specimen> selectedSpecimens = new ArrayList<Specimen>();
				
				for(int i = 0; i < selectionSize; i++) {
					selectedSpecimens.add(specimens.get(i));
				} 
				
				population.setGeneticSpecimens(selectedSpecimens);
			}
			
		};
		
		PopulateFunction pf = new PopulateFunction() {
			
			public Specimen crossOver(Population population) {
				int r1 = NumberUtils.integerInRange(0, selectionSize-1);
				int r2 = r1;
				while(r2 == r1) {
					r2 = NumberUtils.integerInRange(0, selectionSize-1);
				}
				
				Specimen s1 = population.getGeneticSpecimens().get(r1);
				Specimen s2 = population.getGeneticSpecimens().get(r2);
				
				int[] genes = new int[population.getGeneCount()];
				
				for(int i = 0; i < population.getGeneCount();i++) {
					if(i%2==0) {
						genes[i] = s1.getGenes()[i];
					}else {
						genes[i] = s2.getGenes()[i];
					}
				}
				
				return new Specimen(genes);
			}

			@Override
			public void populateSpecimens(Population population) {
				ArrayList<Specimen> newSpecimens = population.getGeneticSpecimens();
				for(int i = selectionSize; i < population.getTargetPopulationSize(); i++) {
					newSpecimens.add(crossOver(population));
				}
				population.setGeneticSpecimens(newSpecimens);
			}
			
		};
		
		MutationFunction mf = new MutationFunction() {

			@Override
			public void mutateSpecimens(Population population) {
				for(Specimen s: population.getGeneticSpecimens()) {
					int r = NumberUtils.integerInRange(0, population.getOutOf());
					if(r <= population.getMutationChance()) {
						int[] genes = s.getGenes();
						for(int i = 0; i < genes.length; i++) {
							r = NumberUtils.integerInRange(0, population.getOutOf());
							if(r <= population.getMutationChance()) {
								genes[i] = NumberUtils.integerInRange(population.getGeneticMin(), population.getGeneticMax());
							}
						}
						s.setGenes(genes);
					}
				}
			}
			
		};
		
		GeneticEvolutionEngine gee = new GeneticEvolutionEngine(ff, sf, pf, mf, 0, 255, 100, 1000, source.length, 1000);
		
		int scale = 10;
		
		JFrame frame = new JFrame();
		frame.setSize(((image.getWidth()*scale)*2)+20, (image.getHeight()*scale)+40);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		boolean step = false;
		while(true) {
			if(!step) {
				gee.step();
				//step=true;
			}
			
			frame.setTitle("Genetic Evolution System : Generation- " + gee.getGenerationCount() + " Fitness- " + gee.getPopulationFitness());
			Graphics2D g2d = (Graphics2D) frame.getGraphics();
			
			g2d.drawImage(image, 0, frame.getHeight()-(image.getHeight()*scale), image.getWidth()*scale, image.getHeight()*scale, null);
			
			int[] bestSolution = gee.getPopulation().getGeneticSpecimens().get(0).getGenes();
			BufferedImage solution = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			loc = 0;
			for(int i = 0; i < solution.getWidth()*solution.getHeight(); i++) {
				int x = i % solution.getWidth();
				int y = i / solution.getHeight(); 
				Color c = new Color(bestSolution[loc],bestSolution[loc+1],bestSolution[loc+2]);
				solution.setRGB(x, y, c.getRGB());
				loc+=3;
			}
			
			g2d.drawImage(solution, frame.getWidth()-(solution.getWidth()*scale), frame.getHeight()-(solution.getHeight()*scale), solution.getWidth()*scale, solution.getHeight()*scale, null);
		}
		
	}
	
	public static void imageDemo2() {

		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(null);
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(jfc.getSelectedFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//int[] source = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		int[] source = new int[image.getWidth()*image.getHeight()*3];
		int loc = 0;
		for(int i = 0; i < image.getWidth()*image.getHeight(); i++) {
			int x = i % image.getWidth();
			int y = i / image.getHeight(); 
			Color c = new Color(image.getRGB(x, y));
			source[loc] = c.getRed();
			source[loc+1] = c.getGreen();
			source[loc+2] = c.getBlue();
			loc+=3;
		}
		
		FitnessFunction ff = new FitnessFunction() {

			@Override
			public int specimenFitness(Specimen specimen) {
				
				int fitness = 0;
				
				for(int i = 0; i < source.length; i++) {
					if(source[i]!=specimen.getGenes()[i]) {
						fitness++; 
					}
				}
				return fitness;
			}

			@Override
			public int specimensFitness(Population population) {
				
				int populationFitnessScore = 0;
				
				if(source.length == population.getGeneticSpecimens().get(0).getGenes().length) {
					
					for(int i = 0; i < population.getGeneticSpecimens().size(); i++) {
						population.getGeneticSpecimens().get(i).setFitnessScore(specimenFitness(population.getGeneticSpecimens().get(i)));
						populationFitnessScore += population.getGeneticSpecimens().get(i).getFitnessScore();
					}
				}
				
				return populationFitnessScore;
			}
			
		};
		
		int selectionSize = 10;
		
		SelectionFunction sf = new SelectionFunction() {

			@Override
			public void selectSpecimens(Population population) {
				ArrayList<Specimen> specimens = population.getGeneticSpecimens();
				for(int i = 0; i < specimens.size(); i++) {
					for(int k = i; k < specimens.size(); k++) {
						if(specimens.get(k).getFitnessScore() >specimens.get(i).getFitnessScore()) {
							Collections.swap(specimens, k, i);
						}
					}
				}
				
				Collections.reverse(specimens);
				
				ArrayList<Specimen> selectedSpecimens = new ArrayList<Specimen>();
				
				for(int i = 0; i < selectionSize; i++) {
					selectedSpecimens.add(specimens.get(i));
				} 
				
				population.setGeneticSpecimens(selectedSpecimens);
			}
			
		};
		
		PopulateFunction pf = new PopulateFunction() {
			
			public Specimen crossOver(Population population) {
				int r1 = NumberUtils.integerInRange(0, selectionSize-1);
				int r2 = r1;
				while(r2 == r1) {
					r2 = NumberUtils.integerInRange(0, selectionSize-1);
				}
				
				Specimen s1 = population.getGeneticSpecimens().get(r1);
				Specimen s2 = population.getGeneticSpecimens().get(r2);
				
				int[] genes = new int[population.getGeneCount()];
				
				for(int i = 0; i < population.getGeneCount();i++) {
					if(i%2==0) {
						genes[i] = s1.getGenes()[i];
					}else {
						genes[i] = s2.getGenes()[i];
					}
				}
				
				return new Specimen(genes);
			}

			@Override
			public void populateSpecimens(Population population) {
				ArrayList<Specimen> newSpecimens = population.getGeneticSpecimens();
				for(int i = selectionSize; i < population.getTargetPopulationSize(); i++) {
					newSpecimens.add(crossOver(population));
				}
				population.setGeneticSpecimens(newSpecimens);
			}
			
		};
		
		MutationFunction mf = new MutationFunction() {

			@Override
			public void mutateSpecimens(Population population) {
				for(Specimen s: population.getGeneticSpecimens()) {
					int r = NumberUtils.integerInRange(0, population.getOutOf());
					if(r <= population.getMutationChance()) {
						int[] genes = s.getGenes();
						for(int i = 0; i < genes.length; i++) {
							r = NumberUtils.integerInRange(0, population.getOutOf());
							if(r <= population.getMutationChance()) {
								genes[i] = NumberUtils.integerInRange(population.getGeneticMin(), population.getGeneticMax());
							}
						}
						s.setGenes(genes);
					}
				}
			}
			
		};
		
		GeneticEvolutionEngine gee = new GeneticEvolutionEngine(ff, sf, pf, mf, 0, 255, 100, 1000, source.length, 1000);
		
		int scale = 10;
		
		JFrame frame = new JFrame();
		frame.setSize(((image.getWidth()*scale)*2)+20, (image.getHeight()*scale)+40);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		boolean step = false;
		while(true) {
			if(!step) {
				gee.step();
				//step=true;
			}
			
			frame.setTitle("Genetic Evolution System : Generation- " + gee.getGenerationCount() + " Fitness- " + gee.getPopulation().getGeneticSpecimens().get(0).getFitnessScore());
			Graphics2D g2d = (Graphics2D) frame.getGraphics();
			
			g2d.drawImage(image, 0, frame.getHeight()-(image.getHeight()*scale), image.getWidth()*scale, image.getHeight()*scale, null);
			
			int[] bestSolution = gee.getPopulation().getGeneticSpecimens().get(0).getGenes();
			BufferedImage solution = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			loc = 0;
			for(int i = 0; i < solution.getWidth()*solution.getHeight(); i++) {
				int x = i % solution.getWidth();
				int y = i / solution.getHeight(); 
				Color c = new Color(bestSolution[loc],bestSolution[loc+1],bestSolution[loc+2]);
				solution.setRGB(x, y, c.getRGB());
				loc+=3;
			}
			
			g2d.drawImage(solution, frame.getWidth()-(solution.getWidth()*scale), frame.getHeight()-(solution.getHeight()*scale), solution.getWidth()*scale, solution.getHeight()*scale, null);
		}
		
	}
	
	public static void imageDemo3() {

		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(null);
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(jfc.getSelectedFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//int[] source = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		int[] source = new int[image.getWidth()*image.getHeight()*3];
		int loc = 0;
		for(int i = 0; i < image.getWidth()*image.getHeight(); i++) {
			int x = i % image.getWidth();
			int y = i / image.getHeight(); 
			Color c = new Color(image.getRGB(x, y));
			source[loc] = c.getRed();
			source[loc+1] = c.getGreen();
			source[loc+2] = c.getBlue();
			loc+=3;
		}
		
		FitnessFunction ff = new FitnessFunction() {

			@Override
			public int specimenFitness(Specimen specimen) {
				
				int fitness = 0;
				
				for(int i = 0; i < source.length; i++) {
					if(source[i]!=specimen.getGenes()[i]) {
						fitness++; 
					}
				}
				return fitness;
			}

			@Override
			public int specimensFitness(Population population) {
				
				int populationFitnessScore = 0;
				
				if(source.length == population.getGeneticSpecimens().get(0).getGenes().length) {
					
					for(int i = 0; i < population.getGeneticSpecimens().size(); i++) {
						population.getGeneticSpecimens().get(i).setFitnessScore(specimenFitness(population.getGeneticSpecimens().get(i)));
						populationFitnessScore += population.getGeneticSpecimens().get(i).getFitnessScore();
					}
				}
				
				return populationFitnessScore;
			}
			
		};
		
		int selectionSize = 10;
		
		SelectionFunction sf = new SelectionFunction() {

			@Override
			public void selectSpecimens(Population population) {
				ArrayList<Specimen> specimens = population.getGeneticSpecimens();
				for(int i = 0; i < specimens.size(); i++) {
					for(int k = i; k < specimens.size(); k++) {
						if(specimens.get(k).getFitnessScore() >specimens.get(i).getFitnessScore()) {
							Collections.swap(specimens, k, i);
						}
					}
				}
				
				Collections.reverse(specimens);
				
				ArrayList<Specimen> selectedSpecimens = new ArrayList<Specimen>();
				
				for(int i = 0; i < selectionSize; i++) {
					selectedSpecimens.add(specimens.get(i));
				} 
				
				population.setGeneticSpecimens(selectedSpecimens);
			}
			
		};
		
		PopulateFunction pf = new PopulateFunction() {
			
			public Specimen crossOver(Population population) {
				int r1 = NumberUtils.integerInRange(0, selectionSize-1);
				int r2 = r1;
				while(r2 == r1) {
					r2 = NumberUtils.integerInRange(0, selectionSize-1);
				}
				
				Specimen s1 = population.getGeneticSpecimens().get(r1);
				Specimen s2 = population.getGeneticSpecimens().get(r2);
				
				int[] genes = new int[population.getGeneCount()];
				
				for(int i = 0; i < population.getGeneCount();i++) {
					if(i%2==0) {
						genes[i] = s1.getGenes()[i];
					}else {
						genes[i] = s2.getGenes()[i];
					}
				}
				
				return new Specimen(genes);
			}

			@Override
			public void populateSpecimens(Population population) {
				ArrayList<Specimen> newSpecimens = population.getGeneticSpecimens();
				for(int i = selectionSize; i < population.getTargetPopulationSize(); i++) {
					newSpecimens.add(crossOver(population));
				}
				population.setGeneticSpecimens(newSpecimens);
			}
			
		};
		
		MutationFunction mf = new MutationFunction() {

			@Override
			public void mutateSpecimens(Population population) {
				for(Specimen s: population.getGeneticSpecimens()) {
					int r = NumberUtils.integerInRange(0, population.getOutOf());
					if(r <= population.getMutationChance()) {
						int[] genes = s.getGenes();
						int gr = NumberUtils.integerInRange(0, genes.length-1);
						genes[gr] = NumberUtils.integerInRange(population.getGeneticMin(), population.getGeneticMax());
						s.setGenes(genes);
					}
				}
			}
			
		};
		
		GeneticEvolutionEngine gee = new GeneticEvolutionEngine(ff, sf, pf, mf, 0, 255, 100, 1000, source.length, 1000);
		
		int scale = 10;
		
		JFrame frame = new JFrame();
		frame.setSize(((image.getWidth()*scale)*2)+20, (image.getHeight()*scale)+40);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		boolean step = false;
		while(true) {
			if(!step) {
				gee.step();
				//step=true;
			}
			
			frame.setTitle("Genetic Evolution System : Generation- " + gee.getGenerationCount() + " Fitness- " + gee.getPopulation().getGeneticSpecimens().get(0).getFitnessScore());
			Graphics2D g2d = (Graphics2D) frame.getGraphics();
			
			g2d.drawImage(image, 0, frame.getHeight()-(image.getHeight()*scale), image.getWidth()*scale, image.getHeight()*scale, null);
			
			int[] bestSolution = gee.getPopulation().getGeneticSpecimens().get(0).getGenes();
			BufferedImage solution = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			loc = 0;
			for(int i = 0; i < solution.getWidth()*solution.getHeight(); i++) {
				int x = i % solution.getWidth();
				int y = i / solution.getHeight(); 
				Color c = new Color(bestSolution[loc],bestSolution[loc+1],bestSolution[loc+2]);
				solution.setRGB(x, y, c.getRGB());
				loc+=3;
			}
			
			g2d.drawImage(solution, frame.getWidth()-(solution.getWidth()*scale), frame.getHeight()-(solution.getHeight()*scale), solution.getWidth()*scale, solution.getHeight()*scale, null);
		}
		
	}
	
	public static void imageDemo4() {

		JFileChooser jfc = new JFileChooser();
		jfc.showOpenDialog(null);
		
		BufferedImage image = null;
		try {
			image = ImageIO.read(jfc.getSelectedFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//int[] source = image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
		int[] source = new int[image.getWidth()*image.getHeight()*3];
		int loc = 0;
		for(int i = 0; i < image.getWidth()*image.getHeight(); i++) {
			int x = i % image.getWidth();
			int y = i / image.getHeight(); 
			Color c = new Color(image.getRGB(x, y));
			source[loc] = c.getRed();
			source[loc+1] = c.getGreen();
			source[loc+2] = c.getBlue();
			loc+=3;
		}
		
		int chunkSize = 3*16;
		
		ArrayList<int[]> chunks = ArrayUtils.chunkArray(source, chunkSize);
		ArrayList<GeneticEvolutionEngine> gees = new ArrayList<GeneticEvolutionEngine>();
		
		for(int[] chunk: chunks) {
			FitnessFunction ff = new FitnessFunction() {

				@Override
				public int specimenFitness(Specimen specimen) {
					
					int fitness = 0;
					
					for(int i = 0; i < chunk.length; i++) {
						if(chunk[i]!=specimen.getGenes()[i]) {
							fitness++; 
						}
					}
					return fitness;
				}

				@Override
				public int specimensFitness(Population population) {
					
					int populationFitnessScore = 0;
					
					if(chunk.length == population.getGeneticSpecimens().get(0).getGenes().length) {
						
						for(int i = 0; i < population.getGeneticSpecimens().size(); i++) {
							population.getGeneticSpecimens().get(i).setFitnessScore(specimenFitness(population.getGeneticSpecimens().get(i)));
							populationFitnessScore += population.getGeneticSpecimens().get(i).getFitnessScore();
						}
					}
					
					return populationFitnessScore;
				}
				
			};
			
			int selectionSize = 10;
			
			SelectionFunction sf = new SelectionFunction() {

				@Override
				public void selectSpecimens(Population population) {
					ArrayList<Specimen> specimens = population.getGeneticSpecimens();
					for(int i = 0; i < specimens.size(); i++) {
						for(int k = i; k < specimens.size(); k++) {
							if(specimens.get(k).getFitnessScore() >specimens.get(i).getFitnessScore()) {
								Collections.swap(specimens, k, i);
							}
						}
					}
					
					Collections.reverse(specimens);
					
					ArrayList<Specimen> selectedSpecimens = new ArrayList<Specimen>();
					
					for(int i = 0; i < selectionSize; i++) {
						selectedSpecimens.add(specimens.get(i));
					} 
					
					population.setGeneticSpecimens(selectedSpecimens);
				}
				
			};
			
			PopulateFunction pf = new PopulateFunction() {
				
				public Specimen crossOver(Population population) {
					int r1 = NumberUtils.integerInRange(0, selectionSize-1);
					int r2 = r1;
					while(r2 == r1) {
						r2 = NumberUtils.integerInRange(0, selectionSize-1);
					}
					
					Specimen s1 = population.getGeneticSpecimens().get(r1);
					Specimen s2 = population.getGeneticSpecimens().get(r2);
					
					int[] genes = new int[population.getGeneCount()];
					
					for(int i = 0; i < population.getGeneCount();i++) {
						if(i%2==0) {
							genes[i] = s1.getGenes()[i];
						}else {
							genes[i] = s2.getGenes()[i];
						}
					}
					
					return new Specimen(genes);
				}

				@Override
				public void populateSpecimens(Population population) {
					ArrayList<Specimen> newSpecimens = population.getGeneticSpecimens();
					for(int i = selectionSize; i < population.getTargetPopulationSize(); i++) {
						newSpecimens.add(crossOver(population));
					}
					population.setGeneticSpecimens(newSpecimens);
				}
				
			};
			
			MutationFunction mf = new MutationFunction() {

				@Override
				public void mutateSpecimens(Population population) {
					for(Specimen s: population.getGeneticSpecimens()) {
						int r = NumberUtils.integerInRange(0, population.getOutOf());
						if(r <= population.getMutationChance()) {
							int[] genes = s.getGenes();
							int gr = NumberUtils.integerInRange(0, genes.length-1);
							genes[gr] = NumberUtils.integerInRange(population.getGeneticMin(), population.getGeneticMax());
							s.setGenes(genes);
						}
					}
				}
				
			};
			
			GeneticEvolutionEngine gee = new GeneticEvolutionEngine(ff, sf, pf, mf, 0, 255, 100, 1000, chunk.length, 1000);
			gees.add(gee);
		}
		
		
		int scale = 10;
		
		JFrame frame = new JFrame();
		frame.setSize(((image.getWidth()*scale)*2)+20, (image.getHeight()*scale)+40);
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		boolean step = true;
		while(true) {
			int fitness = 0;
			int[] bestSolution = new int[0];
			if(step) {
				for(GeneticEvolutionEngine gee: gees) {
					gee.step();
					bestSolution = ArrayUtils.concat(bestSolution, gee.getPopulation().getGeneticSpecimens().get(0).getGenes());
					fitness+=gee.getPopulationFitness();
				}
			}
			
			frame.setTitle("Genetic Evolution System : Generation- " + gees.get(0).getGenerationCount() + " Fitness- " + fitness);
			Graphics2D g2d = (Graphics2D) frame.getGraphics();
			
			g2d.drawImage(image, 0, frame.getHeight()-(image.getHeight()*scale), image.getWidth()*scale, image.getHeight()*scale, null);
			
			BufferedImage solution = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			loc = 0;
			for(int i = 0; i < solution.getWidth()*solution.getHeight(); i++) {
				int x = i % solution.getWidth();
				int y = i / solution.getHeight(); 
				Color c = new Color(bestSolution[loc],bestSolution[loc+1],bestSolution[loc+2]);
				solution.setRGB(x, y, c.getRGB());
				loc+=3;
			}
			
			g2d.drawImage(solution, frame.getWidth()-(solution.getWidth()*scale), frame.getHeight()-(solution.getHeight()*scale), solution.getWidth()*scale, solution.getHeight()*scale, null);
		}
		
	}
	
	public static void sequenceDemo(int length, int min, int max) {
		
		int[] source = new int[length];
		for(int i = 0; i < source.length; i++) {
			source[i] = NumberUtils.integerInRange(min, max);
		}
		
		FitnessFunction ff = new FitnessFunction() {

			@Override
			public int specimenFitness(Specimen specimen) {
				
				int fitness = 0;
				
				for(int i = 0; i < source.length; i++) {
					if(source[i]!=specimen.getGenes()[i]) {
						fitness++; 
					}
				}
				return fitness;
			}

			@Override
			public int specimensFitness(Population population) {
				
				int populationFitnessScore = 0;
				
				if(source.length == population.getGeneticSpecimens().get(0).getGenes().length) {
					
					for(int i = 0; i < population.getGeneticSpecimens().size(); i++) {
						population.getGeneticSpecimens().get(i).setFitnessScore(specimenFitness(population.getGeneticSpecimens().get(i)));
						populationFitnessScore += population.getGeneticSpecimens().get(i).getFitnessScore();
					}
				}
				
				return populationFitnessScore;
			}
			
		};
		
		int selectionSize = 10;
		
		SelectionFunction sf = new SelectionFunction() {

			@Override
			public void selectSpecimens(Population population) {
				ArrayList<Specimen> specimens = population.getGeneticSpecimens();
				for(int i = 0; i < specimens.size(); i++) {
					for(int k = i; k < specimens.size(); k++) {
						if(specimens.get(k).getFitnessScore() >specimens.get(i).getFitnessScore()) {
							Collections.swap(specimens, k, i);
						}
					}
				}
				
				Collections.reverse(specimens);
				
				ArrayList<Specimen> selectedSpecimens = new ArrayList<Specimen>();
				
				for(int i = 0; i < selectionSize; i++) {
					selectedSpecimens.add(specimens.get(i));
				} 
				
				population.setGeneticSpecimens(selectedSpecimens);
			}
			
		};
		
		PopulateFunction pf = new PopulateFunction() {
			
			public Specimen crossOver(Population population) {
				int r1 = NumberUtils.integerInRange(0, selectionSize-1);
				int r2 = r1;
				while(r2 == r1) {
					r2 = NumberUtils.integerInRange(0, selectionSize-1);
				}
				
				Specimen s1 = population.getGeneticSpecimens().get(r1);
				Specimen s2 = population.getGeneticSpecimens().get(r2);
				
				int[] genes = new int[population.getGeneCount()];
				
				for(int i = 0; i < population.getGeneCount();i++) {
					if(i%2==0) {
						genes[i] = s1.getGenes()[i];
					}else {
						genes[i] = s2.getGenes()[i];
					}
				}
				
				return new Specimen(genes);
			}

			@Override
			public void populateSpecimens(Population population) {
				ArrayList<Specimen> newSpecimens = population.getGeneticSpecimens();
				for(int i = selectionSize; i < population.getTargetPopulationSize(); i++) {
					newSpecimens.add(crossOver(population));
				}
				population.setGeneticSpecimens(newSpecimens);
			}
			
		};
		
		MutationFunction mf = new MutationFunction() {

			@Override
			public void mutateSpecimens(Population population) {
				for(Specimen s: population.getGeneticSpecimens()) {
					int r = NumberUtils.integerInRange(0, population.getOutOf());
					if(r <= population.getMutationChance()) {
						int[] genes = s.getGenes();
						for(int i = 0; i < genes.length; i++) {
							r = NumberUtils.integerInRange(0, population.getOutOf());
							if(r <= population.getMutationChance()) {
								genes[i] = NumberUtils.integerInRange(population.getGeneticMin(), population.getGeneticMax());
							}
						}
						s.setGenes(genes);
					}
				}
			}
			
		};
		
		GeneticEvolutionEngine gee = new GeneticEvolutionEngine(ff, sf, pf, mf, min, max, 100, 1000, source.length, 100);
		
		int steps = 0;
		while(true) {
			if(gee.getPopulation().getGeneticSpecimens().get(0).getFitnessScore() == 0 && steps > 0) {
				int[] bestSolution = gee.getPopulation().getGeneticSpecimens().get(0).getGenes();
				
				System.out.println("Solution Found : Generation- " + gee.getGenerationCount() + " Fitness- " + gee.getPopulation().getGeneticSpecimens().get(0).getFitnessScore());
				
				System.out.print("Source: ");
				for(int i = 0; i < source.length; i++) {
					System.out.print(source[i]+" ");
				}
				
				System.out.println();
				
				System.out.print("Solution: ");
				for(int i = 0; i < source.length; i++) {
					System.out.print(bestSolution[i]+" ");
				}
				break;
			}
			
			gee.step();
			steps++;
		}
		
	}

}
