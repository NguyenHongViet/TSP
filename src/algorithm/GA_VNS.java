package algorithm;


public class GA_VNS extends GA {
	
	GVNS gvns;
	
	public GA_VNS(String filename) {
		super(filename);
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
