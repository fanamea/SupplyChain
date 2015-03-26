package demandPattern;

import repast.simphony.random.RandomHelper;
import cern.jet.random.Normal;

public class RandomWalk extends DemandPattern{
	
	private Normal distribution;
	private double lastValue;
	
	public RandomWalk(double start, double mean, double sd){
		this.distribution = RandomHelper.createNormal(mean, sd);
		this.lastValue = start;
	}
	
	@Override
	public double getNextDouble() {
		return Math.max(0, lastValue + this.distribution.nextDouble());
	}

}
