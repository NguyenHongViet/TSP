package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.City;
import model.Graph;
import model.Solution;

public class VNS {
	
	private Graph graph;
	private Random r;
	private int kmax = 5;
	
	public VNS(String filename) {
		graph = new Graph(filename);
		r = new Random();
	}

	public ArrayList<Solution> getNeighborhood_2opt(Solution solution) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		for (int a = 0; a < graph.getCities()-3; a++) {
			int b = a + 1;
			for (int c = a + 2; c < graph.getCities()-1; c++) {
				int d = c + 1;
				double ab = distance(solution.getList()[a], solution.getList()[b]);
				double cd = distance(solution.getList()[c], solution.getList()[d]);
				double ac = distance(solution.getList()[a], solution.getList()[c]);
				double bd = distance(solution.getList()[b], solution.getList()[d]);
				if (ab + cd > ac + bd) {
					int start = b;
					int end = c;
					Solution newSolution = new Solution(solution);
					while (start < end) newSolution.swap(start++, end--);
					newSolution.calcCost();
					neighborhood.add(newSolution);
				}
			}
		}
		return neighborhood;
	}
	
	private double distance(int x1, int x2) {
		City c1 = graph.getCity(x1);
		City c2 = graph.getCity(x2);
		return City.distance(c1, c2);
	}
	
	public ArrayList<Solution> getNeighborhood_insert(Solution solution) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		for (int i=0; i<graph.getCities()-2; i++)
			for (int j=i+2; j<graph.getCities(); j++) {
				Solution newSolution = new Solution(solution);
				int temp = newSolution.getList()[j];
				for (int k=j; k>i; k--)
					newSolution.getList()[k] = newSolution.getList()[k-1];
				newSolution.getList()[i+1] = temp;
				newSolution.calcCost();
				if (newSolution.getCost() < solution.getCost())
					neighborhood.add(newSolution);
			} 
		return neighborhood;
	}
	
	public ArrayList<Solution> getNeighborhood_pairinsert(Solution solution) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		for (int i=0; i<graph.getCities()-3; i++)
			for (int j=i+2; j<graph.getCities()-1; j++) {
				Solution newSolution = new Solution(solution);
				int temp1 = newSolution.getList()[j];
				int temp2 = newSolution.getList()[j+1];
				for (int k=j; k>i+1; k--)
					newSolution.getList()[k] = newSolution.getList()[k-2];
				newSolution.getList()[i+1] = temp1;
				newSolution.getList()[i+2] = temp2;
				newSolution.calcCost();
				if (newSolution.getCost() < solution.getCost())
					neighborhood.add(newSolution);
			} 
		return neighborhood;
	}
	
	public ArrayList<Solution> getNeighborhood_swap(Solution solution) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		for (int i=0; i<graph.getCities()-1; i++)
			for (int j=i+1; j<graph.getCities(); j++) {
				Solution newSolution = new Solution(solution);
				newSolution.swap(i, j);
				newSolution.calcCost();
				if (newSolution.getCost() < solution.getCost())
					neighborhood.add(newSolution);
			}
		return neighborhood;
	}
	
	public ArrayList<Solution> getNeighborhood_pairswap(Solution solution) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		for (int i=0; i<graph.getCities()-3; i++)
			for (int j=i+2; j<graph.getCities()-1; j++) {
				Solution newSolution = new Solution(solution);
				newSolution.swap(i, j);
				newSolution.swap(i+1, j+1);
				newSolution.calcCost();
				if (newSolution.getCost() < solution.getCost())
					neighborhood.add(newSolution);
			}
		return neighborhood;
	}
	
	public ArrayList<Solution> getNeighborhood(Solution solution, int k) {
		switch (k) {
		case 0: return getNeighborhood_2opt(solution);
		case 1: return getNeighborhood_swap(solution);
		case 2: return getNeighborhood_insert(solution);
		case 4: return getNeighborhood_pairswap(solution);
		case 5: return getNeighborhood_pairinsert(solution);
		}
		return null;
	}
	
	public Solution VND(Solution x) {
		int k = 0;
		ArrayList<Solution> neighborhood;
		
		do {
			// Find best neighbor
			neighborhood = getNeighborhood(x, k);
			if (neighborhood.isEmpty()) {
				k++;
				continue;
			}
			Solution x1 = neighborhood.get(0);
			for (int i=1; i<neighborhood.size(); i++)
				if (neighborhood.get(i).getCost() < x1.getCost())
					x1 = neighborhood.get(i);
			
			// Change neighborhood
			if (x1.getCost() < x.getCost()) {
				x = x1;
				k = 0;
			} else k++;
			
		} while (k < kmax);
		
		return x;
	}
	
	public Solution shake(Solution x, int k) {
		ArrayList<Solution> neighborhood = getNeighborhood(x, k);
		if (neighborhood.isEmpty()) return x;
		return neighborhood.get(r.nextInt(neighborhood.size()));
	}
	
	public Solution GVNS(Solution x, int tmax) {
		int t = 0, k;
		
		do {
			k = 0;
			do {
				Solution x1 = shake(x, k);
				Solution x2 = VND(x1);
				// Change neighborhood
				if (x2.getCost() < x.getCost()) {
					x = x2;
					k = 0;
				} else k++;
				t++;
			} while (k < kmax);
		} while (t <= tmax);
		
		return x;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public static void main(String[] args) {
		VNS solve = new VNS("eil51.tsp");
		solve.getGraph().print();
		
		for (int i=0; i<20; i++) {
			System.out.println("GVNS " + i +":");
			Solution x = new Solution(solve.getGraph());
			solve.GVNS(x, 20).print();
		}
	}
}
