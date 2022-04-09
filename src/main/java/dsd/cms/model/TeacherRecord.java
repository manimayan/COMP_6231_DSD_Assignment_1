package dsd.cms.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeacherRecord {

	private String firstName;
	private String lastName;
	private String address;
	private String phone;
	private String specilization;
	private String location;
	private static int idCounter = 10000;
	private String recordID;

	/**
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @param phone
	 * @param specilization
	 * @param location
	 */
	public TeacherRecord(String firstName, String lastName, String address, String phone, String specilization,
			String location) {

		this.recordID = "TR" + idCounter++;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phone = phone;
		this.specilization = specilization;
		this.location = location;
	}

	/**
	 * @param recordID
	 * @param firstName
	 * @param lastName
	 * @param address
	 * @param phone
	 * @param specilization
	 * @param location
	 */
	public TeacherRecord(String recordID, String firstName, String lastName, String address, String phone,
			String specilization, String location) {

		this.recordID = recordID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.phone = phone;
		this.specilization = specilization;
		this.location = location;
	}
}
