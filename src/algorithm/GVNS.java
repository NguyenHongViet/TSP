package algorithm;

import model.Solution;

public class GVNS extends VNS {
	
	private VND vnd;
	
	public GVNS(String filename) {
		super(filename);
		vnd = new VND(filename);
	}

	public Solution algorithm(Solution x, int lmax, int kmax, int tmax) {
		int t = 0, k;
		initNeighborhoods(kmax);
		
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
			} while (k < kmax);
			t++;
		} while (t <= tmax);
		
		return x;
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
