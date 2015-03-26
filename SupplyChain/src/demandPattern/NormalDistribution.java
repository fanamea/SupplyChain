package demandPattern;

import repast.simphony.random.RandomHelper;
import cern.jet.random.AbstractDistribution;
import cern.jet.random.Normal;

public class NormalDistribution extends DemandPattern {
	
	private Normal distribution;
	
	public NormalDistribution(double mean, double sd){
		this.distribution = RandomHelper.createNormal(mean, sd);
	}

	@Override
	public double getNextDouble(){
		return Math.max(0, this.distribution.nextDouble());		
	}

}
