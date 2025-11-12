package com.aerowash.customers.vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Vehicle {
	public static Map<Integer, String> getFlats(Connection conn, int customer_id) {
		String sql = "SELECT flat_id, flat_name FROM flat WHERE customer_id = ?";
		Map<Integer, String> flats = new HashMap<Integer, String>();
		
		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setInt(1, customer_id);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				flats.put(rs.getInt(1), rs.getString(2));
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return flats;
	}
}
