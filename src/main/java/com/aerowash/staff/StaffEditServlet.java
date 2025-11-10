package com.aerowash.staff;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aerowash.auth.Auth;

public class StaffEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public StaffEditServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Session Tracking
			HttpSession session = request.getSession(false);

			if (!Auth.checkSession(response, session, "admin", 3, 3)) {
				return;
			}

			PrintWriter out = response.getWriter();

			response.setContentType("text/html");
			String username = request.getParameter("username");
	
			if (username == null) {
				response.sendRedirect("status?c=4&r=2");
			}
			String password = request.getParameter("password");
			String fname = request.getParameter("fname");
			String mname = request.getParameter("mname");
			String lname = request.getParameter("lname");
			String phone = request.getParameter("phone");
			String email = request.getParameter("email");
			String address = request.getParameter("address");
			String aadhaar = request.getParameter("aadhaar");
			String status = request.getParameter("status");
			String ifsc = request.getParameter("ifsc");
			String account = request.getParameter("account");

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
			    + "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">\n"
			    + "        <li><a href=\"scrud\">Staff</a></li>\n"
			    + "      </ul>\n"
			    + "    </div>\n"
			    + "\n"
			    + "    <h2>Edit Staff</h2>\n"
			    + "\n"
			    + "    <form action=\"sedit\" method=\"POST\" style=\"margin-top: 20px; line-height: 1.8\">\n"
			    + "      <div>\n"
			    + "        <label>Username</label><br />\n"
			    + "        <input type=\"text\" name=\"username\" value=\"" + username + "\" required style=\"padding: 5px; width: 200px\" readonly />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Password</label><br />\n"
			    + "        <input type=\"password\" name=\"password\" value=\"" + password + "\" required pattern=\".{6,}\" title=\"Password must be at least 6 characters\" style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>First Name</label><br />\n"
			    + "        <input type=\"text\" name=\"staff_fname\" value=\"" + fname + "\" required style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Middle Name</label><br />\n"
			    + "        <input type=\"text\" name=\"staff_mname\" value=\"" + mname + "\" style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Last Name</label><br />\n"
			    + "        <input type=\"text\" name=\"staff_lname\" value=\"" + lname + "\" required style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Phone</label><br />\n"
			    + "        <input type=\"text\" name=\"staff_phone\" value=\"" + phone + "\" required pattern=\"[0-9]{10}\" title=\"Phone must be 10 digits\" style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Email</label><br />\n"
			    + "        <input type=\"email\" name=\"staff_email\" value=\"" + email + "\" required pattern=\"^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$\" title=\"Enter a valid email\" style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Address</label><br />\n"
			    + "        <input type=\"text\" name=\"staff_address\" value=\"" + address + "\" required style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Aadhaar</label><br />\n"
			    + "        <input type=\"text\" name=\"staff_aadhaar\" value=\"" + aadhaar + "\" required pattern=\"[0-9]{12}\" title=\"Aadhaar must be exactly 12 digits\" style=\"padding: 5px; width: 200px\" readonly/>\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Status</label><br />\n"
			    + "        <label><input type=\"radio\" name=\"staff_status\" value=\"active\"" + ("active".equalsIgnoreCase(status) ? " checked" : "") + " /> Active</label><br />\n"
			    + "        <label><input type=\"radio\" name=\"staff_status\" value=\"inactive\"" + ("inactive".equalsIgnoreCase(status) ? " checked" : "") + " /> Inactive</label>\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Bank IFSC</label><br />\n"
			    + "        <input type=\"text\" name=\"bank_ifsc\" value=\"" + ifsc + "\" required style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <div style=\"margin-top: 15px\">\n"
			    + "        <label>Bank Account No</label><br />\n"
			    + "        <input type=\"text\" name=\"bank_account\" value=\"" + account + "\" required pattern=\"[0-9]{6,18}\" title=\"Bank account number must be 6 to 18 digits\" style=\"padding: 5px; width: 200px\" />\n"
			    + "      </div>\n"
			    + "\n"
			    + "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">Update</button>\n"
			    + "    </form>\n"
			    + "  </body>\n"
			    + "</html>");

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
			HttpSession session = request.getSession(false);

			if (!Auth.checkSession(response, session, "admin", 3, 3)) {
				return;
			}

			Staff staff = Staff.getFromForm(request);

			// Validate form input
			String error = staff.validateForm();

			if (error != null) {
				// send error code with parameter so UI can show the exact message
				response.sendRedirect("status?c=4&r=4&e=" + error);
				return;
			}

			
			error = staff.updateRecord(conn);
			// Update record in table
			if (error == null) {
				response.sendRedirect("scrud");
			} else {
				response.sendRedirect("status?c=4&r=4&e=" + error);
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}

	}

}
