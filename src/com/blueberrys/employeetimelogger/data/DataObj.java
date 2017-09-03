package com.blueberrys.employeetimelogger.data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataObj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1717317085061489253L;

	private Admin admin;
	private ArrayList<Employee> employees;
	private boolean concluded;

	public DataObj() {
		this.admin = new Admin();
		this.employees = new ArrayList<Employee>();
		this.concluded = true;
	}

	/**
	 * @return the admin
	 */
	public Admin getAdmin() {
		return admin;
	}

	/**
	 * @return the employees
	 */
	public ArrayList<Employee> getEmployees() {
		return employees;
	}

	/**
	 * @return the concluded
	 */
	public boolean isConcluded() {
		return concluded;
	}

	/**
	 * @param concluded the concluded to set
	 */
	public void setConcluded(boolean concluded) {
		this.concluded = concluded;
	}

}
