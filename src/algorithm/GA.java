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
			// Choose parents
			ArrayList<Solution> parent1 = new ArrayList<>();
			ArrayList<Solution> parent2 = new ArrayList<>();
			int PARENT_POP = (int) (MAX_POPULATION * CROSSOVER_PERCENTAGE / 2);
			while (parent1.size() < PARENT_POP) {
				int choose = r.nextInt(population.size());
				parent1.add(population.get(choose));
				
				choose = (choose + 1 + r.nextInt(population.size()-1)) % population.size();
				parent2.add(population.get(choose));
			}
			
			for (int j=0; j<PARENT_POP; j++) {
				Solution[] children = crossover(parent1.get(j), parent2.get(j));
				
				if (children[0] != null) {
					if (r.nextDouble() < MUTATE_PERCENTAGE) {
						Solution mutation = mutate(children[0]);
						if (mutation != null) population.add(mutation);
						else population.add(children[0]);
					} else population.add(children[0]);
				}
				if (children[1] != null) {
					if (r.nextDouble() < MUTATE_PERCENTAGE) {
						Solution mutation = mutate(children[1]);
						if (mutation != null) population.add(mutation);
						else population.add(children[1]);
					} else population.add(children[1]);
				}
			}
			
			population = selection(population);
		}
	}
	
	public Solution mutate(Solution solution) {
		switch (r.nextInt(4)) {
		case 0: return mutate_2opt(solution);
		case 1: return mutate_insert(solution);
		case 2: return mutate_inverse(solution);
		case 3: return mutate_swap(solution);
		}
		return null;
	}
	
	private Solution mutate_2opt(Solution solution) {
		Solution newSolution = new Solution(solution);
		
		for (int a = r.nextInt(solution.getList().length/2); a < solution.getList().length-3; a++) {
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
	
	private Solution mutate_swap(Solution solution) {
		Solution newSolution = new Solution(solution);
		int a = r.nextInt(solution.getList().length);
		int b = (a + 1 + r.nextInt(solution.getList().length)) % solution.getList().length;
		
		newSolution.swap(a, b);
		newSolution.calcCost();
		return newSolution;
	}
	
	private Solution mutate_insert(Solution solution) {
		Solution newSolution = new Solution(solution);
		int a = r.nextInt(solution.getList().length - 1);
		int b = a + 1 + r.nextInt(solution.getList().length - a - 1);
		int temp = newSolution.getList()[b];
		
		for (int k=b; k>a; k--)
			newSolution.getList()[k] = newSolution.getList()[k-1];
		newSolution.getList()[a+1] = temp;
		newSolution.calcCost();
		return newSolution;
	}
	
	private Solution mutate_inverse(Solution solution) {
		Solution newSolution = new Solution(solution);
		int a = r.nextInt(solution.getList().length - 6);
		int b = a + 1 + r.nextInt(5);
		
		while (a < b) newSolution.swap(a++, b--);
		newSolution.calcCost();
		return newSolution;
	}
	
	private double distance(int x1, int x2) {
		City c1 = graph.getCity(x1);
		City c2 = graph.getCity(x2);
		return City.distance(c1, c2);
	}

	public Solution[] crossover(Solution parent1, Solution parent2) {
		switch (r.nextInt(4)) {
		case 0:
			Solution[] children = new Solution[2];
			children[0] = crossover_Greedy(parent1, parent2);
			children[1] = crossover_Greedy(parent1, parent2);
			return children;
		case 1: return crossover_cx(parent1, parent2);
		case 2: 
			int a = r.nextInt(parent1.getList().length - 6);
			int b = a + 1 + r.nextInt(5);
			children = new Solution[2];
			children[0] = crossover_ox(parent1, parent2, a, b);
			children[1] = crossover_ox(parent2, parent1, a, b);
			return children;
		case 3: return crossover_pmx(parent1, parent2);
		}
		return null;
	}
	
	private Solution crossover_Greedy(Solution parent1, Solution parent2) {
		Solution child = new Solution(graph);
		for (int i=0; i<graph.getCities(); i++)
			child.getList()[i] = -1;
		int index = 0;
		boolean fa = true, fb = true;
		int t = r.nextInt(graph.getCities());
		int x = parent1.findCity(t);
		int y = parent2.findCity(t);
		child.getList()[index++] = t;
		
		do {
			x = (x == 0) ? (graph.getCities() - 1) : (x - 1);
			y = (y == graph.getCities() - 1) ? (0) : (y + 1);
			if (fa) {
				int ax = parent1.getList()[x];
				if (child.findCity(ax) == -1) {
					for (int i=index; i>0; i--)
						child.getList()[i] = child.getList()[i-1];
					child.getList()[0] = ax;
					index++;
				} else fa = false;
			}
			
			if (fb) {
				int bx = parent2.getList()[y];
				if (child.findCity(bx) == -1)
					child.getList()[index++] = bx;
				else fb = false;
			}
		} while (fa || fb);
		
		if (index < graph.getCities()) {
			ArrayList<Integer> remain = new ArrayList<>();
			for (int i=0; i<graph.getCities(); i++)
				if (child.findCity(i) == -1)
					remain.add(i);
			
			while (index < graph.getCities()) {
				t = r.nextInt(remain.size());
				child.getList()[index++] = remain.get(t);
				remain.remove(t);
			}
		}
		
		child.calcCost();
		return child;
	}
	
	private Solution[] crossover_pmx(Solution parent1, Solution parent2) {
		Solution[] children = new Solution[2];
		children[0] = new Solution(parent1);
		children[1] = new Solution(parent2);
		int a = r.nextInt(parent1.getList().length - 6);
		int b = a + 1 + r.nextInt(5);
		int pos;
		
		for (int i=a; i<b; i++) {
			pos = children[0].findCity(parent2.getList()[i]);
			children[0].swap(i, pos);
			
			pos = children[1].findCity(parent1.getList()[i]);
			children[1].swap(i, pos);
		}
		children[0].calcCost();
		children[1].calcCost();
		return children;
	}
	
	private Solution[] crossover_cx(Solution parent1, Solution parent2) {
		Solution[] children = new Solution[2];
		children[0] = new Solution(parent1);
		children[1] = new Solution(parent2);
		ArrayList<Integer> cycle = new ArrayList<>();
		int turn = 0, length = 0, start;
		
		for (int i=0; i<parent1.getList().length; i++) {
			children[0].getList()[i] = -1;
			children[1].getList()[i] = -1;
		}
		while (length < parent1.getList().length) {
			cycle.clear();
			
			start = 0;
			while (children[0].getList()[start] != -1) start++;
			
			cycle.add(start); length++;
			int current = start;
			while (parent1.getList()[start] != parent2.getList()[current]) {
				int temp = parent2.getList()[current];
				current = parent1.findCity(temp);
				cycle.add(current); length++;
			}
			
			for (int i=0; i<cycle.size(); i++) {
				int index = cycle.get(i);
				if (turn%2 == 0) {
					children[0].getList()[index] = parent1.getList()[index];
					children[1].getList()[index] = parent2.getList()[index];
				} else {
					children[0].getList()[index] = parent2.getList()[index];
					children[1].getList()[index] = parent1.getList()[index];
				}
			}
			turn++;
		}
		children[0].calcCost();
		children[1].calcCost();
		return children;
	}
	
	private Solution crossover_ox(Solution parent1, Solution parent2, int a, int b) {
		Solution child = new Solution(parent1);
		for (int i=0; i<parent1.getList().length; i++)
			if (i<a || i>=b)
				child.getList()[i] = -1;
		
		int i = b, j = b;
		while (i != a) {
			int temp = parent2.getList()[j];
			if (child.findCity(temp) == -1) {
				child.getList()[i] = temp;
				i = (i == parent1.getList().length-1) ? (0) : (i+1);
			} else
				j = (j == parent1.getList().length-1) ? (0) : (j+1);
		}
		child.calcCost();
		return child;
	}

	public ArrayList<Solution> selection(ArrayList<Solution> population) {
		ArrayList<Solution> newPop = new ArrayList<>();
		
		for (int i=0; i<MAX_POPULATION; i++) {
			int choose = 0;
			for (int j=1; j<population.size(); j++)
				if (population.get(choose).getCost() > population.get(j).getCost())
					choose = j;
			
			boolean exist = false;
			for (int j=0; j<newPop.size(); j++)
				if (newPop.get(j) == population.get(choose)) {
					exist = true;
					break;
				}
			if (!exist) newPop.add(population.get(choose));
			population.remove(choose);
		}
		return newPop;
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
	}
}
