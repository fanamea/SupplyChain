package agents;

import java.util.ArrayList;

import setups.Setup;
import artefacts.DemandData;
import artefacts.Material;
import modules.Link;

public abstract class Node {
	
	static int idCount;
	
	protected int Id;
	protected Setup setup;
	protected int tier;		//1=Customer
	protected ArrayList<Link> downstrLinks;
	protected ArrayList<Link> upstrLinks;
	protected Material product;
	
	public abstract void initNode();
	public abstract String getInformationString();
	public abstract DemandData searchCustomerDemandData();
	
	
	public Node(Setup setup, int tier){
		this.Id = idCount++;
		this.setup = setup;
		this.tier = tier;
		this.downstrLinks = new ArrayList<Link>();
		this.upstrLinks = new ArrayList<Link>();
	};
	
	public void addUpstrLink(Link link){
		this.upstrLinks.add(link);
	}
	
	public void addDownstrLink(Link link){
		this.downstrLinks.add(link);
	}
	
	public String getId(){
		return Integer.toString(this.Id);
	}
	
	public int getTier(){
		return this.tier;
	}
	
	public ArrayList<Link> getUpstrLinks(){
		return this.upstrLinks;
	}
	
	public ArrayList<Link> getDownstrLinks(){
		return this.downstrLinks;
	}
	
	public Material getProduct(){
		if(this.product==null){
			this.product = this.upstrLinks.get(0).getMaterial();
		}
		return this.product;
	}
	
	public Customer searchCustomer(){
		if(this.tier==1){
			return (Customer)this;
		}
		else{
			return (Customer)this.downstrLinks.get(0).getDownstrNode();
		}
	}
	
	public void print(){
		//System.out.println("Node: " + this.Id + ", Tier: " + + this.tier + ", " + getLinksString());
	}
	
	private String getLinksString(){
		String string = "Links up: [";
		for(Link link : upstrLinks){
			string += link.getUpstrNode().getId() + ", ";
		}
		
		string+= "], Links down: [";
		for(Link link : downstrLinks){
			string += link.getDownstrNode().getId() + ", ";
		}
		
		string += "]";
		
		return string;	
	}

}
