package algorithm;

import java.util.ArrayList;
import java.util.Random;

import model.Solution;


public class GA_VNS extends GA {
	
	GVNS gvns;
	Random r;
	
	public GA_VNS(String filename) {
		super(filename);
		gvns = new GVNS(filename);
		r = new Random();
	}
	
	@Override
	public ArrayList<Solution> selection(ArrayList<Solution> population) {
		ArrayList<Solution> chosen = choose(population);
		for (int i=0; i<chosen.size(); i++)
			population.add(gvns.algorithm(chosen.get(i), 5, 5, 5));
		return super.selection(population);
	}
	
	public ArrayList<Solution> choose(ArrayList<Solution> population) {
		ArrayList<Solution> chosen = new ArrayList<>();
		for (int i=0; i<10; i++)
			chosen.add(population.get(r.nextInt(population.size())));
		return chosen;
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
