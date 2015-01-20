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
	private HashMap<Link, Inventory> inInventories;
	private Inventory outInventory;
	private int fcTimeSpanfuture;
	
	private boolean orderUpToLevel;
	private int periodic;
	private boolean continuous;
	
	public InventoryAgent(Business biz){
		this.biz = biz;
		this.inInventories = new HashMap<Link, Inventory>();
		for(Link link : biz.getUpstrLinks()){
			inInventories.put(link, new Inventory());
		}
		this.outInventory = new Inventory();
		this.orderUpToLevel = true;
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
			double safetyStock = this.calcSafetyStock(sd, outInventory.getServiceLevel());
			dueList.put(i, fc + safetyStock);
		}		
		
		//Quasi productionFinishList
		biz.getProductionAgent().handProductionDueList(dueList);		
	}
	
	//TODO
	public void calcInInventoriesDueLists(){
		
		//Über alle Inventories
		for(Link link : this.inInventories.keySet()){
			Inventory inventory = inInventories.get(link);
			HashMap<Integer, Double> productionDueList = biz.getProductionAgent().getProductionDueList();
			//Über alle Eintrage in productionDueList
			for(Integer date : productionDueList.keySet()){
				double amount = biz.getProductionAgent().getResourceDemand(date, link);
				inventory.setDueListEntry(date, amount);
			}			
		}		
	}
	
	/**
	 * Verarbeitet eine Liste von eingehenden Lieferungen
	 * @param link Zugehöriger Link
	 * @param shipments Liste von zu verarbeitenen shipments
	 */
	public void processInShipments(Link link, ArrayList<Shipment> shipments){
		Inventory inventory = inInventories.get(link);
		for(Shipment shipment : shipments){
			inventory.incrInventory(shipment.getSize());
		}		
	}	
	
	/**
	 * Verarbeitet eine Liste von ausgehenden Lieferungen
	 * @param shipments Liste von zu verarbeitenen shipments
	 */
	public void processOutShipments(ArrayList<Shipment> shipments){
		for(Shipment shipment : shipments){
			outInventory.lowerInventory(shipment.getSize());
		}
	}
	
	public void processOutShipment(Shipment shipment){
		outInventory.lowerInventory(shipment.getSize());
	}
	
	/**
	 * Verarbeitet die Fertigstellung eines ProdJobs im Inventory
	 * @param production
	 */
	public void processEndProduction(ArrayList<ProdJob> production){
		for(ProdJob job : production){
			outInventory.incrInventory(job.getSize());
		}
	}
	
	/**
	 * Verarbeitet den Beginn eines ProdJobs im Inventory
	 * @param productionJobs
	 */
	public void processStartProduction(ProdJob prodJob){
		System.out.println("processStartProduction: " + prodJob.getSize());
		for(Link link : inInventories.keySet()){				
				Inventory inventory = inInventories.get(link);
				inventory.lowerInventory(prodJob.getSize()*link.getMaterialFactor());
		}
	}
	
	/**
	 *
	 * @param link
	 * @return Inventory Level des entsprechenden Links
	 */
	public double getInInventoryLevel(Link link){
		return inInventories.get(link).getInventoryLevel();
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
	
	public HashMap<Link, Double> getOrders(){
		HashMap<Link, Double> orders = new HashMap<Link, Double>();
		if(orderUpToLevel){
			for(Link link : inInventories.keySet()){
				double amount = inInventories.get(link).getOrder();
				if(amount!=0.0)
					orders.put(link, amount);
			}
		}
		return orders;
	}
	
	public double getProductionSize(){
		if(orderUpToLevel){
			return outInventory.getOrder();
		}
		return 0.0;
	}
	
	public void prepareTick(){
		for(Inventory inventory : inInventories.values()){
			inventory.prepareTick();
		}
		outInventory.prepareTick();
	}
	
	public double calcSafetyStock(double sd, double serviceLevel){
		NormalDistribution normal = new NormalDistribution(0, sd);
		double safetyStock = normal.inverseCumulativeProbability(serviceLevel);
		return safetyStock;
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

}
