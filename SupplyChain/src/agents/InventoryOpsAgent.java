package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import artefacts.Material;
import InventoryPolicies.InventoryPolicy;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import supplyChain.Inventory;

public class InventoryOpsAgent {
	
	private Business biz;
	private Material endProduct;
	private HashMap<Material, Inventory> inventories;	
	
	private boolean infiniteSupplier;	
	
	public InventoryOpsAgent(Business biz){
		this.biz = biz;
		this.endProduct = biz.getEndProduct();
		
		this.inventories = new HashMap<Material, Inventory>();
		for(Material material : biz.getProductionAgent().getBillOfMaterial().keySet()){
			inventories.put(material, new Inventory(biz, material));
		}
		Material endProduct = biz.getEndProduct();
		inventories.put(endProduct, new Inventory(biz, endProduct));		
	}	
	
	public void storeMaterials(HashMap<Material, Double> materials){
		for(Material material : materials.keySet()){
			inventories.get(material).incrInventory(materials.get(material));
		}
	}
	
	public void storeProducts(double products){
		inventories.get(biz.getEndProduct()).incrInventory(products);
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
			Inventory inventory = inventories.get(material);
			double req = request.get(material);
			double inventoryLevel = inventory.getInventoryLevel();
			double del = Math.min(inventoryLevel, req);
			//infinite supplier
			Material endProduct = biz.getEndProduct();
			if(material==endProduct && infiniteSupplier){
				del = req;
			}
			delivery.put(material, del);
			inventory.lowerInventory(del);			
		}
		return delivery;
	}
	
	public HashMap<Material, Double> getOrders(){
		HashMap<Material, Double> orders = new HashMap<Material, Double>();
		for(Material material : inventories.keySet()){
			double amount = inventories.get(material).getOrder();
			if(amount!=0.0)
				orders.put(material, amount);
		}
		return orders;
	}
	
	public double getInventoryPosition(Material material){
		double inventoryLevel = getInventoryLevel(material);
		double ordered;
		double backlog;
		if(material!=biz.getEndProduct()){
			ordered = biz.getOrderAgent().getProcessingOrders(material);
			backlog = biz.getProductionAgent().getBacklog(material);			
		}
		else{
			ordered = biz.getProductionAgent().getProcessingProduction();
			backlog = biz.getDeliveryAgent().getBacklog();
		}
		return inventoryLevel + ordered - backlog;		
	}
	
	/**
	 *
	 * @param link
	 * @return Inventory Level des entsprechenden Links
	 */
	public double getInventoryLevel(Material material){
		return inventories.get(material).getInventoryLevel();
	}
	
	/**
	 * Für die obersten Lieferanten kann das Inventory als unendlich eingestellt werden
	 * @param b
	 */
	public void setInfiniteInInventories(boolean b){
		for(Inventory inventory : inventories.values()){
			
			inventory.setInfinite(b);
		}
	}	
	
	public void prepareTick(){
		for(Inventory inventory : inventories.values()){
			inventory.prepareTick();
		}
	}
	
	public HashMap<Material, Inventory> getInventories(){
		return this.inventories;
	}
	
	
	public String getInformationString(){
		String string = "";
		string += "      InInventories: \n";
		for(Material material :inventories.keySet()){
			string += "         Material: " + material.getId() + "\n" 
					+ "            " + inventories.get(material).getInformationString() + "\n";
		}
		string += "      OutInventory: " + "\n"
				+ "            " + outInventory.getInformationString() + "\n";
		
		return string;
	}

}
