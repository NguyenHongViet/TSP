package model;

import java.util.ArrayList;
import java.util.Collection;

public class Neighborhood {
	private ArrayList<Solution> neighbors;
	
	public Neighborhood() {
		neighbors = new ArrayList<Solution>();
	}
	
	public boolean add(Solution solution) {
		return neighbors.add(solution);
	}
	
	public boolean addAll(Collection<Solution> collection) {
		return neighbors.addAll(collection);
	}
	
	public boolean set(ArrayList<Solution> neighborhood) {
		neighbors.clear();
		return neighbors.addAll(neighborhood);
	}
	
	public void clear() {
		neighbors.clear();
	}
	
	public Solution get(int i) {
		return neighbors.get(i);
	}
	
	public boolean isEmpty() {
		return neighbors.isEmpty();
	}
	
	public int size() {
		return neighbors.size();
	}
}
