package setups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import modules.Link;
import demandPattern.Constant;
import demandPattern.NormalDistribution;
import demandPattern.RandomWalk;
import agents.Business;
import agents.Customer;
import agents.Manufacturer;
import agents.MaterialSource;
import agents.Node;
import agents.Retailer;
import artefacts.TierComparator;
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public abstract class Setup {
	
	protected ArrayList<MaterialSource> sources;
	protected ArrayList<Business> businesses;
	protected ArrayList<Manufacturer> manufacturers;
	protected ArrayList<Retailer> retailers;
	protected ArrayList<Customer> customers;
	protected ArrayList<Link> links;
	protected ArrayList<ArrayList<Integer>> tiers;
	protected boolean shareCustomerDemandData;
	
	public Setup(){
		sources = new ArrayList<MaterialSource>();
		manufacturers = new ArrayList<Manufacturer>();
		retailers = new ArrayList<Retailer>();
		customers = new ArrayList<Customer>();
		links = new ArrayList<Link>();		
	}
		
	public void init(){
		
		for(MaterialSource source : sources){
			source.initNode();
		}
		for(Retailer retailer : retailers){
			retailer.initNode();
		}
		for(Manufacturer m : manufacturers){
			m.initNode();
		}
		for(Customer customer : customers){
			customer.initNode();
		}
		
		//init information sharing
		for(Retailer retailer : retailers){
			retailer.setCustomerDemandData();
		}
		for(Manufacturer m : manufacturers){
			m.setCustomerDemandData();
		}
		
		Collections.sort(retailers, new TierComparator());
		
	}
	
	@ScheduledMethod(start=1, interval=1, priority=12)
	public void prepareTick(){		
		for(Retailer ret : this.retailers){
			ret.prepareTick();
		}		
	}
	
	@ScheduledMethod(start=1, interval=0, priority=11)
	public void planFirstPeriods(){		
		for(Retailer ret : this.retailers){
			ret.planFirstPeriods();
		}	
	}
	
	@ScheduledMethod(start=1, interval=1, priority=10)
	public void activateAgents(){
		for(Customer customer : this.customers){
			customer.placeOrder();
		}
		
		for(Retailer ret : this.retailers){		
			ret.receiveShipments();
			ret.fetchOrders();
			ret.dispatchShipments();
			ret.plan();
			ret.placeOrders();
			ret.collectData();
			ret.collectRetailerData();
		}	
		
		for(MaterialSource source : this.sources){
			source.shipOrders();
		}
	}
	
	
	
	public ArrayList<Business> getBusinesses(){
		ArrayList<Business> businesses = new ArrayList<Business>();
		businesses.addAll(retailers);
		businesses.addAll(manufacturers);
		return businesses;
	}	
	
	public ArrayList<Customer> getCustomers(){
		return this.customers;
	}
	
	public ArrayList<Manufacturer> getManufacturers(){
		return this.manufacturers;
	}
	
	public ArrayList<Retailer> getRetailers(){
		return this.retailers;
	}
	
	public ArrayList<MaterialSource> getMaterialSources(){
		return this.sources;
	}
	
	public ArrayList<Link> getLinks(){
		return this.links;
	}
	
	public void print(){
		
	}
	
	//@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void printInformation(){
		String string = "Date: " + (int)RepastEssentials.GetTickCount() + "\n";
		for(Manufacturer man : manufacturers){
			string += man.getInformationString();
		}
		for(Retailer ret: retailers){
			string += ret.getInformationString();
		}
		string += "\n";
		string += "MATERIAL SOURCE: " + sources.get(0).getInformationString();
		string += "\n \n";
		System.out.print(string);
	}

}
