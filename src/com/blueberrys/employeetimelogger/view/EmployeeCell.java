package com.blueberrys.employeetimelogger.view;

import java.time.ZonedDateTime;

import com.blueberrys.employeetimelogger.EmployeeTimeLogger;
import com.blueberrys.employeetimelogger.data.Employee;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

public class EmployeeCell {

	private final Employee employee;

	private final StringProperty nameP;
	private final StringProperty signedInP;
	private final StringProperty signInTimeP;

	private final StringProperty totalTimeP;

	public EmployeeCell(Employee employee) {
		this.employee = employee;

		this.nameP = new SimpleStringProperty(employee.getName());
		this.signedInP = new SimpleStringProperty("No");
		this.signInTimeP = new SimpleStringProperty("N/A");

		this.totalTimeP = new SimpleStringProperty("");

		update();
	}

	public void update() {
		nameP.set(employee.getName());

		signedInP.set(employee.isSignedIn() ? "Yes" : "No");

		signInTimeP.set("N/A");
		if (employee.isSignedIn()) {
			ZonedDateTime t = employee.getCurrentSession().getSignInTime();
			if (t != null) {
				signInTimeP.set(t.format(EmployeeTimeLogger.TIME_FORMATTER));
			}
		}
	}

	public void updateTotalTime(ZonedDateTime startDate, ZonedDateTime endDate) {
		Duration d = Duration.seconds(employee.getTotalTimeSec(startDate, endDate));
		long h = (long) d.toHours();
		long m = (long) (d.toMinutes() - (h * 60));
		totalTimeP.set(h + ":" + m);
	}

	//

	/**
	 * @return the employee
	 */
	public Employee getEmployee() {
		return employee;
	}

	/**
	 * @return the name
	 */
	public StringProperty getNameP() {
		return nameP;
	}

	/**
	 * @return the signedIn
	 */
	public StringProperty getSignedInP() {
		return signedInP;
	}

	/**
	 * @return the signInTime
	 */
	public StringProperty getSignInTimeP() {
		return signInTimeP;
	}

	/**
	 * @return the totalTimeP
	 */
	public StringProperty getTotalTimeP() {
		return totalTimeP;
	}

}
