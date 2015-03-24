package simulation;

import java.util.ArrayList;

import modules.Link;
import demandPattern.NormalDistribution;
import agents.Business;
import agents.Customer;
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
	
	public void retailerSetUp(){
		sources.add(new MaterialSource(4));
		businesses.add(new Retailer(3));
		businesses.add(new Retailer(2));
		customers.add(new Customer(new NormalDistribution(10.0, 5.0)));
		
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
	
	public void exampleSetUp(){
		
		for(int i=0; i<11; i++){
			int tier;
			if(i>7) tier = 4;
			else if(i>5) tier = 3;
			else if(i>2) tier = 2;
			else tier = 1;
			
			if(tier==1) customers.add(new Customer(new NormalDistribution(10.0, 1.0)));
			else businesses.add(new Business(tier));
		}
		
		links.add(new Link(businesses.get(0), customers.get(0)));
		links.add(new Link(businesses.get(1), customers.get(1)));
		links.add(new Link(businesses.get(2), customers.get(2)));
		links.add(new Link(businesses.get(3), businesses.get(0)));
		links.add(new Link(businesses.get(3), businesses.get(1)));
		links.add(new Link(businesses.get(4), businesses.get(2)));
		links.add(new Link(businesses.get(5), businesses.get(3)));
		links.add(new Link(businesses.get(6), businesses.get(3)));
		links.add(new Link(businesses.get(6), businesses.get(4)));
		links.add(new Link(businesses.get(7), businesses.get(4)));
		
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
	
	//@ScheduledMethod(start = 1, interval = 1, priority = 1)
	public void printInformation(){
		String string = "Date: " + (int)RepastEssentials.GetTickCount() + "\n";
		for(Node node : nodes){
			string += node.getInformationString();
		}
		string += "\n";
		for(Link link : links){
			string += link.getAmountInformation() + "\n";
		}
		string += "\n \n";
		System.out.print(string);
	}

}
