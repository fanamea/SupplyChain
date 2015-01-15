package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.essentials.RepastEssentials;

public class ProductionAgent {
	
	private Business biz;
	private int productionTime;
	private int productionCapacity;
	private double setUpCost;
	private HashMap<Link, Double> materialFactor;
	private HashMap<Integer, Integer> productionStartPlan;
	private HashMap<Integer, Integer> productionDueList;
	private ArrayList<ProdJob> productionPipeLine;
	private ArrayList<ProdJob> productionHistory;
	
	public ProductionAgent(Business biz){
		this.biz = biz;
		this.productionTime = 1;
		this.productionCapacity = 5;
		this.setUpCost = 1;
		this.materialFactor = new HashMap<Link, Double>();
		for(Link link : biz.getUpstrLinks()){
			materialFactor.put(link, link.getMaterialFactor());
		}
		productionStartPlan = new HashMap<Integer, Integer>();
		productionDueList = new HashMap<Integer, Integer>();
	}
	
	public void startProdJobs(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(productionStartPlan.containsKey(currentTick)){
			double batchSize = productionStartPlan.get(currentTick);
			ProdJob job = new ProdJob(currentTick, batchSize, productionTime);
			productionPipeLine.add(job);
		}
	}
	
	/**
	 * 
	 * @return Liste von ProductionJobs, die im aktuellen Tick fertig werden.
	 */
	public ArrayList<ProdJob> getArrivingProduction(){
		ArrayList<ProdJob> ret = new ArrayList<ProdJob>();
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(ProdJob job : productionPipeLine){			
			if(job.getDate() + job.getLeadTime() == currentTick){
				ret.add(job);
				productionPipeLine.remove(job);
			}
		}
		return ret;
	}
	
	/**
	 * Berechnet die auf Grund von: Input Lagerbeständen und productionCapacity maximal mögliche Produktionsmenge.
	 * @return maximal mögliche Produktionsmenge
	 */
	public double calcMaxProduction(){
		ArrayList<Double> quotients = new ArrayList<Double>();
		
		for(Link link : biz.getUpstrLinks()){
			double quotient = biz.getInventoryAgent().getInInventoryLevel(link)/link.getMaterialFactor();
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
	public HashMap<Link, Double> calcRessourceDemand(double output){
		HashMap<Link, Double> demand = new HashMap<Link, Double>();
		
		for(Link link : biz.getUpstrLinks()){
			double d = link.getMaterialFactor()*output;
			demand.put(link, d);
		}
		return demand;
	}

}
