package algorithm;

import java.util.ArrayList;

import model.Solution;

public class VND extends VNS {
	
	public VND(String filename) {
		super(filename);
	}
	
	public Solution algorithm(Solution x, int kmax) {
		int k = 0;
		initNeighborhoohs(kmax);
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
