package com.aerowash.customers.vehicle;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aerowash.auth.Auth;

public class VehicleAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public VehicleAddServlet() {
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
			Map<Integer, String> flats;

			int customer_id;
			try {
				customer_id = (request.getParameter("cid") == null) ? 0 : Integer.parseInt(request.getParameter("cid"));
			} catch (NumberFormatException ex) {
				customer_id = 0;
				ex.printStackTrace();
			}

			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			if (customer_id == 0) {
				response.sendRedirect("status?c=4&r=5&e=invalid_customer_id");
				return;
			}
			
			flats = Vehicle.getFlats(conn, customer_id);
			if (flats == null) {
				response.sendRedirect("status?e=failed_to_fetch_flat_details");
				return;
			} else if (flats.isEmpty()) {
				response.sendRedirect("status?e=no_flats_available");			}
			
			
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
					+ "        <li><a href=\"vehicle?cid=" + customer_id + "\">Vehicle</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>Add Vehicle Details</h2>\n"
					+ "\n"
					+ "    <form\n"
					+ "      action=\"cedit\"\n"
					+ "      method=\"POST\"\n"
					+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
					+ "    >\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Vehicle ID</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"vehicle_id\"\n"
					+ "          required\n"
					+ "          readonly\n"
					+ "          value=\"2\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Vehicle Name</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"vehicle_name\"\n"
					+ "          required\n"
					+ "          value=\"Joel\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Vehicle License Number</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"vehicle_license_number\"\n"
					+ "          value=\"asdsada\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Flat</label><br />\n"
					+ "        <select name=\"customer_lname\" style=\"padding: 5px; width: 200px\">\n"
					+ "          <option value=\"\" disabled selected>Select a Flat</option>\n");
			
					for (Map.Entry<Integer, String> flat : flats.entrySet()) {
						out.println("          <option value=\"" + flat.getKey() + "\">" + flat.getValue() + "</option>\n");
					}
			
					out.println("\n"
					+ "        </select>\n"
					+ "      </div>\n"
					+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
					+ "        Save\n"
					+ "      </button>\n"
					+ "    </form>\n"
					+ "  </body>\n"
					+ "</html>\n"
					+ "");

		} catch (Exception ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
