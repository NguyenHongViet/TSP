package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Graph {

	private ArrayList<City> list;
	
	public Graph(String filename) {
		File f = new File(filename);
		list = new ArrayList<>();
		try {
			Scanner scanner = new Scanner(f);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				if (Character.isAlphabetic(line.charAt(0))) continue;
				Scanner lineScanner = new Scanner(line);
				lineScanner.nextInt();
				int x = lineScanner.nextInt();
				int y = lineScanner.nextInt();
				list.add(new City(x, y));
				lineScanner.close();
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public Graph(int max) {
		Random r = new Random();
		list = new ArrayList<>();
		for (int i=0; i<max; i++)
			list.add(new City(r.nextInt(100), r.nextInt(100)));
	}
	
	public ArrayList<City> getList() {
		return list;
	}
	
	public City getCity(int i) {
		return list.get(i);
	}
	
	public int findCity(City city) {
		return list.indexOf(city);
	}
	
	public int getCities() {
		return list.size();
	}
	
	public void print() {
		for (int i=0; i<list.size(); i++)
			System.out.println(i + " " + getCity(i).getX() + " " + getCity(i).getY());
	}
	
	public static void main(String[] args) {
		Graph graph = new Graph(10);
		graph.print();
		
		Solution solution = new Solution(graph);
		solution.print();
	}
}
