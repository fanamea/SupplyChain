/**
 * 
 */
package supplyChain;

import java.util.ArrayList;

import org.jdom.Element;

import repast.simphony.random.RandomHelper;

public class DistributionNormal extends Distribution {
	
	private String name = "normal";
	private double my;
	private double sigma;
	
	/* --------- KONSTRUKTOREN --------- */
	
	public DistributionNormal(double my, double sigma){
		this.my = my;
		this.sigma = sigma;
	}
	
	/* --------- PUBLIC METHODEN --------- */
	@Override
	public double computeRandom() {
		return RandomHelper.createNormal(my, sigma).nextDouble();
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public ArrayList<Double> getParameter(){
		ArrayList<Double> params = new ArrayList<Double>();
		params.add(this.sigma);
		return params;
	}

	@Override
	public Element toXML() {
		Element res = new Element("normal");
		res.setAttribute("sigma" , ""+this.sigma);
		
		return res;
	}

	@Override
	public String getParameterName(){
		return "sigma";
	}
	
	@Override
	public void setParameter(ArrayList<Double> params){
		this.sigma = params.get(0);
	}
	
	/* --------- PRINT METHODEN --------- */
	
	public String toString(){
		return "Normalverteilung (sigma = " + sigma + ")";
	}
}
