package com.aerowash.wash.expense;

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
import com.aerowash.wash.Wash;

public class ExpenseAddServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ExpenseAddServlet() {
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

			// Check access
			if (!Auth.checkSession(response, session, "all")) {
				return;
			}

			int wash_id = Wash.getParam(request, "wid");
			int transaction_id = Wash.getParam(request, "tid");

			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.println("<!DOCTYPE html>\n"
					+ "<html>\n"
					+ "  <head>\n"
					+ "    <meta charset=\"UTF-8\" />\n"
					+ "    <title>New Expense Item</title>\n"
					+ "  </head>\n"
					+ "  <body style=\"font-family: Arial, sans-serif; margin: 20px\">\n"
					+ "    <h1 style=\"text-align: center; margin-bottom: 5px\">AeroWash</h1>\n"
					+ "    <hr />\n"
					+ "\n"
					+ "    <div style=\"margin-top: 20px\">\n"
					+ "      <h3>Menu</h3>\n"
					+ "\n"
					+ "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">\n"
					+ "        <li><a href=\"expense?wid=" + wash_id + "&tid=" + transaction_id + "\">Expense</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>New Expense Item</h2>\n"
					+ "\n"
					+ "    <form\n"
					+ "      action=\"eadd\"\n"
					+ "      method=\"POST\"\n"
					+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
					+ "    >\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Wash ID</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"wid\"\n"
					+ "          required\n"
					+ "          readonly\n"
					+ "          value=\"" + wash_id + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Transaction ID</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"tid\"\n"
					+ "          required\n"
					+ "          readonly\n"
					+ "          value=\"" + transaction_id + "\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Expense Item</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"expense_item\"\n"
					+ "          style=\"padding: 5px; width: 200px\"\n"
					+ "        />\n"
					+ "      </div>\n"
					+ "      <div style=\"margin-top: 15px\">\n"
					+ "        <label>Expense Amount</label><br />\n"
					+ "        <input\n"
					+ "          type=\"text\"\n"
					+ "          name=\"expense_amount\"\n"
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
			if (!Auth.checkSession(response, session, "all")) {
				return;
			}
			
			int wash_id = Wash.getParam(request, "wid");
			int transaction_id = Wash.getParam(request, "tid");
			
			Expense expense = Expense.getFromForm(request);
			if (expense == null) {
				response.sendRedirect("status");
				return;
			}

			String error = expense.validateForm();
			if (error != null) {
				response.sendRedirect("status?c=4&r=6&e=" + error);
				return;
			}
			
			if (expense.addRecord(conn)) {
				response.sendRedirect("expense?wid=" + wash_id + "&tid=" + transaction_id);
				return;
			} else {
				response.sendRedirect("status?c=4&r=6&e=failed_to_add_new_item"  );
				return;
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}	
	}

}
