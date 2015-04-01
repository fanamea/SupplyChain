package artefacts;

import java.util.ArrayList;
import java.util.HashMap;

import modules.Link;

public class ProdRequest {
	
	private static int count;
	
	private int Id;
	private Link link;
	private int date;
	private double size;
	private HashMap<Material, Double> boM;
	private double sent;
	private double arrived;
	ArrayList<ProdJob> jobs;
	
	public ProdRequest(int d, double s, HashMap<Material, Double> boM){
		this.Id = count++;
		this.date = d;
		this.size = s;
		this.sent = 0;
		this.jobs = new ArrayList<ProdJob>();
		this.boM = boM;
		
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
		return size<=sent;
	}
	
	public boolean hasArrived(){
		return size<=arrived;
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
	
	public HashMap<Material, Double> getBoM(){
		return this.boM;
	}
	
	public String toString(){
		return "ProdRequest: ID-" + this.Id + ", Date-" + this.date + ", Size-" + this.size;
	}

}
