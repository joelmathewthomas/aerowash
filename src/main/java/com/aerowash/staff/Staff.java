package com.aerowash.staff;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
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

	public String getUsername() {
		return username;
	}

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

	// Remove leading and trailing whitespace
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

	private String getDuplicateKey(String error) {
		if (error.contains("username")) {
			return "user_exists";
		} else if (error.contains("phone")) {
			return "duplicate_phone";
		} else if (error.contains("email")) {
			return "duplicate_email";
		} else if (error.contains("aadhaar")) {
			return "duplicate_aadhaar";
		} else if (error.contains("account")) {
			return "duplicate_bank_account";
		} else {
			return null;
		}

	}

	public String addRecord(Connection conn) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String userSql = "INSERT INTO aerowash.users (username, user_password, user_role) VALUES (?, ?, 'staff')";

		final String staffSql = "INSERT INTO aerowash.staff (user_id, staff_fname, staff_mname, staff_lname, staff_phone, "
				+ "staff_email, staff_address, staff_aadhaar, staff_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

		final String bankSql = "INSERT INTO aerowash.bank (staff_id, bank_ifsc_code, bank_account_no) VALUES (?, ?, ?)";

		try (PreparedStatement pstUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
			// Insert user
			pstUser.setString(1, username);
			pstUser.setString(2, password);

			if (pstUser.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to add user details";
			}

			int userId;
			try (ResultSet rs = pstUser.getGeneratedKeys()) {
				if (!rs.next()) {
					conn.rollback();
					return "Failed to add user details";
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
					return "Failed to add staff details";
				}

				int staffId;
				try (ResultSet rs = pstStaff.getGeneratedKeys()) {
					if (!rs.next()) {
						conn.rollback();
						return "Failed to add staff details";
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
						return "Failed to add bank details";
					}
				}
			}

			// All inserts succeeded
			conn.commit();
			return null;

		} catch (SQLIntegrityConstraintViolationException ex) {
			conn.rollback();
			return getDuplicateKey(ex.getMessage());
		} catch (SQLException ex) {
			conn.rollback();
			return "Failed to add record";
		}
	}

	public String updateRecord(Connection conn) throws SQLException, IOException {

		conn.setAutoCommit(false);

		final String userSql = "UPDATE users SET user_password = ? WHERE username = ?";

		final String staffSql = "UPDATE staff s JOIN users u ON s.user_id = u.user_id SET s.staff_fname = ?, s.staff_mname = ?, s.staff_lname = ?, "
				+ "s.staff_phone = ?, s.staff_email = ?, s.staff_address = ?, s.staff_status = ? WHERE u.username = ?";

		final String bankSql = "UPDATE bank JOIN staff ON bank.staff_id = staff.staff_id JOIN users ON staff.user_id = users.user_id "
				+ "SET bank.bank_ifsc_code = ?, bank.bank_account_no = ? WHERE users.username = ?;";

		try (PreparedStatement pstUser = conn.prepareStatement(userSql)) {
			// Update user
			pstUser.setString(1, password);
			pstUser.setString(2, username);

			if (pstUser.executeUpdate() != 1) {
				conn.rollback();
				return "Failed to update user password";
			}

			// Update staff
			try (PreparedStatement pstStaff = conn.prepareStatement(staffSql, Statement.RETURN_GENERATED_KEYS)) {
				pstStaff.setString(1, fname);
				pstStaff.setString(2, mname.trim().isEmpty() ? null : mname);
				pstStaff.setString(3, lname);
				pstStaff.setString(4, phone);
				pstStaff.setString(5, email);
				pstStaff.setString(6, address);
				pstStaff.setString(7, status);
				pstStaff.setString(8, username);

				if (pstStaff.executeUpdate() != 1) {
					conn.rollback();
					return "Failed to update staff details";
				}

				// Update bank
				try (PreparedStatement pstBank = conn.prepareStatement(bankSql)) {
					pstBank.setString(1, ifsc);
					pstBank.setString(2, account);
					pstBank.setString(3, username);

					if (pstBank.executeUpdate() != 1) {
						conn.rollback();
						return "Failed to update bank details";
					}
				}
			}

			// All updates succeeded
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

	public static String deleteRecord(Connection conn, String username) {

		if (username == null || username.trim().isEmpty()) {
			return "Invalid username";
		}

		try (PreparedStatement pst = conn.prepareStatement("SELECT user_id from users WHERE username = ?")) {
			pst.setString(1, username);
			ResultSet rs = pst.executeQuery();

			if (!rs.next()) {
				return "User does not exist";
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return "Failed to check for username in database";
		}

		try {

			conn.setAutoCommit(false);

			String bankSql = "DELETE FROM bank b WHERE b.staff_id = (SELECT staff_id FROM staff s JOIN users u ON s.user_id = u.user_id WHERE u.username = ?)";
			String staffSql = "DELETE FROM staff WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
			String usersSql = "DELETE FROM users WHERE username = ?";

			// Delete bank record
			try (PreparedStatement pstBank = conn.prepareStatement(bankSql)) {
				pstBank.setString(1, username);

				if (pstBank.executeUpdate() != 1) {
					conn.rollback();
					return "Failed to delete bank details";
				}

				try (PreparedStatement pstStaff = conn.prepareStatement(staffSql)) {
					pstStaff.setString(1, username);

					if (pstStaff.executeUpdate() != 1) {
						conn.rollback();
						return "Failed to delete staff details";
					}

					try (PreparedStatement pstUser = conn.prepareStatement(usersSql)) {
						pstUser.setString(1, username);

						if (pstUser.executeUpdate() != 1) {
							conn.rollback();
							return "Failed to delete staff details";

						}
					}
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
}
