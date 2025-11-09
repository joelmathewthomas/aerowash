package com.aerowash.staff;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.aerowash.auth.Auth;

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
			HttpSession session = request.getSession(false);

			if (!Auth.checkSession(response, session, "admin", 3, 3)) {
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
					+ "        <li><a href=\"scrud\">Staff</a></li>\n"
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

			// Check for duplicates
			error = staff.checkDuplicates(conn);
			if (error != null) {
				response.sendRedirect("status?c=4&r=4&e=" + error);
				return;
			}

			if (staff.addRecord(conn)) {
				response.sendRedirect("scrud");
			} else {
				response.sendRedirect("status");
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}
}
