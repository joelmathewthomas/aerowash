package com.aerowash.customers.flat;

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

public class FlatEditServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public FlatEditServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			// Session Tracking
			HttpSession session = request.getSession(false);
			
			int customer_id;
			int flat_id;
			try {
				customer_id = (request.getParameter("cid") == null) ? 0 : Integer.parseInt(request.getParameter("cid"));
				flat_id = (request.getParameter("fid") == null) ? 0 : Integer.parseInt(request.getParameter("fid"));
			} catch (NumberFormatException ex) {
				customer_id = 0;
				flat_id = 0;
				ex.printStackTrace();
			}

			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			if (customer_id == 0) {
				response.sendRedirect("status?c=4&r=5&e=invalid_customer_id");
				return;
			} else if (flat_id == 0) {
				response.sendRedirect("status?c=4&r=5&e=invalid_flat_id");
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
					+ "        <li><a href=\"flat?cid=" + customer_id + "\">Flat</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>Add a flat</h2>\n"
					+ "\n"
					+ "    <form\n"
					+ "      action=\"fadd\"\n"
					+ "      method=\"POST\"\n"
					+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
					+ "    >\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Customer ID</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"customer_id\"\n"
					+ "          readonly\n"
					+ "          value=\"" + customer_id + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Flat Name</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"flat_name\"\n"
					+ "          value=\"" + request.getParameter("flat_name") + "\"\n"
					+ "          required\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Flat Address</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"flat_address\"\n"
					+ "          value=\"" + request.getParameter("flat_address") + "\"\n"
					+ "          required\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
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

			int flat_id;
			try {
				flat_id = (request.getParameter("fid") == null) ? 0 : Integer.parseInt(request.getParameter("fid"));
			} catch (NumberFormatException ex) {
				flat_id = 0;
				ex.printStackTrace();
			}

			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			if (flat_id == 0) {
				response.sendRedirect("status?c=4&r=5&e=invalid_flat_id");
				return;
			}

			Flat flat = Flat.getFromForm(request);
			if (flat == null) {
				response.sendRedirect("status?c=4&r=6&e=invalid_customer_id");
				return;
			}
			
			// Validate form input
			String error = flat.validateForm();

			if (error != null) {
				// send error code with parameter so UI can show the exact message
				response.sendRedirect("status?c=4&r=6&e=" + error);
				return;
			}

			error = flat.updateRecord(conn, flat_id);
			// Add record to table
			if (error == null) {
				response.sendRedirect("flat?cid=" + flat.getCustomer_id());
			} else {
				response.sendRedirect("status?c=4&r=6&e=" + error);
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

}
