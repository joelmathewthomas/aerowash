package com.aerowash.customers.vehicle;

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

public class VehicleCrudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public VehicleCrudServlet() {
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

			if (!Auth.checkSession(response, session, "staff", 3, 2)) {
				return;
			}

			if (customer_id == 0) {
				response.sendRedirect("status?c=4&r=5&e=invalid_customer_id");
				return;
			}

			PreparedStatement pst = conn.prepareStatement(
					"SELECT v.vehicle_id, v.flat_id, v.vehicle_name, v.vehicle_license_number, v.vehicle_added_date, f.flat_name FROM vehicle v JOIN flat f ON v.flat_id = f.flat_id WHERE v.customer_id = ?");
			pst.setInt(1, customer_id);
			ResultSet rs = pst.executeQuery();

			PrintWriter out = response.getWriter();

			response.setContentType("text/html");
			out.println("<!DOCTYPE html>\n"
					+ "<html>\n"
					+ "  <head>\n"
					+ "    <meta charset=\"UTF-8\" />\n"
					+ "    <title>Vehicle CRUD</title>\n"
					+ "  </head>\n"
					+ "  <body style=\"font-family: Arial, sans-serif; margin: 20px\">\n"
					+ "    <h1 style=\"text-align: center; margin-bottom: 5px\">AeroWash</h1>\n"
					+ "    <hr />\n"
					+ "\n"
					+ "    <div style=\"margin-top: 20px\">\n"
					+ "      <h3>Menu</h3>\n"
					+ "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">\n"
					+ "        <li><a href=\"customers\">Customers</a></li>\n"
					+ "        <li><a href=\"vadd?cid=" + customer_id + "\">Add Vehicle</a></li>\n"
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
					+ "            <th>Vehicle ID</th>\n"
					+ "            <th>Flat Name</th>\n"
					+ "            <th>Vehicle Name</th>\n"
					+ "            <th>License Number</th>\n"
					+ "            <th>Vehicle Added Date</th>\n"
					+ "            <th>Action</th>\n"
					+ "          </tr>\n"
					+ "        </thead>\n"
					+ "        <tbody>");

			while (rs.next()) {

				int vehicle_id = rs.getInt(1);
				String vehicle_name = rs.getString(3);
				String vehicle_license_number = rs.getString(4);
				Date vehicle_added_date = rs.getDate(5);
				String flat_name = rs.getString(6);

				out.println("          <tr>\n"
						+ "            <td>" + vehicle_id + "</td>\n"
						+ "            <td>" + flat_name + "</td>\n"
						+ "            <td>" + vehicle_name + "</td>\n"
						+ "            <td>" + vehicle_license_number + "</td>\n"
						+ "            <td>" + vehicle_added_date + "</td>\n"
						+ "            <td style=\"white-space: nowrap\">\n"
						+ "              <a href=\"vedit?vid=" + vehicle_id + "&cid=" + customer_id + "&vehicle_name=" + vehicle_name + "&vehicle_license_number=" + vehicle_license_number + "\">Edit</a>\n"
						+ "              |\n"
						+ "              <a href=\"vdelete?vid=" + vehicle_id + "&cid=" + customer_id + "\">Delete</a>\n"
						+ "            </td>\n"
						+ "          </tr>");

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
		response.sendRedirect("status");
	}

}
