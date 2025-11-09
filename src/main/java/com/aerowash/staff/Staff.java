package com.aerowash.staff;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

public class Staff {

	private final String username;
	private final String password;
	private final String fname;
	private final String mname;
	private final String lname;
	private final String phone;
	private final String email;
	private final String address;
	private final String aadhaar;
	private final String status;
	private final String ifsc;
	private final String account;

	public Staff(String username, String password, String fname, String mname, String lname, String phone, String email,
			String address, String aadhaar, String status, String ifsc, String account) {

		this.username = sanitizeString(username);
		this.password = sanitizeString(password);
		this.fname = sanitizeString(fname);
		this.mname = sanitizeString(mname);
		this.lname = sanitizeString(lname);
		this.phone = sanitizeString(phone);
		this.email = sanitizeString(email);
		this.address = sanitizeString(address);
		this.aadhaar = sanitizeString(aadhaar);
		this.status = sanitizeString(status);
		this.ifsc = sanitizeString(ifsc);
		this.account = sanitizeString(account);
	}

	// Remove leading and trailing zeroes
	private String sanitizeString(String s) {
		return s == null ? "" : s.trim();
	}

	// Return a new Staff object directly from request
	public static Staff getFromForm(HttpServletRequest request) {
		return new Staff(request.getParameter("username"), request.getParameter("password"),
				request.getParameter("staff_fname"), request.getParameter("staff_mname"),
				request.getParameter("staff_lname"), request.getParameter("staff_phone"),
				request.getParameter("staff_email"), request.getParameter("staff_address"),
				request.getParameter("staff_aadhaar"), request.getParameter("staff_status"),
				request.getParameter("bank_ifsc"), request.getParameter("bank_account"));
	}

	// Check patterns
	public String validateForm() {
		if (!username.matches("^[A-Za-z0-9_]+$"))
			return "username";
		if (!password.matches("^.{6,}$"))
			return "password";
		if (!fname.matches("^[A-Za-z ]+$"))
			return "fname";
		if (!mname.matches("^[A-Za-z ]*$"))
			return "mname";
		if (!lname.matches("^[A-Za-z ]+$"))
			return "lname";
		if (!phone.matches("^[0-9]{10}$"))
			return "phone";
		if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$"))
			return "email";
		if (!address.matches("^.{3,}$"))
			return "address";
		if (!aadhaar.matches("^[0-9]{12}$"))
			return "aadhaar";
		if (!status.matches("^[A-Za-z]+$"))
			return "status";
		if (!ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$"))
			return "ifsc";
		if (!account.matches("^[0-9]{6,18}$"))
			return "account";
		return null;
	}

	public String checkDuplicates(Connection conn) {
		try {

			// Duplicate username
			try (PreparedStatement pst = conn.prepareStatement("SELECT user_id FROM users WHERE username = ?")) {
				pst.setString(1, username);
				try (ResultSet rs = pst.executeQuery()) {
					if (rs.next()) {
						return "username_exists";
					}
				}
			}

			// Duplicate phone
			try (PreparedStatement pst = conn.prepareStatement("SELECT staff_id FROM staff WHERE staff_phone = ?")) {
				pst.setString(1, phone);
				try (ResultSet rs = pst.executeQuery()) {
					if (rs.next()) {
						return "phone_exists";
					}
				}
			}

			// Duplicate email
			try (PreparedStatement pst = conn.prepareStatement("SELECT staff_id FROM staff WHERE staff_email = ?")) {
				pst.setString(1, email);
				try (ResultSet rs = pst.executeQuery()) {
					if (rs.next()) {
						return "email_exists";
					}
				}
			}

			// Duplicate Aadhaar
			try (PreparedStatement pst = conn.prepareStatement("SELECT staff_id FROM staff WHERE staff_aadhaar = ?")) {
				pst.setString(1, aadhaar);
				try (ResultSet rs = pst.executeQuery()) {
					if (rs.next()) {
						return "aadhaar_exists";
					}
				}
			}

			// Duplicate bank account
			try (PreparedStatement pst = conn.prepareStatement("SELECT bank_id FROM bank WHERE bank_account_no = ?")) {
				pst.setString(1, account);
				try (ResultSet rs = pst.executeQuery()) {
					if (rs.next()) {
						return "bank_account_exists";
					}
				}
			}

			return null;

		} catch (SQLException e) {
			e.printStackTrace();
			return null; 
		}
	}

	public boolean addRecord(Connection conn) throws SQLException, IOException {
		
		conn.setAutoCommit(false);

		String userSql = "INSERT INTO aerowash.users (username, user_password, user_role) VALUES (?, ?, 'staff')";

		String staffSql = "INSERT INTO aerowash.staff (user_id, staff_fname, staff_mname, staff_lname, staff_phone, "
				+ "staff_email, staff_address, staff_aadhaar, staff_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		String bankSql = "INSERT INTO aerowash.bank (staff_id, bank_ifsc_code, bank_account_no) VALUES (?, ?, ?)";

		try (PreparedStatement pstUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
			// Insert user
			pstUser.setString(1, username);
			pstUser.setString(2, password);

			if (pstUser.executeUpdate() != 1) {
				conn.rollback();
				return false;
			}

			int userId;
			try (ResultSet rs = pstUser.getGeneratedKeys()) {
				if (!rs.next()) {
					conn.rollback();
					return false;
				}
				userId = rs.getInt(1);
			}

			// Insert staff
			try (PreparedStatement pstStaff = conn.prepareStatement(staffSql, Statement.RETURN_GENERATED_KEYS)) {
				pstStaff.setInt(1, userId);
				pstStaff.setString(2, fname);
				pstStaff.setString(3, mname.trim().isEmpty() ? null : mname);
				pstStaff.setString(4, lname);
				pstStaff.setString(5, phone);
				pstStaff.setString(6, email);
				pstStaff.setString(7, address);
				pstStaff.setString(8, aadhaar);
				pstStaff.setString(9, status);

				if (pstStaff.executeUpdate() != 1) {
					conn.rollback();
					return false;
				}

				int staffId;
				try (ResultSet rs = pstStaff.getGeneratedKeys()) {
					if (!rs.next()) {
						conn.rollback();
						return false;
					}
					staffId = rs.getInt(1);
				}

				// Insert bank
				try (PreparedStatement pstBank = conn.prepareStatement(bankSql)) {
					pstBank.setInt(1, staffId);
					pstBank.setString(2, ifsc);
					pstBank.setString(3, account);

					if (pstBank.executeUpdate() != 1) {
						conn.rollback();
						return false;
					}
				}
			}

			// All inserts succeeded
			conn.commit();
			return true;

		} catch (SQLException ex) {
			conn.rollback();
			throw ex;
		}
	}

}
