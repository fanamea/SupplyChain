package supplyChain;

import java.util.ArrayList;

public class Test {
	
	public static void main(String[] args){
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		for(Integer i : list){
			System.out.println(i);
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
