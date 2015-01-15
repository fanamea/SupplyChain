package supplyChain;

public class Shipment {
	
	private static int count;
	
	private int Id;
	private Link link;
	private int date;
	private double size;
	private int leadTime;
	private Order order;
	
	public Shipment(Link link, int d, double s, int lt, Order order){
		this.Id = count++;
		this.link = link;
		this.date = d;
		this.size = s;
		this.leadTime = lt;
		this.order = order;
	}
	
	public int getDate(){
		return this.date;
	}
	
	public double getSize(){
		return this.size;
	}
	
	public Order getOrder(){
		return this.order;
	}
	
	public int getLeadTime(){
		return this.leadTime;
	}
	
	public String toString(){
		return "Shipment: " + this.Id + ", Date: " + this.date + ", Size " + this.size + ", LeadTime: " + this.leadTime + ", Order: " + this.order;
	}

}
