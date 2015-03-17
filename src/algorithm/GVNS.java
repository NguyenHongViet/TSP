package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.Graph;
import model.Neighborhood;
import model.Solution;

public class GVNS {
	
	private Graph graph;
	private Random r;
	private Neighborhood[] neighborhoods;
	private VND vnd;
	
	public GVNS(String filename) {
		graph = new Graph(filename);
		r = new Random();
		vnd = new VND(filename);
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
	
	public Solution shake(Solution x, int k) {
		ArrayList<Solution> neighborhood = getNeighborhood(x, k);
		if (neighborhood.isEmpty()) return x;
		return neighborhood.get(r.nextInt(neighborhood.size()));
	}
	
	public Solution algorithm(Solution x, int lmax, int kmax, int tmax) {
		int t = 0, k;
		neighborhoods = new Neighborhood[kmax];
		for (int i=0; i<kmax; i++)
			neighborhoods[i]= new Neighborhood();
		
		do {
			k = 0;
			do {
				Solution x1 = shake(x, k);
				Solution x2 = vnd.algorithm(x1, lmax);
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
		GVNS solve = new GVNS("eil51.tsp");
		solve.getGraph().print();
		
		for (int i=0; i<20; i++) {
			System.out.println("GVNS " + i +":");
			Solution x = new Solution(solve.getGraph());
			solve.algorithm(x, 10, 10, 20).print();
		}
	}
}
