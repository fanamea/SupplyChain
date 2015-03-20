package agents;

import java.util.ArrayList;

import artefacts.Order;
import artefacts.Shipment;
import distribution.Distribution;
import distribution.DistributionNormal;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Customer extends Node{
	
	private Distribution demandDistr;
	
	public Customer(){
		super(1);
		demandDistr = new DistributionNormal(5, 1);
	}
	
	public void initNode(){		
	}
	
	
	public void setDemandDistr(Distribution distr){
		this.demandDistr = distr;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 7)
	public void placeOrder(){
		//System.out.println("placeOrderCustomer");
		int size = (int)Math.round(demandDistr.computeRandom());
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
