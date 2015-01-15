package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class InventoryAgent {
	
	private HashMap<Link, Inventory> inInventories;
	private Inventory outInventory;
	
	public InventoryAgent(ArrayList<Link> linkList){
		this.inInventories = new HashMap<Link, Inventory>();
		for(Link link : linkList){
			inInventories.put(link, new Inventory());
		}
		this.outInventory = new Inventory();
	}
	
	//TODO
	public void calcOutInventoryDueList(){
		
		//Forecast holen
		
		//Inventory-Entscheidung: Service Level?
		
		//Quasi productionFinishList
		
	}
	
	//TODO
	public void calcInInventoriesDueLists(){
		
		//productionStartList holen
		
		//Anpassen von OrderUpToLevel, etc.?
		//Entscheidung über Bestellung hier oder OrderAgent?
		
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
	public void processStartProduction(ArrayList<ProdJob> productionJobs){
		for(ProdJob job : productionJobs){
			for(Link link : inInventories.keySet()){
				Inventory inventory = inInventories.get(link);
				inventory.lowerInventory(job.getSize()*link.getMaterialFactor());
			}
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
	
	public void prepareTick(){
		for(Inventory inventory : inInventories.values()){
			inventory.prepareTick();
		}
	}
	

}
