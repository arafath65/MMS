package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableCheckboxHandler;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Student_Management.StudentFeeRoundPaymentMaster;
import Entities.Student_Management.StudentFeeRoundPaymentMasterDetails;
import JPA_DAO.Settings.CourseDAO;
import JPA_DAO.Student_Management.StudentAdditionalFeesDAO;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import JPA_DAO.Student_Management.StudentFeeRoundPaymentDAO;
import Panels.Fees_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;

public class Cancel_Round_Payment extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Cancel_Round_Payment.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    private Fees_Management parentForm;

    CourseDAO dao = new CourseDAO();

    int feeID = 0;
    private int selectedStudentIds;
    String studentName;
    String username;
    String role;

    public Cancel_Round_Payment(Window parent, int selectedStudentIds, String studentName, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.parentForm = parentForm;
        this.selectedStudentIds = selectedStudentIds;
        this.studentName = studentName;
        this.username = username;
        this.role = role;
        initComponents();

        crp_student_name_text.setText(this.studentName);

        //JComboPopulatesBankInfo();
        crp_round_table.setDefaultRenderer(Object.class, new TableGradientCell());
        crp_round_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        crp_round_details_table.setDefaultRenderer(Object.class, new TableGradientCell());
        crp_round_details_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadRoundPayments(this.selectedStudentIds);

    }

    public void loadRoundPayments(int studentId) {

        DefaultTableModel model = (DefaultTableModel) crp_round_table.getModel();
        model.setRowCount(0);

        EntityManager em = HibernateConfig.getEntityManager();

        int count = 1;

        try {

            List<Object[]> list = em.createNativeQuery(
                    "SELECT "
                    + "m.student_fee_round_payment_master_id, "
                    + "DATE(m.payment_date), "
                    + "m.payment_mode, "
                    + "COALESCE(SUM(d.paid_amount),0), "
                    + "COALESCE(( "
                    + "   SELECT SUM(c.cheque_amount) "
                    + "   FROM student_fee_cheque_details c "
                    + "   WHERE c.reference_id = m.student_fee_round_payment_master_id "
                    + "   AND c.reference_type = 'ROUND' "
                    + "   AND c.category = 'STUDENT' "
                    + "   AND c.cheque_status = 'PENDING' "
                    + "   AND c.status = 1 "
                    + "),0) "
                    + "FROM student_fee_round_payment_master m "
                    + "LEFT JOIN student_fee_round_payment_master_details d "
                    + "ON d.student_fee_round_payment_master_id = m.student_fee_round_payment_master_id "
                    + "WHERE m.student_id = ? "
                    + "AND m.status = 1 "
                    // ✅ FILTER LAST 3 DAYS
                    + "AND DATE(m.payment_date) >= CURDATE() - INTERVAL 2 DAY "
                    + "GROUP BY m.student_fee_round_payment_master_id "
                    + "ORDER BY m.payment_date DESC"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            for (Object[] row : list) {

                int masterId = Integer.parseInt(row[0].toString());
                String date = row[1] != null ? row[1].toString() : "";
                String method = row[2].toString();

                double totalPaid = Double.parseDouble(row[3].toString());
                double cheque = Double.parseDouble(row[4].toString());

                double displayPaid = 0;
                double displayCheque = 0;

                // ✅ SHOW ONLY ONE COLUMN
                if ("CHEQUE".equalsIgnoreCase(method)) {
                    displayCheque = cheque;
                } else {
                    displayPaid = totalPaid;
                }

                model.addRow(new Object[]{
                    count++,
                    date,
                    method,
                    GeneralMethods.formatWithComma(displayPaid),
                    GeneralMethods.formatWithComma(displayCheque),
                    masterId
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void loadRoundPaymentDetails(int masterId) {

        DefaultTableModel model = (DefaultTableModel) crp_round_details_table.getModel();
        model.setRowCount(0);

        EntityManager em = HibernateConfig.getEntityManager();

        int count = 1;

        try {

            List<Object[]> list = em.createNativeQuery(
                    "SELECT "
                    + "d.student_fee_round_payment_master_details_id, "
                    + "d.enrollment_id, "
                    + "d.reference_id, "
                    + "d.reference_type, "
                    + "d.paid_amount "
                    + "FROM student_fee_round_payment_master_details d "
                    + "WHERE d.student_fee_round_payment_master_id = ? "
                    + "AND d.status = 1"
            )
                    .setParameter(1, masterId)
                    .getResultList();

            for (Object[] row : list) {

                int detailId = Integer.parseInt(row[0].toString());
                Integer enrollmentId = row[1] != null ? Integer.parseInt(row[1].toString()) : null;
                Integer refId = row[2] != null ? Integer.parseInt(row[2].toString()) : null;
                String refType = row[3].toString();
                double paidAmount = Double.parseDouble(row[4].toString());

                String category = "";
                String name = "";
                int qty = 1;
                String hiddenRef = "";

                // ===============================
                // COURSE
                // ===============================
                if ("COURSE".equalsIgnoreCase(refType)) {

                    category = "COURSE";

                    // ===============================
                    // GET COURSE ID FROM ENROLLMENT
                    // ===============================
                    Integer courseId = ((Number) em.createNativeQuery(
                            "SELECT course_id FROM course_enrollment WHERE enrollment_id = ?"
                    )
                            .setParameter(1, enrollmentId)
                            .getSingleResult()).intValue();

                    // ===============================
                    // GET COURSE DETAILS
                    // ===============================
                    Object[] course = (Object[]) em.createNativeQuery(
                            "SELECT course_name, payment_mode "
                            + "FROM course "
                            + "WHERE course_id = ?"
                    )
                            .setParameter(1, courseId)
                            .getSingleResult();

                    String courseName = course[0].toString();
                    String paymentMode = course[1].toString(); // MONTHLY / ONE-TIME

                    name = courseName + " (" + paymentMode + ")";

                    hiddenRef = "COURSE_" + enrollmentId;

                    // ===============================
                    // QTY LOGIC (IMPORTANT)
                    // ===============================
                    if ("MONTHLY".equalsIgnoreCase(paymentMode)) {

                        int monthCount = ((Number) em.createNativeQuery(
                                "SELECT COUNT(*) "
                                + "FROM student_fee_installments "
                                + "WHERE enrollment_id = ? "
                                + "AND student_fee_round_payment_master_id = ? "
                                + "AND status = 1"
                        )
                                .setParameter(1, enrollmentId)
                                .setParameter(2, masterId)
                                .getSingleResult()).intValue();

                        qty = (monthCount > 0) ? monthCount : 1;

                    } else {
                        qty = 1;
                    }
                } // ===============================
                // ADDITIONAL (SERVICE / INVENTORY)
                // ===============================
                else if ("ADDITIONAL FEE".equalsIgnoreCase(refType)) {

                    Object[] fee = (Object[]) em.createNativeQuery(
                            "SELECT ft.fee_name, ft.item_id "
                            + "FROM student_additional_fees saf "
                            + "JOIN fee_types ft ON saf.fee_type_id = ft.fee_type_id "
                            + "WHERE saf.student_additional_fees_id = ?"
                    )
                            .setParameter(1, refId)
                            .getSingleResult();

                    String feeName = fee[0].toString();
                    int itemId = fee[1] != null ? Integer.parseInt(fee[1].toString()) : 0;

                    category = (itemId == 0) ? "SERVICE" : "INVENTORY";
                    name = feeName;

                    hiddenRef = "ADD_" + refId;
                }

                // ===============================
                // ADD ROW
                // ===============================
                model.addRow(new Object[]{
                    count++,
                    category,
                    name,
                    qty,
                    GeneralMethods.formatWithComma(paidAmount),
                    hiddenRef
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void revokeRoundPayment(int masterId, String user, String note) {

        if (note == null || note.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Revoke note is required!");
            return;
        }

        EntityManager em = HibernateConfig.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            int row = crp_round_table.getSelectedRow();

            if (row == -1) {
                JOptionPane.showMessageDialog(null, "Please select a payment!");
                return;
            }

// =========================================
// GET VALUES FROM JTABLE
// =========================================
// Column indexes (adjust if needed)
            int COL_DATE = 1;
            int COL_METHOD = 2;
            int COL_TOTAL = 3;
            int COL_CHEQUE = 4;

            String paymentDate = crp_round_table.getValueAt(row, COL_DATE) != null
                    ? crp_round_table.getValueAt(row, COL_DATE).toString()
                    : "";

            String paymentMethod = crp_round_table.getValueAt(row, COL_METHOD) != null
                    ? crp_round_table.getValueAt(row, COL_METHOD).toString()
                    : "";

            double totalAmount = 0;
            double chequeAmount = 0;

            Object totalObj = crp_round_table.getValueAt(row, COL_TOTAL);
            if (totalObj != null && !totalObj.toString().trim().isEmpty()) {
                totalAmount = GeneralMethods.parseCommaNumber(totalObj.toString());
            }

            Object chequeObjs = crp_round_table.getValueAt(row, COL_CHEQUE);
            if (chequeObjs != null && !chequeObjs.toString().trim().isEmpty()) {
                chequeAmount = GeneralMethods.parseCommaNumber(chequeObjs.toString());
            }

// =========================================
// FINAL AMOUNT DECISION
// =========================================
            double finalAmount = (totalAmount > 0) ? totalAmount : chequeAmount;

// =========================================
// CONFIRMATION
// =========================================
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "Are you sure you want to revoke this payment?\n\n"
                    + "Date   : " + paymentDate + "\n"
                    + "Method : " + paymentMethod + "\n"
                    + "Amount : " + GeneralMethods.formatWithComma(finalAmount),
                    "Confirm Revoke",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            if (confirm != JOptionPane.YES_OPTION) {
                tx.rollback();
                return;
            }

            // =========================================
            // 1. CHECK CHEQUE STATUS
            // =========================================
            Object chequeObj = em.createNativeQuery(
                    "SELECT cheque_status FROM student_fee_cheque_details "
                    + "WHERE reference_id=? AND reference_type='ROUND' AND status=1"
            )
                    .setParameter(1, masterId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            String chequeStatus = chequeObj != null ? chequeObj.toString() : null;
            boolean isPendingCheque = "PENDING".equalsIgnoreCase(chequeStatus);

            // =========================================
            // 2. GET ALL ENROLLMENTS FROM MASTER
            // =========================================
            List<Integer> enrollmentList = em.createNativeQuery(
                    "SELECT DISTINCT enrollment_id "
                    + "FROM student_fee_round_payment_master_details "
                    + "WHERE student_fee_round_payment_master_id=? AND status=1"
            )
                    .setParameter(1, masterId)
                    .getResultList();

            // =========================================
            // 3. SOFT DELETE MASTER
            // =========================================
            em.createNativeQuery(
                    "UPDATE student_fee_round_payment_master SET status=0 WHERE student_fee_round_payment_master_id=?"
            )
                    .setParameter(1, masterId)
                    .executeUpdate();

            // =========================================
            // 4. SOFT DELETE DETAILS
            // =========================================
            em.createNativeQuery(
                    "UPDATE student_fee_round_payment_master_details SET status=0 WHERE student_fee_round_payment_master_id=?"
            )
                    .setParameter(1, masterId)
                    .executeUpdate();

            // =========================================
            // 5. SOFT DELETE CHEQUE
            // =========================================
            em.createNativeQuery(
                    "UPDATE student_fee_cheque_details "
                    + "SET status=0 WHERE reference_id=? AND reference_type='ROUND'"
            )
                    .setParameter(1, masterId)
                    .executeUpdate();

            // =========================================
            // 6. STOP HERE IF PENDING CHEQUE
            // =========================================
            if (isPendingCheque) {

//                em.createNativeQuery(
//                        "INSERT INTO student_fee_round_payment_revoke_log "
//                        + "(round_master_id, revoke_date, payment_method, amount, note, user) "
//                        + "VALUES (?, NOW(), 'ROUND', 0, ?, ?)"
//                )
//                        .setParameter(1, masterId)
//                        .setParameter(2, note + " (PENDING CHEQUE - NO RECALC)")
//                        .setParameter(3, user)
//                        .executeUpdate();
                tx.commit();

                String message = "Round Payment Revoked (Pending Cheque)\n\n"
                        + "Date        : " + paymentDate + "\n"
                        + "Method      : " + paymentMethod + "\n"
                        + "Amount      : " + GeneralMethods.formatWithComma(finalAmount);

                JOptionPane.showMessageDialog(null, message);
                return;
            }

            // =========================================
            // 7. SOFT DELETE ADDITIONAL PAYMENTS
            // =========================================
            em.createNativeQuery(
                    "UPDATE student_additional_fee_payments "
                    + "SET status=0 WHERE student_fee_round_payment_master_id=?"
            )
                    .setParameter(1, masterId)
                    .executeUpdate();

            // =========================================
            // 8. SOFT DELETE INSTALLMENTS
            // =========================================
            em.createNativeQuery(
                    "UPDATE student_fee_installments "
                    + "SET status=0 WHERE student_fee_round_payment_master_id=?"
            )
                    .setParameter(1, masterId)
                    .executeUpdate();

            // =========================================
            // 9. RECALCULATE PER ENROLLMENT (IMPORTANT FIX)
            // =========================================
            for (Object eObj : enrollmentList) {

                if (eObj == null) {
                    continue;
                }

                int enrollmentId = ((Number) eObj).intValue();

                double totalPaid = ((Number) em.createNativeQuery(
                        "SELECT COALESCE(SUM(amount_paid),0) "
                        + "FROM student_fee_installments "
                        + "WHERE enrollment_id=? AND status=1"
                )
                        .setParameter(1, enrollmentId)
                        .getSingleResult()).doubleValue();

                double totalFee = ((Number) em.createNativeQuery(
                        "SELECT total_fee FROM student_fee_payments WHERE enrollment_id=?"
                )
                        .setParameter(1, enrollmentId)
                        .getSingleResult()).doubleValue();

                double balance = totalFee - totalPaid;
                String status = (balance <= 0) ? "COMPLETE" : "ACTIVE";

                em.createNativeQuery(
                        "UPDATE student_fee_payments "
                        + "SET total_paid=?, total_balance=?, payment_status=? "
                        + "WHERE enrollment_id=?"
                )
                        .setParameter(1, totalPaid)
                        .setParameter(2, balance)
                        .setParameter(3, status)
                        .setParameter(4, enrollmentId)
                        .executeUpdate();
            }

            double originalAmount = GeneralMethods.parseCommaNumber(crp_round_table.getValueAt(crp_round_table.getSelectedRow(), 3).toString());
            String mode = crp_round_table.getValueAt(crp_round_table.getSelectedRow(), 2).toString();

            String logDescription = String.format(
                    "REVOKED Round Student ID: %d | Original Amount: %.2f | Mode: %s | Reason: %s",
                    selectedStudentIds, originalAmount, mode, note
            );

            logHelper.log(
                    "REVOKE_PAYMENT",
                    masterId,
                    "REVOKE",
                    mode,
                    -originalAmount, // Use negative to indicate a reversal in financial logs
                    logDescription,
                    user
            );

            tx.commit();

            tx.commit();

            String message = "Round Payment Revoked Successfully!\n\n"
                    + "Date        : " + paymentDate + "\n"
                    + "Method      : " + paymentMethod + "\n"
                    + "Amount      : " + GeneralMethods.formatWithComma(finalAmount);

            JOptionPane.showMessageDialog(null, message);

        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error while revoking payment!");

        } finally {
            em.close();
        }
    }

    public void loadChequeDetailsToLabel(int masterId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Object[] row = (Object[]) em.createNativeQuery(
                    "SELECT cheque_no, bank, branch, cheque_date "
                    + "FROM student_fee_cheque_details "
                    + "WHERE reference_id=? AND reference_type='ROUND' AND status=1"
            )
                    .setParameter(1, masterId)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            // =========================================
            // IF CHEQUE EXISTS
            // =========================================
            if (row != null) {

                String chequeNo = row[0] != null ? row[0].toString() : "-";
                String bank = row[1] != null ? row[1].toString() : "-";
                String branch = row[2] != null ? row[2].toString() : "-";
                String date = row[3] != null ? row[3].toString().split(" ")[0] : "-";

                String text = "Cheque No: " + chequeNo
                        + " \t | Bank: " + bank
                        + " \t | Branch: " + branch
                        + " \t | Date: " + date;

                crp_round_cheque_details_label.setText(text);

            } // =========================================
            // IF NO CHEQUE (CASH / CARD)
            // =========================================
            else {
                crp_round_cheque_details_label.setText("No cheque details");
            }

        } catch (Exception e) {
            e.printStackTrace();
            crp_round_cheque_details_label.setText("Error loading cheque details");
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelRound2 = new Classes.PanelRound();
        Main_Lable = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        crp_round_table = new javax.swing.JTable();
        crp_student_name_text = new javax.swing.JTextField();
        buttonGradient6 = new Classes.ButtonGradient();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        crp_round_details_table = new javax.swing.JTable();
        crp_round_cheque_details_label = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        crp_round_revoke_note_textpane = new javax.swing.JEditorPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("REVOKE PAYMENT");

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Main_Lable, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Recent Transactions (Last 3 Days Only)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText(" Student Name");

        crp_round_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Date", "Payment Method", "Total Paid", "Pending Cheque", "ids"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        crp_round_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                crp_round_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(crp_round_table);
        if (crp_round_table.getColumnModel().getColumnCount() > 0) {
            crp_round_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            crp_round_table.getColumnModel().getColumn(4).setHeaderValue("Cheque");
            crp_round_table.getColumnModel().getColumn(5).setMinWidth(50);
            crp_round_table.getColumnModel().getColumn(5).setPreferredWidth(50);
            crp_round_table.getColumnModel().getColumn(5).setMaxWidth(50);
        }

        crp_student_name_text.setEditable(false);
        crp_student_name_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        crp_student_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crp_student_name_textActionPerformed(evt);
            }
        });
        crp_student_name_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                crp_student_name_textKeyTyped(evt);
            }
        });

        buttonGradient6.setText("REVOKE\n PAYMENT");
        buttonGradient6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(crp_student_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crp_student_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Total Due Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        crp_round_details_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Category", "Service/Item", "Qty", "Paid Amount", "ids"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        crp_round_details_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                crp_round_details_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(crp_round_details_table);
        if (crp_round_details_table.getColumnModel().getColumnCount() > 0) {
            crp_round_details_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            crp_round_details_table.getColumnModel().getColumn(2).setPreferredWidth(200);
            crp_round_details_table.getColumnModel().getColumn(5).setMinWidth(50);
            crp_round_details_table.getColumnModel().getColumn(5).setPreferredWidth(50);
            crp_round_details_table.getColumnModel().getColumn(5).setMaxWidth(50);
        }

        crp_round_cheque_details_label.setFont(new java.awt.Font("Roboto Medium", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 775, Short.MAX_VALUE)
                    .addComponent(crp_round_cheque_details_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crp_round_cheque_details_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Revoke Note", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jScrollPane4.setViewportView(crp_round_revoke_note_textpane);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void crp_round_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_crp_round_tableMouseClicked

        try {

            int row = crp_round_table.getSelectedRow();
            if (row == -1) {
                return;
            }

            int masterId = Integer.parseInt(crp_round_table.getValueAt(row, 5).toString());

            if (crp_round_table.getValueAt(row, 2).toString().equalsIgnoreCase("CHEQUE")) {
                loadChequeDetailsToLabel(masterId);
            }

            loadRoundPaymentDetails(masterId);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_crp_round_tableMouseClicked

    private void buttonGradient6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient6ActionPerformed

        DefaultTableModel model = (DefaultTableModel) crp_round_table.getModel();

        int roundMasterId = Integer.parseInt(crp_round_table.getValueAt(crp_round_table.getSelectedRow(), 5).toString());
        String note = crp_round_revoke_note_textpane.getText();
        revokeRoundPayment(roundMasterId, username, note);

    }//GEN-LAST:event_buttonGradient6ActionPerformed

    private void crp_round_details_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_crp_round_details_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_crp_round_details_tableMouseClicked

    private void crp_student_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crp_student_name_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_crp_student_name_textActionPerformed

    private void crp_student_name_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_crp_student_name_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_crp_student_name_textKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Cancel_Round_Payment dialog
                    = new Cancel_Round_Payment(frame, 0, "", "", "");

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private Classes.ButtonGradient buttonGradient6;
    private javax.swing.JLabel crp_round_cheque_details_label;
    private javax.swing.JTable crp_round_details_table;
    public static javax.swing.JEditorPane crp_round_revoke_note_textpane;
    private javax.swing.JTable crp_round_table;
    private javax.swing.JTextField crp_student_name_text;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private Classes.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables

}
