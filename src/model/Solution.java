package model;

import java.util.Random;


public class Solution {
	private int[] list;
	private Graph graph;
	private double cost;
	private int level;
	
	public Solution(Graph graph) {
		this.graph = graph;
		list = new int[graph.getList().size()];
		setLevel(0);
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
		setLevel(0);
	}
	
	public void swap(int a, int b) {
		int t = list[a];
		list[a] = list[b];
		list[b] = t;
	}
	
	public void calcCost() {
		cost = 0;
		for (int i=0; i<list.length-1; i++)
			cost += City.distance(graph.getCity(list[i]), graph.getCity(list[i+1]));
		cost += City.distance(graph.getCity(list[list.length-1]), graph.getCity(list[0]));
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
	
	public int getCity(int city) {
		for (int i=0; i<list.length; i++)
			if (list[i] == city)
				return i;
		return -1;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
