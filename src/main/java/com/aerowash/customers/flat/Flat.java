package com.aerowash.customers.flat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

public class Flat {
	private final int customer_id;
	private final String flat_name;
	private final String flat_address;

	public Flat(int customer_id, String flat_name, String flat_address) {
		this.customer_id = customer_id;
		this.flat_name = sanitizeString(flat_name);
		this.flat_address = sanitizeString(flat_address);
	}

	public int getCustomer_id() {
		return customer_id;
	}

	// Remove leading and trailing whitespace
	private static String sanitizeString(String s) {
		return s == null ? "" : s.trim();
	}

	// Return a new Flat object directly from request
	public static Flat getFromForm(HttpServletRequest request) {
		int customer_id = 0;

		try {
			customer_id = Integer.parseInt(sanitizeString(request.getParameter("customer_id")));
			return new Flat(customer_id, request.getParameter("flat_name"), request.getParameter("flat_address"));
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// Check patterns
	public String validateForm() {
		if (!String.valueOf(customer_id).matches("^[0-9]+$"))
			return "customer_id";
		if (!flat_name.matches("^[A-Za-z ]*$"))
			return "flat_name";
		if (!flat_address.matches("^.{3,}$"))
			return "address";
		return null;
	}

	public String addRecord(Connection conn) throws SQLException, IOException {

		LocalDate currentDate = LocalDate.now();

		conn.setAutoCommit(false);

		final String sql = "INSERT INTO aerowash.flat (customer_id, flat_name, flat_location, flat_added_date) VALUES (?, ?, ?, ?)";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			// Insert record
			pst.setInt(1, customer_id);
			pst.setString(2, flat_name);
			pst.setString(3, flat_address);
			pst.setDate(4, java.sql.Date.valueOf(currentDate));

			if (pst.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to add flat details";
			}

			// Insertion succeeded
			conn.commit();
			return null;

		} catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			return "Failed to add record";
		}
	}

	public String updateRecord(Connection conn, int flat_id) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String sql = "UPDATE flat SET flat_name = ?, flat_location = ? WHERE flat_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			// Update customer
			pst.setString(1, flat_name);
			pst.setString(2, flat_address);
			pst.setInt(3, flat_id);

			if (pst.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to update flat details";
			}

			// Update succeeded
			conn.commit();
		} catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			return "Failed to update flat details";
		}

		return null;
	}

	public static String deleteRecord(Connection conn, int flat_id) {

		if (flat_id == 0) {
			return "Invalid flat_id";
		}
		try {
			conn.setAutoCommit(false);
			try (PreparedStatement pst = conn.prepareStatement("DELETE FROM flat WHERE flat_id = ?")) {
				pst.setInt(1, flat_id);
				if (pst.executeUpdate() != 1) {
					conn.rollback();
					return "Failed to delete flat record";
				}
			}

			conn.commit();
			return null;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "Failed to delete records: SQLException";
		}
	}

}
