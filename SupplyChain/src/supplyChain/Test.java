package supplyChain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Test {
	
	public static void main(String[] args){
		ArrayList<Double> list = new ArrayList<Double>();
		for(int i = 0; i<10; i++){
			list.add((double)i);
		}
		ForecastAgent fcAgent = new ForecastAgent();
		HashMap<Integer, Double> forecast = fcAgent.getMovingAverageFC(list, 5, 3);
		
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
