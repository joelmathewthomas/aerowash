package com.aerowash.wash.transactions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Transaction {
	public static boolean setPaymentStatus(Connection conn, int transaction_id, int choice) {

		try {
			conn.setAutoCommit(false);

			String status = (choice == 0) ? "INCOMPLETE" : "COMPLETE";

			try (PreparedStatement pst = conn.prepareStatement(
					"UPDATE transactions SET transaction_status = ?, transaction_date = ? WHERE transaction_id = ?")) {
				pst.setString(1, status);
				if (choice == 1) {
					pst.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
				} else {
					pst.setNull(2, java.sql.Types.DATE);
				}

				pst.setInt(3, transaction_id);

				if (pst.executeUpdate() != 1) {
					conn.rollback();
					return false;
				}
			}

			conn.commit();
			conn.setAutoCommit(true);
			return true;

		} catch (SQLException ex) {
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ex.printStackTrace();
		}

		return true;
	}

}
