package agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
		////System.out.println("Tier: " + this.tier + ", Product: " + this.product);
		
		this.deliveryModule = new DeliveryModule(this);
		this.orderOpsModule= new OrderOpsModule(this);
		this.inventoryOpsModule= new InventoryOpsModule(this);
		this.forecastModule = new ForecastModule(this);
		this.inventoryPlanModule = new InventoryPlanModule(this);		
		this.orderPlanModule = new OrderPlanModule(this);
		this.planningTechniques = new PlanningMethods();
		this.informationModule = new InformationModule(this);
		
		this.customer = this.searchCustomer();
		for(int i=-100; i<1; i++){
			this.informationModule.addIntDemandData(i, customer.getSampleOrder());
			Link link = this.getUpstrLinks().get(0);
			this.informationModule.putLeadTimeData(link, link.genDuration());
		}
		
	}
		
	public void prepareTick(){
		inventoryOpsModule.prepareTick();
	}
	
	public void planFirstPeriods(){
		HashMap<Material, Double> initialInventory = new HashMap<Material, Double>();
		initialInventory.put(this.product, this.initialInventory);
		inventoryOpsModule.storeMaterials(initialInventory);
		
		informationModule.combineDemandData();
		inventoryPlanModule.recalcPolicyParams();			
	}
	
	public void placeOrders(){
		inventoryPlanModule.placeOrderReqs();
		orderOpsModule.placeOrders();
	}
	
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		for(Link link : this.upstrLinks){
			shipments.addAll(link.getArrivingShipmentsDown());			
		}
		for(Link link : this.downstrLinks){
			shipments.addAll(link.getArrivingShipmentsUp());
		}
		orderOpsModule.processInShipments(shipments);
	}
	
	public void fetchOrders(){
		ArrayList<Order> newOrders = new ArrayList<Order>();
		for(Link link : this.downstrLinks){
			newOrders.addAll(link.fetchOrders());
		}
		////System.out.println("newOrders.size: " + newOrders.size());
		deliveryModule.processOrders(newOrders);
	}
	
	public void dispatchShipments(){
		this.deliveryModule.dispatchOrders();
	}
	
	public void plan(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		
		if(currentTick % planningPeriod == 0){
			informationModule.combineDemandData();
			inventoryPlanModule.recalcPolicyParams();
			////System.out.println("Planning Period:" + inventoryPlanModule.getPlanString());
		}
		
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
	
	public InformationModule getInformationModule() {
		return this.informationModule;
	}
	
	public ProductionOpsModule getProductionOpsModule() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public ProductionPlanModule getProductionPlanModule() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "\n";
		string += "   InventoryAgent: \n" 
				+ inventoryOpsModule.getInformationString();
		return string;
	}
	
	
	/*
	 * ----------------- Parameter Setup ------------------------
	 */
	
	
	public void setInventoryPolicy(InventoryPolicy policy){
		policy.setBiz(this);
		this.inventoryPlanModule.setInventoryPolicy(policy);
	}
	
	/*
	 * -----------------Analysis------------------
	 */
	
	
	public double getOUTLevel(){
		Inventory inventory = inventoryOpsModule.getInventories().get(this.product);
		return inventory.getPolicy().getOUTLevel();
	}
	
}
