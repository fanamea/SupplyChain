package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.essentials.RepastEssentials;

public class Factory {
	
	private Business biz;
	private int productionTime;
	private int productionCapacity;
	private double setUpCost;
	private ArrayList<Double> materialFactor;
	private ArrayList<Integer> productionStartPlan;
	private ArrayList<Integer> productionFinishList;
	
	public Factory(Business biz){
		this.biz = biz;
		this.productionTime = 1;
		this.productionCapacity = 5;
		this.setUpCost = 1;
		this.materialFactor = new ArrayList<Double>();
		for(Link link : biz.getUpstrLinks()){
			materialFactor.add(link.getMaterialFactor());
		}
		productionStartPlan.add(0);
		for(int i=0; i<=productionTime; i++){
			productionFinishList.add(0);
		}
	}
	
	public void produce(){
		int now = (int)RepastEssentials.GetTickCount();
		int amount = productionStartPlan.get(now);
		productionFinishList.add(now+productionTime, amount);
	}
	
	/**
	 * Berechnet die auf Grund von: Input Lagerbeständen und productionCapacity maximal mögliche Produktionsmenge.
	 * @return maximal mögliche Produktionsmenge
	 */
	public double calcMaxProduction(){
		ArrayList<Double> quotients = new ArrayList<Double>();
		
		for(Link link : biz.getUpstrLinks()){
			double quotient = link.getInventory().getInventoryLevel()/link.getMaterialFactor();
			quotients.add(quotient);
		}
		double max = 0;
		for(Double i : quotients){
			if(i > max) max = i;
		}
		return Math.min(productionCapacity, max);
	}
	
	/**
	 * Berechnet zu einem bestimmten Produktionsoutput den Ressourcenbedarf
	 * @param output gewünschter Produktionsoutput
	 * @return ArrayList mit den Ressourcenbedarfen
	 */
	public ArrayList<Double> calcRessourceDemand(double output){
		ArrayList<Double> demand = new ArrayList<Double>();
		
		for(Link link : biz.getUpstrLinks()){
			double d = link.getMaterialFactor()*output;
			demand.add(d);
		}
		return demand;
	}

}
