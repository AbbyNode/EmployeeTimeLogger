package com.blueberrys.employeetimelogger.adminv.sessions;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

import com.blueberrys.employeetimelogger.adminv.TabControllerAV;
import com.blueberrys.employeetimelogger.data.Employee;
import com.blueberrys.employeetimelogger.data.Session;
import com.blueberrys.employeetimelogger.view.EmployeeCell;
import com.blueberrys.employeetimelogger.view.SessionCell;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;

public class SessionTabController extends TabControllerAV {

	@FXML
	private ChoiceBox<EmployeeCell> employeeChoiceBox;

	@FXML
	private DatePicker startDatePicker;
	@FXML
	private DatePicker endDatePicker;

	@FXML
	private TableView<SessionCell> sessionTable;
	@FXML
	private TableColumn<SessionCell, String> dateCol;
	@FXML
	private TableColumn<SessionCell, String> signInTimeCol;
	@FXML
	private TableColumn<SessionCell, String> signOutTimeCol;
	@FXML
	private TableColumn<SessionCell, String> durationCol;

	@FXML
	private Spinner<Integer> hourSpinner;
	@FXML
	private Spinner<Integer> minuteSpinner;
	@FXML
	private ChoiceBox<Boolean> amPmChoiceBox;
	@FXML
	private ChoiceBox<Boolean> signInOrOutChoiceBox;

	//

	private ZonedDateTime startDate;
	private ZonedDateTime endDate;

	// private HashMap<Employee, ObservableList<SessionCell>> sessionListCache;
	private ObservableList<SessionCell> sessionList;

	//

	// private boolean modifySignIn;

	//

	@FXML
	private void initialize() {
		employeeChoiceBox.setConverter(new StringConverter<EmployeeCell>() {
			@Override
			public String toString(EmployeeCell eCell) {
				return eCell.getNameP().get();
			}

			@Override
			public EmployeeCell fromString(String string) {
				return new EmployeeCell(dataC.getEmployeeByName(string));
			}
		});
		employeeChoiceBox.getSelectionModel().selectedItemProperty().addListener(obs -> {
			updateSessionList();
		});

		//

		startDatePicker.setOnAction(event -> {
			LocalDate localDate = startDatePicker.getValue();
			startDate = (localDate == null ? null : localDate.atStartOfDay(ZoneOffset.systemDefault()));
			updateSessionList();
		});
		endDatePicker.setOnAction(event -> {
			LocalDate localDate = endDatePicker.getValue();
			endDate = (localDate == null ? null : localDate.atStartOfDay(ZoneOffset.systemDefault()));
			updateSessionList();
		});

		//

		dateCol.setCellValueFactory(cellData -> cellData.getValue().getDateP());
		signInTimeCol.setCellValueFactory(cellData -> cellData.getValue().getSignInTimeP());
		signOutTimeCol.setCellValueFactory(cellData -> cellData.getValue().getSignOutTimeP());
		durationCol.setCellValueFactory(cellData -> cellData.getValue().getDurationP());

		//

		hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12));
		minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60));

		amPmChoiceBox.setConverter(new StringConverter<Boolean>() {
			@Override
			public String toString(Boolean isAm) {
				return (isAm.booleanValue() ? "AM" : "PM");
			}

			@Override
			public Boolean fromString(String string) {
				return string.equals("AM");
			}
		});
		amPmChoiceBox.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));

		signInOrOutChoiceBox.setConverter(new StringConverter<Boolean>() {
			@Override
			public String toString(Boolean isSignIn) {
				return (isSignIn.booleanValue() ? "Sign In Time" : "Sign Out Time");
			}

			@Override
			public Boolean fromString(String string) {
				return string.equals("Sign In Time");
			}
		});
		signInOrOutChoiceBox.setItems(FXCollections.observableArrayList(Boolean.TRUE, Boolean.FALSE));

		//

		// sessionListCache = new HashMap<Employee, ObservableList<SessionCell>>();
		sessionList = FXCollections.observableArrayList();
		sessionTable.setItems(sessionList);
	} // initialize

	//

	/**
	 * Updates sessionList to follow day/time restrictions
	 */
	public void updateSessionList() {
		Employee employee = getSelectedEmployee();
		if (sessionList == null || employee == null) {
			return;
		}

		sessionList.clear();
		for (Session s : employee.getSessionsBetween(startDate, endDate)) {
			sessionList.add(new SessionCell(s));
		}
	}

	//

	@FXML
	private void modifySession() {
		// get session
		SessionCell sCell = sessionTable.getSelectionModel().getSelectedItem();
		if (sCell == null) {
			return;
		}
		Session session = sCell.getSession();

		// get modifications
		int hour = hourSpinner.getValue();
		int min = minuteSpinner.getValue();
		Boolean isAmItem = amPmChoiceBox.getSelectionModel().getSelectedItem();
		if (isAmItem == null) {
			return;
		}
		Boolean isSignInItem = signInOrOutChoiceBox.getSelectionModel().getSelectedItem();
		if (isSignInItem == null) {
			return;
		}
		boolean isSignIn = isSignInItem.booleanValue();

		//

		String confirmText = "Set this session's Sign "
				+ (isSignIn ? "In" : "Out")
				+ " time to: "
				+ hour + ":" + min + (min < 10 ? "0" : "") + " " + (isAmItem.booleanValue() ? "A" : "P") + "M ?";

		//

		ZonedDateTime oldTime;
		if (isSignIn) {
			oldTime = session.getSignInTime();
		} else {
			oldTime = session.getSignOutTime();
		}
		if (oldTime == null) {
			return;
		}

		//

		// get and validate new time
		if (!isAmItem.booleanValue()) {
			hour += 12;
		}
		ZonedDateTime newTime = oldTime.withHour(hour).withMinute(min);
		if (isSignIn) {
			if (newTime.isAfter(session.getSignOutTime())) {
				parentEl.showWarning("Can't set Sign In time later than Sign Out time");
				return;
			}
		} else { // isSignOut
			if (newTime.isBefore(session.getSignInTime())) {
				parentEl.showWarning("Can't set Sign Out time earlier than Sign In time");
				return;
			}
		}

		//

		Optional<ButtonType> result = parentEl.askConfirm(confirmText);
		if (!result.isPresent() || result.get() != ButtonType.OK) {
			return;
		}

		if (isSignIn) {
			session.setSignInTime(newTime);
		} else {
			session.setSignOutTime(newTime);
		}

		dataC.saveData();
		updateSessionList();
	}

	//

	private Employee getSelectedEmployee() {
		EmployeeCell eCell = employeeChoiceBox.getSelectionModel().getSelectedItem();
		return (eCell == null ? null : eCell.getEmployee());
	}

	//

	/* for (SessionCell sCell : getSessionListFromCache(getSelectedEmployee())) { Session s = sCell.getSession(); ZonedDateTime
	 * sessionTime = s.getSignInTime();
	 * 
	 * if (sessionTime == null) { continue; }
	 * 
	 * if (startDate != null && sessionTime.isBefore(startDate)) { continue; }
	 * 
	 * if (endDate != null && sessionTime.isAfter(endDate)) { continue; }
	 * 
	 * sessionList.add(sCell); } */
	/* private ObservableList<SessionCell> getSessionListFromCache(Employee e) { ObservableList<SessionCell> list =
	 * sessionListCache.get(e);
	 * 
	 * if (list == null) { list = FXCollections.observableArrayList(); for (Session s : e.getSessions()) { list.add(new
	 * SessionCell(s)); } sessionListCache.put(e, list); }
	 * 
	 * return list; } */

	//

	@Override
	public void setEmployeeList(ObservableList<EmployeeCell> employeeList) {
		employeeChoiceBox.setItems(employeeList);
	}

}
