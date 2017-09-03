package com.blueberrys.employeetimelogger.adminv;

import java.io.IOException;
import java.util.Optional;

import com.blueberrys.employeetimelogger.EmployeeTimeLogger;
import com.blueberrys.employeetimelogger.adminv.employees.EmployeeTabController;
import com.blueberrys.employeetimelogger.adminv.reports.ReportTabController;
import com.blueberrys.employeetimelogger.adminv.sessions.SessionTabController;
import com.blueberrys.employeetimelogger.data.util.DataController;
import com.blueberrys.employeetimelogger.view.EmployeeCell;
import com.blueberrys.employeetimelogger.view.ViewController;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;

public class ControllerAV extends ViewController {

	@FXML
	private Tab employeeTab;
	@FXML
	private Tab sessionTab;
	@FXML
	private Tab reportTab;

	private EmployeeTabController employeeTC;
	private SessionTabController sessionTC;
	private ReportTabController reportTC;

	@FXML
	private void initialize() {
		reportTab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event t) {
				if (reportTab.isSelected()) {
					reportTC.updateAll();
				}
			}
		});

		sessionTab.setOnSelectionChanged(new EventHandler<Event>() {
			@Override
			public void handle(Event t) {
				if (sessionTab.isSelected()) {
					sessionTC.updateSessionList();
				}
			}
		});
	}

	//

	@FXML
	private void editAdminPass(ActionEvent event) {
		Optional<String> result = parentEl.askUser("New Admin Password:");
		result.ifPresent(pass -> {
			dataC.setAdminPass(pass);
			parentEl.showInfo("Admin password has been updated successfully");
		});
	}

	@FXML
	private void exitToDaily(ActionEvent event) {
		parentEl.setViewDaily();
	}

	//

	public void setEmployeeList(ObservableList<EmployeeCell> employeeList) {
		employeeTC.setEmployeeList(employeeList);
		sessionTC.setEmployeeList(employeeList);
		reportTC.setEmployeeList(employeeList);
	}

	public void loadEmployeeTab(FXMLLoader loader) throws IOException {
		employeeTab.setContent((VBox) loader.load());
		employeeTC = loader.getController();
	}

	public void loadSessionTab(FXMLLoader loader) throws IOException {
		sessionTab.setContent((VBox) loader.load());
		sessionTC = loader.getController();
	}

	public void loadReportTab(FXMLLoader loader) throws IOException {
		reportTab.setContent((VBox) loader.load());
		reportTC = loader.getController();
	}

	//

	@Override
	public void setParentEl(EmployeeTimeLogger parentEl) {
		super.setParentEl(parentEl);

		employeeTC.setParentEl(parentEl);
		sessionTC.setParentEl(parentEl);
		reportTC.setParentEl(parentEl);
	}

	@Override
	public void setDataController(DataController dataC) {
		super.setDataController(dataC);

		employeeTC.setDataController(dataC);
		sessionTC.setDataController(dataC);
		reportTC.setDataController(dataC);
	}
}
