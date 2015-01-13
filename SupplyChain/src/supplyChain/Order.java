package supplyChain;

public class Order {
	
	//TODO Teillieferungen
	
	private static int count;
	
	private int Id;
	private int date;
	private int size;
	private int shipped;
	
	public Order(int d, int s){
		this.Id = count++;
		this.date = d;
		this.size = s;
		this.shipped = 0;
	}
	
	public int getDate(){
		return this.date;
	}
	
	public int getSize(){
		return this.size;
	}
	
	public void incrShipped(int i){
		shipped += i;
	}
	
	public boolean isShipped(){
		return size==shipped;
	}
	
	public int getShortage(){
		return size-shipped;
	}
	
	public String toString(){
		return "Order: " + this.Id + ", Date: " + this.date + ", Size: " + this.size;
	}

}
