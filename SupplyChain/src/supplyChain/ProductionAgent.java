package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import repast.simphony.essentials.RepastEssentials;

public class ProductionAgent {
	
	private Business biz;
	private int productionTime;
	private double productionCapacity;
	private double setUpCost;
	private HashMap<Link, Double> materialFactor;
	private HashMap<Integer, Double> productionStartPlan;
	private HashMap<Integer, Double> productionDueList;
	private CopyOnWriteArrayList<ProdJob> productionPipeLine;
	private ArrayList<ProdJob> productionHistory;
	
	public ProductionAgent(Business biz){
		this.biz = biz;
		this.productionTime = 2;
		this.productionCapacity = 10;
		this.setUpCost = 1;
		this.materialFactor = new HashMap<Link, Double>();
		for(Link link : biz.getUpstrLinks()){
			materialFactor.put(link, link.getMaterialFactor());
		}
		productionStartPlan = new HashMap<Integer, Double>();
		productionDueList = new HashMap<Integer, Double>();
		productionPipeLine = new CopyOnWriteArrayList<ProdJob>();
		productionHistory = new ArrayList<ProdJob>();
	}
	
	public void calcProductionStartPlan(){
		for(Integer i : productionDueList.keySet()){
			int productionStart = i - productionTime;
			if(productionStartPlan.containsKey(productionStart)){
				double sum = productionStartPlan.get(productionStart) + productionDueList.get(i);
				if(sum<productionCapacity){
					productionStartPlan.put(productionStart, sum);
				}
				else{
					productionStartPlan.put(productionStart, productionCapacity);
					//TODO Restliche Menge verteilen
				}
			}
			else{
				productionStartPlan.put(productionStart, productionDueList.get(i));
			}
		}
	}
	
	public void startProdJobs(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(productionStartPlan.containsKey(currentTick)){
			double PlannedBatchSize = productionStartPlan.get(currentTick);
			double maxProduction = calcMaxProduction();
			if(PlannedBatchSize <= maxProduction){
				ProdJob job = new ProdJob(currentTick, PlannedBatchSize, productionTime);
				productionPipeLine.add(job);
				biz.getInventoryAgent().processStartProduction(job);
			}
			else{
				ProdJob job = new ProdJob(currentTick, maxProduction, productionTime);
				productionPipeLine.add(job);
				biz.getInventoryAgent().processStartProduction(job);
				//TODO Fehlmenge behandeln
			}
			
		}
	}
	
	/**
	 * Übergangsweise vereinfachte production ohne Planung.
	 * Anstelle von startProdJobs
	 */
	public void produce(){
		double amount = biz.getInventoryAgent().getProductionSize();
		int currentTick = (int)RepastEssentials.GetTickCount();
		if(amount!=0.0){
			double plannedBatchSize = amount;
			double maxProduction = calcMaxProduction();
			System.out.println("plannedBatchSize: " + plannedBatchSize + ", maxProduction: " + maxProduction);
			if(plannedBatchSize <= maxProduction){
				
				ProdJob job = new ProdJob(currentTick, plannedBatchSize, productionTime);
				System.out.println("ProdJob: " + job.getSize());
				productionPipeLine.add(job);
				biz.getInventoryAgent().processStartProduction(job);
			}
			else{
				ProdJob job = new ProdJob(currentTick, maxProduction, productionTime);
				System.out.println("ProdJob: " + job.getSize());
				productionPipeLine.add(job);
				biz.getInventoryAgent().processStartProduction(job);
				//TODO Fehlmenge behandeln
			}
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
			System.out.println("debug: Biz: " + biz.getId() + "Link: " + link.getId());
			double quotient = biz.getInventoryAgent().getInInventoryLevel(link)/link.getMaterialFactor();
			quotients.add(quotient);
		}
		double max = 0;
		for(Double i : quotients){
			if(i > max) max = i;
		}
		return Math.min(productionCapacity, max);
	}
	
	public double getResourceDemand(int date, Link link){
		if(productionDueList.containsKey(date))
			return productionDueList.get(date)*materialFactor.get(link);		
		else
			return 0.0;
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
	
	public void handProductionDueList(HashMap<Integer, Double> dueList){
		this.productionDueList = dueList;
	}
	
	public  HashMap<Integer, Double> getProductionDueList(){
		return this.productionDueList;
	}

}
