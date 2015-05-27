package lotSizingAlgorithms;

import java.util.TreeMap;

public abstract class LotSizingAlgorithm {
	
	protected double fixCost;
	protected double holdingCost;
	
	public LotSizingAlgorithm(double fixCost, double holdingCost){
		this.fixCost = fixCost;
		this.holdingCost = holdingCost;
	}
	
	public void setFixCost(double fixCost){
		this.fixCost = fixCost;
	}
	
	public void setHoldingCost(double holdingCost){
		this.holdingCost = holdingCost;
	}
	
	public abstract TreeMap<Integer, Double> calcLotPlan(TreeMap<Integer, Double> demand);

}
