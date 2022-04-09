package dsd.cms.server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import dsd.cms.interfaces.CenterServer;
import dsd.cms.model.StudentRecord;
import dsd.cms.model.TeacherRecord;
import dsd.cms.utils.LogWriter;

// TODO: Auto-generated Javadoc
/**
 * The Class DollardServer.
 */
public class DollardServer extends UnicastRemoteObject implements CenterServer {

	/** The main server. */
	MainServer mainServer;

	/** The teacher student records. */
	public Map<String, List<Object>> teacherStudentRecords;

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -8994808684143682770L;

	/** The log writer. */
	static LogWriter logWriter = new LogWriter();

	/**
	 * Instantiates a new dollard server.
	 *
	 * @throws RemoteException the remote exception
	 */
	protected DollardServer() throws RemoteException {
		super();
		this.mainServer = new MainServer();
		this.teacherStudentRecords = this.mainServer.loadData("src/main/resources/DollardData.txt");
	}

	/** The logger. */
	private static Logger logger = Logger.getLogger("Dollard");

	/**
	 * This is used to connect and retrieve data from server with specified port.
	 *
	 * @param port           port of the server to which communication has to be
	 *                       performed
	 * @param dollardServer2 server object
	 * @throws RemoteException       the remote exception
	 * @throws AlreadyBoundException the already bound exception
	 */
	public void serverConnection(int port, DollardServer dollardServer2) throws RemoteException, AlreadyBoundException {

		DollardServer dollardServer = dollardServer2;
		Registry registry3 = LocateRegistry.createRegistry(9993);
		registry3.bind("Dollard-des-Ormeaux", dollardServer);

		logger.info("Dollard-des-Ormeaux Server Started");
		System.out.println(
				"\nDollard-des-Ormeaux server is loaded with initial data. Map size : " + teacherStudentRecords.size());

		logWriter.serverInfo(LogWriter.DOLLARD, LogWriter.SYSTEM, LogWriter.PHASE_STARTUP,
				"Dollard-des-Ormeaux server started");
		logWriter.serverInfo(LogWriter.DOLLARD, LogWriter.SYSTEM, LogWriter.PHASE_STARTUP, "Initial data loaded.");

		while (true) {
			try (DatagramSocket ds = new DatagramSocket(port)) {

				byte[] receive = new byte[65535];
				DatagramPacket dp = new DatagramPacket(receive, receive.length);
				ds.receive(dp);
				byte[] data = dp.getData();
				String serviceName = new String(data);
				String outputStr = "";
				if (serviceName.trim().equalsIgnoreCase("getRecordCounts")) {
					outputStr = "DDO : " + this.mainServer.getRecordCount(this.teacherStudentRecords);
				}
				DatagramPacket dp1 = new DatagramPacket(outputStr.getBytes(), outputStr.length(), dp.getAddress(),
						dp.getPort());
				ds.send(dp1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates the T record.
	 *
	 * @param firstName      the first name
	 * @param lastName       the last name
	 * @param address        the address
	 * @param phone          the phone
	 * @param specialization the specialization
	 * @param location       the location
	 * @param clientId       the client id
	 * @return the string
	 */
	@Override
	public synchronized String createTRecord(String firstName, String lastName, String address, String phone,
			String specialization, String location, String clientId) {

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Teacher record creation requested with : (First Name : " + firstName + ", Last Name : " + lastName
						+ ", Address : " + address + ", Phone : " + phone + ", Specialization : " + specialization
						+ ", Location : " + location + ")");

		TeacherRecord teacherRecord = new TeacherRecord(firstName, lastName, address, phone, specialization, location);

		String resultMsg = this.mainServer.persistTeacherRecordInMap(teacherRecord, this.teacherStudentRecords)
				? "Teacher record " + teacherRecord.getRecordID()
						+ " created in Dollard-des-Ormeaux location with name : " + firstName + " " + lastName
				: "Error in creating Teacher Record.";
		System.out.println(resultMsg);
		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		return resultMsg;
	}

	/**
	 * Creates the S record.
	 *
	 * @param firstName        the first name
	 * @param lastName         the last name
	 * @param courseRegistered the course registered
	 * @param status           the status
	 * @param statusDate       the status date
	 * @param clientId         the client id
	 * @return the string
	 */
	@Override
	public synchronized String createSRecord(String firstName, String lastName, String courseRegistered, String status,
			String statusDate, String clientId) {

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Teacher record creation requested with : (First Name : " + firstName + ", Last Name : " + lastName
						+ ", Courses Registered : " + courseRegistered + ", Status : " + status + ", Status Date : "
						+ statusDate + ")");

		StudentRecord studentRecord = new StudentRecord(firstName, lastName, courseRegistered, status, statusDate);

		String resultMsg = this.mainServer.persistStudentRecordInMap(studentRecord, this.teacherStudentRecords)
				? "Student record " + studentRecord.getRecordID()
						+ " created in Dollard-des-Ormeaux location with name : " + firstName + " " + lastName
				: "Error in creating Student Record.";
		System.out.println(resultMsg);

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		return resultMsg;
	}

	/**
	 * Edits the record.
	 *
	 * @param recordID  the record ID
	 * @param fieldName the field name
	 * @param newValue  the new value
	 * @param clientId  the client id
	 * @return the string
	 * @throws RemoteException the remote exception
	 */
	@Override
	public synchronized String editRecord(String recordID, String fieldName, String newValue, String clientId)
			throws RemoteException {

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Edit Record requested for record with ID : " + recordID + ", Edit Field : " + fieldName
						+ ", New Value : " + newValue);

		String resultMsg = this.mainServer.updateRecordInMap(recordID, fieldName, newValue, this.teacherStudentRecords);

		System.out.println(resultMsg);
		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		return resultMsg;
	}

	/**
	 * Gets the record counts.
	 *
	 * @param clientId the client id
	 * @return the record counts
	 */
	@Override
	public synchronized String getRecordCounts(String clientId) {

		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_REQUEST,
				"Request to get record count of teachers and student from all three server locations.");

		String server1 = this.mainServer.datafromOtherServers(8880);
		String server2 = this.mainServer.datafromOtherServers(8881);
		String server3 = "DDO : " + this.mainServer.getRecordCount(this.teacherStudentRecords);

		String resultMsg = (server1 != null && server2 != null && server3 != null)
				? server1 + ", " + server2 + ", " + server3
				: "Error in retrieving record count. Please try again.";

		System.out.println(resultMsg);
		logWriter.serverInfo(LogWriter.DOLLARD, clientId, LogWriter.PHASE_RESPONSE, resultMsg);
		return resultMsg;
	}
}
