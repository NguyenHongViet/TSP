package algorithm;

import java.util.ArrayList;

import model.Solution;


public class GA_VNS extends GA {
	
	VNS vns;
	
	public GA_VNS(String filename) {
		super(filename);
		vns = new VNS(filename);
	}
	
	@Override
	public ArrayList<Solution> selection(ArrayList<Solution> population) {
		Solution[] elite = new Solution[10];
		elite[0] = population.get(0);
		for (int i=1; i<elite.length; i++)
			elite[i] = null;
		
		for (int i=1; i<population.size(); i++) {
			double cost = population.get(i).getCost();
			for (int j=0; (j < elite.length) || (elite[j] == null); j++) {
				if (cost < elite[j].getCost()) {
					for (int k=elite.length-1; k>j; k--)
						elite[k] = elite[k-1];
					elite[j] = population.get(i);
					break;
				}
			}
		}
		
		for (int i=0; i<elite.length; i++)
			population.add(vns.algorithm(elite[i], 3, 5));
		
		return super.selection(population);
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
