/**
 * 
 */
package distribution;

import java.util.ArrayList;

import org.jdom.Element;

import repast.simphony.random.RandomHelper;

/**
 * @author Johannes Ponge
 *
 */
public class DistributionUniform extends Distribution {
	
	private String name = "uniform";
	private double min;
	private double max;
	
	/* --------- KONSTRUKTOREN --------- */
	
	public DistributionUniform(double min, double max){
		this.min = min;
		this.max = max;
	}
	
	/* --------- PUBLIC METHODEN --------- */
	@Override
	public double computeRandom() {
		return RandomHelper.createUniform(min, max).nextDouble();
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public ArrayList<Double> getParameter(){
		ArrayList<Double> params = new ArrayList<Double>();
		params.add(this.min);
		params.add(this.max);
		return params;
	}

	

	@Override
	public String getParameterName(){
		return "shift";
	}
	
	@Override
	public void setParameter(ArrayList<Double> params){
		this.min = params.get(0);
		this.max = params.get(1);
	}
	
	/* --------- PRINT METHODEN --------- */
	
	public String toString(){
		return "Gleichverteilung (min = " + min + ", max =" + max + ")";
	}

	@Override
	public Element toXML() {
		// TODO Auto-generated method stub
		return null;
	}
}
