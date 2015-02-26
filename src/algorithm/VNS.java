package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.City;
import model.Graph;
import model.Solution;

public class VNS {
	
	private Graph graph;
	private Random r;
	
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
				if (City.distance(graph.getCity(a), graph.getCity(b)) + City.distance(graph.getCity(c), graph.getCity(d)) > 
					City.distance(graph.getCity(a), graph.getCity(c)) + City.distance(graph.getCity(b), graph.getCity(d))) {
					Solution newSolution = new Solution(solution);
					newSolution.swap(b, c, true);
					newSolution.setLevel(solution.getLevel() + 1);
					neighborhood.add(newSolution);
				}
			}
		}
		return neighborhood;
	}
	
	public ArrayList<Solution> getNeighborhood_insert(Solution solution) {
		return null;
	}
	
	public ArrayList<Solution> getNeighborhood_swap(Solution solution) {
		return null;
	}
	
	public ArrayList<Solution> getNeighborhood(Solution solution) {
		return getNeighborhood_2opt(solution);
	}
	
	public Solution VND(Solution x, int kmax) {
		int k = 1;
		ArrayList<Solution> neighborhood = new ArrayList<>();
		ArrayList<Solution> newNeighborhood = new ArrayList<>();
		neighborhood.add(x);
		
		do {
			// Next neighborhood
			newNeighborhood.clear();
			for (int i=0; i<neighborhood.size(); i++)
				newNeighborhood.addAll(getNeighborhood(neighborhood.get(i)));
			neighborhood = newNeighborhood;
			if (neighborhood.isEmpty()) return x;
			
			// Find best neighbor
			Solution x1 = neighborhood.get(0);
			for (int i=1; i<neighborhood.size(); i++)
				if (neighborhood.get(i).getCost() < x1.getCost())
					x1 = neighborhood.get(i);
			
			// Change neighborhood
			if (x1.getCost() < x.getCost()) {
				x = x1;
				k = 1;
			} else k++;
			
		} while (k < kmax);
		
		return x;
	}
	
	public Solution shake(Solution x, int k) {
		ArrayList<Solution> neighborhood = getNeighborhood(x);
		if (neighborhood.isEmpty()) return x;
		return neighborhood.get(r.nextInt(neighborhood.size()));
	}
	
	public Solution GVNS(Solution x, int lmax, int kmax, int tmax) {
		int t = 0, k;
		
		do {
			k = 1;
			do {
				Solution x1 = shake(x, k);
				Solution x2 = VND(x1, lmax);
				// Change neighborhood
				if (x2.getCost() < x.getCost()) {
					x = x2;
					k = 1;
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
			solve.GVNS(x, 2, 5, 20).print();
		}
	}
}
