package InventoryPolicies;

import agents.Business;
import modules.Inventory;

public class PeriodicQ extends InventoryPolicy{
	
	public PeriodicQ(Business biz, Inventory inventory) {
		super();
	}

	@Override
	public void recalcParams() {
		calcPeriod();
		calcOrderQuantity();
		
	}

	@Override
	public double getOrder(int currentTick, double inventoryPosition) {
		if((currentTick-lastOrderDate)==period){
			this.lastOrderDate = currentTick;
			return quantity;
		}
		else{	
			return 0;
		}
	}
	
	private void calcPeriod(){
		double meanOrder = biz.getInformationModule().getMeanDemand();
		double orderFixCost = biz.getOrderPlanModule().getOrderFixCost(inventory.getMaterial());
		double eoq = planningTechniques.getEOQ(meanOrder, orderFixCost, inventory.getHoldingCost());
		this.period = (int)Math.ceil(eoq/meanOrder);
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
