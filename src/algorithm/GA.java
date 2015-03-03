package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.City;
import model.Graph;
import model.Solution;

public class GA {
	
	private Graph graph;
	private ArrayList<Solution> population;
	
	public static final int MAX_POPULATION = 100;
	public static final int MAX_GENERATION = 500;
	public static final double CROSSOVER_PERCENTAGE = 0.9;
	public double MUTATE_PERCENTAGE;
	private Random r;
	
	public GA(String filename) {
		graph = new Graph(filename);
		population = new ArrayList<>();
		MUTATE_PERCENTAGE = 1f/graph.getCities();
		r = new Random();
	}
	
	public void algorithm() {
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
				population.add(crossover(parent1.get(j), parent2.get(j)));
			
			Solution top = population.get(0);
			Solution next = population.get(1);
			for (int j=1; j<MAX_POPULATION; j++) {
				if (population.get(j).getCost() < top.getCost()) {
					next = top;
					top = population.get(j);
				} else if (population.get(j).getCost() < next.getCost())
					next = population.get(j);
			}
			
			Solution topMutation = mutate(top);
			Solution nextMutation = mutate(next);
			if (topMutation != null) population.add(topMutation);
			if (nextMutation != null) population.add(nextMutation);
			
			for (int j=0; j<MAX_POPULATION; j++)
				if (r.nextDouble() < MUTATE_PERCENTAGE) {
					Solution mutation = mutate(population.get(j));
					if (mutation != null) population.add(mutation);
				}
		}
	}

	public Solution mutate(Solution solution) {
		Solution newSolution = new Solution(solution);
		for (int a = 0; a < solution.getList().length-3; a++) {
			int b = a + 1;
			for (int c = a + 2; c < solution.getList().length-1; c++) {
				int d = c + 1;
				double ab = distance(solution.getList()[a], solution.getList()[b]);
				double cd = distance(solution.getList()[c], solution.getList()[d]);
				double ac = distance(solution.getList()[a], solution.getList()[c]);
				double bd = distance(solution.getList()[b], solution.getList()[d]);
				if (ab + cd > ac + bd) {
					int start = b;
					int end = c;
					while (start < end) newSolution.swap(start++, end--);
					newSolution.calcCost();
					return newSolution;
				}
			}
		}
		return null;
	}
	
	private double distance(int x1, int x2) {
		City c1 = graph.getCity(x1);
		City c2 = graph.getCity(x2);
		return City.distance(c1, c2);
	}

	public Solution crossover(Solution parent1, Solution parent2) {
		Solution g = new Solution(graph);
		for (int i=0; i<graph.getCities(); i++)
			g.getList()[i] = -1;
		int index = 0;
		boolean fa = true, fb = true;
		int t = r.nextInt(graph.getCities());
		int x = parent1.getCity(t);
		int y = parent2.getCity(t);
		g.getList()[index++] = t;
		
		do {
			x = (x == 0) ? (graph.getCities() - 1) : (x - 1);
			y = (y == graph.getCities() - 1) ? (0) : (y + 1);
			if (fa) {
				int ax = parent1.getList()[x];
				if (g.getCity(ax) == -1) {
					for (int i=index; i>0; i--)
						g.getList()[i] = g.getList()[i-1];
					g.getList()[0] = ax;
					index++;
				} else fa = false;
			}
			
			if (fb) {
				int bx = parent2.getList()[y];
				if (g.getCity(bx) == -1)
					g.getList()[index++] = bx;
				else fb = false;
			}
		} while (fa || fb);
		
		if (index < graph.getCities()) {
			ArrayList<Integer> remain = new ArrayList<>();
			for (int i=0; i<graph.getCities(); i++)
				if (g.getCity(i) == -1)
					remain.add(i);
			
			while (index < graph.getCities()) {
				t = r.nextInt(remain.size());
				g.getList()[index++] = remain.get(t);
				remain.remove(t);
			}
		}
		
		g.calcCost();
		return g;
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
	
	public Graph getGraph() {
		return graph;
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
	public ArrayList<Solution> getPopulation() {
		return population;
	}
	
	public void populationInfo() {
		double max, min, sum;
		max = sum = 0.0;
		min = 99e100;
		
		for (int i=0; i<population.size(); i++) {
			double cur = population.get(i).getCost();
			if (cur > max) max = cur;
			if (cur < min) min = cur;
			sum += cur;
		}
		
		System.out.println("Max price: " + max);
		System.out.println("Min price: " + min);
		System.out.println("Avg price: " + (sum/population.size()));
	}
	
	public static void main(String[] args) {
		GA solve = new GA("eil51.tsp");
		solve.getGraph().print();
		solve.algorithm();
		for (int i=0; i<solve.getPopulation().size(); i++) {
			System.out.println("Solution "+(i+1)+":");
			solve.getPopulation().get(i).print();
		}
		solve.populationInfo();
		
//		Test Crossover
//		Solution p1 = new Solution(solve.getGraph());
//		Solution p2 = new Solution(solve.getGraph());
//		System.out.println("Parent 1:");
//		p1.print();
//		System.out.println("Parent 2:");
//		p2.print();
//		System.out.println("Child:");
//		solve.crossover(p1, p2).print();
		
//		Test mutation
//		Solution origin = new Solution(solve.getGraph());
//		System.out.println("Origin:");
//		origin.print();
//		System.out.println("Mutated:");
//		while (true) {
//			origin = solve.mutate(origin);
//			if (origin == null) break;
//			System.out.println(origin.getCost());
//		}
	}
}
