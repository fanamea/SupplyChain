package agents;

import java.util.ArrayList;
import java.util.TreeMap;

import modules.*;
import demandPattern.DemandPattern;
import demandPattern.NormalDistribution;
import artefacts.Material;
import artefacts.Order;
import artefacts.Shipment;
import InventoryPolicies.InvPolicies;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public abstract class Business extends Node{	
	
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
	
	public void addDownstrPartner(Link b){
		downstrLinks.add(b);
	}
	
	public void addUpstrPartner(Link b){
		upstrLinks.add(b);
	}

}
