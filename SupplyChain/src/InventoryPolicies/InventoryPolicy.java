package InventoryPolicies;

import agents.Business;
import modules.Inventory;
import modules.PlanningMethods;

public abstract class InventoryPolicy{
	
	Business biz;
	PlanningMethods planningTechniques;
	Inventory inventory;
	
	public InventoryPolicy(Business biz, Inventory inventory){
		this.biz = biz;
		this.planningTechniques = biz.getPlanningMethods();
		this.inventory = inventory;
	}
	
	public abstract void recalcParams();
	public abstract double getOrder(int currentTick, double inventoryPosition);
	public abstract String getParameterString();

}
