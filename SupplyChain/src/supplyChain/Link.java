package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Link {
	
	private static int count;
	
	private int Id;
	private Node upstrNode;
	private Node downstrNode;
	private Inventory inventory;
	private double materialFactor;
	private double transportationCost;
	private double shortageCost;
	private ArrayList<Order> orderList;
	private ArrayList<Shipment> shipmentList;
	
	
	public Link(Node up, Node down){
		this.Id = count++;
		this.upstrNode = up;
		this.downstrNode = down;
		upstrNode.addDownstrLink(this);
		downstrNode.addUpstrLink(this);
		orderList = new ArrayList<Order>();
		shipmentList = new ArrayList<Shipment>();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 9)
	public void executeShipments(){
		//System.out.println("executeShipments");
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Shipment shipment : shipmentList){
			if(shipment.getDate() + shipment.getLeadTime() == currentTick){
				this.inventory.incrInventory(shipment.getSize());
				//System.out.println("Shipment received");
			}
		}
	}
	
	public void induceShipment(Order order){
		int currentTick = (int)RepastEssentials.GetTickCount();
		int shipableAmount = upstrNode.getShipableAmount(order);
		Shipment shipment = new Shipment(currentTick, shipableAmount, genLeadTime(), order);
		order.incrShipped(shipableAmount);
		upstrNode.efectShipment(shipment);
		shipmentList.add(shipment);
	}
	
	public void executeOrders(){
		//System.out.println("executeOrders");
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Order order : orderList){
			if(order.getDate() <= currentTick && !order.isShipped()){
				induceShipment(order);
				//System.out.println("Shipment induced");
			}
		}
	}
	
	public void addOrder(Order order){
		orderList.add(order);
	}
	
	public ArrayList<Order> getCurrentOrders(){
		ArrayList<Order> currentOrders = new ArrayList<Order>();
		for(Order order : orderList){
			if(order.getDate() == RepastEssentials.GetTickCount()){
				currentOrders.add(order);
			}
		}
		return currentOrders;
	}
	
	//TODO: Leadtime generieren
	public int genLeadTime(){
		return 1;
	}
	
	
	public void addShipment(Shipment shipment){
		shipmentList.add(shipment);
	}
	
	public ArrayList<Order> getOrderList(){
		return this.orderList;
	}
	
	public ArrayList<Shipment> getShipmentList(){
		return this.shipmentList;
	}
	
	public Node getDownstrNode(){
		return this.downstrNode;
	}
	
	public Node getUpstrNode(){
		return this.upstrNode;
	}
	
	public Inventory getInventory(){
		return this.inventory;
	}
	
	public double getMaterialFactor(){
		return this.materialFactor;
	}
	
	public int getId(){
		return this.Id;
	}
	
	private boolean isOrderShipable(Order order){
		if(upstrNode.isOrderShipable(order))
			return true;
		else return false;
	}
	
	public String getInformationString(){
		String string = "";
		string += "Link: Up " + this.upstrNode.getId() + ", Down " + this.downstrNode.getId() + "\n"
				+ "   Orderlist:\n";
		for(Order order : orderList){
			string += "      " + order.toString() + "\n";
		}
		string += "   ShipmentList:\n";
		for(Shipment shipment : shipmentList){
			string += "      " + shipment.toString() + "\n";
		}
		string += "\n";
		return string;
	}
	

}
