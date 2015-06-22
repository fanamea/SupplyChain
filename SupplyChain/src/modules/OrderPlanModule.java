package modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import lotSizingAlgorithms.LotSizingAlgorithm;
import lotSizingAlgorithms.SilverMeal;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import agents.Business;
import artefacts.Material;
import artefacts.OrderReq;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public class OrderPlanModule {
	
	private Business biz;
	private PlanningMethods planningTechniques;
	
	private HashMap<Material, ArrayList<Link>> suppliers;
	private HashMap<Material, CopyOnWriteArrayList<OrderReq>> orderReqPipeLine;
	private LotSizingAlgorithm lotSizingAlgo;
	
	private HashMap<Link, DescriptiveStatistics> leadTimeData;
	
	private double serviceLevel;
	
	
	public OrderPlanModule(Business biz){
		this.biz = biz;
		this.planningTechniques = new PlanningMethods();
		this.orderReqPipeLine = biz.getOrderOpsModule().getOrderReqPipeLine();
		this.suppliers = biz.getOrderOpsModule().getSuppliers();
		this.lotSizingAlgo = new SilverMeal(0, 0);
		this.leadTimeData = biz.getOrderOpsModule().getLeadTimeData();
	}
	
	public void plan(){
		placeAllOrderReqs();
	}
	
	public void placeAllOrderReqs(){
		for(Material material : orderReqPipeLine.keySet()){
			ArrayList<OrderReq> newOrderReqs = getOrderReqs(material);
			orderReqPipeLine.get(material).addAll(newOrderReqs);
		}
	}
	
	public ArrayList<OrderReq> getOrderReqs(Material material){
		ArrayList<OrderReq> orderReqs = new ArrayList<OrderReq>();
		TreeMap<Integer, Double> dueList = biz.getInventoryPlanModule().getInventoryDueList(material);
		Link supplier = suppliers.get(material).get(0);
		double fixCost = supplier.getFixCost();
		double holdingCost = biz.getInventoryPlanModule().getInventory(material).getHoldingCost();
		this.lotSizingAlgo.setFixCost(fixCost);
		this.lotSizingAlgo.setHoldingCost(holdingCost);
		TreeMap<Integer, Double> lotPlan = lotSizingAlgo.calcLotPlan(dueList);
		
		double meanLeadTime = calcMeanLeadTime(supplier);
		double sdLeadTime = calcSDLeadTime(supplier);
		double safetyLeadTime = planningTechniques.calcSafetyLeadTime(sdLeadTime, serviceLevel);
		int totalLeadTime = (int)Math.ceil(meanLeadTime + safetyLeadTime);
		
		for(Integer i : lotPlan.keySet()){
			if(lotPlan.get(i)>0){
				OrderReq orderReq = new OrderReq(material, i-totalLeadTime-3, dueList.get(i));
				System.out.println("NEW ORDERREQ: ID: " + orderReq.getId() + ", Material: " + material + "i: " + i + ", totalLeadTime: " + totalLeadTime + ", size: " + dueList.get(i));
				orderReqs.add(orderReq);
			}
		}
		return orderReqs;		
	}
	
	public double getOrderFixCost(Material material){
		Link supplier = suppliers.get(material).get(0);
		return supplier.getFixCost();
	}
	
	public double calcMeanLeadTime(Link link){
		return leadTimeData.get(link).getMean();
	}
	
	public double calcSDLeadTime(Link link){
		return leadTimeData.get(link).getStandardDeviation();
	}
	
	public double calcSDLeadTime(Material material){
		return leadTimeData.get(suppliers.get(material).get(0)).getStandardDeviation();
	}
	
	public double calcMeanLeadTime(Material material){
		double min = calcMeanLeadTime(suppliers.get(material).get(0));		
		return min;
	}
	
	public void setServiceLevel(double serviceLevel){
		this.serviceLevel = serviceLevel;
	}

}
