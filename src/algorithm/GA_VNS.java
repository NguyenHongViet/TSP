package algorithm;

import model.Solution;

public class GA_VNS extends GA {
	
	VNS vns;
	
	public GA_VNS(String filename) {
		super(filename);
		vns = new VNS(filename);
	}
	
	public Solution mutate(Solution solution) {
		return vns.GVNS(solution, 10, 10, 20);
	}
	
	public static void main(String[] args) {
		GA_VNS solve = new GA_VNS("eil51.tsp");
		solve.getGraph().print();
		solve.algorithm();
		for (int i=0; i<solve.getPopulation().size(); i++) {
			System.out.println("Solution "+(i+1)+":");
			solve.getPopulation().get(i).print();
		}
		solve.populationInfo();
	}

}
