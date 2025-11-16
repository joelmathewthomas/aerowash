package com.aerowash.customers.flat;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
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

public class FlatCrudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FlatCrudServlet() {
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
			int customer_id;
			try {
				customer_id = (request.getParameter("cid") == null) ? 0 : Integer.parseInt(request.getParameter("cid"));
			} catch (NumberFormatException ex) {
				customer_id = 0;
				ex.printStackTrace();
			}

			if (!Auth.checkSession(response, session, "all")) {
				return;
			}

			if (customer_id == 0) {
				response.sendRedirect("status?c=4&r=5&e=invalid_customer_id");
			}

			PreparedStatement pst = conn.prepareStatement("SELECT * FROM flat WHERE customer_id = ?");
			pst.setInt(1, customer_id);
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
					+ "        <li><a href=\"customers\">Customers</a></li>\n"
					+ "        <li><a href=\"fadd?cid=" + customer_id + "\">Add flat</a></li>\n"
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
					+ "            <th>Flat ID</th>\n"
					+ "            <th>Flat Name</th>\n"
					+ "            <th>Flat Address</th>\n"
					+ "            <th>Added on</th>\n"
					+ "            <th>Action</th>\n"
					+ "          </tr>\n"
					+ "        </thead>\n"
					+ "\n"
					+ "        <tbody>\n");
			
					while (rs.next()) {
						int flat_id = rs.getInt(1);
						String flat_name = rs.getString(3);
						String flat_address = rs.getString(4);
						Date flat_added_date = rs.getDate(5);
						
						String editUrl = "fedit?cid=" + customer_id + "&fid=" + flat_id + "&flat_name=" + flat_name + "&flat_address=" + flat_address;
						
						out.println("\n"
								+ "          <tr>\n"
								+ "            <td>" + flat_id + "</td>\n"
								+ "            <td>" + flat_name + "</td>\n"
								+ "            <td>" + flat_address + "</td>\n"
								+ "            <td>" + flat_added_date + "</td>\n"
								+ "            <td style=\"white-space: nowrap\">\n"
								+ "              <a href=\"" + editUrl + "\">Edit</a>\n"
								+ "              |\n"
								+ "              <a href=\"fdelete?fid=" + flat_id + "&cid=" + customer_id + "\">Delete</a>\n"
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
