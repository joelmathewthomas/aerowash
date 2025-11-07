package com.aerowash.status;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/status")
public class StatusServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Map<Integer, String> MESSAGES = Map.of(1, "Invalid username or password.", 2,
			"Please login to continue.", 3, "You are not authorized to view this page.");

	public StatusServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		int code = 0;

		if (request.getParameter("c") != null && request.getParameter("c").matches("\\d+")) {
		    code = Integer.parseInt(request.getParameter("c"));
		}

		PrintWriter out = response.getWriter();

		out.println("<!DOCTYPE html>\n"
				+ "<html>\n"
				+ "<head>\n"
				+ "    <meta charset=\"UTF-8\">\n"
				+ "    <title>AeroWash</title>\n"
				+ "</head>\n"
				+ "<body style=\"font-family: Arial, sans-serif;\">\n"
				+ "\n"
				+ "    <h1>AeroWash</h1>\n"
				+ "    <hr>\n"
				+ "\n"
				+ "    <p>" + MESSAGES.getOrDefault(code, "Unkown error occured!") + "</p>\n"
				+ "\n"
				+ "    <a href=\"index.html\" style=\"display:inline-block; margin-top:15px; text-decoration:none;\">\n"
				+ "        Go Home\n"
				+ "    </a>\n"
				+ "\n"
				+ "</body>\n"
				+ "</html>\n"
				+ "\n"
				+ "");

		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

}
