package artefacts;

public class ProdJob {
	
private static int count;
	
	private int Id;
	private int date;
	private double size;
	private int leadTime;
	private ProdRequest prodRequest;
	
	public ProdJob(int d, double s, int lt, ProdRequest pReq){
		this.Id = count++;
		this.date = d;
		this.size = s;
		this.leadTime = lt;
		this.prodRequest = pReq;
	}
	
	public int getDate(){
		return this.date;
	}
	
	public double getSize(){
		return this.size;
	}
	
	public ProdRequest getProdRequest(){
		return this.prodRequest;
	}
	
	public int getLeadTime(){
		return this.leadTime;
	}
	
	public String toString(){
		return "ProdJob: ID-" + this.Id + ", Date-" + this.date + ", Size-" + this.size + ", LeadTime-" + this.leadTime + ", ProdRequest-" + this.prodRequest;
	}

}
