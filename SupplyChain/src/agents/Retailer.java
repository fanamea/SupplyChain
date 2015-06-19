package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sourceforge.openforecast.DataSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import modules.*;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.DemandData;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import InventoryPolicies.InventoryPolicy;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import setups.Setup;
import modules.Link;

public class Retailer extends Business{
	
	
		
	public Retailer(Setup setup, int tier){
		super(setup, tier);
	}
	
	public void initNode(){	
		this.product = this.upstrLinks.get(0).getMaterial();
		//System.out.println("Tier: " + this.tier + ", Product: " + this.product);
		
		this.deliveryModule = new DeliveryModule(this);
		this.orderOpsModule= new OrderOpsModule(this);
		this.inventoryOpsModule= new InventoryOpsModule(this);
		this.forecastModule = new ForecastModule(this);
		this.inventoryPlanModule = new InventoryPlanModule(this);		
		this.orderPlanModule = new OrderPlanModule(this);
		this.planningTechniques = new PlanningMethods();
		this.informationModule = new InformationModule(this);
			
		DemandPattern pattern = new NormalDistribution(10.0, 1.0);
		for(int i=-100; i<1; i++){
			this.informationModule.addIntDemandData(i, pattern.getNextDouble());
		}
		
	}	
	
	@ScheduledMethod(start=1, interval = 1, priority = 10)
	public void prepareTick(){
		inventoryOpsModule.prepareTick();
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 9)
	public void plan(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		informationModule.combineDemandData();
		if(currentTick % planningPeriod == 0){
			inventoryPlanModule.recalcPolicyParams();
			//System.out.println("Planning Period:" + inventoryPlanModule.getPlanString());
		}
		
	}	
	
	@ScheduledMethod(start=1, interval = 1, priority = 8)
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		for(Link link : this.upstrLinks){
			shipments = link.getArrivingShipments();
			orderOpsModule.processInShipments(shipments);
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 7)
	public void checkOrders(){
		inventoryPlanModule.placeOrderReqs();
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 6)
	public void placeOrders(){
		orderOpsModule.placeOrders();
		}
	
	@ScheduledMethod(start=1, interval = 1, priority = 4)
	public void fetchOrders(){
		ArrayList<Order> newOrders = new ArrayList<Order>();
		for(Link link : this.downstrLinks){
			newOrders.addAll(link.fetchOrders());
		}
		//System.out.println("newOrders.size: " + newOrders.size());
		deliveryModule.processOrders(newOrders);
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 2)
	public void dispatchShipments(){
		this.deliveryModule.dispatchShipments();
	}	
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		this.inventoryOpsModule.setInfinite(false);
		upstrLinks.add(b);
	}
	
	public DeliveryModule getDeliveryModule(){
		return this.deliveryModule;
	}
	
	public InventoryPlanModule getInventoryPlanModule(){
		return this.inventoryPlanModule;
	}
	
	public ForecastModule getForecastModule(){
		return this.forecastModule;
	}
	
	public OrderPlanModule getOrderPlanModule(){
		return this.orderPlanModule;
	}
	
	public OrderOpsModule getOrderOpsModule(){
		return this.orderOpsModule;
	}
	
	public PlanningMethods getPlanningMethods(){
		return this.planningTechniques;
	}
	
	public InventoryOpsModule getInventoryOpsModule(){
		return this.inventoryOpsModule;
	}	
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "\n";
		string += "   InventoryAgent: \n" 
				+ inventoryOpsModule.getInformationString();
		return string;
	}

	@Override
	public ProductionOpsModule getProductionOpsModule() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void handExtDemandData(DemandData demandData) {
		this.forecastModule.setDemandData(demandData);		
	}
	
	/*
	 * ----------------- Analysis -------------------------------
	 */
	
	
	/*
	 * ----------------- Parameter Setup ------------------------
	 */
	
	@Override
	public void setHoldingCost(double holdingCost) {
		this.inventoryPlanModule.setHoldingCosts(holdingCost);		
	}
	
	public void setServiceLevel(double serviceLevel){
		this.inventoryPlanModule.setServiceLevels(serviceLevel);
	}
	
	public void setInventoryPolicy(InvPolicies policy){
		this.inventoryPlanModule.setInventoryPolicy(policy);
	}
	
	public void setTrustLevel(double trustLevel){
		this.informationModule.setTrustLevel(trustLevel);
	}
	
	@Override
	public InformationModule getInformationModule() {
		return this.informationModule;
	}

	@Override
	public DemandData searchCustomerDemandData() {
		return this.informationModule.searchCustomerDemandData();
	}

	@Override
	public void setCustomerDemandData() {
		this.informationModule.setCustomerDemandData();		
	}

	@Override
	public Class<?> getDataType() {
		
		return Double.class;
	}

	@Override
	public Class<?> getSourceType() {
		return Retailer.class;
	}

	@Override
	public Object get(Object obj) {
		return informationModule.getOrderVariance();
	}


}
