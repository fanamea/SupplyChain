package supplyChain;

public class ProdJob {
	
private static int count;
	
	private int Id;
	private int date;
	private int size;
	private int leadTime;
	private Order order;
	
	public ProdJob(int d, int s, int lt, Order order){
		this.Id = count++;
		this.date = d;
		this.size = s;
		this.leadTime = lt;
		this.order = order;
	}
	
	public int getDate(){
		return this.date;
	}
	
	public int getSize(){
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
