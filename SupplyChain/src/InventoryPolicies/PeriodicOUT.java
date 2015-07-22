package InventoryPolicies;

import org.apache.commons.math3.distribution.NormalDistribution;

import agents.Business;
import artefacts.Material;
import modules.Inventory;

public class PeriodicOUT extends InventoryPolicy{	
	
	public PeriodicOUT() {
		super();
	}

	@Override
	public void recalcParams() {
		calcPeriod();
		calcOutLevel();
		
	}

	@Override
	public double getOrder(int currentTick, double inventoryPosition) {
		if((currentTick-lastOrderDate)==period){
			this.lastOrderDate = currentTick;
			return outLevel-inventoryPosition;
		}
		else{	
			return 0;
		}
	}
	
	private void calcPeriod(){
		double meanOrder = getMeanDemand();
		double orderFixCost = getOrderFixCost();
		double eoq = planningTechniques.getEOQ(meanOrder, orderFixCost, inventory.getHoldingCost());
		this.period = (int)Math.ceil(eoq/meanOrder);
	}
	
	private void calcOutLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = getMeanDemand();
		double meanLeadTime = getMeanLeadTime();
		double sdOrder = biz.getInformationModule().getSDDemand();
		double sdLeadTime = getSDLeadTime();
		double safetyStock = planningTechniques.calcSafetyStock(sdOrder, period+meanLeadTime, inventory.getServiceLevel());
		
		this.outLevel = (period+meanLeadTime)*meanOrder + safetyStock;
	}

	@Override
	public String getParameterString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
