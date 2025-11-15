package com.aerowash.wash.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Expense {
	public static ResultSet getItems(Connection conn, int wash_id) {
		String sql = "SELECT * FROM expense WHERE wash_id = ?";

		try {
			PreparedStatement pst;

			pst = conn.prepareStatement(sql);
			pst.setInt(1, wash_id);

			return pst.executeQuery();

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
