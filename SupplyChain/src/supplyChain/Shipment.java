package supplyChain;

public class Shipment {
	
	private static int count;
	
	private int Id;
	private Link link;
	private int date;
	private double size;
	private int duration;
	private Order order;
	private int arriving;
	
	public Shipment(Link link, int d, double s, int dur, Order order){
		this.Id = count++;
		this.link = link;
		this.date = d;
		this.size = s;
		this.duration = dur;
		this.order = order;
		this.arriving = d + dur;
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
	
	public int getDuration(){
		return this.duration;
	}
	
	public int getArriving(){
		return this.arriving;
	}
	
	public String toString(){
		return "Shipment: " + this.Id + ", Date: " + this.date + ", Size " + this.size + ", LeadTime: " + this.duration + ", Order: " + this.order;
	}

}
