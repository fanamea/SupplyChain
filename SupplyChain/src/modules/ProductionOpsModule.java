package modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import agents.Business;
import agents.Manufacturer;
import artefacts.Material;
import artefacts.ProdJob;
import artefacts.ProdRequest;
import artefacts.ProdRequestComparator;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public class ProductionOpsModule {
	
	private Manufacturer biz;
	private ProductionPlanModule planAgent;
	private int productionTime;
	private double productionCapacity;
	
	private TreeMap<Integer, Double> productionDueList;
	private CopyOnWriteArrayList<ProdJob> productionPipeLine;
	private CopyOnWriteArrayList<ProdRequest> prodRequestPipeLine;
	
	public ProductionOpsModule(Manufacturer biz){
		this.biz = biz;
		this.planAgent = biz.getProductionPlanModule();
		this.productionTime = 2;
		
		productionDueList = new TreeMap<Integer, Double>();
		productionPipeLine = new CopyOnWriteArrayList<ProdJob>();
		prodRequestPipeLine = new CopyOnWriteArrayList<ProdRequest>();
	}
	
	
	public void startProdJobs(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		double productionCounter = 0;		
		double capacityLeft = productionCapacity;
		boolean isProdRequest = false;
		double batchSize = 0;
		boolean condition;
		Collections.sort(prodRequestPipeLine, new ProdRequestComparator());
		do{
			if(!prodRequestPipeLine.isEmpty()){
				ProdRequest pReq = prodRequestPipeLine.get(0);
				isProdRequest = pReq.getDate()<=currentTick;
				if(isProdRequest){
					capacityLeft = productionCapacity-productionCounter;				
					batchSize = calcMaxProduction(pReq.getShortageSent());
					if(batchSize>0){
						batchSize = Math.max(capacityLeft, batchSize);
						HashMap<Material, Double> request = calcRessourceDemand(batchSize);
						biz.getinventoryOpsModule().requestMaterials(request);
						ProdJob job = new ProdJob(currentTick, batchSize, productionTime);
						pReq.addProdJob(job);
						pReq.incrSent(batchSize);
						capacityLeft -= batchSize;
						productionPipeLine.add(job);
						if(pReq.isSent()){
							prodRequestPipeLine.remove(pReq);
						}
					}
				}
			}
			condition = !prodRequestPipeLine.isEmpty() && isProdRequest && capacityLeft>0 && batchSize>0;
		}while(condition);
	}
		
	/**
	 * 
	 * @return Liste von ProductionJobs, die im aktuellen Tick fertig werden.
	 */
	public double getArrivingProduction(){
		double output = 0;
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(ProdJob job : productionPipeLine){			
			if(job.getDate() + job.getLeadTime() == currentTick){
				output +=job.getSize();
				productionPipeLine.remove(job);
			}
		}
		return output;
	}
	
	public double getBacklog(Material material){
		int currentTick = (int)RepastEssentials.GetTickCount();
		double sum = 0;
		for(ProdRequest pReq : prodRequestPipeLine){
			if(pReq.getDate()<=currentTick){
				sum += pReq.getShortageSent();
			}
		}
		return sum;
	}
	
	/**
	 * Nur wenn Endproduktlager Inventory Policy hat (Pull production)
	 * @return
	 */
	public double getProcessingProduction(){
		double currentTick = (int)RepastEssentials.GetTickCount();
		double sum = 0;
		for(ProdRequest pReq : prodRequestPipeLine){
			if(pReq.getDate()<=currentTick){
				sum += pReq.getShortageArrived();
			}
		}
		return sum;
	}
	
	
	/*
	 **
	 * Übergangsweise vereinfachte production ohne Planung.
	 * Anstelle von startProdJobs
	 *
	public void produce(){
		double amount = biz.getInventoryAgent().getProductionSize();
		int currentTick = (int)RepastEssentials.GetTickCount();
		
		if(amount!=0.0){
			double plannedBatchSize = amount;
			double maxProduction = calcMaxProduction();
			HashMap<Material, Double> request = calcRessourceDemand(maxProduction);
			HashMap<Material, Double> delivery = biz.getInventoryAgent().requestMaterials(request);
			//System.out.println("plannedBatchSize: " + plannedBatchSize + ", maxProduction: " + maxProduction);
			if(plannedBatchSize <= maxProduction){
				
				ProdJob job = new ProdJob(currentTick, plannedBatchSize, productionTime);
				//System.out.println("ProdJob: " + job.getSize());
				productionPipeLine.add(job);
			}
			else{
				ProdJob job = new ProdJob(currentTick, maxProduction, productionTime);
				//System.out.println("ProdJob: " + job.getSize());
				productionPipeLine.add(job);
				//TODO Fehlmenge behandeln
			}
		}
	}
	*/
	
	
	
	
	/**
	 * Berechnet die auf Grund von: Input Lagerbeständen und productionCapacity maximal mögliche Produktionsmenge.
	 * @return maximal mögliche Produktionsmenge
	 */
	public double calcMaxProduction(double plannedBatchSize){
		ArrayList<Double> quotients = new ArrayList<Double>();
		HashMap<Material, Double> bom = planAgent.getBoM();
		
		for(Material material : bom.keySet()){
			//System.out.println("debug: Biz: " + biz.getId() + "Link: " + link.getId());
			double quotient = biz.getinventoryOpsModule().getInventoryLevel(material)/bom.get(material);
			quotients.add(quotient);
		}
		double max = 0;
		for(Double i : quotients){
			if(i > max) max = i;
		}
		return Math.min(plannedBatchSize, max);
	}
	
	
	
	/**
	 * Berechnet zu einem bestimmten Produktionsoutput den Ressourcenbedarf
	 * @param output gewünschter Produktionsoutput
	 * @return ArrayList mit den Ressourcenbedarfen
	 */
	public HashMap<Material, Double> calcRessourceDemand(double output){
		HashMap<Material, Double> demand = new HashMap<Material, Double>();
		HashMap<Material, Double> bom = planAgent.getBoM();
		
		for(Material material : bom.keySet()){
			double d = bom.get(material)*output;
			demand.put(material, d);
		}
		return demand;
	}
	
	public void handProductionDueList(TreeMap<Integer, Double> dueList){
		this.productionDueList = dueList;
	}
	
	public  TreeMap<Integer, Double> getProductionDueList(){
		return this.productionDueList;
	}
	
	public int getProductionTime(){
		return this.productionTime;
	}
	
	public HashMap<Material, Double> getBillOfMaterial(){
		return this.billOfMaterial;
	}

}
