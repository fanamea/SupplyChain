package agents;

import java.util.ArrayList;

import supplyChain.Link;

public abstract class Node {
	
	static int idCount;
	
	protected int Id;
	protected int tier;		//1=Customer
	protected ArrayList<Link> downstrLinks;
	protected ArrayList<Link> upstrLinks;
	
	public abstract void initNode();
	public abstract String getInformationString();
	
	
	public Node(int tier){
		this.Id = idCount++;
		this.tier = tier;
		this.downstrLinks = new ArrayList<Link>();
		this.upstrLinks = new ArrayList<Link>();
	};
	
	protected void addUpstrLink(Link link){
		this.upstrLinks.add(link);
	}
	
	protected void addDownstrLink(Link link){
		this.downstrLinks.add(link);
	}
	
	protected int getId(){
		return this.Id;
	}
	
	protected int getTier(){
		return this.tier;
	}
	
	public ArrayList<Link> getUpstrLinks(){
		return this.upstrLinks;
	}
	
	public ArrayList<Link> getDownstrLinks(){
		return this.downstrLinks;
	}
	
	public void print(){
		System.out.println("Node: " + this.Id + ", Tier: " + + this.tier + ", " + getLinksString());
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
