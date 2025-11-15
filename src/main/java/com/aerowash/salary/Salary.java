package com.aerowash.salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Salary {
	public static ResultSet getUnpaidStaff(Connection conn, String month, int year) {

		try {
			PreparedStatement pst = conn
					.prepareStatement("SELECT s.staff_id, s.staff_fname, s.staff_mname, s.staff_lname "
							+ "FROM staff s LEFT JOIN salary sa ON sa.staff_id = s.staff_id "
							+ "AND sa.salary_month = ? AND sa.salary_year = ? " + "WHERE sa.staff_id IS NULL");

			pst.setString(1, month);
			pst.setInt(2, year);

			return pst.executeQuery(); // return open ResultSet
		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
