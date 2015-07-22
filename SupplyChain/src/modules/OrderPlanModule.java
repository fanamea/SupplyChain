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
	
	private double serviceLevel;
	
	
	public OrderPlanModule(Business biz){
		this.biz = biz;
		this.planningTechniques = new PlanningMethods();
		this.orderReqPipeLine = biz.getOrderOpsModule().getOrderReqPipeLine();
		this.suppliers = biz.getOrderOpsModule().getSuppliers();
		this.lotSizingAlgo = new SilverMeal(0, 0);
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
		
		double meanLeadTime = biz.getInformationModule().getMeanLeadTime(supplier);
		double sdLeadTime = biz.getInformationModule().getSDLeadTime(supplier);
		double safetyLeadTime = planningTechniques.calcSafetyLeadTime(sdLeadTime, serviceLevel);
		int totalLeadTime = (int)Math.ceil(meanLeadTime + safetyLeadTime);
		
		biz.getInformationModule().setPlanningLeadTimeShipments(totalLeadTime);
		
		for(Integer i : lotPlan.keySet()){
			if(lotPlan.get(i)>0){
				//i-totalLeadTime-3?!?!?!?!
				OrderReq orderReq = new OrderReq(material, i-totalLeadTime, dueList.get(i));
				//System.out.println("NEW ORDERREQ: ID: " + orderReq.getId() + ", Material: " + material + "i: " + i + ", totalLeadTime: " + totalLeadTime + ", size: " + dueList.get(i));
				orderReqs.add(orderReq);
			}
			biz.getInformationModule().putOrder(i-totalLeadTime, lotPlan.get(i));
		}
		return orderReqs;		
	}
	
	/**
	 * Only works for only one supplier per material
	 */
	public Link getLink(Material material){
		return this.suppliers.get(material).get(0);
	}
	
	public double getOrderFixCost(Material material){
		Link supplier = suppliers.get(material).get(0);
		return supplier.getFixCost();
	}
	
	public void setLotSizingAlgorithm(LotSizingAlgorithm algo){
		this.lotSizingAlgo = algo;
	}
	
	public void setServiceLevel(double serviceLevel){
		this.serviceLevel = serviceLevel;
	}

}
