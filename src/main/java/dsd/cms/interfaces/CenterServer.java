package dsd.cms.interfaces;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ExecutionException;

public interface CenterServer extends Remote {
	/**
	 * @param firstName      first name of teacher
	 * @param lastName       last name of teacher
	 * @param address        address details
	 * @param phone          phone number
	 * @param specialization specialization
	 * @param location       location
	 * @param clientId       manager id
	 * @return result message
	 * @throws RemoteException
	 */
	public abstract String createTRecord(String firstName, String lastName, String address, String phone,
			String specialization, String location, String clientId) throws RemoteException;

	/**
	 * @param firstName        first name of student
	 * @param lastName         last name of student
	 * @param courseRegistered courses enrolled
	 * @param status           status active or inactive
	 * @param statusDate       status updated date
	 * @param clientId         manager id
	 * @return result message
	 * @throws RemoteException
	 */
	public abstract String createSRecord(String firstName, String lastName, String courseRegistered, String status,
			String statusDate, String clientId) throws RemoteException;

	/**
	 * @param clientId manager id
	 * @return result message
	 * @throws RemoteException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws AlreadyBoundException
	 */
	public abstract String getRecordCounts(String clientId)
			throws RemoteException, InterruptedException, ExecutionException, AlreadyBoundException;

	/**
	 * @param recordID  record id to edit
	 * @param fieldName field name to edit
	 * @param newValue  value to be updated
	 * @param clientId  manager id
	 * @return result message
	 * @throws RemoteException
	 */
	public abstract String editRecord(String recordID, String fieldName, String newValue, String clientId)
			throws RemoteException;

}
