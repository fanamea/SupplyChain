package InventoryPolicies;


import agents.Business;
import modules.PlanningMethods;
import artefacts.Material;
import modules.Inventory;


public class ContinuousOUT extends InventoryPolicy{
	
	public ContinuousOUT() {
		super();
	}
	
	public void recalcParams(){
		calcReorderLevel();
		calcOutLevel();
	}
	
	public double getOrder(int currentTick, double inventoryPosition){
		if(inventoryPosition<=reorderPoint)
			return outLevel - inventoryPosition;
		else
			return 0.0;
	}
	
	public void calcReorderLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = getMeanDemand();
		double meanLeadTime = getMeanLeadTime();
		double sdOrder = biz.getInformationModule().getSDDemand();
		double sdLeadTime = getSDLeadTime();
		double safetyStock = planningTechniques.calcSafetyStock(sdOrder, meanLeadTime, inventory.getServiceLevel());
		
		//System.out.println("Tier: " + biz.getTier() + ", meanOrder: " + meanOrder + ", meanLeadTime: " + meanLeadTime + ", sdOrder: " + sdOrder + ", sdLeadTime: " + sdLeadTime + ", safetyStock: " + safetyStock);
		
		reorderPoint = meanLeadTime*meanOrder + safetyStock;
	}
			
	public void calcOutLevel(){
		double meanOrder = biz.getInformationModule().getMeanDemand();
		double orderFixCost = biz.getOrderPlanModule().getOrderFixCost(inventory.getMaterial());
		double orderQuantity = planningTechniques.getEOQ(meanOrder, orderFixCost, inventory.getHoldingCost());
		
		//System.out.println("meanOrder: " + meanOrder + ", orderQuantity: " + orderQuantity);
		this.outLevel = this.reorderPoint + orderQuantity;
	}
	
	public String getParameterString(){
		String string = "";
		string += "         OUT Level: " + this.outLevel + "\n";
		string += "         Reorderlevel: " + this.reorderPoint + "\n";
		return string;
	}
	
	

}
