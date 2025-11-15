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

	public static boolean addPayment(Connection conn, String month, int year, int staffId, float amount) {

		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {

			PreparedStatement pst = conn.prepareStatement(
					"INSERT INTO `aerowash`.`salary` (`staff_id`, `salary_month`, `salary_year`, `salary_amount`) VALUES (?, ?, ?, ?)");
			pst.setInt(1, staffId);
			pst.setString(2, month);
			pst.setInt(3, year);
			pst.setFloat(4, amount);

			if (pst.executeUpdate() != 1) {
				conn.rollback();
				return false;
			}

			conn.commit();
			conn.setAutoCommit(true);
			return true;

		} catch (SQLException ex) {
			ex.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return false;
		}
	}
}
