package InventoryPolicies;


import agents.Business;
import modules.PlanningMethods;
import artefacts.Material;
import modules.Inventory;


public class ContinuousOUT extends InventoryPolicy{
	
	private double outLevel;
	private double reorderLevel;
	private PlanningMethods planningTechniques;
	
	public ContinuousOUT(Business biz, Inventory inventory) {
		super(biz, inventory);
		this.planningTechniques = new PlanningMethods();
	}
	
	public void recalcParams(){
		calcReorderLevel();
		calcOutLevel();
	}
	
	public double getOrder(int currentTick, double inventoryPosition){
		if(inventoryPosition<=reorderLevel)
			return outLevel - inventoryPosition;
		else
			return 0.0;
	}
	
	public void calcReorderLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = biz.getForecastModule().getMeanDemand();
		double meanLeadTime = biz.getOrderPlanModule().calcMeanLeadTime(material);
		double sdOrder = biz.getForecastModule().getSDDemand();
		double sdLeadTime = biz.getOrderPlanModule().calcSDLeadTime(material);
		double safetyStock = planningTechniques.calcSafetyStock(sdOrder, meanLeadTime, inventory.getServiceLevel());
		
		System.out.println("Tier: " + biz.getTier() + ", meanOrder: " + meanOrder + ", meanLeadTime: " + meanLeadTime + ", sdOrder: " + sdOrder + ", sdLeadTime: " + sdLeadTime + ", safetyStock: " + safetyStock);
		
		reorderLevel = meanLeadTime*meanOrder + safetyStock;
	}
			
	public void calcOutLevel(){
		double meanOrder = biz.getForecastModule().getMeanDemand();
		double orderFixCost = biz.getOrderPlanModule().getOrderFixCost(inventory.getMaterial());
		double orderQuantity = planningTechniques.getEOQ(meanOrder, orderFixCost, inventory.getHoldingCost());
		
		System.out.println("meanOrder: " + meanOrder + ", orderQuantity: " + orderQuantity);
		this.outLevel = this.reorderLevel + orderQuantity;
	}
	
	public String getParameterString(){
		String string = "";
		string += "         OUT Level: " + this.outLevel + "\n";
		string += "         Reorderlevel: " + this.reorderLevel + "\n";
		return string;
	}
	
	

}
