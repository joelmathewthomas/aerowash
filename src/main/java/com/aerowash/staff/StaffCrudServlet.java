package com.aerowash.staff;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.aerowash.auth.Auth;

public class StaffCrudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public StaffCrudServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

			PreparedStatement pst = conn.prepareStatement("SELECT * FROM staff s JOIN users u ON s.user_id = u.user_id LEFT JOIN bank b ON b.staff_id = s.staff_id;");
			ResultSet rs = pst.executeQuery();
			
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
					+ "        <li><a href=\"admin\">Home</a></li>\n"
					+ "        <li><a href=\"sadd\">Add staff</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <div style=\"margin-top: 25px\">\n"
					+ "      <table\n"
					+ "        border=\"1\"\n"
					+ "        cellpadding=\"8\"\n"
					+ "        cellspacing=\"0\"\n"
					+ "        style=\"border-collapse: collapse; width: 100%\"\n"
					+ "      >\n"
					+ "        <thead\n"
					+ "          style=\"background-color: #f2f2f2; font-weight: bold; text-align: left\"\n"
					+ "        >\n"
					+ "          <tr>\n"
					+ "            <th>Username</th>\n"
					+ "            <th>Password</th>\n"
					+ "\n"
					+ "            <th>First Name</th>\n"
					+ "            <th>Middle Name</th>\n"
					+ "            <th>Last Name</th>\n"
					+ "            <th>Phone</th>\n"
					+ "            <th>Email</th>\n"
					+ "            <th>Address</th>\n"
					+ "            <th>Aadhaar</th>\n"
					+ "            <th>Status</th>\n"
					+ "\n"
					+ "            <th>Bank IFSC</th>\n"
					+ "            <th>Bank Account No</th>\n"
					+ "            <th>Action</th>\n"
					+ "          </tr>\n"
					+ "        </thead>\n"
					+ "\n"
					+ "        <tbody>\n");
			
					while (rs.next()) {
						out.println("\n"
								+ "          <tr>\n"
								+ "            <td>" + rs.getString(12) + "</td>\n"
								+ "            <td>" + rs.getString(13) + "</td>\n"
								+ "\n"
								+ "            <td>" + rs.getString(3) + "</td>\n"
								+ "            <td>" + rs.getString(4) + "</td>\n"
								+ "            <td>" + rs.getString(5) + "</td>\n"
								+ "            <td>" + rs.getString(6) + "</td>\n"
								+ "            <td>" + rs.getString(7) + "</td>\n"
								+ "            <td>" + rs.getString(8) + "</td>\n"
								+ "            <td>" + rs.getString(9) + "</td>\n"
								+ "            <td>" + rs.getString(10) + "</td>\n"
								+ "\n"
								+ "            <td>" + rs.getString(17) + "</td>\n"
								+ "            <td>" + rs.getString(18) + "</td>\n"
								+ "            <td style=\"white-space: nowrap\"><a href=\"#\">Edit</a>&nbsp;&nbsp;<a href=\"#\">Delete</a></td>\n"
								+ "          </tr>\n");
					}

					out.println("        </tbody>\n"
					+ "      </table>\n"
					+ "    </div>\n"
					+ "  </body>\n"
					+ "</html>\n"
					+ "");
					
					out.close();

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

}
