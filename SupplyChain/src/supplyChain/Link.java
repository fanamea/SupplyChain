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
	private double materialFactor;
	private double transportationCost;
	private double shortageCost;
	private ArrayList<Order> orderHistory;
	private ArrayList<Double> orderAmountHistory;
	private ArrayList<Order> orderPipeLine;
	private ArrayList<Shipment> shipmentHistory;
	private ArrayList<Shipment> shipmentPipeLine;
	private HashMap<Integer, Double> orderDueList;
	
	
	public Link(Node up, Node down){
		this.Id = count++;
		this.upstrNode = up;
		this.downstrNode = down;
		upstrNode.addDownstrLink(this);
		downstrNode.addUpstrLink(this);
		orderHistory = new ArrayList<Order>();
		orderAmountHistory = new ArrayList<Double>();
		orderPipeLine = new ArrayList<Order>();
		shipmentHistory = new ArrayList<Shipment>();
		shipmentPipeLine = new ArrayList<Shipment>();
		orderDueList = new HashMap<Integer, Double>();
	}
	
	/**
	 * 
	 * @return Liste von Shipments, die in diesem Tick ankommen
	 */
	public ArrayList<Shipment> getArrivingShipments(){
		ArrayList<Shipment> ret = new ArrayList<Shipment>();
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Shipment shipment : shipmentPipeLine){
			if(shipment.getDate() + shipment.getLeadTime() == currentTick){
				ret.add(shipment);
				shipmentPipeLine.remove(shipment);
			}
		}
		return ret;
	}
	
	public void induceShipment(Shipment shipment){
		shipmentHistory.add(shipment);
		shipmentPipeLine.add(shipment);
	}
	
	//TODO: Leadtime generieren
	public int genLeadTime(){
		return 1;
	}
	
	/**
	 * TODO: Histories werden momentan nur für einelementige orderList gepflegt.
	 * Downstream Business kann hiermit Orders in den Link geben, das Upstream Business fetcht diese dann später.
	 * Außerdem werden die histories gepflegt.
	 * @param orderList
	 */
	public void putOrders(ArrayList<Order> orderList){
		int currentTick = (int)RepastEssentials.GetTickCount();
		this.orderPipeLine.addAll(orderList);
		//Histories pflegen TODO: Wird nur für eine Order in der orderList gepflegt
		this.orderHistory.addAll(orderList);
		for(int i = orderAmountHistory.size(); i<currentTick; i++){
			orderAmountHistory.add(i, 0.0);
		}
		orderAmountHistory.add(orderList.get(0).getSize());
		
	}
	
	public ArrayList<Order> fetchOrders(){
		ArrayList<Order> copy = new ArrayList<Order>();
		copy.addAll(this.orderPipeLine);
		orderPipeLine.clear();
		return copy;
	}
	
	public void addShipment(Shipment shipment){
		shipmentHistory.add(shipment);
	}
	
	public ArrayList<Order> getOrderHistory(){
		return this.orderHistory;
	}
	
	public ArrayList<Double> getOrderAmountHistory(){
		return this.orderAmountHistory;
	}
	
	public ArrayList<Shipment> getShipmentHistory(){
		return this.shipmentHistory;
	}
	
	public Node getDownstrNode(){
		return this.downstrNode;
	}
	
	public Node getUpstrNode(){
		return this.upstrNode;
	}
	
	public double getMaterialFactor(){
		return this.materialFactor;
	}
	
	public int getId(){
		return this.Id;
	}
	
	public HashMap<Integer, Double> getOrderDueList(){
		return this.orderDueList;
	}
	
	public double getOrderDueListEntry(int index){
		return this.orderDueList.get(index);
	}
	
	public void setOrderDueListEntry(int index, double amount){
		this.orderDueList.put(index, amount);
	}
	
	public String getInformationString(){
		String string = "";
		string += "Link: Up " + this.upstrNode.getId() + ", Down " + this.downstrNode.getId() + "\n"
				+ "   Orderlist:\n";
		for(Order order : orderHistory){
			string += "      " + order.toString() + "\n";
		}
		string += "   ShipmentList:\n";
		for(Shipment shipment : shipmentHistory){
			string += "      " + shipment.toString() + "\n";
		}
		string += "\n";
		return string;
	}
	

}
