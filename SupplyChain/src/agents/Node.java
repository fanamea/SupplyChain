package agents;

import java.util.ArrayList;

import artefacts.Material;
import modules.Link;

public abstract class Node {
	
	static int idCount;
	
	private int Id;
	protected int tier;		//1=Customer
	protected ArrayList<Link> downstrLinks;
	protected ArrayList<Link> upstrLinks;
	protected Material endProduct;
	
	public abstract void initNode();
	public abstract String getInformationString();
	
	
	public Node(int tier){
		this.Id = idCount++;
		this.tier = tier;
		this.downstrLinks = new ArrayList<Link>();
		this.upstrLinks = new ArrayList<Link>();
		this.endProduct = new Material("");
	};
	
	public void addUpstrLink(Link link){
		this.upstrLinks.add(link);
	}
	
	public void addDownstrLink(Link link){
		this.downstrLinks.add(link);
	}
	
	public int getId(){
		return this.Id;
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
	
	public Material getEndProduct(){
		return this.endProduct;
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
