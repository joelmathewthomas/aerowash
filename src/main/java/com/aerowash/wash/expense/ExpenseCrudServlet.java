package com.aerowash.wash.expense;

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

public class ExpenseCrudServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public ExpenseCrudServlet() {
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

			String role = (String) session.getAttribute("role");
			
			int wash_id = 0;
			int transaction_id = 0;
			int payment_status = 0;
			try {
				wash_id = Integer.parseInt(request.getParameter("wid"));
				transaction_id = Integer.parseInt(request.getParameter("tid"));
				payment_status = Integer.parseInt(request.getParameter("s"));
			} catch (NumberFormatException ex) {
				response.sendRedirect("status?c=4&r=6&e=invalid_wash_id_or_transaction_id");
				ex.printStackTrace();
				return;
			}

			ResultSet rs = Expense.getItems(conn, wash_id);
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
					+ "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">"
					+ "        <li><a href=\"wash\">Wash</a></li>\n");
					
					if (payment_status == 0) {
						out.println("        <li><a href=\"eadd?wid=" + wash_id + "&tid=" + transaction_id + "\">New item</a></li>\n");
					}
					
					out.println(""
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
					+ "            <th>Expense ID</th>\n"
					+ "            <th>Expense Item</th>\n"
					+ "            <th>Amount</th>\n"
					+ "            <th>Expense Date</th>\n");
					
					if (role.equals("admin") && payment_status == 0) {
						out.println("            <th>Action</th>\n");
					}
					
					out.println(""
					+ "          </tr>\n"
					+ "        </thead>");
			
			while (rs.next()) {
				out.println("        <tbody>\n"
						+ "          <tr>\n"
						+ "            <td>" + rs.getInt(1) + "</td>\n"
						+ "            <td>" + rs.getString(3) + "</td>\n"
						+ "            <td>" + rs.getFloat(4) + "</td>\n"
						+ "            <td>" + rs.getDate(5) + "</td>\n");

						if (role.equals("admin") && payment_status == 0) {
							out.println(""
							+ "            <td style=\"white-space: nowrap\">\n"
							+ "             <a href=\"washview?wid=1\">Delete</a>\n"
							+ "            </td>\n");
						}

						out.println(""
						+ "          </tr>\n"
						+ "        </tbody>");
			}
			
			out.println("      </table>\n"
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
		response.sendRedirect("status?e=method_not_allowed");
	}

}
