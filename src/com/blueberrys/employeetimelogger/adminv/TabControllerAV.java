package com.blueberrys.employeetimelogger.adminv;

import com.blueberrys.employeetimelogger.view.EmployeeCell;
import com.blueberrys.employeetimelogger.view.ViewController;

import javafx.collections.ObservableList;

public abstract class TabControllerAV extends ViewController {

	public abstract void setEmployeeList(ObservableList<EmployeeCell> employeeList);

}
