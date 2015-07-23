package setups;

import inventoryPlannningAlgorithm.BookbinderTan;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.random.RandomHelper;
import lotSizingAlgorithms.CapacitatedSilverMeal;
import lotSizingAlgorithms.SilverMeal;
import modules.Link;
import agents.Business;
import agents.Customer;
import agents.Manufacturer;
import agents.MaterialSource;
import agents.Node;
import agents.Retailer;
import artefacts.Material;
import demandPattern.Constant;
import demandPattern.NormalDistribution;

public class OneManufacturerNormal extends Setup{
		
	public OneManufacturerNormal(){
		
		super();
		
		sources.add(new MaterialSource(this, 3));
		manufacturers.add(new Manufacturer(this, 2));
		customers.add(new Customer(this, new NormalDistribution(10.0, 1.0)));
		
		links.add(new Link(sources.get(0), manufacturers.get(0)));
		links.add(new Link(manufacturers.get(0), customers.get(0)));
		
		//Define Product
		
		//init
		init();
		
		//Parameters
		MaterialSource source = sources.get(0);
		source.setCapacity(100);
		
		for(Manufacturer m : manufacturers){
			
			m.setPlanningPeriod(10);
			
			//Bill of Material
			HashMap<Material, Double> billOfMaterial = new HashMap<Material, Double>();
			billOfMaterial.put(sources.get(0).getProduct(), 1.0);
			m.setBillOfMaterial(billOfMaterial);
			
			//Inventory
			m.setHoldingCost(2);
			m.setServiceLevel(0.95);
			m.setInventoryPlanningAlgorithm(new BookbinderTan());
			
			//Order
			m.setOrderPlanningAlgorithm(new SilverMeal(40, 2));
			
			//Production			
			m.setProductionCapacity(50);
			m.setProductionTime(2);
			m.setSetUpCost(50);
			m.setLotSizingAlgorithm(new CapacitatedSilverMeal(50, 2, 50));
		}
		
		for(Link link : links){
			link.setFixCost(40);
			link.setDistrDuration(RandomHelper.createUniform(1, 1));
		}
		
	}

}
