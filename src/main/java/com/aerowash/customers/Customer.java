package com.aerowash.customers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import javax.servlet.http.HttpServletRequest;

public class Customer {
	private final String fname;
	private final String mname;
	private final String lname;
	private final String phone;

	public Customer(String fname, String mname, String lname, String phone) {
		this.fname = sanitizeString(fname);
		this.mname = sanitizeString(mname);
		this.lname = sanitizeString(lname);
		this.phone = sanitizeString(phone);
	}

	// Remove leading and trailing whitespace
	private String sanitizeString(String s) {
		return s == null ? "" : s.trim();
	}

	// Return a new Customer object directly from request
	public static Customer getFromForm(HttpServletRequest request) {
		return new Customer(request.getParameter("customer_fname"), request.getParameter("customer_mname"),
				request.getParameter("customer_lname"), request.getParameter("customer_phone"));
	}

	// Check patterns
	public String validateForm() {
		if (!fname.matches("^[A-Za-z ]+$"))
			return "fname";
		if (!mname.matches("^[A-Za-z ]*$"))
			return "mname";
		if (!lname.matches("^[A-Za-z ]+$"))
			return "lname";
		if (!phone.matches("^[0-9]{10}$"))
			return "phone";
		return null;
	}

	private String getDuplicateKey(String error) {
		if (error.contains("phone")) {
			return "duplicate_phone";
		} else {
			return null;
		}

	}

	public String addRecord(Connection conn) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String sql = "INSERT INTO aerowash.customer (customer_fname, customer_mname, customer_lname, customer_phone) VALUES (?, ?, ?, ?)";

		try (PreparedStatement pstUser = conn.prepareStatement(sql)) {
			// Insert user
			pstUser.setString(1, fname);
			pstUser.setString(2, mname);
			pstUser.setString(3, lname);
			pstUser.setString(4, phone);

			if (pstUser.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to add customer details";
			}

			// Insertion succeeded
			conn.commit();
			return null;

		} catch (SQLIntegrityConstraintViolationException ex) {
			conn.rollback();
			ex.printStackTrace();
			return getDuplicateKey(ex.getMessage());
		} catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
			return "Failed to add record";
		}
	}

	public String updateRecord(Connection conn, int customer_id) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String sql = "UPDATE customer SET customer_fname = ?, customer_mname = ?, customer_lnmame = ?, customer_phone = ? WHERE customer_id = ?";

		try (PreparedStatement pst = conn.prepareStatement(sql)) {
			// Update customer
			pst.setString(1, fname);
			pst.setString(2, mname);
			pst.setString(3, lname);
			pst.setString(4, phone);
			pst.setInt(5, customer_id);

			if (pst.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to update customer details";
			}

			// Update succeeded
			conn.commit();
		} catch (SQLIntegrityConstraintViolationException ex) {
			conn.rollback();
			ex.printStackTrace();
			return getDuplicateKey(ex.getMessage());
		} catch (SQLException ex) {
			conn.rollback();
			ex.printStackTrace();
		}

		return null;
	}
}
