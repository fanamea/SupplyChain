package InventoryPolicies;

import agents.Business;
import modules.Inventory;

public class PeriodicQ extends InventoryPolicy{
	
	private int period;
	private double orderQuantity;
	private int lastOrderDate;
	
	public PeriodicQ(Business biz, Inventory inventory) {
		super(biz, inventory);
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
			return orderQuantity;
		}
		else{	
			return 0;
		}
	}
	
	private void calcPeriod(){
		double meanOrder = biz.getForecastModule().getMeanDemand();
		double eoq = planningTechniques.getEOQ(meanOrder, inventory.getFixOrderCost(), inventory.getHoldingCost());
		this.period = (int)Math.ceil(eoq/meanOrder);
	}
	
	public void calcOrderQuantity(){
		double meanOrder = biz.getForecastModule().getMeanDemand();
		this.orderQuantity = planningTechniques.getEOQ(meanOrder, inventory.getFixOrderCost(), inventory.getHoldingCost());
	}

}
