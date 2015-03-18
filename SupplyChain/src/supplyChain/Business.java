package supplyChain;

import java.util.ArrayList;
import java.util.TreeMap;
import InventoryPolicies.InvPolicies;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Business extends Node{
	
	private DeliveryAgent deliveryAgent;
	private ForecastAgent forecastAgent;
	private InventoryOpsAgent inventoryOpsAgent;
	private InventoryPlanAgent inventoryPlanAgent;
	private ProductionAgent productionAgent;
	private ProductionPlanningAgent productionPlanAgent;
	private OrderAgent orderAgent;
	private PlanningTechniques planningTechniques;
	
	private Material endProduct;
	
	private int planningPeriod;
	
		
	public Business(int tier){
		super(tier);		
	}
	
	public void initNode(){
		this.deliveryAgent = new DeliveryAgent(this);
		this.forecastAgent = new ForecastAgent(this);
		this.inventoryOpsAgent = new InventoryOpsAgent(this);
		this.inventoryPlanAgent = new InventoryPlanAgent(this, InvPolicies.ContOUT);
		this.productionAgent = new ProductionAgent(this);
		this.productionPlanAgent = new ProductionPlanningAgent(this);
		this.orderAgent = new OrderAgent(this);
		this.planningTechniques = new PlanningTechniques();
		
		this.endProduct = new Material("");
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 10)
	public void prepareTick(){
		inventoryOpsAgent.prepareTick();
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 9)
	public void receiveShipments(){
		ArrayList<Shipment> shipments = new ArrayList<Shipment>();
		for(Link link : upstrLinks){
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
	
	@ScheduledMethod(start=11, interval = 1, priority = 3)
	public void plan(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		int productionTime = this.productionAgent.getProductionTime();
		if(currentTick % planningPeriod == 0){
			forecastAgent.calcForecastTotal(planningPeriod+productionTime);
			productionPlanAgent.handForecast(forecastAgent.getOrderForecast(currentTick+productionTime));
			productionPlanAgent.planProduction();
		}
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
