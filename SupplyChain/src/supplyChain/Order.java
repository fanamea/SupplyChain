package supplyChain;

public class Order {
	
	//TODO Teillieferungen
	
	private static int count;
	
	private int Id;
	private Link link;
	private int date;
	private double size;
	private double shipped;
	
	public Order(Link link, int d, double s){
		this.Id = count++;
		this.link = link;
		this.date = d;
		this.size = s;
		this.shipped = 0;
	}
	
	public int getDate(){
		return this.date;
	}
	
	public Link getLink(){
		return this.link;
	}
	
	public double getSize(){
		return this.size;
	}
	
	public void incrShipped(double i){
		shipped += i;
	}
	
	public boolean isShipped(){
		return size==shipped;
	}
	
	public double getShortage(){
		return size-shipped;
	}
	
	public String toString(){
		return "Order: " + this.Id + ", Date: " + this.date + ", Size: " + this.size;
	}

}
