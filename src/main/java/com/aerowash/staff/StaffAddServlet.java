package com.aerowash.staff;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/StaffAddServlet")
public class StaffAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public StaffAddServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// Session Tracking
			HttpSession session = request.getSession();

			if (session == null) {
				response.sendRedirect("status?c=2&r=1");
				return;
			} else if (!"admin".equals(session.getAttribute("role"))) {
				response.sendRedirect("status?c=3&r=3");
				return;
			}

			PrintWriter out = response.getWriter();

			response.setContentType("text/html");
			out.println("<!DOCTYPE html>\n"
					+ "<html>\n"
					+ "  <head>\n"
					+ "    <meta charset=\"UTF-8\" />\n"
					+ "    <title>AeroWash</title>\n"
					+ "  </head>\n"
					+ "  <body style=\"font-family: Arial, sans-serif; margin: 20px\">\n"
					+ "    <h1 style=\"text-align: center; margin-bottom: 5px\">AeroWash</h1>\n"
					+ "    <hr />\n"
					+ "\n"
					+ "    <div style=\"margin-top: 20px\">\n"
					+ "      <h3>Menu</h3>\n"
					+ "\n"
					+ "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">\n"
					+ "        <li><a href=\"#\">Home</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>Add Staff</h2>\n"
					+ "\n"
					+ "    <form action=\"sadd\" method=\"POST\" style=\"margin-top: 20px; line-height: 1.8\">\n"
					+ "      <div>\n"
					+ "        <label>Username</label><br />\n"
					+ "        <input type=\"text\" name=\"username\" required style=\"padding: 5px; width: 200px\" />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Password</label><br />\n"
					+ "        <input\n"
					+ "          type=\"password\"\n"
					+ "          name=\"password\"\n"
					+ "          required\n"
					+ "          pattern=\".{6,}\"\n"
					+ "          title=\"Password must be at least 6 characters\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>First Name</label><br />\n"
					+ "        <input type=\"text\" name=\"staff_fname\" required style=\"padding: 5px; width: 200px\" />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Middle Name</label><br />\n"
					+ "        <input type=\"text\" name=\"staff_mname\" style=\"padding: 5px; width: 200px\" />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Last Name</label><br />\n"
					+ "        <input type=\"text\" name=\"staff_lname\" required style=\"padding: 5px; width: 200px\" />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Phone</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"staff_phone\"\n"
					+ "          required\n"
					+ "          pattern=\"[0-9]{10}\"\n"
					+ "          title=\"Phone must be 10 digits\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Email</label><br />\n"
					+ "        <input\n"
					+ "          type=\"email\"\n"
					+ "          name=\"staff_email\"\n"
					+ "          required\n"
					+ "          pattern=\"^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$\"\n"
					+ "          title=\"Enter a valid email\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Address</label><br />\n"
					+ "        <input type=\"text\" name=\"staff_address\" required style=\"padding: 5px; width: 200px\" />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Aadhaar</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"staff_aadhaar\"\n"
					+ "          required\n"
					+ "          pattern=\"[0-9]{12}\"\n"
					+ "          title=\"Aadhaar must be exactly 12 digits\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Status</label><br />\n"
					+ "\n"
					+ "        <label>\n"
					+ "          <input type=\"radio\" name=\"staff_status\" value=\"active\" required />\n"
					+ "          Active\n"
					+ "        </label>\n"
					+ "\n"
					+ "        <br />\n"
					+ "\n"
					+ "        <label>\n"
					+ "          <input type=\"radio\" name=\"staff_status\" value=\"inactive\" required />\n"
					+ "          Inactive\n"
					+ "        </label>\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Bank IFSC</label><br />\n"
					+ "        <input type=\"text\" name=\"bank_ifsc\" required style=\"padding: 5px; width: 200px\" />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Bank Account No</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"bank_account\"\n"
					+ "          required\n"
					+ "          pattern=\"[0-9]{6,18}\"\n"
					+ "          title=\"Bank account number must be 6 to 18 digits\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
					+ "        Add\n"
					+ "      </button>\n"
					+ "    </form>\n"
					+ "  </body>\n"
					+ "</html>\n"
					+ "");
			out.close();

		} catch (Exception ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			Class.forName(getServletContext().getInitParameter("Driver"));
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		try (Connection conn = DriverManager.getConnection(getServletContext().getInitParameter("DbUrl"),
				getServletContext().getInitParameter("DbUser"), getServletContext().getInitParameter("DbPassword"))) {
			// Session Tracking
			HttpSession session = request.getSession();

			if (session == null) {
				response.sendRedirect("status?c=2&r=1");
				return;
			} else if (!"admin".equals(session.getAttribute("role"))) {
				response.sendRedirect("status?c=3&r=3");
				return;
			}

			String username = request.getParameter("username");
			String password = request.getParameter("password");

			String fname = request.getParameter("staff_fname");
			String mname = request.getParameter("staff_mname");
			String lname = request.getParameter("staff_lname");

			String phone = request.getParameter("staff_phone");
			String email = request.getParameter("staff_email");
			String address = request.getParameter("staff_address");

			String aadhaar = request.getParameter("staff_aadhaar");
			String status = request.getParameter("staff_status");

			String ifsc = request.getParameter("bank_ifsc");
			String account = request.getParameter("bank_account");

			// Validate form input
			String error = null;

			if (!username.matches("^[A-Za-z0-9_]+$")) {
			    error = "username";
			} else if (!password.matches("^.{6,}$")) {
			    error = "password";
			} else if (!fname.matches("^[A-Za-z ]+$")) {
			    error = "fname";
			} else if (!mname.matches("^[A-Za-z ]*$")) {
			    error = "mname";
			} else if (!lname.matches("^[A-Za-z ]+$")) {
			    error = "lname";
			} else if (!phone.matches("^[0-9]{10}$")) {
			    error = "phone";
			} else if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
			    error = "email";
			} else if (!address.matches("^.{3,}$")) {
			    error = "address";
			} else if (!aadhaar.matches("^[0-9]{12}$")) {
			    error = "aadhaar";
			} else if (!status.matches("^[A-Za-z]+$")) {
			    error = "status";
			} else if (!ifsc.matches("^[A-Z]{4}0[A-Z0-9]{6}$")) {
			    error = "ifsc";
			} else if (!account.matches("^[0-9]{6,18}$")) {
			    error = "account";
			}

			if (error != null) {
			    // send error code with parameter so UI can show the exact message
			    response.sendRedirect("status?c=4&r=4&e=" + error);
			    return;
			}
			
			// Check if user exists
			PreparedStatement pst  = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
			ResultSet rs;
			pst.setString(1, username);
			if (pst.executeQuery().next()) {
				response.sendRedirect("status?c=4&r=4&e=" + "Staff already exists!");
				return;
			}

			// Insert into user table
			pst = conn.prepareStatement("INSERT INTO `aerowash`.`users` (`username`, `user_password`, `user_role`) VALUES (? , ?, 'staff')", Statement.RETURN_GENERATED_KEYS);
			
			pst.setString(1, username);
			pst.setString(2, password);
			
			// If record is added to table users
			if (pst.executeUpdate() == 1) {
				rs  =  pst.getGeneratedKeys();
				if (rs.next()) {
					int user_id = rs.getInt(1);
					
					pst = conn.prepareStatement("INSERT INTO `aerowash`.`staff` (`user_id`, `staff_fname`, `staff_mname`, `staff_lname`, `staff_phone`, `staff_email`, `staff_address`, `staff_aadhaar`, `staff_status`) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
					pst.setInt(1, user_id);
					pst.setString(2, fname);
					
					if (mname.trim().isEmpty()) {
						pst.setNull(3, java.sql.Types.VARCHAR);
					} else {
						pst.setString(3, mname);
					}
					
					pst.setString(4, lname);
					pst.setString(5, phone);
					pst.setString(6, email);
					pst.setString(7, address);
					pst.setString(8, aadhaar);
					pst.setString(9, status);
				
					// If record is added to table staff;
					if (pst.executeUpdate() == 1) {
						rs = pst.getGeneratedKeys();
						if (rs.next()) {
							int staff_id = rs.getInt(1);
							pst = conn.prepareStatement("INSERT INTO `aerowash`.`bank` (`staff_id`, `bank_ifsc_code`, `bank_account_no`) VALUES (?, ?, ?)");
							pst.setInt(1, staff_id);
							pst.setString(2, ifsc);
							pst.setString(3, account);

							if (pst.executeUpdate() == 1) {
								response.sendRedirect("scrud");
							} else {
								response.sendRedirect("status");
							}
						}
					} else {
						response.sendRedirect("status");
					}
				}
			} else {
				response.sendRedirect("status");
			}
			
		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

}
