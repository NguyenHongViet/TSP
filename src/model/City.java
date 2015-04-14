package model;


public class City {

	private int x;
	private int y;
	
	public City(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public static double distance(City city1, City city2) {
		return Math.sqrt(Math.pow(city1.x - city2.x, 2) + Math.pow(city1.y - city2.y, 2));
	}
	
	public boolean isEqual(City city) {
		if (city.x == this.x && city.y == this.y)
			return true;
		return false;
	}
}
