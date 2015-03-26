package artefacts;

import java.util.ArrayList;

import modules.Link;

public class Order {
	
	//TODO Teillieferungen
	
	private static int count;
	
	private int Id;
	private Link link;
	private int date;
	private double size;
	private double sent;
	private double arrived;
	ArrayList<Shipment> shipments;
	
	public Order(Link link, int d, double s){
		this.Id = count++;
		this.link = link;
		this.date = d;
		this.size = s;
		this.sent = 0;
		this.arrived = 0;
		this.shipments = new ArrayList<Shipment>();
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
	
	public void addShipment(Shipment shipment){
		this.shipments.add(shipment);
	}
	
	public ArrayList<Shipment> getShipments(){
		return this.shipments;
	}
	
	public String toString(){
		return "Order: " + this.Id + ", Date: " + this.date + ", Size: " + this.size;
	}

}
