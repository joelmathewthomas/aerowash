package com.aerowash.customers.vehicle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class Vehicle {

	private final int flat_id;
	private final int customer_id;
	private final String vehicle_name;
	private final String vehicle_license_number;
	private final LocalDate vehicle_added_date;

	public Vehicle(int customer_id, int flat_id, String vehicle_name, String vehicle_license_number) {
		this.customer_id = customer_id;
		this.flat_id = flat_id;
		this.vehicle_name = sanitizeString(vehicle_name);
		this.vehicle_license_number = sanitizeString(vehicle_license_number);
		this.vehicle_added_date = LocalDate.now();
	}

	public int getCustomer_id() {
		return customer_id;
	}

	// Remove leading and trailing whitespace
	private static String sanitizeString(String s) {
		return s == null ? "" : s.trim();
	}

	// Return a new Flat object directly from request
	public static Vehicle getFromForm(HttpServletRequest request) {
		int customer_id = 0;
		int flat_id = 0;

		try {
			customer_id = Integer.parseInt(sanitizeString(request.getParameter("customer_id")));
			flat_id = Integer.parseInt(sanitizeString(request.getParameter("flat_id")));
			return new Vehicle(customer_id, flat_id, request.getParameter("vehicle_name"),
					request.getParameter("vehicle_license_number"));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// Check patterns
	public String validateForm() {
		if (!String.valueOf(customer_id).matches("^[0-9]+$"))
			return "customer_id";
		if (!String.valueOf(flat_id).matches("^[0-9]+$"))
			return "flat_id";
		if (!vehicle_name.matches("^[A-Za-z ]*$"))
			return "vehicle_name";
		if (!vehicle_license_number.matches("^[A-Z]{2}\\s?\\d{1,2}\\s?[A-Z]{0,3}\\s?\\d{1,4}$"))
			return "vehicle_license_number";

		return null;
	}

	private static String getDuplicateKey(String error) {
		if (error.contains("vehicle_number")) {
			return "duplicate_vehicle_license_number";
		} else {
			return null;
		}

	}

	public String addRecord(Connection conn) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String sql = "INSERT INTO aerowash.vehicle (flat_id, customer_id, vehicle_name, vehicle_license_number, vehicle_added_date) VALUES (?, ?, ?, ?, ?)";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			// Insert record
			pst.setInt(1, flat_id);
			pst.setInt(2, customer_id);
			pst.setString(3, vehicle_name);
			pst.setString(4, vehicle_license_number);
			pst.setDate(5, java.sql.Date.valueOf(vehicle_added_date));

			if (pst.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to add vehicle details";
			}

			// Insertion succeeded
			conn.commit();
			return null;

		} catch (SQLIntegrityConstraintViolationException ex) {
			conn.rollback();
			ex.printStackTrace();
			return Vehicle.getDuplicateKey(ex.getMessage());
		} catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			return "Failed to add record";
		}
	}

	public String updateRecord(Connection conn, int vehicle_id) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String sql = "UPDATE vehicle SET vehicle_name = ?, vehicle_license_number = ?, flat_id = ? WHERE vehicle_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			// Update customer
			pst.setString(1, vehicle_name);
			pst.setString(2, vehicle_license_number);
			pst.setInt(3, flat_id);
			pst.setInt(4, vehicle_id);

			if (pst.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to update vehicle details";
			}

			// Update succeeded
			conn.commit();
		} catch (SQLIntegrityConstraintViolationException ex) {
			conn.rollback();
			ex.printStackTrace();
			return Vehicle.getDuplicateKey(ex.getMessage());
		} catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			return "Failed to update details";
		}

		return null;
	}

	public static String deleteRecord(Connection conn, int vehicle_id, int customer_id) {

		try (PreparedStatement pst = conn.prepareStatement("SELECT vehicle_id from vehicle WHERE customer_id = ?")) {
			pst.setInt(1, customer_id);
			ResultSet rs = pst.executeQuery();

			if (!rs.next()) {
				return "Customer does not exist";
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "Failed to check for customer in database";
		}

		try {

			conn.setAutoCommit(false);

			String Sql = "DELETE FROM vehicle WHERE vehicle_id = ?";

			// Delete vehicle record
			try (PreparedStatement pst = conn.prepareStatement(Sql)) {
				pst.setInt(1, vehicle_id);

				if (pst.executeUpdate() != 1) {
					conn.rollback();
					return "Failed to delete vehicle records";
				}

			} catch (SQLException ex) {
				conn.rollback();
				ex.printStackTrace();
				return "Failed to delete records";
			}

			conn.commit();
			return null;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "Failed to delete records: SQLException";
		}
	}

	public static Map<Integer, String> getFlats(Connection conn, int customer_id) {
		String sql = "SELECT flat_id, flat_name FROM flat WHERE customer_id = ?";
		Map<Integer, String> flats = new HashMap<Integer, String>();

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			pst.setInt(1, customer_id);
			ResultSet rs = pst.executeQuery();

			while (rs.next()) {
				flats.put(rs.getInt(1), rs.getString(2));
			}

			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return flats;
	}

	public static ResultSet getVehicleList(Connection conn, int vehicle_id) {
		String sql = "SELECT vehicle_id, vehicle_name FROM vehicle";

		try {
			PreparedStatement pst;
			pst = conn.prepareStatement(sql);
			return pst.executeQuery();

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
