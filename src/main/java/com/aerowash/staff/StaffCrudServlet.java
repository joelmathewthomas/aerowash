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
					+ "            <th>S.No</th>\n"
					+ "            <th>Username</th>\n"
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
			
					int rowCount = 1;
					while (rs.next()) {
					    String username = rs.getString(12);
					    String password = rs.getString(13);
					    String fname = rs.getString(3);
					    String mname = rs.getString(4);
					    String lname = rs.getString(5);
					    String phone = rs.getString(6);
					    String email = rs.getString(7);
					    String address = rs.getString(8);
					    String aadhaar = rs.getString(9);
					    String status = rs.getString(10);
					    String ifsc = rs.getString(17);
					    String account = rs.getString(18);

					    String editUrl =
					        "sedit?" +
					        "username=" + username +
					        "&password=" + password +
					        "&fname=" + fname +
					        "&mname=" + mname +
					        "&lname=" + lname +
					        "&phone=" + phone +
					        "&email=" + email +
					        "&address=" + address +
					        "&aadhaar=" + aadhaar +
					        "&status=" + status +
					        "&ifsc=" + ifsc +
					        "&account=" + account;

					    out.println(
					        "          <tr>\n" +
					        "            <td>" + rowCount++ + "</td>\n" +
					        "            <td>" + username + "</td>\n" +
					        "            <td>" + fname + "</td>\n" +
					        "            <td>" + mname + "</td>\n" +
					        "            <td>" + lname + "</td>\n" +
					        "            <td>" + phone + "</td>\n" +
					        "            <td>" + email + "</td>\n" +
					        "            <td>" + address + "</td>\n" +
					        "            <td>" + aadhaar + "</td>\n" +
					        "            <td>" + status.substring(0,1).toUpperCase() + status.substring(1) + "</td>\n" +
					        "            <td>" + ifsc + "</td>\n" +
					        "            <td>" + account + "</td>\n" +
					        "            <td style=\"white-space: nowrap\">\n" +
					        "              <a href=\"" + editUrl + "\">Edit</a>&nbsp;|&nbsp;<a href=\"#\">Delete</a>\n" +
					        "            </td>\n" +
					        "          </tr>\n"
					    );
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
		response.sendRedirect("status");
	}

}
