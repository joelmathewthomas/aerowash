package com.aerowash.customers;

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

public class CustomersCrudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CustomersCrudServlet() {
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
			String role;

			if (!Auth.checkSession(response, session, "all")) {
				return;
			}
			
			role = (String) session.getAttribute("role");

			PreparedStatement pst = conn.prepareStatement("SELECT * FROM customer");
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
					+ "        <li><a href=\"staff\">Home</a></li>\n");
			
					if (role.equals("staff")) {
						out.println("        <li><a href=\"cadd\">Add customer</a></li>\n");
					}
					
					out.println(""
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
					+ "            <th>Customer ID</th>\n"
					+ "            <th>First Name</th>\n"
					+ "            <th>Middle Name</th>\n"
					+ "            <th>Last Name</th>\n"
					+ "            <th>Phone</th>\n"
					+ "            <th>Action</th>\n"
					+ "          </tr>\n"
					+ "        </thead>\n"
					+ "\n"
					+ "        <tbody>\n");

					while (rs.next()) {
						
						int cid = rs.getInt(1);
						String fname = rs.getString(2);
						String mname = (rs.getString(3) == null) ? "" : rs.getString(3);
						String lname = rs.getString(4);
						String phone = rs.getString(5);
					
						String editUrl = "cedit?cid=" + cid + "&fname=" + fname + "&mname=" + mname + "&lname=" + lname + "&phone=" + phone;

						out.println("\n"
								+ "          <tr>\n"
								+ "            <td>" + cid + "</td>\n"
								+ "            <td>" + fname + "</td>\n"
								+ "            <td>" + mname + "</td>\n"
								+ "            <td>" + lname + "</td>\n"
								+ "            <td>" + phone + "</td>\n"
								+ "            <td style=\"white-space: nowrap\">\n"
								+ "              <a href=\"flat?cid=" + cid +"\">Flat</a> |\n"
								+ "              <a href=\"vehicle?cid=" + cid + "\">Vehicles</a> |\n"
								+ "              <a href=\"" + editUrl +"\">Edit</a> |\n"
								+ "              <a href=\"cdelete?cid=" + cid + "\">Delete</a>\n"
								+ "            </td>\n"
								+ "          </tr>\n");
					}
					
					out.println("\n"
					+ "        </tbody>\n"
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
		doGet(request, response);
	}

}
