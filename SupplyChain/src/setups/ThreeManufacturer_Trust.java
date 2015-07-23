package setups;

import java.util.ArrayList;
import java.util.HashMap;

import repast.simphony.random.RandomHelper;
import lotSizingAlgorithms.CapacitatedSilverMeal;
import modules.Link;
import agents.Business;
import agents.Customer;
import agents.Manufacturer;
import agents.MaterialSource;
import agents.Node;
import agents.Retailer;
import artefacts.Material;
import demandPattern.Constant;

public class ThreeManufacturer_Trust extends Setup{
		
	public ThreeManufacturer_Trust(){
		
		super();
		
		sources.add(new MaterialSource(this, 5));
		manufacturers.add(new Manufacturer(this, 4));
		manufacturers.add(new Manufacturer(this, 3));
		manufacturers.add(new Manufacturer(this, 2));
		customers.add(new Customer(this, new Constant(10.0)));
		
		links.add(new Link(sources.get(0), manufacturers.get(0)));
		links.add(new Link(manufacturers.get(0), manufacturers.get(1)));
		links.add(new Link(manufacturers.get(1), manufacturers.get(2)));
		links.add(new Link(manufacturers.get(2), customers.get(0)));
		
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
			Material upstrProduct = m.getUpstrLinks().get(0).getMaterial();
			billOfMaterial.put(upstrProduct, 1.0);
			m.setBillOfMaterial(billOfMaterial);
			
			//Information Sharing
			m.setCustomerDemandData();
			m.setTrustFeedback();
			
			//Inventory
			m.setHoldingCost(2);
			m.setServiceLevel(0.95);
			
			//Production			
			m.setProductionCapacity(20);
			m.setProductionTime(2);
			m.setSetUpCost(50);
			m.setLotSizingAlgorithm(new CapacitatedSilverMeal(50, 2, 20));
		}
		
		for(Link link : links){
			link.setFixCost(40);
			link.setDistrDuration(RandomHelper.createUniform(1, 1));
		}
		
	}

}
