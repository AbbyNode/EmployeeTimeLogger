package com.blueberrys.employeetimelogger.adminv.employees;

import java.util.Optional;

import com.blueberrys.employeetimelogger.adminv.TabControllerAV;
import com.blueberrys.employeetimelogger.data.Employee;
import com.blueberrys.employeetimelogger.view.EmployeeCell;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class EmployeeTabController extends TabControllerAV {

	@FXML
	private TableView<EmployeeCell> employeeTable;
	@FXML
	private TableColumn<EmployeeCell, String> nameCol;
	@FXML
	private TableColumn<EmployeeCell, String> signedInCol;

	@FXML
	private void initialize() {
		nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameP());
		signedInCol.setCellValueFactory(celldata -> celldata.getValue().getSignedInP());
	}

	//

	@FXML
	private void addNewEmployee(ActionEvent event) {
		Optional<String> result = parentEl.askUser("Employee Name:");
		result.ifPresent(name -> {
			String pass = generatePass();
			parentEl.showInfo("Employee " + name + " added\n"
					+ "Password: " + pass);
			dataC.addEmployee(name, pass);
			parentEl.updateEmployeeList();
		});
	}

	@FXML
	private void editEmployeeName(ActionEvent event) {
		EmployeeCell eCell = employeeTable.getSelectionModel().getSelectedItem();
		if (eCell == null) {
			return;
		}
		Employee e = eCell.getEmployee();

		Optional<String> result = parentEl.askUser("Edit Employee Name:", e.getName());
		result.ifPresent(name -> {
			dataC.setEmployeeName(e, name);
			eCell.update();
		});
	}

	@FXML
	private void generateNewPass(ActionEvent event) {
		EmployeeCell eCell = employeeTable.getSelectionModel().getSelectedItem();
		if (eCell == null) {
			return;
		}
		Employee e = eCell.getEmployee();

		Optional<ButtonType> result = parentEl.askConfirm("Are you sure you want to generate a new password for " + e.getName() + "?");
		if (!result.isPresent() || result.get() != ButtonType.OK) {
			return;
		}

		String pass = generatePass();
		parentEl.showInfo("New password generated for " + e.getName() + "\nNew Password: " + pass);
		dataC.setEmployeePass(e, pass);
		// eCell.update();
	}

	@FXML
	private void signOutEmployee(ActionEvent event) {
		EmployeeCell eCell = employeeTable.getSelectionModel().getSelectedItem();
		if (eCell == null) {
			return;
		}
		Employee e = eCell.getEmployee();

		if (!e.isSignedIn()) {
			return;
		}

		dataC.signOut(e, null, true);
		eCell.update();
	}

	/* @FXML private void deleteEmployee(ActionEvent event) { EmployeeCell eCell = employeeTable.getSelectionModel().getSelectedItem();
	 * if (eCell == null) { return; } Employee e = eCell.getEmployee();
	 * 
	 * Optional<ButtonType> result = parentEl.askConfirm("Are you sure you want to delete " + e.getName() + "?\n" +
	 * "All associated data will be lost permanently.\n" + "This action is not reversible."); if (!result.isPresent() || result.get()
	 * != ButtonType.OK) { return; }
	 * 
	 * // dataC.removeEmployee(e); parentEl.updateEmployeeList(); } */

	//

	private String generatePass() {
		return String.valueOf((int) ((Math.random() * 8999) + 1000));
	}

	//

	@Override
	public void setEmployeeList(ObservableList<EmployeeCell> employeeList) {
		employeeTable.setItems(employeeList);
	}

}
