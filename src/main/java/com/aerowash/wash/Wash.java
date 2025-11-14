package com.aerowash.wash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

		String baseQuery = "SELECT w.wash_id, w.staff_id, w.vehicle_id, w.wash_date, "
				+ "t.transaction_date, t.transaction_amount, " + "t.transaction_status, t.transaction_mode "
				+ "FROM transactions t " + "JOIN wash w ON t.transaction_id = w.transaction_id "
				+ "LEFT JOIN expense e ON w.wash_id = e.wash_id";

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

	public static int getParam(HttpServletRequest request, String arg) {
		int param;

		try {
			param = Integer.parseInt(request.getParameter(arg));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return 0;
		}

		return param;

	}

}
