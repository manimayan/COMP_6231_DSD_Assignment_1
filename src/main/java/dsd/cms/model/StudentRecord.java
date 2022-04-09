package dsd.cms.model;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentRecord {

	private String firstName;
	private String lastName;
	private String coursesRegistered;
	private String status;
	private static int idCounter = 10000;
	private LocalDate statusDate;
	private String recordID;

	/**
	 * @param firstName
	 * @param lastName
	 * @param coursesRegistered
	 * @param status
	 * @param statusDate
	 */
	public StudentRecord(String firstName, String lastName, String coursesRegistered, String status,
			String statusDate) {
		this.recordID = "SR" + idCounter++;
		this.firstName = firstName;
		this.lastName = lastName;
		this.coursesRegistered = coursesRegistered;
		this.status = status;
		LocalDate localDate = LocalDate.parse(statusDate);
		this.statusDate = localDate;
	}

	/**
	 * @param recordID
	 * @param firstName
	 * @param lastName
	 * @param coursesRegistered
	 * @param status
	 * @param statusDate
	 */
	public StudentRecord(String recordID, String firstName, String lastName, String coursesRegistered, String status,
			String statusDate) {
		this.recordID = recordID;
		this.firstName = firstName;
		this.lastName = lastName;
		this.coursesRegistered = coursesRegistered;
		this.status = status;
		LocalDate localDate = LocalDate.parse(statusDate);
		this.statusDate = localDate;
	}
}
