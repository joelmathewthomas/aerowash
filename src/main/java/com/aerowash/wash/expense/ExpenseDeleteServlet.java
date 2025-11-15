package com.aerowash.wash.expense;

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

public class ExpenseDeleteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ExpenseDeleteServlet() {
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

			int expense_id;
			int wash_id;
			int transaction_id;
			int payment_status;

			try {
				expense_id = Integer.parseInt(request.getParameter("eid"));
				transaction_id = Integer.parseInt(request.getParameter("tid"));
				wash_id = Integer.parseInt(request.getParameter("wid"));
				payment_status = Integer.parseInt(request.getParameter("s"));
			} catch (NumberFormatException ex) {
				ex.printStackTrace();
				response.sendRedirect("status?c=4&r=5&e=invalid_expense_id_or_transaction_id_or_wash_id");
				return;
			}

			if (Expense.deleteRecord(conn, expense_id, transaction_id)) {

				response.sendRedirect("expense?wid=" + wash_id + "&tid=" + transaction_id + "&s=" + payment_status);
			} else {
				response.sendRedirect("status?c=4&r=6&e=failed_to_delete_expense_item");
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("status?e=method_not_allowed");
	}

}
