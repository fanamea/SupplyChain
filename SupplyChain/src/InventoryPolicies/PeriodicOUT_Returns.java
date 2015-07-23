package InventoryPolicies;

import org.apache.commons.math3.distribution.NormalDistribution;

import agents.Business;
import artefacts.Material;
import modules.Inventory;

public class PeriodicOUT_Returns extends InventoryPolicy{
	
	private int periodMA;
	
	
	public PeriodicOUT_Returns() {
		super();
		this.period = 1;
		this.periodMA = 15;
	}

	@Override
	public void recalcParams() {
		calcOutLevel();
		
	}

	@Override
	public double getOrder(int currentTick, double inventoryPosition) {
		if((currentTick-lastOrderDate)==period){
			this.lastOrderDate = currentTick;
			double ret = outLevel-inventoryPosition;
			return Math.max(ret, 0.0);
		}
		else{	
			return 0;
		}
	}
	
	private void calcOutLevel(){
		Material material = inventory.getMaterial();
		double meanOrder = getMeanDemand(this.periodMA);
		double meanLeadTime = getMeanLeadTime();
		double sdOrder = getSDDemand(this.periodMA);
		double sdLeadTime = getSDLeadTime();
		double x = (period+4)*meanOrder;
		double z = 2.0;
		double sx = Math.sqrt((period+meanLeadTime)*Math.pow(sdOrder, 2)+Math.pow(meanOrder, 2)*Math.pow(sdLeadTime, 2));
		
		this.outLevel = x + z * sx;
		//System.out.println("OUTLevel: " + outLevel + ", meanOrder: " + meanOrder + ", meanLeadTime: " + meanLeadTime + ", sdOrder: " + sdOrder + ", sdLeadTime: " + sdLeadTime + ", x: " + x + ", z: " + z + ", sx: " + sx);
	}

	@Override
	public String getParameterString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
