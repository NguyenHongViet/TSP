package algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import model.City;
import model.Graph;
import model.Neighborhood;
import model.Solution;

public class VNS {
	
	private Graph graph;
	private Random r;
	private Neighborhood[] neighborhoods;
	
	public VNS(String filename) {
		graph = new Graph(filename);
		r = new Random();
	}
	
	public ArrayList<Solution> getNeighborhood(Solution solution, int k) {
		return getNeighborhood_2opt(solution, k);
//		return getNeighborhood_subMST(solution, k);
	}

	public ArrayList<Solution> getNeighborhood_2opt(Solution solution, int k) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		
		if (k == 0) {
			for (int a = 0; a < graph.getCities()-3; a++) {
				int b = a + 1;
				double ab = solution.distance(a, b);
				for (int c = b + 1; c < graph.getCities()-1; c++) {
					int d = c + 1;
					double cd = solution.distance(c, d);
					double ac = solution.distance(a, c);
					double bd = solution.distance(b, d);
					if (ab + cd > ac + bd) {
						int start = b;
						int end = c;
						Solution newSolution = new Solution(solution);
						while (start < end) newSolution.swap(start++, end--);
						newSolution.calcCost();
						neighborhood.add(newSolution);
					}
				}
			}
		} else {
			for (int i=0; i<neighborhoods[k-1].size(); i++) {
				Solution cur = neighborhoods[k-1].get(i);
				for (int a = 0; a < graph.getCities()-3; a++) {
					int b = a + 1;
					double ab = cur.distance(a, b);
					for (int c = b + 1; c < graph.getCities()-1; c++) {
						int d = c + 1;
						double cd = cur.distance(c, d);
						double ac = cur.distance(a, c);
						double bd = cur.distance(b, d);
						if (ab + cd > ac + bd) {
							int start = b;
							int end = c;
							Solution newSolution = new Solution(cur);
							while (start < end) newSolution.swap(start++, end--);
							newSolution.calcCost();
							neighborhood.add(newSolution);
						}
					}
				}
			}
		}
		
		neighborhoods[k].set(neighborhood);
		return neighborhood;
	}
	
	public Solution shake(Solution x, int k) {
		ArrayList<Solution> neighborhood = getNeighborhood(x, k);
		if (neighborhood.isEmpty()) return x;
		return neighborhood.get(r.nextInt(neighborhood.size()));
	}
	
	public Solution algorithm(Solution x, int kmax, int tmax) {
		int t = 0, k;
		initNeighborhoods(kmax);
		
		do {
			k = 0;
			do {
				Solution x1 = shake(x, k);
				// Change neighborhood
				if (x1.getCost() < x.getCost()) {
					x = x1;
					k = 0;
				} else k++;
			} while (k < kmax);
			t++;
		} while (t <= tmax);
		
		return x;
	}
	
	public void initNeighborhoods(int kmax) {
		neighborhoods = new Neighborhood[kmax];
		for (int i=0; i<kmax; i++)
			neighborhoods[i]= new Neighborhood();
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	private class TreeNode {
		protected City head, tail;
		
		public TreeNode(City head, City tail) {
			this.head = head;
			this.tail = tail;
		}
	}

	public ArrayList<TreeNode> buildPrimTree(City[] input) {
		ArrayList<TreeNode> list = new ArrayList<>();
		LinkedList<City> remain = new LinkedList<City>(Arrays.asList(input));
		LinkedList<City> used = new LinkedList<>();
		
		used.addLast(remain.removeFirst());
		while (!remain.isEmpty()) {
			City curCity = remain.removeFirst();
			City nearestCity = used.getFirst();
			for (int i=0; i<used.size(); i++) {
				if (City.distance(curCity, nearestCity) > City.distance(curCity, used.get(i)))
					nearestCity = used.get(i);
			}
			list.add(new TreeNode(nearestCity, curCity));
			used.addLast(curCity);
		}
		
		return list;
	}
	
	public ArrayList<City> preorder(ArrayList<TreeNode> tree, City root) {
		ArrayList<City> order = new ArrayList<>();
		
		if (tree.size() == 1) {
			order.add(tree.get(0).head);
			order.add(tree.get(0).tail);
		} else {
			order.add(root);
			for (int i=0; i<tree.size(); i++)
				if (tree.get(i).head.isEqual(root))
					order.addAll(preorder(tree, tree.get(i).tail));
		}
		
		return order;
	}
	
	public ArrayList<Solution> getNeighborhood_subMST(Solution solution, int k) {
		ArrayList<Solution> neighborhood = new ArrayList<>();
		
		if (k == 0) {
			for (int a = 0; a < graph.getCities()-10; a++) {
				int b = a + 10;
				City[] input = new City[b-a+1];
				for (int i=0; i<input.length; i++)
					input[i] = graph.getCity(solution.getList()[a+i]);
				ArrayList<TreeNode> MST = buildPrimTree(input);
				ArrayList<City> order = preorder(MST, input[0]);
				Solution newSolution = new Solution(solution);
				for (int i=0; i<order.size(); i++)
					newSolution.getList()[a+i] = graph.findCity(order.get(i));
				newSolution.calcCost();
				neighborhood.add(newSolution);
			}
		} else {
			for (int j=0; j<neighborhoods[k-1].size(); j++) {
				Solution cur = neighborhoods[k-1].get(j);
				for (int a = 0; a < graph.getCities()-10; a++) {
					int b = a + 10;
					City[] input = new City[b-a+1];
					for (int i=0; i<input.length; i++)
						input[i] = graph.getCity(cur.getList()[a+i]);
					ArrayList<TreeNode> MST = buildPrimTree(input);
					ArrayList<City> order = preorder(MST, input[0]);
					Solution newSolution = new Solution(cur);
					for (int i=0; i<order.size(); i++)
						newSolution.getList()[a+i] = graph.findCity(order.get(i));
					newSolution.calcCost();
					neighborhood.add(newSolution);
				}
			}
		}
		
		neighborhoods[k].set(neighborhood);
		return neighborhood;
	}
	
	public static void main(String[] args) {
		VNS solve = new VNS("eil51.tsp");
		solve.getGraph().print();
		
		for (int i=0; i<20; i++) {
			System.out.println("VNS " + i +":");
			Solution x = new Solution(solve.getGraph());
			solve.algorithm(x, 10, 20).print();
		}
		
		solve.graph = new Graph(10);
		System.out.println("Graph");
		solve.graph.print();
		City[] input = new City[10];
		for (int i=0; i<10; i++)
			input[i] = solve.graph.getList().get(i);
		 ArrayList<TreeNode> tree = solve.buildPrimTree(input);
		 System.out.println("MST");
		 for (int i=0; i<tree.size(); i++)
			 System.out.println(solve.graph.findCity(tree.get(i).head) + " " + solve.graph.findCity(tree.get(i).tail));
		 ArrayList<City> order = solve.preorder(tree, input[0]);
		 System.out.println("Preorder");
		 for (int i=0; i<order.size(); i++)
			 System.out.println(solve.graph.findCity(order.get(i)));
	}
}
