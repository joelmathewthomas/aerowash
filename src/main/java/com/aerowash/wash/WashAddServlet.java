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
import com.aerowash.customers.Customer;
import com.aerowash.customers.vehicle.Vehicle;

public class WashAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public WashAddServlet() {
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
			ResultSet rs;

			// Check access
			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			int customer_id = Wash.getParam(request, "cid"); 
			int vehicle_id = Wash.getParam(request, "vid"); 
			
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
					+ "        <li><a href=\"wash\">Wash</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>New Wash Job</h2>");
			
			if (customer_id == 0 ) {
				rs = Customer.getCustomerList(conn);
				out.println("    <form action=\"wadd\" method=\"GET\" style=\"margin-top: 20px; line-height: 1.8\">\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Select Customer</label><br />\n"
						+ "        <select name=\"cid\" style=\"padding: 5px; width: 200px\" required>\n"
						+ "          <option value=\"\" disabled selected>Select Customer</option>\n"
						+ "\n");

						while(rs.next()) {
							out.println("          <option value=\"" + rs.getInt(1) + "\">" + rs.getInt(1) + " " + rs.getString(2) + " "  + rs.getString(3) + " " + rs.getString(4) + "</option>\n");
						}

						out.println(""
						+ "        </select>\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
						+ "        Next\n"
						+ "      </button>\n"
						+ "    </form>");
			} else if (vehicle_id == 0){
				rs = Vehicle.getVehicleList(conn, customer_id);
				out.println("    <form action=\"wadd\" method=\"GET\" style=\"margin-top: 20px; line-height: 1.8\">\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Customer ID</label><br />\n"
						+ "        <input\n"
						+ "          type=\"text\"\n"
						+ "          name=\"cid\"\n"
						+ "          value=\"" + customer_id + "\"\n"
						+ "          readonly\n"
						+ "          required\n"
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Select Vehicle</label><br />\n"
						+ "        <select name=\"vid\" style=\"padding: 5px; width: 200px\" required>\n"
						+ "          <option value=\"\" disabled selected>Select Vehicle</option>\n");

						while(rs.next()) {
							out.println("          <option value=\"" + rs.getInt(1) + "\">" + rs.getInt(1) + " " + rs.getString(2) + "</option>\n");
						}

						out.println("\n"
						+ "        </select>\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
						+ "        Next\n"
						+ "      </button>\n"
						+ "    </form>");
			} else if (customer_id !=0 && vehicle_id !=0) {
				out.println("    <form\n"
						+ "      action=\"wadd\"\n"
						+ "      method=\"POST\"\n"
						+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
						+ "    >\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Customer ID</label><br />\n"
						+ "        <input\n"
						+ "          type=\"text\"\n"
						+ "          name=\"cid\"\n"
						+ "          value=\"" + customer_id + "\"\n"
						+ "          readonly\n"
						+ "          required\n"
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Vehicle</label><br />\n"
						+ "        <input\n"
						+ "          type=\"text\"\n"
						+ "          name=\"vid\"\n"
						+ "          value=\"" + vehicle_id + "\"\n"
						+ "          readonly\n"
						+ "          required\n"
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Select Payment Mode</label><br />\n"
						+ "        <select name=\"payment_mode\" style=\"padding: 5px; width: 200px\" required>\n"
						+ "          <option value=\"\" disabled selected>Select Payment Mode</option>\n"
						+ "          <option value=\"ONLINE\">ONLINE</option>\n"
						+ "          <option value=\"OFFLINE\">OFFLINE</option>\n"
						+ "        </select>\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
						+ "        Add\n"
						+ "      </button>\n"
						+ "    </form>");
			}

			out.println("  </body>\n"
					+ "</html>");
			out.close();

		} catch (SQLException ex) {
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

			// Check session
			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}
			
			int vehicle_id;
			String transaction_mode = request.getParameter("payment_mode");
			
			if (transaction_mode == null) {
				response.sendRedirect("status?e=transaction_mode_not_specified" );
				return;
			}
			
			try {
				vehicle_id = Integer.parseInt(request.getParameter("vid"));
				
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				response.sendRedirect("status?e=invalid_vehicle_id" );
				return;
			}
			
			if (Wash.addNewJob(conn, session, vehicle_id, transaction_mode)) {
				response.sendRedirect("wash");
				return;
			} else {
				response.sendRedirect("status?e=failed_to_add_new_wash_job" );
				return;
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}	
	}

}
