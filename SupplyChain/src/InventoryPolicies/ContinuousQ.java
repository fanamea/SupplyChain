package InventoryPolicies;


import agents.Business;
import artefacts.Material;
import modules.Inventory;
import modules.PlanningMethods;

public class ContinuousQ extends InventoryPolicy{
	
	
	public ContinuousQ(Business biz, Inventory inventory) {
		super();
	}
	
	public void recalcParams(){
		calcReorderLevel();
		calcOrderQuantity();
	}
	
	public double getOrder(int currentTick, double inventoryPosition){
		if(inventoryPosition<=reorderPoint)
			return quantity;
		else
			return 0.0;
	}
	
	public void calcReorderLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = getMeanDemand();
		double meanLeadTime = getMeanLeadTime();
		double sdOrder = getSDDemand();
		double sdLeadTime = getSDLeadTime();
		double safetyStock = planningTechniques.calcSafetyStock(sdOrder, meanLeadTime, inventory.getServiceLevel());
		
		reorderPoint = meanLeadTime*meanOrder + safetyStock;
	}
			
	public void calcOrderQuantity(){
		double meanOrder = biz.getInformationModule().getMeanDemand();
		double orderFixCost = biz.getOrderPlanModule().getOrderFixCost(inventory.getMaterial());
		this.quantity = planningTechniques.getEOQ(meanOrder, orderFixCost, inventory.getHoldingCost());
	}

	@Override
	public String getParameterString() {
		// TODO Auto-generated method stub
		return null;
	}	
}
