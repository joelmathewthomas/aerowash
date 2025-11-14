package com.aerowash.home;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.aerowash.auth.Auth;

public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AdminServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		try {
			// Session Tracking
			HttpSession session = request.getSession(false);

			if (!Auth.checkSession(response, session, "admin", 3, 3)) {
				return;
			}
		
			response.setContentType("text/html");
			response.getWriter().println("<!DOCTYPE html>\n"
					+ "<html>\n"
					+ "<head>\n"
					+ "    <meta charset=\"UTF-8\">\n"
					+ "    <title>AeroWash</title>\n"
					+ "</head>\n"
					+ "<body style=\"font-family: Arial, sans-serif; margin: 20px\">\n"
					+ "\n"

    				+ "    <h1 style=\"text-align: center; margin-bottom: 5px;\">AeroWash</h1>\n"
					+ "    <hr>\n"
					+ "\n"
					+ "    <h2>Welcome, " + session.getAttribute("username") + "</h2>\n"
					+ "\n"
					+ "    <div style=\"margin-top: 20px;\">\n"
					+ "        <h3>Menu</h3>\n"
					+ "\n"
					+ "        <ul style=\"line-height: 1.8;\">\n"
					+ "            <li><a href=\"scrud\">Staff</a></li>\n"
					+ "            <li><a href=\"customers\">Customers</a></li>\n"
					+ "            <li><a href=\"wash\">Wash</a></li>\n"
					+ "            <li><a href=\"logout\">Logout</a></li>\n"
					+ "        </ul>\n"
					+ "    </div>\n"
					+ "\n"
					+ "</body>\n"
					+ "</html>\n"
					+ "\n"
					+ "");

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendRedirect("status");
	}

}
