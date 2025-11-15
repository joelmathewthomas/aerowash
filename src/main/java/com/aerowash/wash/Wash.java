package com.aerowash.wash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Wash {

	public static ResultSet getJobs(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null)
			return null;

		Integer staffId = (Integer) session.getAttribute("staffid");
		if (staffId == null)
			return null;

		// Staff does not exist
		if (staffId == 0)
			return null;

		String baseQuery = "SELECT w.wash_id, w.staff_id, w.vehicle_id, w.wash_date, t.transaction_date, t.transaction_amount, t.transaction_status, t.transaction_mode, t.transaction_id FROM transactions t JOIN wash w ON t.transaction_id = w.transaction_id";

		try {
			PreparedStatement pst;

			if (staffId == -1) {
				// Admin
				pst = conn.prepareStatement(baseQuery);
			} else {
				// Valid staff
				pst = conn.prepareStatement(baseQuery + " WHERE w.staff_id = ?");
				pst.setInt(1, staffId);
			}

			return pst.executeQuery();

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static boolean addNewJob(Connection conn, HttpSession session, int vehicle_id, String transaction_mode) {

		Integer staffId = (Integer) session.getAttribute("staffid");
		if (staffId == null || staffId <= 0) {
			return false;
		}

		String transactionsSql = "INSERT INTO transactions (transaction_amount, transaction_status, transaction_mode) VALUES (500, 'INCOMPLETE', ?)";
		String washSql = "INSERT INTO wash (staff_id, vehicle_id, transaction_id, wash_date) VALUES (?, ?, ?, ?)";
		String expenseSql = "INSERT INTO expense (wash_id, expense_item, expense_amount, expense_date ) VALUES (?, 'SERVICE_CHARGE' , 500, ?)";

		try {
			conn.setAutoCommit(false);

			try (PreparedStatement pstTransaction = conn.prepareStatement(transactionsSql,
					Statement.RETURN_GENERATED_KEYS)) {

				if (!("ONLINE".equals(transaction_mode) || "OFFLINE".equals(transaction_mode))) {
					conn.rollback();
					System.out.println("false1");
					return false;
				}
				pstTransaction.setString(1, transaction_mode);

				if (pstTransaction.executeUpdate() != 1) {
					conn.rollback();
					System.out.println("false2");
					return false;
				}

				// get generated transaction_id
				ResultSet rs = pstTransaction.getGeneratedKeys();
				if (!rs.next()) {
					conn.rollback();
					System.out.println("false3");
					return false;
				}

				int transaction_id = rs.getInt(1);

				// Insert into wash
				try (PreparedStatement pstWash = conn.prepareStatement(washSql, Statement.RETURN_GENERATED_KEYS)) {
					pstWash.setInt(1, staffId);
					pstWash.setInt(2, vehicle_id);
					pstWash.setInt(3, transaction_id);
					pstWash.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

					if (pstWash.executeUpdate() != 1) {
						conn.rollback();
						System.out.println("false4");
						return false;
					}

					rs = pstWash.getGeneratedKeys();
					if (!rs.next()) {
						conn.rollback();
						System.out.println("false5");
						return false;
					}

					int wash_id = rs.getInt(1);

					// Insert into expense
					try (PreparedStatement pstExpense = conn.prepareStatement(expenseSql)) {
						pstExpense.setInt(1, wash_id);
						pstExpense.setDate(2, java.sql.Date.valueOf(LocalDate.now()));

						if (pstExpense.executeUpdate() != 1) {
							conn.rollback();
							System.out.println("false6");
							return false;
						}
					}
				}

				conn.commit();
				return true;
			}

		} catch (SQLException ex) {
			ex.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("fale7");
			return false;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException ex) {
				ex.printStackTrace();
			}
		}
	}

	public static int getParam(HttpServletRequest request, String arg) {
		int param;

		try {
			param = Integer.parseInt(request.getParameter(arg));
		} catch (NumberFormatException ex) {
			return 0;
		}

		return param;

	}

}
