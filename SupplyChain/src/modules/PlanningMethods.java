package modules;
import java.util.TreeMap;
import java.util.Iterator;

import org.apache.commons.math3.distribution.NormalDistribution;


public class PlanningMethods {
	
	public PlanningMethods(){
	}
	
	
	/**
	 * Zusätzlichen SafetyStock!!
	 * @param sd
	 * @param serviceLevel
	 * @return
	 */
	public double calcSafetyStock(double sdOrder, double period, double serviceLevel){
		//TODO: krasser Workaround!!!
		////System.out.println("calcSafetyStock: sd: " + sd + ", avgLeadTime: " + avgLeadTime + ", serviceLevel: " + serviceLevel);
		if(sdOrder==0) sdOrder=0.001;
		NormalDistribution normal = new NormalDistribution(0, sdOrder/period);
		double safetyStock = normal.inverseCumulativeProbability(serviceLevel);
		////System.out.println("SafetyStock: " + safetyStock);
		return safetyStock;
	}
	
	public int calcSafetyLeadTime(double sdLeadTime, double serviceLevel){
		if(sdLeadTime==0) return 0;
		NormalDistribution normal = new NormalDistribution(0, sdLeadTime);
		int safetyLeadTime = (int)Math.ceil(normal.inverseCumulativeProbability(serviceLevel));
		////System.out.println("SafetyStock: " + safetyStock);
		return safetyLeadTime;
	}
	
	public double getEOQ(double demand, double fixCost, double holdingCost){
		return Math.sqrt((2*demand*fixCost)/holdingCost);
	}
	
	private int getMinKey(TreeMap<Integer, Double> map){
		int min = map.keySet().iterator().next();
		for(Integer i : map.keySet()){
			if(i<min) min=i;
		}
		return min;
	}

}
