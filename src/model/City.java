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
	
	public double distance(City city) {
		return Math.sqrt(Math.pow(x - city.x, 2) + Math.pow(y - city.y, 2));
	}
}
