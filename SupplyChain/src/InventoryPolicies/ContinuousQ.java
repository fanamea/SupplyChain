package InventoryPolicies;


import agents.Business;
import artefacts.Material;
import modules.Inventory;
import modules.PlanningMethods;

public class ContinuousQ extends InventoryPolicy{
	
	private double orderQuantity;
	private double reorderLevel;
	private PlanningMethods planningTechniques;
	
	public ContinuousQ(Business biz, Inventory inventory) {
		super(biz, inventory);
		this.planningTechniques = new PlanningMethods();
	}
	
	public void recalcParams(){
		calcReorderLevel();
		calcOrderQuantity();
	}
	
	public double getOrder(int currentTick, double inventoryPosition){
		if(inventoryPosition<=reorderLevel)
			return orderQuantity;
		else
			return 0.0;
	}
	
	public void calcReorderLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = biz.getForecastModule().getMeanDemand();
		double meanLeadTime = biz.getOrderPlanModule().calcMeanLeadTime(material);
		double sdOrder = biz.getForecastModule().getSDDemand()*biz.getProductionAgent().getBillOfMaterial().get(material);
		double sdLeadTime = biz.getOrderPlanModule().calcSDLeadTime(material);
		double safetyStock = planningTechniques.calcSafetyStock(sdOrder, meanLeadTime, inventory.getServiceLevel());
		
		reorderLevel = meanLeadTime*meanOrder + safetyStock;
	}
			
	public void calcOrderQuantity(){
		double meanOrder = biz.getForecastModule().getMeanDemand();
		this.orderQuantity = planningTechniques.getEOQ(meanOrder, inventory.getFixOrderCost(), inventory.getHoldingCost());
	}	
}
