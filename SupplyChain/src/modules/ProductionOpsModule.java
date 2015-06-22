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
	
	
	private CopyOnWriteArrayList<ProdJob> productionPipeLine;
	private CopyOnWriteArrayList<ProdRequest> prodRequestPipeLine;
	
	public ProductionOpsModule(Manufacturer biz){
		this.biz = biz;
		
		productionPipeLine = new CopyOnWriteArrayList<ProdJob>();
		prodRequestPipeLine = new CopyOnWriteArrayList<ProdRequest>();
	}
	
	
	public void startProdJobs(){
		////System.out.println("startProdJobs");
		int currentTick = (int)RepastEssentials.GetTickCount();
		double productionCounter = 0;		
		double capacityLeft = biz.getProductionPlanModule().getProductionCapacity();
		int productionTime = biz.getProductionPlanModule().getProductionTime();
		boolean isProdRequest = false;
		double maxProduction = 0;
		double batchSize = 0;
		boolean condition;
		ArrayList<ProdRequest> temp = new ArrayList<ProdRequest>(prodRequestPipeLine);
		Collections.sort(temp, new ProdRequestComparator());
		this.prodRequestPipeLine.clear();
		this.prodRequestPipeLine.addAll(temp);
		do{
			if(!prodRequestPipeLine.isEmpty()){
				ProdRequest pReq = prodRequestPipeLine.get(0);
				isProdRequest = pReq.getDate()<=currentTick;
				if(isProdRequest){
					maxProduction = calcMaxProduction(pReq.getBoM());
					//System.out.println("CapacityLeft: " + capacityLeft + ", maxProduction: " + maxProduction);
					batchSize = Math.min(maxProduction, pReq.getShortageSent());
					//System.out.println("batchSize: " + batchSize);
					if(batchSize>0){
						batchSize = Math.min(capacityLeft, batchSize);
						////System.out.println("batchSize: " + batchSize);
						HashMap<Material, Double> request = calcRessourceDemand(batchSize, pReq.getBoM());
						biz.getInventoryOpsModule().requestMaterials(request);						
						ProdJob job = new ProdJob(currentTick, batchSize, productionTime, pReq);
						pReq.addProdJob(job);
						pReq.incrSent(batchSize);
						capacityLeft -= batchSize;
						productionPipeLine.add(job);
					}
					if(pReq.isSent()){
						prodRequestPipeLine.remove(pReq);
					}
				}
			}
			//System.out.println("Condition: !empty: " + !prodRequestPipeLine.isEmpty() + ", date: " + isProdRequest + ", capacityLeft: " + capacityLeft + ", batchSize: " + batchSize);
			condition = !prodRequestPipeLine.isEmpty() && isProdRequest && capacityLeft>0 && batchSize>0;
		}while(condition);
		
		
		////System.out.println("ProdRequestPipeLine: " + prodRequestPipeLine);
		////System.out.println("ProductionPipeLine: " + productionPipeLine);
	}
		
	/**
	 * 
	 * @return Liste von ProductionJobs, die im aktuellen Tick fertig werden.
	 */
	public HashMap<Material, Double> getArrivingProduction(){
		HashMap<Material, Double> output = new HashMap<Material, Double>();
		output.put(biz.getProduct(), 0.0);
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(ProdJob job : productionPipeLine){			
			if(job.getDate() + job.getLeadTime() <= currentTick){
				output.put(biz.getProduct(), output.get(biz.getProduct())+job.getSize());
				productionPipeLine.remove(job);
			}
		}
		
		////System.out.println("getArrivingProduction");
		////System.out.println("ProdRequestPipeLine: " + prodRequestPipeLine);
		////System.out.println("ProductionPipeLine: " + productionPipeLine);
		
		return output;		
	}
	
	public double getBacklogStart(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		double sum = 0;
		for(ProdRequest pReq : prodRequestPipeLine){
			if(pReq.getDate()<=currentTick){
				sum += pReq.getShortageSent();
			}
		}
		return sum;
	}
	
	public double getBacklogEnd(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		int productionTime = biz.getProductionPlanModule().getProductionTime();
		double sum = 0;
		for(ProdRequest pReq : prodRequestPipeLine){
			if(pReq.getDate()+productionTime <= currentTick){
				sum += pReq.getShortageArrived();
			}
		}
		for(ProdJob job : productionPipeLine){
			if(job.getDate()+job.getLeadTime() <= currentTick){
				sum += job.getSize();
			}
		}
		return sum;
	}
	
	/**
	 * including processing production
	 * @return
	 */
	public double getFutureProduction(){
		double sum = 0;
		for(ProdRequest pReq : this.prodRequestPipeLine){
			sum += pReq.getShortageSent();
		}
		for(ProdJob job : this.productionPipeLine){
			sum += job.getSize();
		}
		return sum;
	}
	
	public double getProcessingProduction(){
		double currentTick = (int)RepastEssentials.GetTickCount();
		double sum = 0;
		for(ProdJob job : productionPipeLine){
			sum += job.getSize();
		}
		return sum;
	}	
	
	/**
	 * Berechnet die auf Grund von: Input Lagerbeständen die maximal mögliche Produktionsmenge.
	 * @return maximal mögliche Produktionsmenge
	 */
	public double calcMaxProduction(HashMap<Material, Double> boM){
		ArrayList<Double> quotients = new ArrayList<Double>();		
		
		for(Material material : boM.keySet()){
			////System.out.println("debug: Biz: " + biz.getId() + "Link: " + link.getId());
			double quotient = biz.getInventoryOpsModule().getInventoryLevel(material)/boM.get(material);
			quotients.add(quotient);
		}
		double max = 0;
		for(Double i : quotients){
			if(i > max) max = i;
		}
		return max;
	}
	
	
	
	/**
	 * Berechnet zu einem bestimmten Produktionsoutput den Ressourcenbedarf
	 * @param output gewünschter Produktionsoutput
	 * @return ArrayList mit den Ressourcenbedarfen
	 */
	public HashMap<Material, Double> calcRessourceDemand(double output, HashMap<Material, Double> boM){
		HashMap<Material, Double> demand = new HashMap<Material, Double>();
		
		for(Material material : boM.keySet()){
			double d = boM.get(material)*output;
			demand.put(material, d);
		}
		return demand;
	}
	
	public CopyOnWriteArrayList<ProdRequest> getProdReqPipeLine(){
		return this.prodRequestPipeLine;
	}
	
	public String getInformationString(){
		String string = "";
		string += "      ProdRequestPipeLine: " + prodRequestPipeLine + "\n";
		string += "      ProductionPipeLine: " + productionPipeLine + "\n";
		string += "      Processing Production: " + getProcessingProduction() + "\n";
		string += "      Backlog: " + getBacklogStart();
		return string;
	}

}
