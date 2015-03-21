package agents;

import java.util.ArrayList;

import cern.jet.random.AbstractDistribution;
import artefacts.Order;
import artefacts.Shipment;
import distribution.Distribution;
import distribution.DistributionNormal;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.random.RandomHelper;

public class Customer extends Node{
	
	private AbstractDistribution distribution;
	
	public Customer(){
		super(1);
		distribution = RandomHelper.createNormal(5, 1);
	}
	
	public void initNode(){		
	}
	
	public ArrayList<Double> getSampleData(int size){
		ArrayList<Double> data = new ArrayList<Double>();
		for(int i=0; i<size; i++){
			data.add(distribution.nextDouble());
		}
		return data;
	}
	
	
	@ScheduledMethod(start = 1, interval = 1, priority = 7)
	public void placeOrder(){
		//System.out.println("placeOrderCustomer");
		double size = distribution.nextDouble();
		ArrayList<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(upstrLinks.get(0),(int)RepastEssentials.GetTickCount(), size));
		upstrLinks.get(0).putOrders(orderList);
	}
	
	public void efectShipment(Shipment shipment){		
	}
	
	public void receive(Shipment shipment){		
	}
	
	public boolean isOrderShipable(Order order){
		return false;
	}
	
	public int getShipableAmount(Order order){
		return 0;
	}
	
	public String getInformationString(){
		String string = "Customer " + this.Id + "\n";
		return string;
	}

}
