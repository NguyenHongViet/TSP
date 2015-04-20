package prog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import algorithm.GA_VNS;


public class Program {
	public static String INPUT_PATH = "data/";
	public static String OUTPUT_PATH = "result/";
	
	public static void main(String[] args) {
		long t1, t2;
		
		File folder = new File(INPUT_PATH);
		File[] listOfFiles = folder.listFiles();

		for (File file : listOfFiles) {
	        System.out.println(INPUT_PATH + file.getName());
	        t1 = System.currentTimeMillis();
	        GA_VNS ga_vns = new GA_VNS(INPUT_PATH + file.getName());
	        ga_vns.algorithm();
	        t2 = System.currentTimeMillis();
	        
	        double max, min, sum;
			max = sum = 0.0;
			min = 99e100;
			
			for (int i=0; i<ga_vns.getPopulation().size(); i++) {
				double cur = ga_vns.getPopulation().get(i).getCost();
				if (cur > max) max = cur;
				if (cur < min) min = cur;
				sum += cur;
			}
	        
	        File outfile = new File(OUTPUT_PATH + file.getName());
	        PrintStream output = null;
	        try {
				output = new PrintStream(outfile);
				for (int i=0; i < ga_vns.getPopulation().size(); i++) {
					output.println("Solution "+i );
					output.println(ga_vns.getPopulation().get(i).toString());
					output.println("Cost: " + ga_vns.getPopulation().get(i).getCost());
				}
				output.println("Max: " + max);
				output.println("Min: " + min);
				output.println("Avg: " + sum/ga_vns.getPopulation().size());
				output.println("Time: " + (t2-t1));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
	        output.close();
		}
	}
}
