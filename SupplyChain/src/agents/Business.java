package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import supplyChain.Link;

public class Business extends Node{
	
	private DeliveryAgent deliveryAgent;
	private ForecastAgent forecastAgent;
	private InventoryOpsAgent inventoryOpsAgent;
	private InventoryPlanAgent inventoryPlanAgent;
	private ProductionAgent productionAgent;
	private ProductionPlanningAgent productionPlanAgent;
	private OrderAgent orderAgent;
	private OrderPlanAgent orderPlanAgent;
	private PlanningTechniques planningTechniques;
	
	private Material endProduct;
	private boolean infinite;
	
	private int planningPeriod;
	
		
	public Business(int tier){
		super(tier);
		this.endProduct = new Material("");
	}
	
	public void initNode(){		
		
		this.productionPlanAgent = new ProductionPlanningAgent(this);
		this.deliveryAgent = new DeliveryAgent(this);
		this.forecastAgent = new ForecastAgent(this);
		this.inventoryOpsAgent = new InventoryOpsAgent(this);
		this.inventoryPlanAgent = new InventoryPlanAgent(this, InvPolicies.ContOUT);
		this.productionAgent = new ProductionAgent(this);
		
		this.orderAgent = new OrderAgent(this);
		this.orderPlanAgent = new OrderPlanAgent(this);
		this.planningTechniques = new PlanningTechniques();
		
		if(this.tier==4){
			this.infinite=true;
			inventoryOpsAgent.setInfinite(true);
		};
		
		this.planningPeriod = 10;
		
		DemandPattern pattern = new NormalDistribution(10.0, 1.0);
		for(int i=-100; i<1; i++){
			this.forecastAgent.handDemandData(i, pattern.getNextDouble());
		}
		
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 10)
	public void prepareTick(){
		inventoryOpsAgent.prepareTick();
	}
	
	@ScheduledMethod(start=1, interval = 1, priority = 9.5)
	public void plan(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		int productionTime = this.productionAgent.getProductionTime();
		if(currentTick % planningPeriod == 0 && !infinite){
			inventoryPlanAgent.handForecast(forecastAgent.getForecast(currentTick+productionTime, currentTick+planningPeriod+productionTime+2));
			inventoryPlanAgent.planEndProduct();
			productionPlanAgent.planProduction();
			inventoryPlanAgent.planMRP();			
			orderPlanAgent.plan();
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 9)
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		for(Link link : this.upstrLinks){
			shipments = link.getArrivingShipments();
			orderAgent.processInShipments(shipments);
		}
	}
	
	@ScheduledMethod(start=1, interval=1, priority = 8)
	public void produce(){
		productionAgent.startProdJobs();
		inventoryOpsAgent.storeProducts(productionAgent.getArrivingProduction());
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 6)
	public void placeOrder(){
		if(upstrLinks.size()==0) return;
		//System.out.println("placeOrder");
		orderAgent.placeOrders();
			//System.out.println("Order added");
		}	
	@ScheduledMethod(start = 1, interval = 1, priority = 5)
	public void fetchOrders(){
		ArrayList<Order> newOrders = new ArrayList<Order>();
		for(Link link : this.downstrLinks){
			newOrders.addAll(link.fetchOrders());
		}
		deliveryAgent.processOrders(newOrders);
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 4)
	public void dispatchShipments(){
		this.deliveryAgent.dispatchShipments();
	}
	
	
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		this.inventoryOpsAgent.setInfiniteInInventories(false);
		upstrLinks.add(b);
	}
	
	public DeliveryAgent getDeliveryAgent(){
		return this.deliveryAgent;
	}
	
	public ForecastAgent getForecastAgent(){
		return this.forecastAgent;
	}
	
	public InventoryOpsAgent getInventoryOpsAgent(){
		return this.inventoryOpsAgent;
	}
	
	public InventoryPlanAgent getInventoryPlanAgent(){
		return this.inventoryPlanAgent;
	}
	
	public ProductionAgent getProductionAgent(){
		return this.productionAgent;
	}
	
	public OrderAgent getOrderAgent(){
		return this.orderAgent;
	}
	
	public OrderPlanAgent getOrderPlanAgent(){
		return this.orderPlanAgent;
	}
	
	public PlanningTechniques getPlanningTechniques(){
		return this.planningTechniques;
	}
	
	public ProductionPlanningAgent getProductionPlanAgent(){
		return this.productionPlanAgent;
	}
	
	public Material getEndProduct(){
		return this.endProduct;
	}
	
	public String getInformationString(){
		String string = "";
		string += "Node: " + this.Id + ", Tier: " + this.tier + "\n";
		string += "   InventoryAgent: \n" 
				+ inventoryOpsAgent.getInformationString();
		return string;
	}


}
