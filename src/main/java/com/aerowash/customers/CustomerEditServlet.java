package com.aerowash.customers;

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

public class CustomerEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public CustomerEditServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Session Tracking
			HttpSession session = request.getSession(false);

			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			PrintWriter out = response.getWriter();

			response.setContentType("text/html");

			if (request.getParameter("cid") == null) {
				response.sendRedirect("status?c=4&r=2");
				return;
			}

			int customer_id = Integer.parseInt(request.getParameter("cid"));
			String fname = request.getParameter("fname");
			String mname = request.getParameter("mname");
			String lname = request.getParameter("lname");
			String phone = request.getParameter("phone");

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
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>Edit Customer Details</h2>\n"
					+ "\n"
					+ "    <form\n"
					+ "      action=\"cedit\"\n"
					+ "      method=\"POST\"\n"
					+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
					+ "    >\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Customer ID</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"customer_id\"\n"
					+ "          required\n"
					+ "          readonly\n"
					+ "          value=\"" + customer_id + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>First Name</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"customer_fname\"\n"
					+ "          required\n"
					+ "          value=\"" + fname + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Middle Name</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"customer_mname\"\n"
					+ "          value=\"" + mname + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Last Name</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"customer_lname\"\n"
					+ "          required\n"
					+ "          value=\"" + lname + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Phone</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"customer_phone\"\n"
					+ "          required\n"
					+ "          pattern=\"[0-9]{10}\"\n"
					+ "          title=\"Phone must be 10 digits\"\n"
					+ "          value=\"" + phone + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
					+ "        Save\n"
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
			int customer_id = (request.getParameter("customer_id") == null) ? 0 : Integer.parseInt(request.getParameter("customer_id"));
			
			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			Customer customer = Customer.getFromForm(request);

			// Validate form input
			String error = customer.validateForm();

			if (error != null) {
				// send error code with parameter so UI can show the exact message
				response.sendRedirect("status?c=4&r=5&e=" + error);
				return;
			}

			error = customer.updateRecord(conn, customer_id);
			// Update record in table
			if (error == null) {
				response.sendRedirect("customers");
			} else {
				response.sendRedirect("status?c=4&r=5&e=" + error);
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}

	}

}
