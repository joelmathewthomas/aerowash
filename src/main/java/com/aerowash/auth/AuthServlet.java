package com.aerowash.auth;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AuthServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public AuthServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
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
			HttpSession session = request.getSession();

			PreparedStatement pst = conn
					.prepareStatement("SELECT * FROM users WHERE username = ? AND user_password = ?");
			pst.setString(1, request.getParameter("username"));
			pst.setString(2, request.getParameter("password"));
			ResultSet rs = pst.executeQuery();

			if (rs.next()) {
				session.setAttribute("username", rs.getString(2));
				String role = (String) rs.getString(4);
				session.setAttribute("role", role);
				
				if (role.equals("admin")) {
					response.sendRedirect("admin");
				} else if (role.equals("staff")) {
					response.sendRedirect("staff");
				}
				
			} else {
				response.sendRedirect("status?c=1&r=1");
			}

		} catch (SQLException ex) {
			response.sendRedirect("status");
			ex.printStackTrace();
		}
	}

}