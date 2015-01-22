package supplyChain;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.*;
import org.apache.commons.math3.distribution.NormalDistribution;

public class Test {
	
	public static void main(String[] args){
		String string = "blabla \nblabla";
		System.out.println(string);
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
