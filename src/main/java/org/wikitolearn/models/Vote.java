/**
 * 
 */
package org.wikitolearn.models;

/**
 * @author aletundo, valsdav
 *
 */
public class Vote {

	private double value;
	private double reliability;
	
	/**
	 * 
	 */
	public Vote() {
	}
	
	/**
	 * @param value
	 * @param reliability
	 */
	public Vote(double value, double reliability) {
		this.value = value;
		this.reliability = reliability;
	}

	/**
	 * @return the value
	 */
	public double getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * @return the reliability
	 */
	public double getReliability() {
		return reliability;
	}

	/**
	 * @param reliability the reliability to set
	 */
	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Vote [value=" + value + ", reliability=" + reliability + "]";
	}
	
}
