package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class InventoryAgent {
	
	private Business biz;
	private PlanningTechniques planningTechniques;
	private ForecastAgent fcAgent;
	private HashMap<Material, Inventory> inInventories;
	private Inventory outInventory;
	private int fcTimeSpanfuture;
	
	private HashMap<Integer, Double> demandForecast;
	
	private boolean continuous;
	private boolean periodic;
	private boolean fixedQuantity;
	private boolean orderUpTo;
	
	private double serviceLevel;
	private double fixedOrderCost;
	private double holdingCost;
	
	private boolean infiniteSupplier;
	
	
	public InventoryAgent(Business biz){
		this.biz = biz;
		this.planningTechniques = biz.getPlanningTechniques();
		this.fcAgent = biz.getForecastAgent();
		this.continuous = false;
		this.periodic = false;
		this.fixedQuantity = false;
		this.orderUpTo = false;
		this.serviceLevel = 0.98;
		this.fixedOrderCost = 100;
		this.holdingCost = 0.5;
		
		this.inInventories = new HashMap<Material, Inventory>();
		for(Material material : biz.getProductionAgent().getBillOfMaterial().keySet()){
			inInventories.put(material, new Inventory(this, material));
		}
		this.outInventory = new Inventory(this, biz.getProductionAgent().getEndProduct());
		
	}
	
	public void setInventoryPolicy()
	
	public void calcAllPeriods(){
		for(Inventory inventory : inInventories.values()){
			inventory.setPeriod(calcPeriod(inventory));
		}
		outInventory.setPeriod(calcPeriod(outInventory));
	}
	
	public int calcPeriod(Inventory inventory){
		double meanDemand = fcAgent.getAvgOrderFC();
		double eoq = calcOrderQuantity(inventory);
		return (int)Math.ceil(eoq/meanDemand);
	}
	
	public void calcAllOUTLcontinuous(){
		for(Inventory inventory : inInventories.values()){
			inventory.setOrderUpToLevel(calcOUTcontinuous(inventory));
		}
		outInventory.setOrderUpToLevel(calcOUTcontinuous(outInventory));
	}
	
	public double calcOUTcontinuous(Inventory inventory){
		double orderUpToLevel = inventory.getReorderLevel() + calcOrderQuantity(inventory);
		return orderUpToLevel;
	}
	
	public void calcOrderQuantities(){
		for(Inventory inventory : inInventories.values()){
			inventory.setOrderQuantity(calcOrderQuantity(inventory));
		}
		outInventory.setOrderQuantity(calcOrderQuantity(outInventory));
	}
	
	public double calcOrderQuantity(Inventory inventory){
		double meanOrder = fcAgent.getAvgOrderFC();
		double fixOrderCost = inventory.getFixOrderCost();
		double holdingCost = inventory.getHoldingCost();
		double quantity = planningTechniques.getEOQ(meanOrder, fixOrderCost, holdingCost);
		return quantity;
	}
	
	public double calcReorderLevel(double avgOrder, double avgLeadTime, double sdOrder, double sdLeadTime, double serviceLevel){
		//System.out.println("calcAimLevel: avgOrder: " + avgOrder + ", avgLeadTime: " + avgLeadTime + ", sdOrder: " + sdOrder + ", sdLeadTime " + sdLeadTime);
		return avgLeadTime*avgOrder + this.calcSafetyStock(sdOrder, avgLeadTime, serviceLevel);
	}
	
	/**
	 * Setzt für alle Inventories den reorderPoint neu.
	 * Basis sind die am Anfang deklarierten Variablen. Wegen AVG also nur für normalverteilt überhaupt ansatzweise vernünftig!!
	 */
	public void recalcReorderLevels(){
		double avgOrder;
		double avgLeadTime;
		double sdOrder;
		double sdLeadTime;
		double reorderLevel;
		
		//InInventories
		for(Material material : inInventories.keySet()){
			Inventory inventory = inInventories.get(material);
			avgOrder = biz.getForecastAgent().getAvgOrderFC()*biz.getProductionAgent().getBillOfMaterial().get(material);
			avgLeadTime = biz.getOrderAgent().calcMeanLeadTime(material);
			sdOrder = biz.getDeliveryAgent().calcOrdersSD()*biz.getProductionAgent().getBillOfMaterial().get(material);
			sdLeadTime = biz.getOrderAgent().calcSDLeadTime(material);
			reorderLevel = calcReorderLevel(avgOrder, avgLeadTime, sdOrder, sdLeadTime, inventory.getServiceLevel());
			//System.out.println("Neuer ReorderPoint: " + inventory.getAimLevel() + " -> " +  aimLevel);
			inventory.setReorderLevel(reorderLevel);
		}
		//OutInventory
		avgOrder = biz.getForecastAgent().getAvgOrderFC();
		avgLeadTime = biz.getProductionAgent().getProductionTime();
		sdOrder = biz.getDeliveryAgent().calcOrdersSD();
		sdLeadTime = 0;
		reorderLevel = calcReorderLevel(avgOrder, avgLeadTime, sdOrder, sdLeadTime, outInventory.getServiceLevel());
		outInventory.setReorderLevel(reorderLevel);
	}
	
	
	
	//TODO
	public void calcOutInventoryDueList(){
		
		//Forecast holen
		HashMap<Integer, Double> forecast = biz.getForecastAgent().getOrderForecast();
		
		//Inventory-Entscheidung: Service Level?
		HashMap<Integer, Double> dueList = new HashMap<Integer, Double>();
		double sd = biz.getDeliveryAgent().calcOrdersSD();
		for(Integer i : forecast.keySet()){
			double fc = forecast.get(i);
			double safetyStock = this.calcSafetyStock(sd, 0, outInventory.getServiceLevel());
			dueList.put(i, fc + safetyStock);
		}		
		
		//Quasi productionFinishList
		biz.getProductionAgent().handProductionDueList(dueList);		
	}
	
	//TODO
	public void calcInInventoriesDueLists(){
		
		//Über alle Inventories
		for(Material material : this.inInventories.keySet()){
			Inventory inventory = inInventories.get(material);
			HashMap<Integer, Double> productionDueList = biz.getProductionAgent().getProductionDueList();
			//Über alle Eintrage in productionDueList
			for(Integer date : productionDueList.keySet()){
				double amount = biz.getProductionAgent().getResourceDemand(date, material);
				inventory.setDueListEntry(date, amount);
			}			
		}		
	}
		
	public void storeMaterials(HashMap<Material, Double> materials){
		for(Material material : materials.keySet()){
			inInventories.get(material).incrInventory(materials.get(material));
		}
	}
	
	public void storeProducts(double products){
		outInventory.incrInventory(products);
	}
	
	
	/**
	 * TODO: Backlogging speichern
	 * Gibt auf ein Material Request eine MaterialLieferung zurück.
	 * Material schon im Inentory abgezogen, frei zur Verwendung in production.
	 * @param request
	 * @return
	 */
	public HashMap<Material, Double> requestMaterials(HashMap<Material, Double> request){
		HashMap<Material, Double> delivery = new HashMap<Material, Double>();
		for(Material material : request.keySet()){
			Inventory inventory = inInventories.get(material);
			double req = request.get(material);
			double inventoryLevel = inventory.getInventoryLevel();
			double del = Math.min(inventoryLevel, req);
			delivery.put(material, del);
			inventory.lowerInventory(del);			
		}
		return delivery;
	}
	
	/**
	 * TODO: Backlogging speichern
	 * Gibt auf ein Product Request eine Produktlieferung zurück.
	 * Produktverbrauch schon im Inventory abgezogen, frei zur Auslieferung.
	 * @param request
	 * @return
	 */
	public double requestProducts(double request){
		if(infiniteSupplier) return request;
		
		double delivery = Math.min(outInventory.getInventoryLevel(), request);
		outInventory.lowerInventory(delivery);
		return delivery;
	}
	
	/**
	 *
	 * @param link
	 * @return Inventory Level des entsprechenden Links
	 */
	public double getInInventoryLevel(Material material){
		return inInventories.get(material).getInventoryLevel();
	}
	
	/**
	 * Überprüft zu einer Order, wieviel davon geshipt werden kann
	 * @param order Zu überprüfende Order
	 * @return maximal shipbare Menge
	 */
	public double getShipableAmount(Double amount){
		return Math.min(outInventory.getInventoryLevel(), amount);
	}
	
	/**
	 * Für die obersten Lieferanten kann das Inventory als unendlich eingestellt werden
	 * @param b
	 */
	public void setInfiniteInInventories(boolean b){
		for(Inventory inventory : inInventories.values()){
			inventory.setInfinite(b);
		}
	}
	
	public HashMap<Material, Double> getOrders(){
		HashMap<Material, Double> orders = new HashMap<Material, Double>();
		for(Material material : inInventories.keySet()){
			double amount = inInventories.get(material).getOrder();
			if(amount!=0.0)
				orders.put(material, amount);
		}
		return orders;
	}
	
	public double getProductionSize(){
			return outInventory.getOrder();
	}
	
	public void handDemandForecast(HashMap<Integer, Double> forecast){
		this.demandForecast = forecast;
	}
	
	public void prepareTick(){
		for(Inventory inventory : inInventories.values()){
			inventory.prepareTick();
		}
		outInventory.prepareTick();
	}
	
	
	
	public double calcSD(ArrayList<Double> history){
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for(Double d : history){
			stats.addValue(d);
		}
		return stats.getStandardDeviation();
	}
	
	public HashMap<Integer, Double> getOutInventoryDueList(){
		return this.outInventory.getDueList();
	}
	
	public void setServiceLevels(double serviceLevel){
		for(Inventory inventory : inInventories.values()){
			inventory.setServiceLevel(serviceLevel);
		}
		outInventory.setServiceLevel(serviceLevel);
	}
	
	public double getOutInventory(){
		return outInventory.getInventoryLevel();
	}
	
	public PlanningTechniques getPlanningTechniques(){
		return this.planningTechniques;
	}
	
	public ForecastAgent getForecastAgent(){
		return this.fcAgent;
	}
	
	public String getInformationString(){
		String string = "";
		string += "      InInventories: \n";
		for(Material material :inInventories.keySet()){
			string += "         Material: " + material.getId() + "\n" 
					+ "            " + inInventories.get(material).getInformationString() + "\n";
		}
		string += "      OutInventory: " + "\n"
				+ "            " + outInventory.getInformationString() + "\n";
		
		return string;
	}

}
