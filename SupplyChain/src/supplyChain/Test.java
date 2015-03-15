package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.math3.*;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Test {
	
	public static void main(String[] args){
		HashMap<Integer, Double> map = new HashMap<Integer, Double>();
		
		PlanningTechniques techs = new PlanningTechniques();
		map.put(1, 200.0);
		map.put(2, 60.0);
		map.put(3, 105.0);
		map.put(4, 195.0);
		map.put(5, 50.0);
		map.put(6, 90.0);
		HashMap<Integer, Double> lots = techs.silverMeal(map, 475, 2);
		for(Integer i : lots.keySet()){
			System.out.println(i + ": " + lots.get(i));
		}
	}
	
	
	
	public static void exampleSetUp(Setup setup){
		
		for(int i = 0; i < 7; i++){
			setup.addNode(i);
		}
		
		setup.addLink(0, 3, 3);
		setup.addLink(1, 3, 3);
		setup.addLink(2, 4, 3);
		setup.addLink(3, 5, 2);
		setup.addLink(4, 5, 2);
		setup.addLink(5, 6, 1);
		
		setup.print();
	}

}
