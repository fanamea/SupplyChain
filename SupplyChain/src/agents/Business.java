package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import net.sourceforge.openforecast.DataSet;
import modules.*;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.DemandData;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public abstract class Business extends Node{	
	
	protected int planningPeriod;
	
	public Business(int tier){
		super(tier);
	}
	
	public abstract void initNode();	
	public abstract void plan();	
	public abstract void prepareTick();	
	public abstract void receiveShipments();	
	public abstract void placeOrders();	
	public abstract void fetchOrders();	
	public abstract void dispatchShipments();
	
	public abstract OrderOpsModule getOrderOpsModule();
	public abstract DeliveryModule getDeliveryModule();
	public abstract InventoryOpsModule getInventoryOpsModule();
	public abstract InventoryPlanModule getInventoryPlanModule();
	public abstract PlanningMethods getPlanningMethods();
	public abstract ForecastModule getForecastModule();
	public abstract OrderPlanModule getOrderPlanModule();
	public abstract ProductionOpsModule getProductionOpsModule();
	public abstract InformationModule getInformationModule();
	
	//Setup
	public abstract void setHoldingCost(double holdingCost);
	
	//Information Sharing
	public abstract void handExtDemandData(DemandData demandData);
	public abstract void setCustomerDemandData();
	
	//Analysis
	public abstract double getOrderVariance();
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		upstrLinks.add(b);
	}
	
	public void setPlanningPeriod(int period){
		this.planningPeriod = period;
	}

}
