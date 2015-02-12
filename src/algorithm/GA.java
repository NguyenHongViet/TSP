package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.Graph;
import model.Solution;

public class GA {
	
	private Graph graph;
	private ArrayList<Solution> population;
	
	public static final int MAX_POPULATION = 100;
	public static final int MAX_GENERATION = 200;
	public static final double CROSSOVER_PERCENTAGE = 0.9;
	public double MUTATE_PERCENTAGE;
	private Random r;
	
	public GA(String filename) {
		graph = new Graph(filename);
		population = new ArrayList<>();
		MUTATE_PERCENTAGE = 1f/(graph.getList().size());
		r = new Random();
	}
	
	public void algorithm(Graph graph) {
		population = initialization(graph);
		for (int i=0; i<MAX_GENERATION; i++) {
			population = selection(population);
			
			ArrayList<Solution> parents, parent1, parent2;
			parents = new ArrayList<>(population);
			parent1 = new ArrayList<>();
			parent2 = new ArrayList<>();
			int PARENT_POP = (int) (MAX_POPULATION * CROSSOVER_PERCENTAGE / 2);
			while (parent1.size() < PARENT_POP) {
				int choose = r.nextInt(parents.size());
				parent1.add(parents.get(choose));
				parents.remove(choose);
				
				choose = r.nextInt(parents.size());
				parent2.add(parents.get(choose));
				parents.remove(choose);
			}
			
			for (int j=0; j<PARENT_POP; j++)
				crossover(population, parent1.get(j), parent2.get(j));
			
			
			for (int j=0; j<MAX_POPULATION; j++)
				if (r.nextDouble() < MUTATE_PERCENTAGE)
					population.add(mutate(population.get(j)));
		}
	}

	public Solution mutate(Solution solution) {
		Solution newSolution = new Solution(solution);
		return newSolution;
	}

	public void crossover(ArrayList<Solution> pop, Solution parent1, Solution parent2) {

	}

	public ArrayList<Solution> selection(ArrayList<Solution> population) {
		ArrayList<Solution> pop = new ArrayList<>();
		
		for (int i=0; i<MAX_POPULATION; i++) {
			int choose = 0;
			for (int j=1; j<population.size(); j++)
				if (population.get(choose).getCost() > population.get(j).getCost())
					choose = j;
			
			pop.add(population.get(choose));
			population.remove(choose);
		}
		return pop;
	}

	public ArrayList<Solution> initialization(Graph graph) {
		ArrayList<Solution> pop = new ArrayList<>();
		for (int i=0; i<MAX_POPULATION; i++)
			pop.add(new Solution(graph));
		return pop;
	}
}
