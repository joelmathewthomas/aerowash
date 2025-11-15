package com.aerowash.report;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aerowash.auth.Auth;
import com.aerowash.salary.Salary;

public class SalaryReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SalaryReportServlet() {
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

			if (!Auth.checkSession(response, session, "admin", 3, 3)) {
				return;
			}
			
			String month = (request.getParameter("m") == null) ? null : request.getParameter("m");
			int year;
			
			try {
				year = Integer.parseInt(request.getParameter("y"));
			} catch (NumberFormatException | NullPointerException ex) {
				year = 0;
			}
			
			List<String> months = Arrays.stream(Month.values())
                    .map(Month::name)
                    .toList();

			boolean validMonth = months.contains(month);
			
			ResultSet rs = null;
			if (validMonth && year > 2010 && year <= LocalDate.now().getYear()) {
				rs = Salary.getReport(conn, month, year); 
			}
			
			response.setContentType("text/html");
			PrintWriter out = response.getWriter();
			
			out.println("<!DOCTYPE html>\n"
					+ "<html>\n"
					+ "  <head>\n"
					+ "    <meta charset=\"UTF-8\" />\n"
					+ "    <title>Salary</title>\n"
					+ "  </head>\n"
					+ "  <body style=\"font-family: Arial, sans-serif; margin: 20px\">\n"
					+ "    <h1 style=\"text-align: center; margin-bottom: 5px\">AeroWash</h1>\n"
					+ "    <hr />\n"
					+ "\n"
					+ "    <div style=\"margin-top: 20px\">\n"
					+ "      <h3>Menu</h3>\n"
					+ "\n"
					+ "      <ul style=\"line-height: 1.8; margin-left: 0; padding-left: 15px\">\n"
					+ "        <li><a href=\"report\">Reports</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>Salary Payment</h2>");
			
			if (!(validMonth && year > 2010 && year <= LocalDate.now().getYear())) {
				out.println("    <form\n"
						+ "      action=\"sreport\"\n"
						+ "      method=\"GET\"\n"
						+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
						+ "    >\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Select Month</label><br />\n"
						+ "        <select name=\"m\" style=\"padding: 5px; width: 200px\" required>\n"
						+ "          <option value=\"\" disabled selected>Select Month</option>\n"
						+ "          <option value=\"JANUARY\">JANUARY</option>\n"
						+ "          <option value=\"FEBRUARY\">FEBRUARY</option>\n"
						+ "          <option value=\"MARCH\">MARCH</option>\n"
						+ "          <option value=\"APRIL\">APRIL</option>\n"
						+ "          <option value=\"MAY\">MAY</option>\n"
						+ "          <option value=\"JUNE\">JUNE</option>\n"
						+ "          <option value=\"JULY\">JULY</option>\n"
						+ "          <option value=\"AUGUST\">AUGUST</option>\n"
						+ "          <option value=\"SEPTEMBER\">SEPTEMBER</option>\n"
						+ "          <option value=\"OCTOBER\">OCTOBER</option>\n"
						+ "          <option value=\"NOVEMBER\">NOVEMBER</option>\n"
						+ "          <option value=\"DECEMBER\">DECEMBER</option>\n"
						+ "        </select>\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Year</label><br />\n"
						+ "        <input\n"
						+ "          type=\"text\"\n"
						+ "          name=\"y\"\n"
						+ "          required\n"
						+ "          placeholder=\"Enter Year\"\n"
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
						+ "        Next\n"
						+ "      </button>\n"
						+ "    </form>");
			} else {
				out.println("<table\n"
						+ "  border=\"1\"\n"
						+ "  cellpadding=\"8\"\n"
						+ "  cellspacing=\"0\"\n"
						+ "  style=\"border-collapse: collapse; width: 100%\"\n"
						+ ">\n"
						+ "  <thead\n"
						+ "    style=\"background-color: #f2f2f2; font-weight: bold; text-align: left\"\n"
						+ "  >\n"
						+ "    <tr>\n"
						+ "      <th>Staff ID</th>\n"
						+ "      <th>First Name</th>\n"
						+ "      <th>Middle Name</th>\n"
						+ "      <th>Last Name</th>\n"
						+ "      <th>Month</th>\n"
						+ "      <th>Year</th>\n"
						+ "      <th>Amount</th>\n"
						+ "    </tr>\n"
						+ "  </thead>\n"
						+ "\n"
						+ "  <tbody>\n"
						+ "\n");
						while(rs.next()) {
							out.println(""
									+ "    <tr>\n"
									+ "      <td>" + rs.getInt(1) + "</td>\n"
									+ "      <td>" + rs.getString(2) + "</td>\n"
									+ "      <td>" + rs.getString(3) + "</td>\n"
									+ "      <td>" + rs.getString(4) + "</td>\n"
									+ "      <td>" + rs.getString(5) + "</td>\n"
									+ "      <td>" + rs.getInt(6) + "</td>\n"
									+ "      <td>" + rs.getInt(7) + "</td>\n"
									+ "    </tr>\n");
						}
						
						out.println(""
						+ "\n"
						+ "  </tbody>\n"
						+ "</table>\n"
						+ "");
			}
			
			
			out.close();

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("status&e=method_not_supported");
	}

}
