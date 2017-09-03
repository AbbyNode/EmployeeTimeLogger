package com.blueberrys.employeetimelogger.dailyv;

import java.util.Optional;

import com.blueberrys.employeetimelogger.data.Employee;
import com.blueberrys.employeetimelogger.data.util.DataController.SignResult;
import com.blueberrys.employeetimelogger.view.EmployeeCell;
import com.blueberrys.employeetimelogger.view.ViewController;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ControllerDV extends ViewController {

	@FXML
	private TableView<EmployeeCell> employeeTable;
	@FXML
	private TableColumn<EmployeeCell, String> nameCol;
	@FXML
	private TableColumn<EmployeeCell, String> signedInCol;
	@FXML
	private TableColumn<EmployeeCell, String> signInTimeCol;

	@FXML
	private void initialize() {
		nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameP());
		signedInCol.setCellValueFactory(celldata -> celldata.getValue().getSignedInP());
		signInTimeCol.setCellValueFactory(celldata -> celldata.getValue().getSignInTimeP());
	}

	//

	@FXML
	private void adminManage(ActionEvent event) {
		adminControl(() -> parentEl.setViewAdmin());
	}

	@FXML
	private void adminSignOutAll(ActionEvent event) {
		adminControl(() -> {
			dataC.endAllSessions();
			parentEl.updateEmployeeData();
		});
	}

	private void adminControl(Runnable fn) {
		Optional<String> result = parentEl.askUserPass();
		result.ifPresent(pass -> {
			if (dataC.getAdmin().testPass(pass)) {
				fn.run();
			} else {
				parentEl.showInfo("Invalid password");
			}
		});
	}

	//

	@FXML
	private void signIn(ActionEvent event) {
		EmployeeCell eCell = employeeTable.getSelectionModel().getSelectedItem();
		if (eCell == null) {
			return;
		}
		Employee e = eCell.getEmployee();

		if (e.isSignedIn()) {
			parentEl.showInfo(e.getName() + " is already signed in");
			return;
		}

		String pass = null;
		if (e.hasPass()) {
			Optional<String> result = parentEl.askUserPass();
			if (!result.isPresent()) {
				return;
			}

			pass = result.get();
		}

		if (dataC.signIn(e, pass) == SignResult.INVALID_PASS) {
			parentEl.showInfo("Invalid password");
			return;
		}

		eCell.update();
	}

	@FXML
	private void signOut(ActionEvent event) {
		EmployeeCell eCell = employeeTable.getSelectionModel().getSelectedItem();
		if (eCell == null) {
			return;
		}
		Employee e = eCell.getEmployee();

		if (!e.isSignedIn()) {
			parentEl.showInfo(e.getName() + " is already signed out");
			return;
		}

		String pass = null;
		if (e.hasPass()) {
			Optional<String> result = parentEl.askUserPass();
			if (!result.isPresent()) {
				return;
			}

			pass = result.get();
		}

		if (dataC.signOut(e, pass) == SignResult.INVALID_PASS) {
			parentEl.showInfo("Invalid password");
			return;
		}

		eCell.update();
	}

	//

	public void setEmployeeList(ObservableList<EmployeeCell> employeeList) {
		employeeTable.setItems(employeeList);
	}
}
