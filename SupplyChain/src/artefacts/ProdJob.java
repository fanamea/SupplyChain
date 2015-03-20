package artefacts;

public class ProdJob {
	
private static int count;
	
	private int Id;
	private int date;
	private double size;
	private int leadTime;
	private Order order;
	
	public ProdJob(int d, double s, int lt){
		this.Id = count++;
		this.date = d;
		this.size = s;
		this.leadTime = lt;
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
