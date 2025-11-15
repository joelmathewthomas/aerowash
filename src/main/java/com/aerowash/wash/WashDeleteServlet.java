package com.aerowash.wash;

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

public class WashDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public WashDeleteServlet() {
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

			if (!Auth.checkSession(response, session, "admin", 3, 2)) {
				return;
			}

			int transaction_id;

			try {
				transaction_id = Integer.parseInt(request.getParameter("tid"));
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				response.sendRedirect("status?c=4&r=6&e=invalid_transaction_id");
				return;
			}

			if (Wash.deleteRecord(conn, transaction_id)) {

				response.sendRedirect("wash");
			} else {
				response.sendRedirect("status?c=4&r=6&e=failed_to_delete_wash_job");
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("stauts?e=method_not_allowed");
	}

}
