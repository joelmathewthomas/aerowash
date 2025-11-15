package com.aerowash.wash;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aerowash.auth.Auth;

public class WashCrudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public WashCrudServlet() {
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

			// Check access
			if (!Auth.checkSession(response, session, "all")) {
				return;
			}
			
			String role = (String) session.getAttribute("role");

			ResultSet rs = Wash.getJobs(conn, request);
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
					+ "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">\n");

					if (role.equals("admin")) {
						out.println("        <li><a href=\"admin\">Home</a></li>\n");
					} else {
						out.println("        <li><a href=\"staff\">Home</a></li>\n");
						out.println("        <li><a href=\"wadd\">New Wash Job</a></li>\n");
					}

					out.println("\n"
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
					+ "            <th>Wash ID</th>\n");
					
					if (role.equals("admin")) {
						out.println("            <th>Staff ID</th>\n");
					}

					out.println(""
					+ "            <th>Vehicle ID</th>\n"
					+ "            <th>Date</th>\n"
					+ "\n"
					+ "            <th>Transaction Date</th>\n"
					+ "            <th>Amount</th>\n"
					+ "            <th>Status</th>\n"
					+ "            <th>Mode</th>\n"
					+ "            <th>Action</th>\n"
					+ "          </tr>\n"
					+ "        </thead>\n"
					+ "\n");
					
					while(rs.next()) {
						out.println(""
								+ "        <tbody>\n"
								+ "          <tr>\n"
								+ "            <td>" + rs.getInt(1) + "</td>\n");

						if (role.equals("admin")) {
							out.println("            <td>" + rs.getInt(2) + "</td>\n");
						}
								
						out.println(""
								+ "            <td>" + rs.getInt(3) + "</td>\n"
								+ "            <td>" + rs.getDate(4) + "</td>\n"
								+ "\n"
								+ "            <td>" + ((rs.getDate(5) == null) ? "" : rs.getDate(5)) + "</td>\n"
								+ "            <td>" + rs.getFloat(6) + "</td>\n"
								+ "            <td>" + rs.getString(7) + "</td>\n"
								+ "            <td>" + rs.getString(8) + "</td>\n"
								+ "\n"
								+ "            <td style=\"white-space: nowrap\">\n"
								+ "[" + "              <a href=\"expense?wid=" + rs.getInt(1) + "&tid=" + rs.getInt(9) + "\">Expense</a> ] \n");
							
								if (rs.getString(7).equals("INCOMPLETE")) {
									out.println("[" + "              <a href=\"washview?wid=1\">Mark as Paid</a> ]\n");
								} else if (rs.getString(7).equals("COMPLETE") && role.equals("admin")) {
									out.println("[" + "              <a href=\"washview?wid=1\">Mark as Unpaid</a> ]\n");
								}
								
								if (role.equals("admin")) {
									out.println("[" + "              <a href=\"washdelete?wid=1\">Delete</a> ]\n");
								}
								
								out.println(""
								+ "            </td>\n"
								+ "          </tr>\n"
								+ "        </tbody>\n");
					}
					
					out.println(""
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
		response.sendRedirect("status?e=method_not_supported");
	}

}
