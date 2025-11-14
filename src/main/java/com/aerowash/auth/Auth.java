package com.aerowash.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Auth {

	public static boolean checkSession(HttpServletResponse response, HttpSession session, String role, int code,
			int route) {
		// response -> HttpServletResponse object
		// session -> HttpSession object
		// role -> target role for check
		// code -> Error code index
		// route -> route index
		try {

			if (session == null) {
				response.sendRedirect("status?c=2&r=1");
				return false;
			} else if (session.getAttribute("role").equals("inactive")) {
				response.sendRedirect("status?c=5&r=1");
				return false;
			} else if (!role.equals(session.getAttribute("role"))) {
				response.sendRedirect("status?c=" + code + "&r=" + route);
				return false;
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return true;

	}
}
