package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.Graph;
import model.Neighborhood;
import model.Solution;

public class VNS {
	
	private Graph graph;
	private Random r;
	private Neighborhood[] neighborhoods;
	
	public VNS(String filename) {
		graph = new Graph(filename);
		r = new Random();
	}

	public ArrayList<Solution> getNeighborhood(Solution solution, int k) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		
		if (k == 0) {
			for (int a = 0; a < graph.getCities()-3; a++) {
				int b = a + 1;
				double ab = solution.distance(a, b);
				for (int c = b + 1; c < graph.getCities()-1; c++) {
					int d = c + 1;
					double cd = solution.distance(c, d);
					double ac = solution.distance(a, c);
					double bd = solution.distance(b, d);
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
		} else {
			for (int i=0; i<neighborhoods[k-1].size(); i++) {
				Solution cur = (Solution) neighborhoods[k-1].get(i);
				for (int a = 0; a < graph.getCities()-3; a++) {
					int b = a + 1;
					double ab = cur.distance(a, b);
					for (int c = b + 1; c < graph.getCities()-1; c++) {
						int d = c + 1;
						double cd = cur.distance(c, d);
						double ac = cur.distance(a, c);
						double bd = cur.distance(b, d);
						if (ab + cd > ac + bd) {
							int start = b;
							int end = c;
							Solution newSolution = new Solution(cur);
							while (start < end) newSolution.swap(start++, end--);
							newSolution.calcCost();
							neighborhood.add(newSolution);
						}
					}
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
	
	public Solution algorithm(Solution x, int kmax, int tmax) {
		int t = 0, k;
		initNeighborhoods(kmax);
		
		do {
			k = 0;
			do {
				Solution x1 = shake(x, k);
				// Change neighborhood
				if (x1.getCost() < x.getCost()) {
					x = x1;
					k = 0;
				} else k++;
			} while (k < kmax);
			t++;
		} while (t <= tmax);
		
		return x;
	}
	
	public void initNeighborhoods(int kmax) {
		neighborhoods = new Neighborhood[kmax];
		for (int i=0; i<kmax; i++)
			neighborhoods[i]= new Neighborhood();
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
			solve.algorithm(x, 10, 20).print();
		}
	}
}
