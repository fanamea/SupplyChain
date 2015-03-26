package InventoryPolicies;

import org.apache.commons.math3.distribution.NormalDistribution;

import agents.Business;
import artefacts.Material;
import modules.Inventory;

public class PeriodicOUT extends InventoryPolicy{
	
	private int period;
	private int lastOrderDate;
	private double outLevel;
	
	
	public PeriodicOUT(Business biz, Inventory inventory) {
		super(biz, inventory);
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
		double meanOrder = biz.getForecastModule().getMeanDemand();
		double eoq = planningTechniques.getEOQ(meanOrder, inventory.getFixOrderCost(), inventory.getHoldingCost());
		this.period = (int)Math.ceil(eoq/meanOrder);
	}
	
	private void calcOutLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = biz.getForecastModule().getMeanDemand();
		double meanLeadTime = biz.getOrderPlanModule().calcMeanLeadTime(material);
		double sdOrder = biz.getForecastModule().getSDDemand()*biz.getProductionAgent().getBillOfMaterial().get(material);
		double sdLeadTime = biz.getOrderPlanModule().calcSDLeadTime(material);
		double safetyStock = planningTechniques.calcSafetyStock(sdOrder, period+meanLeadTime, inventory.getServiceLevel());
		
		this.outLevel = (period+meanLeadTime)*meanOrder + safetyStock;
	}
	
}
