package com.blueberrys.employeetimelogger.view;

import com.blueberrys.employeetimelogger.EmployeeTimeLogger;
import com.blueberrys.employeetimelogger.data.util.DataController;

public abstract class ViewController {

	protected EmployeeTimeLogger parentEl;
	protected DataController dataC;

	//

	/**
	 * 
	 * @param parentEl
	 */
	public void setParentEl(EmployeeTimeLogger parentEl) {
		this.parentEl = parentEl;
	}

	/**
	 * @param dataC the dc to set
	 */
	public void setDataController(DataController dataC) {
		this.dataC = dataC;
	}

}
