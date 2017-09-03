package com.blueberrys.employeetimelogger.data;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import com.blueberrys.employeetimelogger.data.util.Encoder;

public class Employee implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7818871161830197996L;

	private String name;
	private byte[] encodedPass;

	private final ArrayList<Session> sessions;

	private transient boolean signedIn;
	private transient Session currentSession;

	public Employee(String name) {
		this.name = name;
		this.sessions = new ArrayList<Session>();

		this.signedIn = false;
	}

	//

	/**
	 * 
	 * @return true if successful, false if already signed in.
	 */
	public boolean signIn() {
		if (signedIn) {
			return false;
		}

		currentSession = new Session();
		currentSession.setSignInTime(ZonedDateTime.now());
		sessions.add(currentSession);
		signedIn = true;

		return true;
	}

	/**
	 * 
	 * @return true if successful, false if already signed out.
	 */
	public boolean signOut() {
		if (!signedIn) {
			return false;
		}

		currentSession.setSignOutTime(ZonedDateTime.now());
		currentSession = null;
		signedIn = false;

		return true;
	}

	/**
	 * 
	 * @param pass
	 * @return true if password is valid, or if there is no stored password
	 */
	public boolean testPass(String pass) {
		if (!hasPass()) {
			return true;
		}
		return (pass != null && Arrays.equals(Encoder.encodePass(pass), encodedPass));
	}

	/**
	 * 
	 * @return true if success, false otherwise
	 */
	public boolean tryResumeLastSession() {
		if (signedIn) {
			// Currently signed in, don't override
			return false;
		}

		if (sessions.size() < 1) {
			// No previous sessions
			return false;
		}

		Session s = sessions.get(sessions.size() - 1);
		if (s.getSignInTime() == null || s.getSignOutTime() != null) {
			// Old session was:
			// not signed in ||
			// already signed out
			return false;
		}

		// Resume old session
		signedIn = true;
		currentSession = s;

		return true;
	}

	// special get

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public ArrayList<Session> getSessionsBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
		ArrayList<Session> outSessions = new ArrayList<Session>();

		for (Session s : sessions) {
			ZonedDateTime sessionTime = s.getSignInTime();

			if (sessionTime == null) {
				continue;
			}

			if (startDate != null && sessionTime.isBefore(startDate)) {
				continue;
			}

			if (endDate != null && sessionTime.isAfter(endDate)) {
				continue;
			}

			outSessions.add(s);
		}

		return outSessions;
	}

	/**
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public long getTotalTimeSec(ZonedDateTime startDate, ZonedDateTime endDate) {
		long t = 0;
		for (Session s : getSessionsBetween(startDate, endDate)) {
			if (s.getSignInTime() != null && s.getSignOutTime() != null) {
				t += s.getDuration().getSeconds();
			}
		}
		return t;
	}

	// get/set

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public boolean hasPass() {
		return (encodedPass != null);
	}

	/**
	 * 
	 * @param pass
	 */
	public void setPass(String pass) {
		setEncodedPass(Encoder.encodePass(pass));
	}

	/**
	 * @param encodedPass the storedPass to set
	 */
	public void setEncodedPass(byte[] encodedPass) {
		this.encodedPass = encodedPass;
	}

	/**
	 * @return the sessions
	 */
	public ArrayList<Session> getSessions() {
		return sessions;
	}

	/**
	 * @return the signedIn
	 */
	public boolean isSignedIn() {
		return signedIn;
	}

	/**
	 * @return the currentSession
	 */
	public Session getCurrentSession() {
		return currentSession;
	}

	/**
	 * 
	 * @return The last session stored in {@link #sessions}.<br>
	 *         Will be {@link #currentSession} if {@link #signIn()} was called.
	 */
	public Session getLastSession() {
		return sessions.get(sessions.size() - 1);
	}
}
