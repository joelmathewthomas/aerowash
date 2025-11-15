package com.aerowash.wash.expense;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.http.HttpServletRequest;

public class Expense {

	private int wash_id;
	private int transaction_id;
	private String expense_item;
	private float expense_amount;

	public Expense(int wash_id, int transaction_id, String expense_item, float expense_amount) {
		super();
		this.wash_id = wash_id;
		this.transaction_id = transaction_id;
		this.expense_item = sanitizeString(expense_item);
		this.expense_amount = expense_amount;
	}

	// Remove leading and trailing whitespace
	private String sanitizeString(String s) {
		return s == null ? "" : s.trim();
	}

	// Return a new Expense object directly from request
	public static Expense getFromForm(HttpServletRequest request) {
		int wash_id = Integer.parseInt(request.getParameter("wid"));
		int transaction_id = Integer.parseInt(request.getParameter("tid"));
		float expense_amount = Float.parseFloat(request.getParameter("expense_amount"));
		return new Expense(wash_id, transaction_id, request.getParameter("expense_item"), expense_amount);
	}

	public String validateForm() {
		if (wash_id <= 0)
			return "wash_id";

		if (transaction_id <= 0)
			return "transaction_id";

		if (expense_item == null || !expense_item.matches("^[A-Za-z0-9 ]{1,50}$"))
			return "expense_item";

		if (expense_amount < 0)
			return "expense_amount";

		return null;
	}

	public boolean addRecord(Connection conn) {
		try {
			conn.setAutoCommit(false);

			try (PreparedStatement pst = conn.prepareStatement(
					"INSERT INTO `aerowash`.`expense` (`wash_id`, `expense_item`, `expense_amount`, `expense_date`) VALUES (?, ?, ?, ?)")) {
				pst.setInt(1, wash_id);
				pst.setString(2, expense_item);
				pst.setFloat(3, expense_amount);
				pst.setDate(4, java.sql.Date.valueOf(LocalDate.now()));

				if (pst.executeUpdate() != 1) {
					conn.rollback();
					return false;
				}

				try (PreparedStatement pstSum = conn
						.prepareStatement("SELECT SUM(expense_amount) FROM expense WHERE wash_id = ?")) {
					pstSum.setInt(1, wash_id);
					ResultSet rs = pstSum.executeQuery();
					float expense_total = 0;

					if (rs.next()) {
						expense_total = rs.getFloat(1);
					} else {
						conn.rollback();
						return false;
					}

					try (PreparedStatement pstTransaction = conn.prepareStatement(
							"UPDATE transactions SET transaction_amount = ? WHERE transaction_id = ?")) {
						pstTransaction.setFloat(1, expense_total);
						pstTransaction.setInt(2, transaction_id);

						if (pstTransaction.executeUpdate() != 1) {
							conn.rollback();
							return false;
						}

						conn.commit();
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	public static boolean deleteRecord(Connection conn, int expense_id, int transaction_id) {
		try {
			conn.setAutoCommit(false);

			try (PreparedStatement pstExpense = conn
					.prepareStatement("SELECT expense_amount FROM expense WHERE expense_id = ?")) {
				pstExpense.setInt(1, expense_id);

				ResultSet rs = pstExpense.executeQuery();
				float expense_amount;
				if (rs.next()) {
					expense_amount = rs.getFloat(1);
				} else {
					conn.rollback();
					return false;
				}

				try (PreparedStatement pstTransaction = conn
						.prepareStatement("SELECT transaction_amount FROM transactions WHERE transaction_id = ?")) {
					pstTransaction.setInt(1, transaction_id);
					rs = pstTransaction.executeQuery();
					float transaction_amount;

					if (rs.next()) {
						transaction_amount = rs.getFloat(1);
					} else {
						conn.rollback();
						return false;
					}

					float new_transaction_amount = transaction_amount - expense_amount;

					try (PreparedStatement pstTransactionUpdate = conn.prepareStatement(
							"UPDATE transactions SET transaction_amount = ? WHERE transaction_id = ?")) {
						pstTransactionUpdate.setFloat(1, new_transaction_amount);
						pstTransactionUpdate.setInt(2, transaction_id);

						if (pstTransactionUpdate.executeUpdate() != 1) {
							conn.rollback();
							return false;
						}

						try (PreparedStatement pstExpenseDelete = conn
								.prepareStatement("DELETE FROM expense WHERE expense_id = ?")) {
							pstExpenseDelete.setInt(1, expense_id);
							if (pstExpenseDelete.executeUpdate() != 1) {
								conn.rollback();
								return false;
							}

							conn.commit();
						}
					}
				}
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}

		return true;
	}

	public static ResultSet getItems(Connection conn, int wash_id) {
		String sql = "SELECT * FROM expense WHERE wash_id = ?";

		try {
			PreparedStatement pst;

			pst = conn.prepareStatement(sql);
			pst.setInt(1, wash_id);

			return pst.executeQuery();

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
}
