package agents;

import java.util.ArrayList;

import cern.jet.random.AbstractDistribution;
import artefacts.DemandData;
import artefacts.Order;
import artefacts.OrderReq;
import artefacts.Shipment;
import demandPattern.DemandPattern;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import repast.simphony.random.RandomHelper;
import setups.Setup;

public class Customer extends Node{
	
	private DemandPattern pattern;
	private DemandData demandData;
	
	public Customer(Setup setup, DemandPattern pattern){
		super(setup, 1);
		this.pattern = pattern;
		this.demandData = new DemandData();
	}
	
	public void initNode(){		
	}
	
	public ArrayList<Double> getSampleData(int size){
		ArrayList<Double> data = new ArrayList<Double>();
		for(int i=0; i<size; i++){
			data.add(pattern.getNextDouble());
		}
		return data;
	}
	
	
	@ScheduledMethod(start = 1, interval = 1, priority = 7)
	public void placeOrder(){
		//System.out.println("placeOrderCustomer");
		double size = pattern.getNextDouble();
		//System.out.println("Customer Order: " + size);
		ArrayList<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(upstrLinks.get(0),(int)RepastEssentials.GetTickCount(), size));
		upstrLinks.get(0).putOrders(orderList);
		this.demandData.handDemandData((int)RepastEssentials.GetTickCount(), size);
	}
	
	public double getVarianceOrders(){
		return this.demandData.getVariance();
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

	@Override
	public DemandData searchCustomerDemandData() {
		// TODO Auto-generated method stub
		return null;
	}

}
