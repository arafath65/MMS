
package JPA_DAO.Student_Management;

import Entities.Student_Management.StudentAdditionalFeePayment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;

public class PaymentDAO {

    private Connection con;

    public PaymentDAO(Connection con) {
        this.con = con;
    }

    // =====================================
    // SAVE ADDITIONAL FEE PAYMENT
    // =====================================
    public int saveAdditionalFeePayment(StudentAdditionalFeePayment p) throws Exception {

        String sql = "INSERT INTO student_additional_fee_payments "
                + "(student_additional_fees_id, paid_date, amount_paid, payment_method, user, status) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        ps.setInt(1, p.getStudentAdditionalFeesId());
        ps.setDate(2, new java.sql.Date(p.getPaidDate().getTime()));
        ps.setDouble(3, p.getAmountPaid());
        ps.setString(4, p.getPaymentMethod());
        ps.setString(5, p.getUser());
        ps.setInt(6, p.getStatus());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            return rs.getInt(1);
        }

        return -1;
    }

    // =====================================
    // SAVE STOCK TRANSACTION (ONLY INVENTORY)
    // =====================================
    public void saveStockTransaction(
            int itemId,
            int studentId,
            double qty,
            String user
    ) throws Exception {

        String sql = "INSERT INTO stock_transactions "
                + "(item_id, student_id, quantity, transaction_type, transaction_date, remarks, user, status) "
                + "VALUES (?, ?, ?, 'OUT', NOW(), 'ROUND_PAYMENT', ?, 1)";

        PreparedStatement ps = con.prepareStatement(sql);

        ps.setInt(1, itemId);
        ps.setInt(2, studentId);
        ps.setDouble(3, qty);
        ps.setString(4, user);

        ps.executeUpdate();
    }
}
