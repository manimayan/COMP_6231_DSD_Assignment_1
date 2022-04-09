package dsd.cms.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dsd.cms.interfaces.CenterServer;
import dsd.cms.utils.LogWriter;
import dsd.cms.utils.Validator;

// TODO: Auto-generated Javadoc
/**
 * The Class ManagerClient.
 */
public class ManagerClient {

	/** The buffer reader. */
	static BufferedReader bufferReader;

	/** The registry mtl. */
	static Registry registryMtl;

	/** The registry lvl. */
	static Registry registryLvl;

	/** The registry ddo. */
	static Registry registryDdo;

	/** The server instance. */
	static CenterServer serverInstance;

	/** The validate. */
	static Validator validate = new Validator();

	/** The log. */
	static LogWriter log = new LogWriter();

	/**
	 * main method to run the client side of Class Management System.
	 *
	 * @param args the arguments
	 * @throws IOException           Signals that an I/O exception has occurred.
	 * @throws NotBoundException     the not bound exception
	 * @throws InterruptedException  the interrupted exception
	 * @throws ExecutionException    the execution exception
	 * @throws AlreadyBoundException the already bound exception
	 */
	public static void main(String[] args)
			throws IOException, NotBoundException, InterruptedException, ExecutionException, AlreadyBoundException {

		bufferReader = new BufferedReader(new InputStreamReader(System.in));
		registryMtl = LocateRegistry.getRegistry(9991);
		registryLvl = LocateRegistry.getRegistry(9992);
		registryDdo = LocateRegistry.getRegistry(9993);

		while (true) {
			System.out.println("\nDistributed Class Management System\n===================================\n"
					+ "\nCreate an User to interact with the system" + "\n Format: MTL**** or LVL**** or DDO****"
					+ "\n\t *****:numbers\n" + "\nEnter an User Name:");
			String userName = bufferReader.readLine().trim();
			if (userName != null && validate.userName(userName)) {
				String managerPrefix = userName.substring(0, 3).toUpperCase();
				String choice;
				Matcher matcher = null;
				do {
					System.out.println(
							"\nChoose any of the options below to access the system\n1 : Create a Teacher Record\n2 : Create a Student Record"
									+ "\n3 : Edit a Record\n4 : Get Record Count\nC : To create new user\nSelect : ");
					choice = bufferReader.readLine().trim().toUpperCase();
					log.clientInfo(userName, LogWriter.ACCESS_SYSTEM, choice);
					switch (choice) {
					case "1":
						// create teacher record
						log.clientInfo(userName, LogWriter.PHASE_TR, "selected Teacher Record");
						createTeacherRecord(userName, managerPrefix, bufferReader);
						break;
					case "2":
						// create student record
						log.clientInfo(userName, LogWriter.PHASE_SR, "selected Student Record");
						createStudentRecord(userName, managerPrefix, bufferReader);
						break;
					case "3":
						// edit a record
						log.clientInfo(userName, LogWriter.PHASE_ER, "selected Edit Record");
						editRecord(userName, managerPrefix, bufferReader);
						break;
					case "4":
						// get record count
						log.clientInfo(userName, LogWriter.PHASE_GRC, "selected getRecordCount");
						createServerInstance(managerPrefix);
						String recordCount = serverInstance.getRecordCounts(userName);
						System.out.println(recordCount);
						log.clientInfo(userName, LogWriter.PHASE_GRC, recordCount);
						break;
					case "C":
						break;
					default:
						System.out.println("Invalid Choice");
						log.clientInfo(userName, LogWriter.ACCESS_SYSTEM, "Invalid Choice");
						continue;
					}
					Pattern pattern = Pattern.compile("^[1-2-3-4]{1}");
					matcher = pattern.matcher(choice);
				} while (matcher != null && matcher.matches() && !choice.equalsIgnoreCase("C"));
			} else {
				System.out.println("===== user name is invalid =====");
			}
		}

	}

	/**
	 * Creates the teacher record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws IOException       Signals that an I/O exception has occurred.
	 * @throws NotBoundException the not bound exception
	 */
	private static void createTeacherRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws IOException, NotBoundException {
		System.out.println("Enter teacher record inputs in the following format\n");
		System.out.println(
				"FirstName, LastName, Address, Phone, Specialization(Format: JAVA|HTML5|PHP), location (Format : MTL or LVL or DDO)");
		System.out.println("Example: Kevin, Peterson, 23,Lords street,London-987, 9023123445, Java|Python|Scala, MTL");
		System.out.println("\nenter a teacher record : ");
		String trInput = bufferReader.readLine().trim();

		List<String> trOutput = validate.teacherRecord(userName, trInput);

		if (trOutput != null) {
			createServerInstance(managerPrefix);
			String teacherSaveStatus = serverInstance.createTRecord(trOutput.get(0), trOutput.get(1), trOutput.get(2),
					trOutput.get(3), trOutput.get(4), trOutput.get(5), userName);
			System.out.println(teacherSaveStatus);
			log.clientInfo(userName, LogWriter.PHASE_TR, teacherSaveStatus);
		}
	}

	/**
	 * Creates the student record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws IOException       Signals that an I/O exception has occurred.
	 * @throws NotBoundException the not bound exception
	 */
	private static void createStudentRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws IOException, NotBoundException {
		System.out.println("Enter Student record inputs in the following format\n");
		System.out.println(
				"FirstName, LastName, CourseRegistered(Format: JAVA|HTML5|PHP), Status(Format: 1-Active or 0-InActive), StatusDate(Format: yyyy-mm-dd)");
		System.out.println("Example: James, Anderson, Java|Python|Scala, 1, 2021-12-22");
		System.out.println("\nenter a Student record : ");
		String stInput = bufferReader.readLine().trim();

		List<String> stOutput = validate.studentRecord(userName, stInput);

		if (stOutput != null) {
			createServerInstance(managerPrefix);
			String studentSaveStatus = serverInstance.createSRecord(stOutput.get(0), stOutput.get(1), stOutput.get(2),
					stOutput.get(3), stOutput.get(4), userName);
			System.out.println(studentSaveStatus);
			log.clientInfo(userName, LogWriter.PHASE_SR, studentSaveStatus);
		}
	}

	/**
	 * Edits the record.
	 *
	 * @param userName      the user name
	 * @param managerPrefix the manager prefix
	 * @param bufferReader  the buffer reader
	 * @throws IOException       Signals that an I/O exception has occurred.
	 * @throws NotBoundException the not bound exception
	 */
	private static void editRecord(String userName, String managerPrefix, BufferedReader bufferReader)
			throws IOException, NotBoundException {
		System.out.println("\nEnter input to edit a record in the following format");
		System.out.println("\nRecordId, fieldName, newValue");
		System.out.println("\nTeacher Record Format: (\"TR*****\", \"ADDRESS or PHONE or LOCATION\", \"newValue\")");
		System.out.println("Example : (TR12345, PHONE, 9023412231)");
		System.out.println(
				"\nStudent Record Format: (\"SR*****\", \"COURSEREGISTERED or STATUS or STATUSDATE\", \"newValue\")");
		System.out.println("Example: (SR12345, COURSEREGISTERED, JAVA|HTML5|PHP)");
		System.out.println("\nenter a record to edit : ");
		String erInput = bufferReader.readLine().trim();

		List<String> erOutput = validate.editRecord(userName, erInput);

		if (erOutput != null) {
			createServerInstance(managerPrefix);
			String editStatus = serverInstance.editRecord(erOutput.get(0), erOutput.get(1), erOutput.get(2), userName);
			System.out.println(editStatus);
			log.clientInfo(userName, LogWriter.PHASE_ER, editStatus);
		}
	}

	/**
	 * This method is used to set the server instance based on location.
	 *
	 * @param server the server
	 * @throws RemoteException   the remote exception
	 * @throws NotBoundException the not bound exception
	 */
	public static void createServerInstance(String server) throws RemoteException, NotBoundException {
		if ("MTL".equalsIgnoreCase(server)) {
			serverInstance = (CenterServer) registryMtl.lookup("Montreal");
		}
		if ("LVL".equalsIgnoreCase(server)) {
			serverInstance = (CenterServer) registryLvl.lookup("Laval");
		}
		if ("DDO".equalsIgnoreCase(server)) {
			serverInstance = (CenterServer) registryDdo.lookup("Dollard-des-Ormeaux");
		}
	}
}
