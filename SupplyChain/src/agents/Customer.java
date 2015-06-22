package agents;

import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

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
	private DescriptiveStatistics received;
	
	public Customer(Setup setup, DemandPattern pattern){
		super(setup, 1);
		this.pattern = pattern;
		this.demandData = new DemandData();
		this.received = new DescriptiveStatistics();
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
	
	
	@ScheduledMethod(start = 1, interval = 1, priority = 10)
	public void placeOrder(){
		//System.out.println("placeOrderCustomer");
		double size = pattern.getNextDouble();
		//System.out.println("Customer Order: " + size);
		ArrayList<Order> orderList = new ArrayList<Order>();
		orderList.add(new Order(upstrLinks.get(0),(int)RepastEssentials.GetTickCount(), size));
		upstrLinks.get(0).putOrders(orderList);
		this.demandData.handDemandData((int)RepastEssentials.GetTickCount(), size);
	}
	
	public double getSampleOrder(){
		return this.pattern.getNextDouble();
	}
	
	public double getVarianceOrders(){
		return this.demandData.getVariance();
	}
	
	public void efectShipment(Shipment shipment){		
	}
	
	@ScheduledMethod(start=1, interval=1, priority=10)
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		shipments = this.upstrLinks.get(0).getArrivingShipments();
		for(Shipment shipment : shipments){
			this.received.addValue(shipment.getSize());
		}
	}
	
	public boolean isOrderShipable(Order order){
		return false;
	}
	
	public int getShipableAmount(Order order){
		return 0;
	}
	
	public double getSumOrders(){
		return this.demandData.getDemandStats().getSum();
	}
	
	public double getSumReceived(){
		return this.received.getSum();
	}
	
	public double getBacklog(){
		return getSumOrders()-getSumReceived();
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
