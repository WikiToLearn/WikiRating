/**
 * 
 */
package org.wikitolearn.models;

/**
 * @author alessandro
 *
 */
public class User {
	private String username;
	private long userId;
	private double votesReliability;
	private double contributesReliability;
	private double totalReliability;
	
	/**
	 * 
	 */
	public User() {
	}
	
	/**
	 * @param username
	 * @param userId
	 * @param votesReliability
	 * @param contributesReliability
	 * @param totalReliability
	 */
	public User(String username, long userId, double votesReliability, double contributesReliability,
			double totalReliability) {
		this.username = username;
		this.userId = userId;
		this.votesReliability = votesReliability;
		this.contributesReliability = contributesReliability;
		this.totalReliability = totalReliability;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the votesReliability
	 */
	public double getVotesReliability() {
		return votesReliability;
	}

	/**
	 * @param votesReliability the votesReliability to set
	 */
	public void setVotesReliability(double votesReliability) {
		this.votesReliability = votesReliability;
	}

	/**
	 * @return the contributesReliability
	 */
	public double getContributesReliability() {
		return contributesReliability;
	}

	/**
	 * @param contributesReliability the contributesReliability to set
	 */
	public void setContributesReliability(double contributesReliability) {
		this.contributesReliability = contributesReliability;
	}

	/**
	 * @return the totalReliability
	 */
	public double getTotalReliability() {
		return totalReliability;
	}

	/**
	 * @param totalReliability the totalReliability to set
	 */
	public void setTotalReliability(double totalReliability) {
		this.totalReliability = totalReliability;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "User [username=" + username + ", userId=" + userId + ", votesReliability=" + votesReliability
				+ ", contributesReliability=" + contributesReliability + ", totalReliability=" + totalReliability + "]";
	}
	
}
