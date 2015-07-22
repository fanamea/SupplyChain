package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import modules.*;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.DemandData;
import artefacts.Material;
import artefacts.Order;
import artefacts.OrderComparator;
import artefacts.Shipment;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import setups.Setup;
import modules.Link;

public class MaterialSource extends Node{
	
	private Material product;
	private double capacity;
	private CopyOnWriteArrayList<Order> orderPipeLine;
	
	public MaterialSource(Setup setup, int tier){
		super(setup, tier);
		this.product = new Material("");
		this.orderPipeLine = new CopyOnWriteArrayList<Order>();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 4)
	public void shipOrders(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		double shipped = 0;
		////System.out.println("CurrentTick: " + currentTick + ", ORDERPIPELINE: " + orderPipeLine);
		Link link = this.getDownstrLinks().get(0);
		ArrayList<Order> newOrders = new ArrayList<Order>();
		newOrders.addAll(link.fetchOrders());
		orderPipeLine.addAll(newOrders);
		ArrayList<Order> temp = new ArrayList<Order>(orderPipeLine);		
		Collections.sort(temp, new OrderComparator());
		orderPipeLine.clear();
		orderPipeLine.addAll(temp);
		
		////System.out.println("CurrentTick: " + currentTick + ", ORDERPIPELINE: " + orderPipeLine);
		
		while(shipped<capacity && !orderPipeLine.isEmpty()){
			////System.out.println("Pipeline size: " + orderPipeLine.size() + ", isEmpty: " + orderPipeLine.isEmpty());
			Order order = orderPipeLine.get(0);
			int duration = link.genDuration();
			double amount = Math.min(capacity-shipped, order.getSize());
			Shipment newShipment = new Shipment(link, currentTick, amount, duration, order);
			//System.out.println("SHIPMENT FROM SOURCE: " + newShipment);
			order.addShipment(newShipment);
			order.incrSent(amount);
			////System.out.println("Shortage sent: " + order.getShortageSent() + ", isSent(): " + order.isSent() + ", orderPipeLine.size: " + orderPipeLine.size());
			if(order.isSent()){
				//orderPipeLine.remove(order);
				orderPipeLine.remove(0);
			}
			////System.out.println("OrderPipeLine.size after remove: " + orderPipeLine.size());
			link.induceShipmentDown(newShipment);
			shipped += amount;
		}
	}
	
	public Material getProduct(){
		return this.product;
	}
	
	public void setCapacity(double capacity){
		this.capacity = capacity;
	}
	
	@Override
	public void initNode() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getInformationString() {
		String string = "";
		string += "OrderPipeLine: " + this.orderPipeLine;
		return string;
	}

	@Override
	public DemandData searchCustomerDemandData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getVarianceOrders() {
		// TODO Auto-generated method stub
		return 0;
	}	
	
	


}
