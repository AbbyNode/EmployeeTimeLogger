package com.blueberrys.employeetimelogger.data.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.blueberrys.employeetimelogger.data.Admin;
import com.blueberrys.employeetimelogger.data.DataObj;
import com.blueberrys.employeetimelogger.data.Employee;

public class DataController {
	public static final String DATAFILE_PATH = "EmployeeTimeLogger_Data.ser";

	private File dataFile;
	private DataObj dataObj;
	private int activeSessions;

	private boolean dataCorrupted;

	public DataController() {
		this.dataFile = new File(DATAFILE_PATH);
	}

	//

	/**
	 * 
	 * @param e
	 * @return
	 */
	public SignResult signIn(Employee e) {
		return signIn(e, null);
	}

	/**
	 * 
	 * @param e
	 * @param pass
	 * @return
	 */
	public SignResult signIn(Employee e, String pass) {
		return signIn(e, pass, false);
	}

	/**
	 * 
	 * @param e
	 * @param pass
	 * @param admin
	 * @return
	 */
	public SignResult signIn(Employee e, String pass, boolean admin) {
		if (!dataObj.getEmployees().contains(e)) {
			return SignResult.NOT_IN_LIST;
		}

		if (e.isSignedIn()) {
			return SignResult.ALREADY_COMPLETE;
		}

		if (!admin && !e.testPass(pass)) {
			return SignResult.INVALID_PASS;
		}

		e.signIn();

		activeSessions++;
		dataObj.setConcluded(false);

		saveData();

		return SignResult.SUCCESS;
	}

	//

	/**
	 * 
	 * @param e
	 * @return
	 */
	public SignResult signOut(Employee e) {
		return signOut(e, null);
	}

	/**
	 * 
	 * @param e
	 * @param pass
	 * @return
	 */
	public SignResult signOut(Employee e, String pass) {
		return signOut(e, pass, false);
	}

	/**
	 * 
	 * @param e
	 * @param pass
	 * @param admin
	 * @return
	 */
	public SignResult signOut(Employee e, String pass, boolean admin) {
		if (!dataObj.getEmployees().contains(e)) {
			return SignResult.NOT_IN_LIST;
		}

		if (!e.isSignedIn()) {
			return SignResult.ALREADY_COMPLETE;
		}

		if (!admin && !e.testPass(pass)) {
			return SignResult.INVALID_PASS;
		}

		e.signOut();

		activeSessions--;
		if (activeSessions <= 0) {
			dataObj.setConcluded(true);
		}

		saveData();

		return SignResult.SUCCESS;
	}

	//

	/**
	 * 
	 * @return true if success, false if invalid password
	 */
	public void endAllSessions() {
		for (Employee e : dataObj.getEmployees()) {
			// if (e.isSignedIn()) {
			e.signOut();
		}

		activeSessions = 0;
		dataObj.setConcluded(true);

		saveData();
	}

	//

	/**
	 * 
	 * @param pass
	 */
	public void setAdminPass(String pass) {
		getAdmin().setPass(pass);
		saveData();
	}

	/**
	 * Add an employee without a password
	 * 
	 * @param name
	 */
	public void addEmployee(String name) {
		addEmployee(name, null);
	}

	/**
	 * Add an employee with a new password
	 * 
	 * @param name
	 * @param pass
	 */
	public void addEmployee(String name, String pass) {
		Employee e = new Employee(name);
		if (pass != null) {
			e.setPass(pass);
		}
		dataObj.getEmployees().add(e);
		saveData();
	}

	/* public void removeEmployee(Employee e) { dataObj.getEmployees().remove(e); // dataObj.getEmployees().removeIf(e ->
	 * e.getName().equals(name)); saveData(); } */

	public boolean setEmployeeName(Employee e, String name) {
		if (!dataObj.getEmployees().contains(e)) {
			return false;
		}

		e.setName(name);
		saveData();
		return true;
	}

	public boolean setEmployeePass(Employee e, String pass) {
		if (!dataObj.getEmployees().contains(e)) {
			return false;
		}

		e.setPass(pass);
		saveData();
		return true;
	}

	//

	/**
	 * 
	 */
	public void resumeLastSessions() {
		for (Employee e : dataObj.getEmployees()) {
			if (e.tryResumeLastSession()) {
				activeSessions++;
			}
		}
	}

	//

	/**
	 * 
	 */
	public void loadData() {
		if (!dataFile.isFile()) {
			dataObj = new DataObj();
			return;
		}

		// Auto-close
		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dataFile))) {
			Object obj = in.readObject();
			if (obj instanceof DataObj) {
				dataObj = (DataObj) obj;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (dataObj == null) {
			dataCorrupted = true;
			dataObj = new DataObj();
		}

		if (!dataObj.isConcluded()) {
			resumeLastSessions();
		}

		// addEmployee("one", "1");
		// addEmployee("two", "2");
		// addEmployee("three", "3");

		// Employee e = dataObj.getEmployees().get(0);
		// for (int i = 0; i <= 100000; i++) {
		// e.signIn();
		// e.signOut();
		// }
		// saveData();
	}

	/**
	 * 
	 */
	public void saveData() {
		if (dataCorrupted) {
			return;
		}

		// Auto-close
		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dataFile))) {
			out.writeObject(dataObj);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// get/set

	/**
	 * 
	 * @return the admin
	 */
	public Admin getAdmin() {
		return dataObj.getAdmin();
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public Employee getEmployeeByName(String name) {
		for (Employee e : dataObj.getEmployees()) {
			if (e.getName().equals(name)) {
				return e;
			}
		}
		return null;
	}

	/**
	 * @return the employees
	 */
	public ArrayList<Employee> getEmployees() {
		return dataObj.getEmployees();
	}

	//

	/**
	 * @return the dataCorrupted
	 */
	public boolean isDataCorrupted() {
		return dataCorrupted;
	}

	//

	/**
	 * 
	 * @author Blueberry
	 *
	 */
	public enum SignResult {
		SUCCESS, ALREADY_COMPLETE, INVALID_PASS, NOT_IN_LIST
	}
}
