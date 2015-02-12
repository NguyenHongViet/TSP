package model;

import java.util.Random;


public class Solution {
	private int[] list;
	private Graph graph;
	private double cost;
	
	public Solution(Graph graph) {
		this.graph = graph;
		list = new int[graph.getList().size()];
		for (int i=0; i<list.length; i++)
			list[i] = i;
		Random r = new Random();
		for (int i=0; i<list.length*2; i++)
			swap(r.nextInt(list.length), r.nextInt(list.length), false);
		calcCost();
	}
	
	public Solution(Solution solution) {
		this.graph = solution.graph;
		list = new int[graph.getList().size()];
		for (int i=0; i<list.length; i++)
			list[i] = solution.list[i];
		this.cost = solution.cost;
	}
	
	public void swap(int a, int b, boolean reCalc) {
		int t = list[a];
		list[a] = list[b];
		list[b] = t;
		if (reCalc) calcCost();
	}
	
	public void calcCost() {
		cost = 0;
		for (int i=0; i<list.length-1; i++)
			cost += graph.getCity(list[i]).distance(graph.getCity(list[i+1]));
		cost += graph.getCity(list[list.length-1]).distance(graph.getCity(list[0]));
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
	
	public int getNext(int city) {
		int index = getCity(city);
		if (index == -1) return -1;
		if (index == list.length - 1) return 0;
		return index + 1;
	}
	
	public int getPrev(int city) {
		int index = getCity(city);
		if (index == -1) return -1;
		if (index == 0) return list.length - 1;
		return index - 1;
	}
}