package simulation;

import java.util.ArrayList;

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
import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Setup {
	
	ArrayList<MaterialSource> sources;
	ArrayList<Business> businesses;
	ArrayList<Customer> customers;
	ArrayList<Link> links;
	ArrayList<ArrayList<Integer>> tiers;
	ArrayList<ArrayList<Integer>> structure;
	
	public Setup(){
		sources = new ArrayList<MaterialSource>();
		structure = new ArrayList<ArrayList<Integer>>();
		businesses = new ArrayList<Business>();
		customers = new ArrayList<Customer>();
		links = new ArrayList<Link>();
		
	}
	
	public void mixedSetUp(){
	}
	
	public void retailerSetUp(){
		sources.add(new MaterialSource(4));
		businesses.add(new Retailer(3));
		businesses.add(new Retailer(2));
		customers.add(new Customer(new RandomWalk(10.0, 0.0, 2.0)));
		
		links.add(new Link(sources.get(0), businesses.get(0)));
		links.add(new Link(businesses.get(0), businesses.get(1)));
		links.add(new Link(businesses.get(1), customers.get(0)));
		
		for(MaterialSource source : sources){
			source.initNode();
		}
		for(Node business : businesses){
			business.initNode();
		}
		for(Customer customer : customers){
			customer.initNode();
		}
	}
	
	public void productionSetUp(){
		
		sources.add(new MaterialSource(3));
		businesses.add(new Manufacturer(2));
		customers.add(new Customer(new Constant(10.0)));
		
		links.add(new Link(sources.get(0), businesses.get(0)));
		links.add(new Link(businesses.get(0), customers.get(0)));
		
		for(MaterialSource source : sources){
			source.initNode();
		}
		for(Node business : businesses){
			business.initNode();
		}
		for(Customer customer : customers){
			customer.initNode();
		}
		
	}
	
	public ArrayList<Business> getBusinesses(){
		return this.businesses;
	}
	
	public ArrayList<Customer> getCustomers(){
		return this.customers;
	}
	
	public ArrayList<MaterialSource> getMaterialSources(){
		return this.sources;
	}
	
	public boolean checkLink(int a, int b){
		for(Link link : links){
			int idUp = link.getUpstrNode().getId();
			int idDown = link.getDownstrNode().getId();
			
			if((idUp==a && idDown==b) || (idUp==b && idDown == a)){
				return true;
			}
			else return false;
		}
		return false;
		
	}
	
	public void addNode(int pos){
		
		structure.add(pos, new ArrayList<Integer>());
		
		for(int i = 0; i < structure.size()-1; i++){
			structure.get(pos).add(i, 0);
		}
		for(int i = 0; i < structure.size(); i++){
			structure.get(i).add(pos, 0);
		}
	}
	
	public void addLink(int a, int b, int tier){
		structure.get(a).set(b, tier);
		structure.get(b).set(a, tier);
	}
	
	public void removeLink(int a, int b){
		structure.get(a).set(b, 0);
		structure.get(b).set(a, 0);
	}
	
	public boolean isLinked(int a, int b){
		return (structure.get(a).get(b) != 0);
	}
	
	public ArrayList<Link> getLinks(){
		return this.links;
	}
	
	public void print(){
		
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void printInformation(){
		String string = "Date: " + (int)RepastEssentials.GetTickCount() + "\n";
		for(Business biz : businesses){
			string += biz.getInformationString();
		}
		string += "\n";
		string += "MATERIAL SOURCE: " + sources.get(0).getInformationString();
		string += "\n \n";
		System.out.print(string);
	}

}
