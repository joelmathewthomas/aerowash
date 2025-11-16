package com.aerowash.report;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.aerowash.auth.Auth;

public class ReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ReportServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		if (!Auth.checkSession(response, session, "admin", 3, 3)) {
			return;
		}
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "  <head>\n"
				+ "    <meta charset=\"UTF-8\" />\n"
				+ "    <title>AeroWash</title>\n"
				+ "  </head>\n"
				+ "  <body style=\"font-family: Arial, sans-serif; margin: 20px\">\n"
				+ "    <h1 style=\"text-align: center; margin-bottom: 5px\">AeroWash</h1>\n"
				+ "    <hr />\n"
				+ "    <div style=\"margin-top: 20px\">\n"
				+ "      <h3>Menu</h3>\n"
				+ "\n"
				+ "      <ul style=\"line-height: 1.8\">\n"
				+ "        <li><a href=\"admin\">Home</a></li>\n"
				+ "        <li><a href=\"ereport\">Expense Report</a></li>\n"
				+ "        <li><a href=\"sreport\">Salary Report</a></li>\n"
				+ "      </ul>\n"
				+ "    </div>\n"
				+ "  </body>\n"
				+ "</html>\n"
				+ "");
		
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("status?e=method_not_allowed");
	}

}
