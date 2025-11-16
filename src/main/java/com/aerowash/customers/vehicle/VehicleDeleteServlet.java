package com.aerowash.customers.vehicle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aerowash.auth.Auth;

public class VehicleDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public VehicleDeleteServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Class.forName(getServletContext().getInitParameter("Driver"));
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		try (Connection conn = DriverManager.getConnection(getServletContext().getInitParameter("DbUrl"),
				getServletContext().getInitParameter("DbUser"), getServletContext().getInitParameter("DbPassword"))) {
			// Session Tracking
			HttpSession session = request.getSession(false);
			
			if (!Auth.checkSession(response, session, "all")) {
				return;
			}

			int vehicle_id;
			int customer_id;
			
			try {
				vehicle_id = Integer.parseInt(request.getParameter("vid"));
				customer_id = Integer.parseInt(request.getParameter("cid"));
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				response.sendRedirect("status?c=4&r=5&e=invalid_vehicle_id_or_customer_id");
				return;
			}

			String error = Vehicle.deleteRecord(conn, vehicle_id, customer_id);

			if (error == null) {
				response.sendRedirect("vehicle?cid=" + customer_id);
			} else {
				response.sendRedirect("status?c=4&r=5&e=" + error);
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("status?e=\"method_not_allowed\"");
	}

}
