package InventoryPolicies;

import org.apache.commons.math3.distribution.NormalDistribution;

import repast.simphony.essentials.RepastEssentials;
import agents.Business;
import artefacts.Material;
import modules.Inventory;

public class PeriodicOUT_Quantify extends InventoryPolicy{
	
	private double delta;
	private double phi;
	
	
	public PeriodicOUT_Quantify(double delta, double phi) {
		super();
		this.period = 1;
		this.delta = delta;
		this.phi = phi;
	}

	@Override
	public void recalcParams() {
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
	
	private void calcOutLevel(){
		Material material = inventory.getMaterial();
		int lastTick = (int)RepastEssentials.GetTickCount()-1;
		double prevDemand = biz.getInformationModule().getInternDemandData().getDemandData(lastTick);
		double meanOrder = getMeanDemand();
		double meanLeadTime = getMeanLeadTime();
		double sdOrder = biz.getInformationModule().getSDDemand();
		double sdLeadTime = getSDLeadTime();
		double x = (period+meanLeadTime)*meanOrder;
		double z = 2.0;
		double sx = (period+meanLeadTime)*Math.pow(sdOrder, 2)+Math.pow(meanOrder, 2)*Math.pow(sdLeadTime, 2);
		
		this.outLevel = x + z * sx;
	}

	@Override
	public String getParameterString() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
