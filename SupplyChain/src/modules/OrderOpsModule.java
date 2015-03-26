package modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import agents.Business;
import artefacts.Material;
import artefacts.Order;
import artefacts.OrderReq;
import artefacts.Shipment;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public class OrderOpsModule {
	
	private Business biz;
	private ArrayList<Link> linkList;
	private HashMap<Material, ArrayList<Link>> suppliers;
	private HashMap<Material, CopyOnWriteArrayList<Order>> orderPipeLine;
	private HashMap<Material, CopyOnWriteArrayList<OrderReq>> orderReqPipeLine;
	private ArrayList<Order> orderHistory;	
	private HashMap<Link, DescriptiveStatistics> leadTimeData;	
	
	public OrderOpsModule(Business biz){
		this.biz = biz;
		this.linkList = biz.getUpstrLinks();
		
		this.suppliers = new HashMap<Material, ArrayList<Link>>();
		for(Link link : biz.getUpstrLinks()){
			if(!suppliers.containsKey(link.getMaterial())){
				suppliers.put(link.getMaterial(), new ArrayList<Link>());
			}
			suppliers.get(link.getMaterial()).add(link);
		}
		this.orderPipeLine = new HashMap<Material, CopyOnWriteArrayList<Order>>();
		this.orderReqPipeLine = new HashMap<Material, CopyOnWriteArrayList<OrderReq>>();
		for(Material material : suppliers.keySet()){
			orderPipeLine.put(material, new CopyOnWriteArrayList<Order>());
			orderReqPipeLine.put(material, new CopyOnWriteArrayList<OrderReq>());
		}		
		this.leadTimeData = new HashMap<Link, DescriptiveStatistics>();
		for(Link link : linkList){
			leadTimeData.put(link, new DescriptiveStatistics());
		}
	}
	
	/**
	 * TODO: Auswahl des Suppliers/Allokation der Bestellungen
	 * Macht aus OrderReqs Orders
	 */
	public void placeOrders(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		for(Material material : orderReqPipeLine.keySet()){
			System.out.println("placeOrders, orderReqPipeLine.size: " + orderReqPipeLine.get(material).size());
			CopyOnWriteArrayList<OrderReq> pipeline = orderReqPipeLine.get(material);
			for(OrderReq orderReq : orderReqPipeLine.get(material)){
				if(orderReq.getDate()<=currentTick){
					Link supplier = suppliers.get(material).get(0);
					Order newOrder = new Order(supplier, currentTick, orderReq.getSize());
					pipeline.remove(orderReq);
					orderPipeLine.get(material).add(newOrder);
					supplier.putOrder(newOrder);
				}
			}			
		}
	}
	
	
	/**
	 * Bei Teillieferungen gewichteter Durchschnitt
	 * @param shipment
	 */
	public void maintainLeadTimeData(Shipment shipment){
		Order order = shipment.getOrder();
		double sum = 0;
		if(order.isSent()){
			ArrayList<Shipment> shipments = order.getShipments();
			for(Shipment shipm : shipments){
				double leadTime = shipm.getArriving()-order.getDate();
				double weight = shipm.getSize()/order.getSize();
				sum += weight*leadTime;
			}
		}
		this.leadTimeData.get(shipment.getLink()).addValue(sum);
	}
	
	public void processInShipments(ArrayList<Shipment> shipments){
		HashMap<Material, Double> materials = new HashMap<Material, Double>();
		for(Shipment shipment : shipments){
			
			materials.put(shipment.getMaterial(), shipment.getSize());
			Order order = shipment.getOrder();
			order.incrArrived(shipment.getSize());
			if(order.hasArrived()){
				orderPipeLine.get(shipment.getMaterial()).remove(order);
				//System.out.println("Shipment arrived: " + shipment);
			}
			
			maintainLeadTimeData(shipment);
			//System.out.println("maintainLeadTimeData, Tier: " + biz.getTier() + ", " + shipment);
		}
		biz.getInventoryOpsModule().storeMaterials(materials);
	}
	
	public void handOrderReqs(Material material, ArrayList<OrderReq> orderReqs){
		this.orderReqPipeLine.get(material).addAll(orderReqs);
	}
	
	
	public double getProcessingOrders(Material material){
		double sum = 0;
		for(Order order : orderPipeLine.get(material)){
			sum += order.getShortageArrived();
		}
		return sum;
	}
	
	public HashMap<Link, DescriptiveStatistics> getLeadTimeData(){
		return this.leadTimeData;
	}
	
	public HashMap<Material, CopyOnWriteArrayList<OrderReq>> getOrderReqPipeLine(){
		return this.orderReqPipeLine;
	}
	
	public HashMap<Material, ArrayList<Link>> getSuppliers(){
		return this.suppliers;
	}
	
	
	
	

}
