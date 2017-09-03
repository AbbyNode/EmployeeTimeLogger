package com.blueberrys.employeetimelogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.blueberrys.employeetimelogger.adminv.ControllerAV;
import com.blueberrys.employeetimelogger.dailyv.ControllerDV;
import com.blueberrys.employeetimelogger.data.Employee;
import com.blueberrys.employeetimelogger.data.util.DataController;
import com.blueberrys.employeetimelogger.view.EmployeeCell;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class EmployeeTimeLogger extends Application {

	public static final String APP_NAME = "Employee Time Logger";

	public static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy, MMM dd");
	public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

	private static final URL LOADING_VIEW_FXML = EmployeeTimeLogger.class.getResource("LoadingView.fxml");
	private static final URL DAILY_VIEW_FXML = EmployeeTimeLogger.class.getResource("dailyv/DailyView.fxml");
	private static final URL ADMIN_VIEW_FXML = EmployeeTimeLogger.class.getResource("adminv/AdminView.fxml");
	private static final URL AV_EMPLOYEE_TAB_FXML = EmployeeTimeLogger.class.getResource("adminv/employees/EmployeeTab.fxml");
	private static final URL AV_SESSION_TAB_FXML = EmployeeTimeLogger.class.getResource("adminv/sessions/SessionTab.fxml");
	private static final URL AV_REPORT_TAB_FXML = EmployeeTimeLogger.class.getResource("adminv/reports/ReportTab.fxml");

	private Stage stage;

	private DataController dataC;

	private ObservableList<EmployeeCell> employeeList;
	private ControllerDV dailyVc;
	private ControllerAV adminVc;

	private Scene loadingS;
	private Scene dailyS;
	private Scene adminS;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		stage.setTitle(APP_NAME);

		initLoadingView();
		stage.setScene(loadingS);
		stage.show();

		//

		Task<Integer> t = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception {
				loadData();
				return 1;
			}
		};
		t.onSucceededProperty().set(e -> {
			setViewDaily();
			stage.toFront();

			validateData();

			initAdminView();
		});
		new Thread(t).start();
	}

	private void loadData() {
		dataC = new DataController();
		dataC.loadData();

		employeeList = FXCollections.observableArrayList();
		updateEmployeeList();

		initDailyView();
	}

	private void validateData() {
		if (dataC.isDataCorrupted()) {
			showWarning("Warning: Attempted to load invalid data file.\n"
					+ "Please remove the corrupted file and restart the application.");
			Platform.exit();
			return;
		}

		if (!dataC.getAdmin().isPassSet()) {
			Optional<String> result = askUser("New Admin Password:");
			if (!result.isPresent() || result.get().isEmpty()) {
				showWarning("Password must be set before use");
				Platform.exit();
				return;
			}

			dataC.setAdminPass(result.get());
		}
	}

	//

	private void initLoadingView() {
		FXMLLoader loadingView = new FXMLLoader();
		loadingView.setLocation(EmployeeTimeLogger.LOADING_VIEW_FXML);
		try {
			loadingS = new Scene(loadingView.load());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initDailyView() {
		FXMLLoader dailyLoader = new FXMLLoader();
		dailyLoader.setLocation(EmployeeTimeLogger.DAILY_VIEW_FXML);
		try {
			dailyS = new Scene(dailyLoader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		dailyVc = (ControllerDV) dailyLoader.getController();
		dailyVc.setParentEl(this);
		dailyVc.setEmployeeList(employeeList);
		dailyVc.setDataController(dataC);
	}

	private void initAdminView() {
		FXMLLoader adminLoader = new FXMLLoader();
		adminLoader.setLocation(EmployeeTimeLogger.ADMIN_VIEW_FXML);
		try {
			adminS = new Scene(adminLoader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		adminVc = (ControllerAV) adminLoader.getController();

		//

		FXMLLoader tabLoader = new FXMLLoader();
		tabLoader.setLocation(EmployeeTimeLogger.AV_EMPLOYEE_TAB_FXML);
		try {
			adminVc.loadEmployeeTab(tabLoader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		tabLoader = new FXMLLoader();
		tabLoader.setLocation(EmployeeTimeLogger.AV_SESSION_TAB_FXML);
		try {
			adminVc.loadSessionTab(tabLoader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		tabLoader = new FXMLLoader();
		tabLoader.setLocation(EmployeeTimeLogger.AV_REPORT_TAB_FXML);
		try {
			adminVc.loadReportTab(tabLoader);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//

		adminVc.setParentEl(this);
		adminVc.setDataController(dataC);
		adminVc.setEmployeeList(employeeList);
	}

	//

	/**
	 * 
	 */
	public void setViewDaily() {
		stage.setScene(dailyS);
	}

	/**
	 * 
	 */
	public void setViewAdmin() {
		stage.setScene(adminS);
	}

	//

	public void updateEmployeeList() {
		if (employeeList.size() == dataC.getEmployees().size()) {
			return;
		}

		employeeList.clear();
		for (Employee e : dataC.getEmployees()) {
			employeeList.add(new EmployeeCell(e));
		}
	}

	public void updateEmployeeData() {
		for (EmployeeCell eCell : employeeList) {
			eCell.update();
		}
	}

	//

	public void initDialog(Dialog<?> dialog) {
		dialog.setTitle(APP_NAME);
		dialog.setHeaderText(null);
		// dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

		dialog.initStyle(StageStyle.UTILITY);
		dialog.initOwner(stage);
	}

	public void showInfo(String info) {
		Alert infoDialog = new Alert(AlertType.INFORMATION);
		initDialog(infoDialog);

		infoDialog.setContentText(info);
		infoDialog.show();
	}

	public void showWarning(String info) {
		Alert warningDialog = new Alert(AlertType.WARNING);
		warningDialog.setTitle("Warning");
		initDialog(warningDialog);

		warningDialog.setContentText(info);
		warningDialog.showAndWait();
	}

	public Optional<ButtonType> askConfirm(String info) {
		Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
		initDialog(confirmDialog);

		confirmDialog.setContentText(info);
		return confirmDialog.showAndWait();
	}

	public Optional<String> askUserPass() {
		return askUser("Password:");
	}

	public Optional<String> askUser(String query) {
		return askUser(query, "");
	}

	public Optional<String> askUser(String query, String defaultValue) {
		TextInputDialog passDialog = new TextInputDialog();
		initDialog(passDialog);

		passDialog.setContentText(query);
		passDialog.getEditor().setText(defaultValue);
		return passDialog.showAndWait();
	}

	public File pickFile() {
		return pickSaveTextFile("Select a file");
	}

	public File pickSaveTextFile(String msg) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle(msg);
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text", "*.txt*"));
		fileChooser.setInitialFileName("*.txt");
		return fileChooser.showSaveDialog(stage);
	}

	//

	// Backups. Monthly, when removing employees, etc.

	// Make partial session list retrieval more efficient
	// Use a lazy-created sorter
	// Possible binary tree for sorting/finding

}
