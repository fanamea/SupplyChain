package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import net.sourceforge.openforecast.DataSet;
import lotSizingAlgorithms.LotSizingAlgorithm;
import modules.*;
import demandPattern.Constant;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.DemandData;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public class Manufacturer extends Business{	
	
	private DeliveryModule deliveryModule;
	private OrderOpsModule orderOpsModule;
	private InventoryOpsModule inventoryOpsModule;	
	private InventoryPlanModule inventoryPlanModule;
	private ForecastModule forecastModule;
	private OrderPlanModule orderPlanModule;
	private ProductionOpsModule productionOpsModule;
	private ProductionPlanModule productionPlanModule;	
	private PlanningMethods planningTechniques;	
	private InformationModule informationModule;
	
	public Manufacturer(int tier){
		super(tier);
		this.product = new Material("");
	}
	
	public void initNode(){		
		this.deliveryModule = new DeliveryModule(this);
		this.orderOpsModule= new OrderOpsModule(this);
		this.inventoryOpsModule= new InventoryOpsModule(this);
		this.inventoryPlanModule = new InventoryPlanModule(this);
		this.forecastModule = new ForecastModule(this);
		this.productionOpsModule = new ProductionOpsModule(this);
		this.productionPlanModule = new ProductionPlanModule(this);
		this.orderPlanModule = new OrderPlanModule(this);
		this.planningTechniques = new PlanningMethods();
		this.informationModule = new InformationModule(this);
		
		this.inventoryOpsModule.setUpResourceInventories();
		
		this.planningPeriod = 10;
		
		DemandPattern pattern = new Constant(10.0);
		for(int i=-100; i<1; i++){
			this.informationModule.addIntDemandData(i, pattern.getNextDouble());
		}
		
		inventoryPlanModule.handForecast(forecastModule.getForecast(1, 10));
		inventoryPlanModule.planEndProduct();
		productionPlanModule.planProduction();
		inventoryPlanModule.planMRP(productionPlanModule);			
		orderPlanModule.plan();
		
	}	
	
	@ScheduledMethod(start=1, interval = 1, priority = 10)
	public void prepareTick(){
		inventoryOpsModule.prepareTick();
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 9)
	public void plan(){
		//System.out.println("Biz: " + this.Id + ", plan");
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(currentTick % planningPeriod == 1){
			forecastModule.setDemandData(informationModule.fuseDemandData());
			inventoryPlanModule.handForecast(forecastModule.getForecast(currentTick+planningPeriod, currentTick+2*planningPeriod-1));
			inventoryPlanModule.planEndProduct();
			productionPlanModule.planProduction();
			inventoryPlanModule.planMRP(productionPlanModule);			
			orderPlanModule.plan();
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
	
	@ScheduledMethod(start=1, interval=1, priority = 7)
	public void produce(){
		//System.out.println("Biz: " + this.Id + ", produce");
		productionOpsModule.startProdJobs();
		inventoryOpsModule.storeMaterials(productionOpsModule.getArrivingProduction());		
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 6)
	public void placeOrders(){
		orderOpsModule.placeOrders();
		}
	
	@ScheduledMethod(start=1, interval = 1, priority = 4)
	public void fetchOrders(){
		//System.out.println("Biz: " + this.Id + ", fetchOrders");
		ArrayList<Order> newOrders = new ArrayList<Order>();
		for(Link link : this.downstrLinks){
			newOrders.addAll(link.fetchOrders());
		}
		deliveryModule.processOrders(newOrders);
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 9.5)
	public void dispatchShipments(){
		this.deliveryModule.dispatchShipments();
	}
	
	public void setProduct(Material material){
		this.product = material;
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
	
	public InventoryOpsModule getInventoryOpsModule(){
		return this.inventoryOpsModule;
	}
	
	public InventoryPlanModule getInventoryPlanModule(){
		return this.inventoryPlanModule;
	}
	
	public OrderOpsModule getOrderOpsModule(){
		return this.orderOpsModule;
	}
	
	public ForecastModule getForecastModule(){
		return this.forecastModule;
	}
	
	public ForecastModule getForecastAgent(){
		return this.forecastModule;
	}
	
	
	public ProductionOpsModule getProductionAgent(){
		return this.productionOpsModule;
	}
	
	public OrderPlanModule getOrderPlanAgent(){
		return this.orderPlanModule;
	}
	
	public PlanningMethods getPlanningMethods(){
		return this.planningTechniques;
	}
	
	public ProductionPlanModule getProductionPlanModule(){
		return this.productionPlanModule;
	}
	
	public OrderPlanModule getOrderPlanModule() {
		return this.orderPlanModule;
	}
	
	public ProductionOpsModule getProductionOpsModule(){
		return this.productionOpsModule;
	}
	
	public double getOrderVariance(){
		return this.informationModule.getSDinternDemand();
	}
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "\n";
		string += "   OrderOpsModule: \n"
				+ orderOpsModule.getInformationString() + "\n";
		string += "   ProductionOpsMoule: \n" 
				+ productionOpsModule.getInformationString() + "\n";
		string += "   InventoryOpsModule: \n" 
				+ inventoryOpsModule.getInformationString() + "\n";
		string += "    DeliveryModule: \n"
				+ deliveryModule.getInformationString() + "\n";
		
		return string;
	}

	@Override
	public void handExtDemandData(DemandData demandData) {
		this.forecastModule.setDemandData(demandData);		
	}
	
	/*
	 * ---------------Parameter Setup-------------------------------
	 */
	
	//Inventory

	@Override
	public void setHoldingCost(double holdingCost) {
		this.inventoryPlanModule.setHoldingCosts(holdingCost);		
	}
	
	public void setServiceLevel(double serviceLevel){
		this.inventoryPlanModule.setServiceLevels(serviceLevel);
	}
	
	//Production
	
	public void setProductionTime(int productionTime){
		this.productionPlanModule.setProductionTime(productionTime);
	}
	
	public void setProductionCapacity(double capacity){
		this.productionPlanModule.setProductionCapacity(capacity);
	}
	
	public void setBillOfMaterial(HashMap<Material, Double> boM){
		this.productionPlanModule.setBillOfMaterial(boM);
	}
	
	public void setSetUpCost(double setUpCost){
		this.productionPlanModule.setSetUpCost(setUpCost);
	}
	
	public void setLotSizingAlgorithm(LotSizingAlgorithm lotSizingAlgo){
		this.productionPlanModule.setLotSizingAlgorithm(lotSizingAlgo);
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
	
}
