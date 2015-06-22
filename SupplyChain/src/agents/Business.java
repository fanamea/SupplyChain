package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import net.sourceforge.openforecast.DataSet;
import modules.*;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.DemandData;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import repast.simphony.data2.NonAggregateDataSource;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import setups.Setup;
import modules.Link;

public abstract class Business extends Node{	
	
	protected int planningPeriod;
	
	protected DeliveryModule deliveryModule;
	protected OrderOpsModule orderOpsModule;
	protected InventoryOpsModule inventoryOpsModule;	
	protected ForecastModule forecastModule;
	protected InventoryPlanModule inventoryPlanModule;
	protected OrderPlanModule orderPlanModule;
	protected PlanningMethods planningTechniques;
	protected InformationModule informationModule;
	
	public Business(Setup setup, int tier){
		super(setup, tier);
	}
	
	public abstract void initNode();	
	public abstract void plan();	
	public abstract void prepareTick();	
	public abstract void receiveShipments();	
	public abstract void placeOrders();	
	public abstract void fetchOrders();	
	public abstract void dispatchShipments();
	
	public abstract OrderOpsModule getOrderOpsModule();
	public abstract DeliveryModule getDeliveryModule();
	public abstract InventoryOpsModule getInventoryOpsModule();
	public abstract InventoryPlanModule getInventoryPlanModule();
	public abstract PlanningMethods getPlanningMethods();
	public abstract ForecastModule getForecastModule();
	public abstract OrderPlanModule getOrderPlanModule();
	public abstract ProductionOpsModule getProductionOpsModule();
	public abstract InformationModule getInformationModule();
	
	public int getPlanningPeriod(){
		return this.planningPeriod;
	}
	
	//Setup
	
	//Parameter
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
	
	//Information Sharing
	
	public DemandData searchCustomerDemandData() {
		return this.informationModule.searchCustomerDemandData();
	}

	public void setCustomerDemandData() {
		this.informationModule.setCustomerDemandData();		
	}
	
	//Analysis
	public double getBWEMeasure(){
		Customer customer = setup.getCustomers().get(0);
		////System.out.println("BWE: " + this.informationModule.getVarianceOrders() + ", " + customer.getVarianceOrders());
		return this.informationModule.getVarianceOrders()/customer.getVarianceOrders();
	}
	
	public double getVarianceOrders(){
		return this.informationModule.getVarianceOrders();
	}
	
	public double getTrustLevel(){
		return this.informationModule.getTrustLevel();
	}
	
	public double getOrderAmount(){
		return this.informationModule.getOrderAmount();
	}
	
	public double getInventoryLevel(){
		return this.inventoryOpsModule.getInventoryLevel(this.product);
	}
	
	public double getBacklog(){
		return this.deliveryModule.getBacklog();
	}
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		upstrLinks.add(b);
	}
	
	public void setPlanningPeriod(int period){
		this.planningPeriod = period;
	}
	
	
}
