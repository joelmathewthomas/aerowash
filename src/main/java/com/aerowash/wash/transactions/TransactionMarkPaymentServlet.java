package com.aerowash.wash.transactions;

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
import com.aerowash.wash.Wash;

public class TransactionMarkPaymentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public TransactionMarkPaymentServlet() {
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

			if (!Auth.checkSession(response, session, "all")) {
				return;
			}

			String role = (String) session.getAttribute("role");
			int transaction_id = Wash.getParam(request, "tid");
			int choice = Wash.getParam(request, "c");

			if (role.equals("staff") && choice == 0) {
				response.sendRedirect("status?c=3&r=6");
				return;
			}

			if (Transaction.setPaymentStatus(conn, transaction_id, choice)) {
				response.sendRedirect("wash");
				return;
			} else {
				response.sendRedirect("status?r=6&e=failed_to_process_payment");
				return;
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("status&e=method_not_allowed");
	}

}
