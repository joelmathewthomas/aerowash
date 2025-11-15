package com.aerowash.salary;

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

public class SalaryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public SalaryServlet() {
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
				rs = Salary.getUnpaidStaff(conn, month, year); 
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
					+ "        <li><a href=\"admin\">Home</a></li>\n"
					+ "      </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "    <h2>Salary Payment</h2>");
			
			if (!(validMonth && year > 2010 && year <= LocalDate.now().getYear())) {
				out.println("    <form\n"
						+ "      action=\"salary\"\n"
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
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
						+ "        Next\n"
						+ "      </button>\n"
						+ "    </form>");
			} else {
				out.println("    <form\n"
						+ "      action=\"salary\"\n"
						+ "      method=\"POST\"\n"
						+ "      style=\"margin-top: 20px; line-height: 1.8\"\n"
						+ "    >\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Month</label><br />\n"
						+ "        <input\n"
						+ "          type=\"text\"\n"
						+ "          name=\"m\"\n"
						+ "          value=\"" + month + "\"\n"
						+ "          readonly\n"
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Year</label><br />\n"
						+ "        <input\n"
						+ "          type=\"text\"\n"
						+ "          name=\"y\"\n"
						+ "          value=\"" + year + "\"\n"
						+ "          readonly\n"
						+ "          style=\"padding: 5px; width: 200px\"\n"
						+ "        />\n"
						+ "      </div>\n"
						+ "      <div style=\"margin-top: 15px\">\n"
						+ "        <label>Select Staff</label><br />\n"
						+ "        <select name=\"staff\" style=\"padding: 5px; width: 200px\" required>\n"
						+ "          <option value=\"\" disabled selected>Select Staff</option>\n");
						
						if (rs !=null) {
							while (rs.next()) {
								out.println("          <option value=\"" + rs.getInt(1) + "\">" + rs.getString(1) + " " + rs.getString(2) + " " + ((rs.getString(3) == null) ? "" : rs.getString(3)) + " " + rs.getString(4) + "</option>\n");
							}
						}
						
						out.println(""
						+ "        </select>\n"
						+ "      </div>\n"
						+ "\n"
						+ "      <button type=\"submit\" style=\"margin-top: 20px; padding: 8px 20px\">\n"
						+ "        Pay\n"
						+ "      </button>\n"
						+ "    </form>");
			}
			
			
			out.close();

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(request.getParameter("m"));
		System.out.println(request.getParameter("y"));
		System.out.println(request.getParameter("staff"));
	}

}
