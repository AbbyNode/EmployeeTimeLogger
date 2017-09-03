package com.blueberrys.employeetimelogger.data;

import java.io.Serializable;
import java.time.Duration;
import java.time.ZonedDateTime;

public class Session implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7965545310712560327L;

	private ZonedDateTime signInTime;
	private ZonedDateTime signOutTime;

	/**
	 * @return the signInTime
	 */
	public ZonedDateTime getSignInTime() {
		return signInTime;
	}

	/**
	 * @param signInTime the signInTime to set
	 */
	public void setSignInTime(ZonedDateTime signInTime) {
		this.signInTime = signInTime;
	}

	/**
	 * @return the signOutTime
	 */
	public ZonedDateTime getSignOutTime() {
		return signOutTime;
	}

	/**
	 * @param signOutTime the signOutTime to set
	 */
	public void setSignOutTime(ZonedDateTime signOutTime) {
		this.signOutTime = signOutTime;
	}

	/**
	 * 
	 * @return Duration between {@link #signInTime} and {@link #signOutTime}, or null if either are not defined
	 */
	public Duration getDuration() {
		if (signInTime == null || signOutTime == null) {
			return null;
		}

		return Duration.between(signInTime, signOutTime);
	}

}
