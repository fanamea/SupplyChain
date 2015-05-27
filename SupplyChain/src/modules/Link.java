package modules;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;
import agents.Business;
import agents.Node;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.random.RandomHelper;

public class Link {
	
	private static int count;
	
	private int Id;
	private Node upstrNode;
	private Node downstrNode;
	
	private Material material;	
	private double fixCost;
	private AbstractDistribution  distrDuration;
	
	private ArrayList<Order> orderHistory;
	private CopyOnWriteArrayList<Order> orderPipeLine;
	private ArrayList<Shipment> shipmentHistory;
	private CopyOnWriteArrayList<Shipment> shipmentPipeLine;
	
	public Link(Node up, Node down){
		this.Id = count++;
		this.upstrNode = up;
		this.downstrNode = down;
		upstrNode.addDownstrLink(this);
		downstrNode.addUpstrLink(this);
		this.material = upstrNode.getProduct();
		orderHistory = new ArrayList<Order>();
		orderPipeLine = new CopyOnWriteArrayList<Order>();
		shipmentHistory = new ArrayList<Shipment>();
		shipmentPipeLine = new CopyOnWriteArrayList<Shipment>();
	}
	
	/**
	 * 
	 * @return Liste von Shipments, die in diesem Tick ankommen
	 */
	public ArrayList<Shipment> getArrivingShipments(){
		ArrayList<Shipment> ret = new ArrayList<Shipment>();
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Shipment shipment : shipmentPipeLine){
			if(shipment.getDate() + shipment.getDuration() <= currentTick){
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
	
	public int genDuration(){
		return this.distrDuration.nextInt();
	}
	
	public void setDistrDuration(AbstractDistribution distr){
		this.distrDuration = distr;
	}
	
	/**
	 * Downstream Business kann hiermit Orders in den Link geben, das Upstream Business fetcht diese dann später.
	 * Außerdem werden die histories gepflegt.
	 * @param orderList
	 */
	public void putOrders(ArrayList<Order> orderList){
		for(Order order : orderList){
			putOrder(order);
		}
		
	}
	
	public void putOrder(Order order){
		this.orderPipeLine.add(order);
		this.orderHistory.add(order);
	}
	
	public ArrayList<Order> fetchOrders(){
		ArrayList<Order> copy = new ArrayList<Order>();
		copy.addAll(this.orderPipeLine);
		orderPipeLine.clear();
		//System.out.println("Link PipeLine size: " + orderPipeLine.size());
		return copy;
	}
	
	
	public ArrayList<Order> getOrderHistory(){
		return this.orderHistory;
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
	
	public int getId(){
		return this.Id;
	}
	
	public Material getMaterial(){
		return this.material;
	}
	
	public void setMaterial(Material material){
		this.material = material;
	}
	
	public double getFixCost(){
		return this.fixCost;
	}
	
	public void setFixCost(double fixCost){
		this.fixCost = fixCost;
	}

	public String getAmountInformation(){
		String string = "";
		string += "Link: " + getId() + ", Up " + this.upstrNode.getId() + ", Down " + this.downstrNode.getId() + "\n"
				+ "   Shipments:";
		for(Shipment shipment : shipmentHistory){
			string += "   " + shipment.getSize() + ", ";
		}
		return string;
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
		return string;
	}
	

}
