package com.blueberrys.employeetimelogger.adminv.reports;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.blueberrys.employeetimelogger.EmployeeTimeLogger;
import com.blueberrys.employeetimelogger.adminv.TabControllerAV;
import com.blueberrys.employeetimelogger.view.EmployeeCell;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ReportTabController extends TabControllerAV {

	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;

	@FXML
	private TableView<EmployeeCell> employeeTable;
	@FXML
	private TableColumn<EmployeeCell, String> nameCol;
	@FXML
	private TableColumn<EmployeeCell, String> totalTimeCol;

	//

	private ZonedDateTime startDate;
	private ZonedDateTime endDate;

	//

	@FXML
	private void initialize() {
		startDatePicker.setOnAction(event -> {
			LocalDate localDate = startDatePicker.getValue();
			startDate = (localDate == null ? null : localDate.atStartOfDay(ZoneOffset.systemDefault()));
			updateAll();
		});
		endDatePicker.setOnAction(event -> {
			LocalDate localDate = endDatePicker.getValue();
			endDate = (localDate == null ? null : localDate.atStartOfDay(ZoneOffset.systemDefault()));
			updateAll();
		});

		//

		nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameP());
		totalTimeCol.setCellValueFactory(cellData -> cellData.getValue().getTotalTimeP());
	}

	//

	@FXML
	private void saveToFile(ActionEvent event) {
		File file = parentEl.pickFile();
		if (file == null) {
			return;
		}

		//

		String s = System.lineSeparator();

		String data = "Employee Time Logger Report" + s;

		data += s + "Start Date: " + (startDate == null ? "N/A" : startDate.format(EmployeeTimeLogger.DAY_FORMATTER));
		data += s + "End Date: " + (endDate == null ? "N/A" : endDate.format(EmployeeTimeLogger.DAY_FORMATTER));

		data += s + s + "Time (h:m)\tEmployee Name";

		for (EmployeeCell eCell : employeeTable.getItems()) {
			String line = eCell.getTotalTimeP().get() + "\t\t" + eCell.getNameP().get();
			data += s + line;
		}

		//

		try (PrintWriter writer = new PrintWriter(file)) {
			writer.write(data);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	//

	public void updateAll() {
		for (EmployeeCell eCell : employeeTable.getItems()) {
			eCell.updateTotalTime(startDate, endDate);
		}
	}

	//

	@Override
	public void setEmployeeList(ObservableList<EmployeeCell> employeeList) {
		employeeTable.setItems(employeeList);
	}

}
