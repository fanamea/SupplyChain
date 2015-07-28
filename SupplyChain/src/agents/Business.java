package agents;

import java.util.ArrayList;
import java.util.HashMap;
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
import repast.simphony.data2.NonAggregateDataSource;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import setups.Setup;
import modules.Link;

public abstract class Business extends Node{	
	
	protected int planningPeriod;
	
	protected Customer customer;
	
	protected DeliveryModule deliveryModule;
	protected OrderOpsModule orderOpsModule;
	protected InventoryOpsModule inventoryOpsModule;	
	protected ForecastModule forecastModule;
	protected InventoryPlanModule inventoryPlanModule;
	protected OrderPlanModule orderPlanModule;
	protected PlanningMethods planningTechniques;
	protected InformationModule informationModule;
	
	protected double initialInventory;
	
	public Business(Setup setup, int tier){
		super(setup, tier);
		
		this.initialInventory = 0;
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
	public abstract ProductionPlanModule getProductionPlanModule();
	public abstract InformationModule getInformationModule();
	
	
	
	public void collectData(){
		double holdingCostProduct = 0;
		double holdingCostResources = 0;
		for(Material material : inventoryOpsModule.getInventories().keySet()){
			if(material==product){
				holdingCostProduct = this.inventoryOpsModule.getInventoryLevel(product);
			}
			else{
				holdingCostResources += inventoryOpsModule.getInventoryLevel(material);
			}
		}
		double backlog = deliveryModule.getBacklog();
		
		informationModule.addHoldingCostProduct(holdingCostProduct);
		informationModule.addHoldingCostResources(holdingCostResources);
		informationModule.addBacklog(backlog);
		
	}
	
	public int getPlanningPeriod(){
		return this.planningPeriod;
	}
	
	//Setup
	
	//-------------------------Parameter---------------------------
	
	public void setHoldingCost(double holdingCost) {
		this.inventoryPlanModule.setHoldingCosts(holdingCost);		
	}
	
	public void setServiceLevel(double serviceLevel){
		this.inventoryPlanModule.setServiceLevels(serviceLevel);
	}
	
	public void setTrustLevel(double trustLevel){
		this.informationModule.setTrustLevel(trustLevel);
	}
	
	public void setInitialInventory(double inventory){
		this.initialInventory = inventory;
	}
	
	public void setReturnsAllowed(boolean returns){
		this.inventoryPlanModule.setReturnsAllowed(returns);
	}
	
	//------------------------Information Sharing-------------------------
	
	public DemandData searchCustomerDemandData() {
		return this.informationModule.searchCustomerDemandData();
	}

	public void setCustomerDemandData() {
		this.informationModule.setCustomerDemandData();		
	}
	
	public void setInformationSharing(boolean b){
		this.informationModule.setInformationSharing(b);
	}
	
	
	//------------------------Analysis-------------------------------
	
	public double getBWE(){
		//System.out.println("BWE: " + this.informationModule.getVarianceOrders() + ", " + customer.getVarianceOrders());
		return this.informationModule.getVarianceOrders()/informationModule.getVarianceCustomerOrders();
	}
	
	public double getVarianceOrders(){
		return this.informationModule.getVarianceOrders();
	}
	
	public double getTrustLevel(){
		return this.informationModule.getTrustLevel();
	}
	
	public double getOrderAmountIn(){
		return this.informationModule.getOrderAmountIn();
	}
	
	public double getOrderAmountOut(){
		return this.informationModule.getOrderAmountOut();
	}
	
	public double getInventoryLevel(){
		return this.inventoryOpsModule.getInventoryLevel(this.product);
	}
	
	public double getBacklog(){
		return this.deliveryModule.getBacklog();
	}
	
	public double getSumBacklog(){
		return this.informationModule.getSumBacklog();
	}
	
	public double getMeanBacklog(){
		return this.informationModule.getMeanBacklog();
	}
	
	public double getArrivingShipments(){
		return this.informationModule.getArrivingShipments();
	}
	
	public double getHoldingCostProdudct(){
		return informationModule.getHoldingCostProduct();
	}
	
	public double getHoldingCostResources(){
		return informationModule.getHoldingCostResources();
	}
	
	public double getOrderCost(){
		return informationModule.getOrderCost();
	}
	
	public double getProductionCost(){
		return informationModule.getProductionCost();
	}
	
	public double getMeanLeadTimeShipments(){
		return this.informationModule.getMeanLeadTimeAll();
	}
	
	public double getProcessingOrders(){
		return informationModule.getOrdered();
	}
	
	public double getInventoryPosition(){
		return this.inventoryOpsModule.getInventoryPosition(this.product);
	}
	
	//------------------------------------------------------------
	
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
