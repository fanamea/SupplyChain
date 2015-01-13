package supplyChain;

import java.util.ArrayList;

import repast.simphony.engine.schedule.ScheduledMethod;
import repast.simphony.essentials.RepastEssentials;

public class Setup {
	
	
	ArrayList<Node> nodes;
	ArrayList<Link> links;
	ArrayList<ArrayList<Integer>> tiers;
	ArrayList<ArrayList<Integer>> structure;
	
	public Setup(){
		structure = new ArrayList<ArrayList<Integer>>();
		nodes = new ArrayList<Node>();
		links = new ArrayList<Link>();
		
	}
	
	public void exampleSetUp(){
		
		for(int i=0; i<11; i++){
			int tier;
			if(i>7) tier = 4;
			else if(i>5) tier = 3;
			else if(i>2) tier = 2;
			else tier = 1;
			
			if(tier==1) nodes.add(new Customer());
			else nodes.add(new Business(tier));
		}
		
		links.add(new Link(nodes.get(3), nodes.get(0)));
		links.add(new Link(nodes.get(4), nodes.get(1)));
		links.add(new Link(nodes.get(5), nodes.get(2)));
		links.add(new Link(nodes.get(6), nodes.get(3)));
		links.add(new Link(nodes.get(6), nodes.get(4)));
		links.add(new Link(nodes.get(7), nodes.get(5)));
		links.add(new Link(nodes.get(8), nodes.get(6)));
		links.add(new Link(nodes.get(9), nodes.get(6)));
		links.add(new Link(nodes.get(9), nodes.get(7)));
		links.add(new Link(nodes.get(10), nodes.get(7)));
		
	}
	
	public void setUpAgents(){
		
		//Businesses erstellen
		for(int i=0; i<structure.size(); i++){
			for(int j=0; j<structure.size(); j++){
				int tier = structure.get(i).get(j);
				if(tier>1){
					nodes.add(new Business(tier));
					break;
				}
				else if(i==1){
					nodes.add(new Customer());
					break;
				}
			}
		}
		//Links erstellen
		for(int i=0; i<structure.size(); i++){
			for(int j=0; j<structure.size(); j++){
				if(structure.get(i).get(j)>0 && !checkLink(i,j)){
					Link newLink = new Link(nodes.get(i), nodes.get(j));
					links.add(newLink);
					if(i<j){
						nodes.get(i).addDownstrLink(newLink);
						nodes.get(j).addUpstrLink(newLink);
					}
					else{
						nodes.get(i).addUpstrLink(newLink);
						nodes.get(j).addDownstrLink(newLink);
					}
				}
			}
		}
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
	
	public ArrayList<Node> getNodes(){
		return this.nodes;
	}
	
	public ArrayList<Link> getLinks(){
		return this.links;
	}
	
	public void print(){
		for(Node node : nodes){
			node.print();
		}
	}
	
	@ScheduledMethod(start = 1, interval = 1, priority = 11)
	public void printInformation(){
		String string = "Date: " + (int)RepastEssentials.GetTickCount();
		for(Node node : nodes){
			string += node.getInformationString();
		}
		string += "\n";
		for(Link link : links){
			string += link.getInformationString();
		}
		string += "\n \n";
		System.out.print(string);
	}

}
