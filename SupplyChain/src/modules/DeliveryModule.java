package modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sourceforge.openforecast.DataPoint;
import net.sourceforge.openforecast.DataSet;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import agents.Business;
import artefacts.Material;
import artefacts.Order;
import artefacts.OrderComparator;
import artefacts.Shipment;
import repast.simphony.essentials.RepastEssentials;
import modules.Link;

public class DeliveryModule {
	
	private Business biz;
	private CopyOnWriteArrayList<Order> orderPipeLine;

	
	public DeliveryModule(Business biz){
		this.biz = biz;
		this.orderPipeLine = new CopyOnWriteArrayList<Order>();


	}
	
	/**
	 * TODO: Momentan keine Angabe von DueDates für Orders, sondern werden sofort verarbeitet und
	 * so schnell wie möglich dispatcht.
	 * Erhält eine orderList vom Business mit eingegangen Orders und fügt sie zur History und PipeLine.
	 * 
	 * @param orderList
	 */
	public void processOrders(ArrayList<Order> orderList){
		orderPipeLine.addAll(orderList);
		InformationModule infoModule = biz.getInformationModule();
		int currentTick = (int)RepastEssentials.GetTickCount();		
		double sum=0;
		for(Order order : orderList){
			sum += order.getSize();
		}
		////System.out.println("handDemandData: " + currentTick + ", sum: " + sum);
		infoModule.addIntDemandData(currentTick, sum);
	}
	
	/**
	 * Macht aus allen Orders in der PipeLine Shipments, sofern möglich.
	 * Entfernt diese dann aus der PipeLine.
	 */
	public void dispatchShipments(){
		Material endProduct = biz.getProduct();
		ArrayList<Order> temp = new ArrayList<Order>(orderPipeLine);
		Collections.shuffle(temp);
		Collections.sort(temp, new OrderComparator());
		this.orderPipeLine.clear();
		this.orderPipeLine.addAll(temp);
		for(Order order : orderPipeLine){
				HashMap<Material, Double> request = new HashMap<Material, Double>();
				double stillToShip = order.getShortageSent();
				request.put(endProduct, stillToShip);
				HashMap<Material, Double> shipableAmount = biz.getInventoryOpsModule().requestMaterials(request);
				Link link = order.getLink();
				int currentTick = (int)RepastEssentials.GetTickCount();
				if(shipableAmount.get(endProduct) > 0){
					Shipment shipment = new Shipment(link, currentTick, shipableAmount.get(endProduct), link.genDuration(), order);
					////System.out.println("Shipment: " + shipment);
					order.addShipment(shipment);
					order.incrSent(shipableAmount.get(endProduct));
					link.induceShipment(shipment);
				}
				////System.out.println(order.isSent());
				if(order.isSent()){
					orderPipeLine.remove(order);
				}
			}		
	}
	
	public double getBacklog(){
		int currentTick = (int)RepastEssentials.GetTickCount();
		double sum = 0;
		for(Order order : orderPipeLine){
			if(order.getDate()<=currentTick){
				sum += order.getShortageSent();
			}
		}
		return sum;
	}
	
	public String getInformationString(){
		String string = "";
		string += "      OrderPipeLine: " + orderPipeLine + "\n";
		string += "Backlog: " + getBacklog();
		return string;
	}
	

}
