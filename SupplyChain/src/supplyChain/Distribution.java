/**
 * 
 */
package supplyChain;

import java.util.ArrayList;

import org.jdom.Element;

/**
 * @author Johannes Ponge
 *
 */
abstract public class Distribution {

	public abstract double computeRandom();
	
	public abstract String getName();
	
	public abstract Element toXML();
	
	public abstract ArrayList<Double> getParameter();
	
	public abstract String getParameterName();
	
	public abstract void setParameter(ArrayList<Double> params);
}
