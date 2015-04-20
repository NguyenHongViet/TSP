package model;

import java.util.Random;


public class Solution {
	private int[] list;
	private Graph graph;
	private double cost;
	public static int countCalc;
	
	public Solution(Graph graph) {
		this.graph = graph;
		list = new int[graph.getList().size()];
		for (int i=0; i<list.length; i++)
			list[i] = i;
		Random r = new Random();
		for (int i=0; i<list.length*2; i++)
			swap(r.nextInt(list.length), r.nextInt(list.length));
		calcCost();
	}
	
	public Solution(Solution solution) {
		this.graph = solution.graph;
		list = new int[graph.getList().size()];
		for (int i=0; i<list.length; i++)
			list[i] = solution.list[i];
		this.cost = solution.cost;
	}
	
	public void swap(int a, int b) {
		int t = list[a];
		list[a] = list[b];
		list[b] = t;
	}
	
	public double distance(int a, int b) {
		City ca = graph.getCity(list[a]);
		City cb = graph.getCity(list[b]);
		return City.distance(ca, cb);
	}
	
	public void calcCost() {
		cost = 0;
		for (int i=0; i<list.length-1; i++)
			cost += City.distance(graph.getCity(list[i]), graph.getCity(list[i+1]));
		cost += City.distance(graph.getCity(list[list.length-1]), graph.getCity(list[0]));
		Solution.countCalc++;
	}
	
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	public double getCost() {
		return cost;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	public int[] getList() {
		return list;
	}
	
	public void print() {
		for (int i=0; i<list.length; i++)
			System.out.print(list[i] + " ");
		System.out.println();
		System.out.println(cost);
	}
	
	@Override
	public String toString() {
		String string = "";
		for (int i=0; i<list.length; i++)
			string += list[i] + " ";
		return string;
	}
	
	public int findCity(int city) {
		for (int i=0; i<list.length; i++)
			if (list[i] == city)
				return i;
		return -1;
	}

}
