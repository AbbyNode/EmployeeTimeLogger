package com.blueberrys.employeetimelogger.view;

import java.time.Duration;
import java.time.ZonedDateTime;

import com.blueberrys.employeetimelogger.EmployeeTimeLogger;
import com.blueberrys.employeetimelogger.data.Session;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class SessionCell {

	private final Session session;

	private final StringProperty dateP;
	private final StringProperty signInTimeP;
	private final StringProperty signOutTimeP;
	private final StringProperty durationP;

	public SessionCell(Session session) {
		this.session = session;

		this.dateP = new SimpleStringProperty("N/A");
		this.signInTimeP = new SimpleStringProperty("N/A");
		this.signOutTimeP = new SimpleStringProperty("N/A");
		this.durationP = new SimpleStringProperty("N/A");

		update();
	}

	public void update() {
		ZonedDateTime signInTime = session.getSignInTime();
		if (signInTime != null) {
			dateP.set(signInTime.format(EmployeeTimeLogger.DAY_FORMATTER));
			signInTimeP.set(signInTime.format(EmployeeTimeLogger.TIME_FORMATTER));

			ZonedDateTime signOutTime = session.getSignOutTime();
			if (signOutTime != null) {
				signOutTimeP.set(signOutTime.format(EmployeeTimeLogger.TIME_FORMATTER));

				Duration d = session.getDuration();
				int h = (int) d.toHours();
				int m = (int) d.toMinutes() - (h * 60);
				durationP.set(h + ":" + m);
			}
		}
	}

	//

	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @return the date
	 */
	public StringProperty getDateP() {
		return dateP;
	}

	/**
	 * @return the signInTime
	 */
	public StringProperty getSignInTimeP() {
		return signInTimeP;
	}

	/**
	 * @return the signOutTime
	 */
	public StringProperty getSignOutTimeP() {
		return signOutTimeP;
	}

	/**
	 * @return the duration
	 */
	public StringProperty getDurationP() {
		return durationP;
	}

}
