package dsd.cms.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import dsd.cms.model.StudentRecord;
import dsd.cms.model.TeacherRecord;

// TODO: Auto-generated Javadoc
/**
 * The Class MainServer.
 */
public class MainServer {

	/**
	 * Instantiates a new main server.
	 */
	public MainServer() {
		super();
	}

	/**
	 * main method to run all the servers.
	 *
	 * @param args arguments for main function
	 */
	public static void main(String args[]) {

		try {
			buildLogDirectory("./logs");
			startServers();
		} catch (RemoteException e) {
			System.out.println("Error in starting the server...");
		}
	}

	/**
	 * This method is used to create logs directory to store the logs.
	 * 
	 * @param path location of the logs folder
	 */
	public static void buildLogDirectory(String path) {
		File outputDir = new File(path);
		if (!outputDir.exists()) {
			outputDir.mkdir();
		}
	}

	/**
	 * Starts all three servers.
	 *
	 * @throws RemoteException the remote exception
	 */
	public static void startServers() throws RemoteException {
		MontrealServer montrealServer = new MontrealServer();
		Runnable eu = () -> {
			try {
				montrealServer.serverConnection(8880, montrealServer);
			} catch (RemoteException | AlreadyBoundException e) {
				e.printStackTrace();
			}
		};
		Thread t1 = new Thread(eu);
		t1.start();

		LavalServer lavalServer = new LavalServer();
		Runnable as = () -> {
			try {
				lavalServer.serverConnection(8881, lavalServer);
			} catch (RemoteException | AlreadyBoundException e) {
				e.printStackTrace();
			}
		};
		Thread t2 = new Thread(as);
		t2.start();

		DollardServer dollardServer = new DollardServer();
		Runnable na = () -> {
			try {
				dollardServer.serverConnection(8882, dollardServer);
			} catch (RemoteException | AlreadyBoundException e) {
				e.printStackTrace();
			}
		};
		Thread t3 = new Thread(na);
		t3.start();
	}

	/**
	 * Persists teacher record in map against correct key.
	 *
	 * @param teacherRecord         teacher record to be stored
	 * @param teacherStudentRecords map maintained at server
	 * @return true, if successful
	 */
	public boolean persistTeacherRecordInMap(TeacherRecord teacherRecord,
			Map<String, List<Object>> teacherStudentRecords) {
		if (teacherStudentRecords.isEmpty() || !teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))))) {
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))),
					Arrays.asList(teacherRecord));
			return true;
		} else if (teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))))) {

			List<Object> records = new ArrayList<>(teacherStudentRecords
					.get(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0)))));
			records.add(teacherRecord);
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(teacherRecord.getLastName().charAt(0))),
					records);
			return true;
		}
		return false;
	}

	/**
	 * Persists student record in map against correct key.
	 *
	 * @param studentRecord         student record to be stored
	 * @param teacherStudentRecords map maintained at server
	 * @return true, if successful
	 */
	public synchronized boolean persistStudentRecordInMap(StudentRecord studentRecord,
			Map<String, List<Object>> teacherStudentRecords) {
		if (teacherStudentRecords.isEmpty() || !teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))))) {
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))),
					Arrays.asList(studentRecord));
			return true;
		} else if (teacherStudentRecords
				.containsKey(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))))) {
			List<Object> records = new ArrayList<>(teacherStudentRecords
					.get(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0)))));
			records.add(studentRecord);
			teacherStudentRecords.put(Character.toString(Character.toUpperCase(studentRecord.getLastName().charAt(0))),
					records);
			return true;
		}
		return false;
	}

	/**
	 * Updated the record information in map.
	 *
	 * @param recordID              record id which needs to be updated
	 * @param fieldName             field name which needs to be updated
	 * @param newValue              new value to be updated against field
	 * @param teacherStudentRecords the teacher student records
	 * @return result message
	 */
	public synchronized String updateRecordInMap(String recordID, String fieldName, String newValue,
			Map<String, List<Object>> teacherStudentRecords) {
		if (recordID.startsWith("SR")) {
			return this.updateStudentRecord(recordID, fieldName, newValue, teacherStudentRecords);
		} else if (recordID.contains("TR")) {
			return this.updateTeacherRecord(recordID, fieldName, newValue, teacherStudentRecords);
		}
		return "Update failed due to invalid Record ID passed";
	}

	/**
	 * Updates teacher record.
	 * 
	 * @param recordID              record id which needs to be updated
	 * @param fieldName             field name which needs to be updated
	 * @param newValue              new value to be updated against field
	 * @param teacherStudentRecords map maintained at server
	 * @return result message
	 */
	public synchronized String updateTeacherRecord(String recordID, String fieldName, String newValue,
			Map<String, List<Object>> teacherStudentRecords) {
		String resultMsg = null;
		boolean result = false;
		String output = null;
		for (Entry<String, List<Object>> entry : teacherStudentRecords.entrySet()) {
			for (Object object : entry.getValue()) {
				if (object instanceof TeacherRecord
						&& ((TeacherRecord) object).getRecordID().equalsIgnoreCase(recordID)) {
					if (fieldName.equalsIgnoreCase("ADDRESS")) {
						((TeacherRecord) object).setAddress(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("PHONE")) {
						((TeacherRecord) object).setPhone(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("LOCATION")) {
						((TeacherRecord) object).setLocation(newValue.toUpperCase());
						result = true;
					}
					output = "Id:" + ((TeacherRecord) object).getRecordID() + ", FN:"
							+ ((TeacherRecord) object).getFirstName() + ", LN:" + ((TeacherRecord) object).getLastName()
							+ ", Address:" + ((TeacherRecord) object).getAddress() + ", Phone:"
							+ ((TeacherRecord) object).getPhone() + ", Specialization:"
							+ ((TeacherRecord) object).getSpecilization() + ", Location:"
							+ ((TeacherRecord) object).getLocation();
				}
			}
		}
		resultMsg = result ? "Edit operation performed successfully.\nUpdated Record: " + output
				: "Teacher Record with given id : " + recordID + " was not found.";
		return resultMsg;
	}

	/**
	 * Updates student record.
	 * 
	 * @param recordID              record id which needs to be updated
	 * @param fieldName             field name which needs to be updated
	 * @param newValue              new value to be updated against field
	 * @param teacherStudentRecords map maintained at server
	 * @return result message
	 */
	public synchronized String updateStudentRecord(String recordID, String fieldName, String newValue,
			Map<String, List<Object>> teacherStudentRecords) {
		boolean result = false;
		String resultMsg = null;
		String output = null;
		for (Entry<String, List<Object>> entry : teacherStudentRecords.entrySet()) {
			for (Object object : entry.getValue()) {
				if (object instanceof StudentRecord
						&& ((StudentRecord) object).getRecordID().equalsIgnoreCase(recordID)) {
					if (fieldName.equalsIgnoreCase("COURSEREGISTERED")) {
						((StudentRecord) object).setCoursesRegistered(newValue);
						result = true;
					} else if (fieldName.equalsIgnoreCase("STATUS")) {
						((StudentRecord) object).setStatus(newValue);
						((StudentRecord) object).setStatusDate(LocalDate.now());
						result = true;
					} else if (fieldName.equalsIgnoreCase("STATUSDATE")) {
						LocalDate localDate = LocalDate.parse(newValue);
						((StudentRecord) object).setStatusDate(localDate);
						result = true;
					}
					output = "Id:" + ((StudentRecord) object).getRecordID() + ", FN:"
							+ ((StudentRecord) object).getFirstName() + ", LN:" + ((StudentRecord) object).getLastName()
							+ ", CoursesRegistered:" + ((StudentRecord) object).getCoursesRegistered() + ", Status:"
							+ ((StudentRecord) object).getStatus() + ", StatusDate:"
							+ ((StudentRecord) object).getStatusDate();
				}
			}
		}
		resultMsg = result ? "Edit operation performed successfully.\nUpdated Record: " + output
				: "Student Record with given id : " + recordID + " was not found.";
		return resultMsg;
	}

	/**
	 * This method is used to communicate with the other servers to info about
	 * student and teacher counts.
	 *
	 * @param port port of the other servers that are running on
	 * @return String containing total number of students and teacher present at
	 *         server of given port
	 */
	public String datafromOtherServers(int port) {

		try (DatagramSocket socket = new DatagramSocket();) {

			byte[] b = new byte[65535];
			String request = "getRecordCounts";

			TimeUnit.MILLISECONDS.sleep(20);
			DatagramPacket packetToSend = new DatagramPacket(request.getBytes(), request.getBytes().length,
					InetAddress.getByName("localhost"), port);
			socket.send(packetToSend);

			DatagramPacket recievedPacket = new DatagramPacket(b, b.length);
			socket.receive(recievedPacket);
			String returnData = new String(recievedPacket.getData());
			return returnData.trim();
		} catch (IOException | InterruptedException e) {
			return null;
		}
	}

	/**
	 * Computes record from map.
	 *
	 * @param teacherStudentRecords the teacher student records
	 * @return the record count
	 */
	public int getRecordCount(Map<String, List<Object>> teacherStudentRecords) {
		int count = 0;
		for (Entry<String, List<Object>> entry : teacherStudentRecords.entrySet()) {
			count += entry.getValue().size();
		}
		return count;
	}

	/**
	 * Loads initial data into server.
	 * 
	 * @param filePath path at which data is stored
	 * @return updated map
	 */
	public synchronized Map<String, List<Object>> loadData(String filePath) {
		Map<String, List<Object>> teacherStudentRecords = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

			String line = reader.readLine();
			while (line != null) {
				String[] listParts = line.split(",");

				// recordID
				String recordID = listParts[0];
				// firstName
				String firstName = listParts[1];
				// lastName
				String lastName = listParts[2];

				if (recordID.contains("TR")) {
					// address
					String[] addArr = Arrays.copyOfRange(listParts, 3, listParts.length - 3);
					String address = String.join(",", addArr).trim();
					// phone
					String phone = listParts[listParts.length - 3];
					// specialization
					String specArr = listParts[listParts.length - 2];
					String specialization = specArr.replace("|", ",");
					// location
					String location = listParts[listParts.length - 1];

					TeacherRecord teacherRecord = new TeacherRecord(recordID, firstName, lastName, address, phone,
							specialization, location);
					this.persistTeacherRecordInMap(teacherRecord, teacherStudentRecords);

				} else if (recordID.contains("SR")) {
					// course registered
					String courseArr = listParts[3];
					String courseRegistered = courseArr.replace("|", ",");
					// status
					String status = listParts[4];
					// status date
					String statusDate = listParts[5];

					StudentRecord studentRecord = new StudentRecord(firstName, lastName, courseRegistered, status,
							statusDate);
					this.persistStudentRecordInMap(studentRecord, teacherStudentRecords);
				}
				line = reader.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return teacherStudentRecords;
	}
}
