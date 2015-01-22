package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
	private CopyOnWriteArrayList<Order> orderPipeLine;
	private ArrayList<Shipment> shipmentHistory;
	private CopyOnWriteArrayList<Shipment> shipmentPipeLine;
	private HashMap<Integer, Double> orderDueList;
	private DescriptiveStatistics leadTimeData;
	
	
	public Link(Node up, Node down){
		this.Id = count++;
		this.upstrNode = up;
		this.downstrNode = down;
		upstrNode.addDownstrLink(this);
		downstrNode.addUpstrLink(this);
		materialFactor = 1;
		orderHistory = new ArrayList<Order>();
		orderAmountHistory = new ArrayList<Double>();
		orderPipeLine = new CopyOnWriteArrayList<Order>();
		shipmentHistory = new ArrayList<Shipment>();
		shipmentPipeLine = new CopyOnWriteArrayList<Shipment>();
		orderDueList = new HashMap<Integer, Double>();
		leadTimeData = new DescriptiveStatistics();
	}
	
	/**
	 * 
	 * @return Liste von Shipments, die in diesem Tick ankommen
	 */
	public ArrayList<Shipment> getArrivingShipments(){
		ArrayList<Shipment> ret = new ArrayList<Shipment>();
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Shipment shipment : shipmentPipeLine){
			if(shipment.getDate() + shipment.getDuration() == currentTick){
				ret.add(shipment);
				shipmentPipeLine.remove(shipment);
				maintainLeadTimeData(shipment);				
			}
		}
		return ret;
	}
	
	/**
	 * Bei Teillieferungen gewichteter Durchschnitt
	 * @param shipment
	 */
	public void maintainLeadTimeData(Shipment shipment){
		Order order = shipment.getOrder();
		double sum = 0;
		if(order.isShipped()){
			ArrayList<Shipment> shipments = order.getShipments();
			for(Shipment shipm : shipments){
				double leadTime = shipm.getArriving()-order.getDate();
				double weight = shipm.getSize()/order.getSize();
				sum += weight*leadTime;
			}
		}
		this.leadTimeData.addValue(sum);
	}
	
	public void induceShipment(Shipment shipment){
		shipmentHistory.add(shipment);
		shipmentPipeLine.add(shipment);
	}
	
	//TODO: Leadtime generieren
	public int genDuration(){
		return 2;
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
	
	
	public double getAvgLeadTime(){
		//System.out.println("getAvgLeadTime: " + leadTimeData.getMean() + "Link ID: " + getId() + ", Data: " +  this.leadTimeData);
		return this.leadTimeData.getMean();
	}
	
	public double getSDLeadTime(){
		return this.leadTimeData.getStandardDeviation();
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

	public String getAmountInformation(){
		String string = "";
		string += "Link: " + getId() + ", Up " + this.upstrNode.getId() + ", Down " + this.downstrNode.getId() + "\n"
				+ "   OrderAmount History: " + orderAmountHistory + "\n"
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
