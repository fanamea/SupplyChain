package artefacts;

import java.util.ArrayList;

public class OrderReq {
	
	private static int count;
	
	private int Id;
	private Material material;
	private int date;
	private double size;
	boolean placed;
	Order order;
	
	public OrderReq(Material material, int d, double s){
		this.Id = count++;
		this.material = material;
		this.date = d;
		this.size = s;
		placed = false;
	}
	
	public int getId(){
		return this.Id;
	}
	
	public Integer getDate(){
		return this.date;
	}
	
	public Material getMaterial(){
		return this.material;
	}
	
	public double getSize(){
		return this.size;
	}
	
	public void setPlaced(){
		this.placed = true;
	}
	
	public boolean istPlaced(){
		return placed;
	}
	
	public void setOrder(Order order){
		this.order = order;
	}
	
	public String toString(){
		return "OrderRequest: ID-" + this.Id + ", Date-" + this.date + ", Size-" + this.size;
	}

}
