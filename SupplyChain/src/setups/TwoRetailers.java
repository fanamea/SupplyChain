package setups;

import java.util.ArrayList;

import repast.simphony.random.RandomHelper;
import modules.Link;
import InventoryPolicies.ContinuousOUT;
import InventoryPolicies.PeriodicOUT;
import InventoryPolicies.PeriodicOUT_Returns;
import agents.Business;
import agents.Customer;
import agents.MaterialSource;
import agents.Node;
import agents.Retailer;
import demandPattern.Constant;
import demandPattern.NormalDistribution;
import demandPattern.RandomWalk;

public class TwoRetailers extends Setup{
	
	
	public TwoRetailers(){
		
		super();
		
		//Structure
		sources.add(new MaterialSource(this, 4));
		retailers.add(new Retailer(this, 3));
		retailers.add(new Retailer(this, 2));
		customers.add(new Customer(this, new NormalDistribution(10.0, 1.0)));
		
		//Define Product
		
		//Links
		
		links.add(new Link(sources.get(0), retailers.get(0)));
		links.add(new Link(retailers.get(0), retailers.get(1)));
		links.add(new Link(retailers.get(1), customers.get(0)));		
		
		//Init
		init();
		
		
		//Parameters
		MaterialSource source = sources.get(0);
		source.setCapacity(100);
		
		for(Retailer ret: retailers){
			
			ret.setPlanningPeriod(10);
			
			//Inventory
			ret.setHoldingCost(2);
			ret.setServiceLevel(0.95);
			ret.setInventoryPolicy(new PeriodicOUT_Returns());
		}
		
		for(Link link : links){
			link.setFixCost(40);
			link.setDistrDuration(RandomHelper.createUniform(1,1));
		}
		
	}
	
}
