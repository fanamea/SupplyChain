package artefacts;

import java.util.ArrayList;

import modules.Link;

public class ProdRequest {
	
	private static int count;
	
	private int Id;
	private Link link;
	private int date;
	private double size;
	private double sent;
	private double arrived;
	ArrayList<ProdJob> jobs;
	
	public ProdRequest(int d, double s){
		this.Id = count++;
		this.date = d;
		this.size = s;
		this.sent = 0;
		this.jobs = new ArrayList<ProdJob>();
	}
	
	public Integer getDate(){
		return this.date;
	}
	
	public Link getLink(){
		return this.link;
	}
	
	public double getSize(){
		return this.size;
	}
	
	public void incrSent(double i){
		sent += i;
	}
	
	public void incrArrived(double i){
		arrived += i;
	}
	
	public boolean isSent(){
		return size==sent;
	}
	
	public boolean hasArrived(){
		return size==arrived;
	}
	
	public double getShortageSent(){
		return size-sent;
	}
	
	public double getShortageArrived(){
		return size-arrived;
	}
	
	public void addProdJob(ProdJob job){
		this.jobs.add(job);
	}
	
	public ArrayList<ProdJob> getJobs(){
		return this.jobs;
	}
	
	public String toString(){
		return "ProdRequest: " + this.Id + ", Date: " + this.date + ", Size: " + this.size;
	}

}
