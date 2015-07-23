package demandPattern;

import repast.simphony.essentials.RepastEssentials;

public class Constant extends DemandPattern{
	
	private double constant;
	
	public Constant(double c){
		this.constant = c;
	}

	@Override
	public double getNextDouble() {
		return this.constant;	
	}
	
	

}
