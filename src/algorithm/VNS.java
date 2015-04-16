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
		ArrayList<Solution> neighborhood = new ArrayList<>();
		if (k == 0) {
//			neighborhood = getNeighbors_2opt(solution);
			neighborhood = getNeighbors_subMST(solution);
			neighborhoods[k].set(neighborhood);
		} else {
			for (int i=0; i<neighborhoods[k-1].size(); i++) {
				Solution cur = neighborhoods[k-1].get(i);
//				neighborhood.addAll(getNeighbors_2opt(cur));
				neighborhood.addAll(getNeighbors_subMST(cur));
				neighborhoods[k].set(neighborhood);
			}
		}
		
		return neighborhood;
	}
	
	public ArrayList<Solution> getNeighbors_2opt(Solution solution) {
		ArrayList<Solution> neighbors = new ArrayList<>();
		
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
					neighbors.add(newSolution);
				}
			}
		}
		
		return neighbors;
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
					x.print();
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
	
	public ArrayList<Solution> getNeighbors_subMST(Solution solution) {
		ArrayList<Solution> neighbors = new ArrayList<>();
		int length = 10;
		
		for (int a = 0; a < graph.getCities()-length; a++) {
			City[] input = new City[length];
			for (int i=0; i<length; i++)
				input[i] = graph.getCity(solution.getList()[a+i]);
			
			City before_start, after_end;
			if (a == 0)
				before_start = graph.getCity(solution.getList()[graph.getCities()-1]);
			else before_start = graph.getCity(solution.getList()[a-1]);
			if (a == graph.getCities()-1)
				after_end = graph.getCity(solution.getList()[0]);
			else after_end = graph.getCity(solution.getList()[(a+length) % graph.getCities()]);
			
			int start_order = 0;
			for (int i=1; i<length; i++)
				if (City.distance(before_start, input[i]) < City.distance(before_start, input[start_order]))
					start_order = i;
			if (start_order != 0) {
				City temp = input[0];
				input[0] = input[start_order];
				input[start_order] = temp;
			}
			ArrayList<TreeNode> tree = buildPrimTree(input);
			ArrayList<City> order = preorder(tree, input[0]);
			
			double before, after;
			before = City.distance(before_start, input[0]);
			after = City.distance(before_start, order.get(0));
			for (int i=0; i<length-1; i++) {
				before += City.distance(input[i], input[i+1]);
				after += City.distance(order.get(i), order.get(i+1));
			}
			before += City.distance(input[length-1], after_end);
			after += City.distance(order.get(length-1), after_end);
			
			if (before > after) {
				Solution newSolution = new Solution(solution);
				for (int i=0; i<length; i++)
					newSolution.getList()[a+i] = graph.findCity(order.get(i));
				newSolution.calcCost();
				neighbors.add(newSolution);
			}
		}

		return neighbors;
	}
	
	public static void main(String[] args) {
		VNS solve = new VNS("eil51.tsp");
		solve.getGraph().print();
		
		for (int i=0; i<20; i++) {
			System.out.println("VNS " + i +":");
			Solution x = new Solution(solve.getGraph());
			solve.algorithm(x, 5, 20).print();
		}
				
//		City[] input = new City[10];
//		for (int i=0; i<10; i++)
//			input[i] = solve.graph.getList().get(i+solve.graph.getCities()-10);
//		 ArrayList<TreeNode> tree = solve.buildPrimTree(input);
//		 System.out.println("MST");
//		 for (int i=0; i<tree.size(); i++)
//			 System.out.println(solve.graph.findCity(tree.get(i).head) + " " + solve.graph.findCity(tree.get(i).tail));
//		 ArrayList<City> order = solve.preorder(tree, input[0]);
//		 System.out.println("Preorder");
//		 for (int i=0; i<order.size(); i++)
//			 System.out.println(solve.graph.findCity(order.get(i)));
	}
}
