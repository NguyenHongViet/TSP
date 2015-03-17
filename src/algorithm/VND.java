package algorithm;

import java.util.ArrayList;

import model.Graph;
import model.Neighborhood;
import model.Solution;

public class VND {
	
	private Graph graph;
	private Neighborhood[] neighborhoods;
	
	public VND(String filename) {
		graph = new Graph(filename);
	}

	public ArrayList<Solution> getNeighborhood(Solution solution, int k) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		
		if (k == 0) {
			for (int b = 1; b < graph.getCities()-2; b++)
				for (int c = b + 1; c < graph.getCities()-1; c++) {
					int start = b;
					int end = c;
					Solution newSolution = new Solution(solution);
					while (start < end) newSolution.swap(start++, end--);
					newSolution.calcCost();
					neighborhood.add(newSolution);
				}
		} else {
			for (int i=0; i<neighborhoods[k-1].size(); i++) {
				Solution cur = (Solution) neighborhoods[k-1].get(i);
				for (int b = 1; b < graph.getCities()-2; b++)
					for (int c = b + 1; c < graph.getCities()-1; c++) {
						int start = b;
						int end = c;
						Solution newSolution = new Solution(cur);
						while (start < end) newSolution.swap(start++, end--);
						newSolution.calcCost();
						neighborhood.add(newSolution);
					}
			}
		}
		
		neighborhoods[k].set(neighborhood);
		return neighborhood;
	}
	
	public Solution algorithm(Solution x, int kmax) {
		int k = 0;
		neighborhoods = new Neighborhood[kmax];
		for (int i=0; i<kmax; i++)
			neighborhoods[i] = new Neighborhood();
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
	
	public Graph getGraph() {
		return graph;
	}
	
	public static void main(String[] args) {
		VND solve = new VND("eil51.tsp");
		solve.getGraph().print();
		
		for (int i=0; i<20; i++) {
			System.out.println("VND " + i +":");
			Solution x = new Solution(solve.getGraph());
			solve.algorithm(x, 2).print();
		}
	}
}
