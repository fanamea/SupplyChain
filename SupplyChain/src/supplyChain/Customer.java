package supplyChain;

import java.util.ArrayList;

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
	
	
	public void setDemandDistr(Distribution distr){
		this.demandDistr = distr;
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 8)
	public void placeOrder(){
		//System.out.println("placeOrderCustomer");
		int size = (int)Math.round(demandDistr.computeRandom());
		upstrLinks.get(0).addOrder(new Order((int)RepastEssentials.GetTickCount(), size));
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
