package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.styleDateChooser;
import JPA_DAO.Settings.CourseDAO;
import Panels.Fees_Management;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;

public class Admission_Fee_Payment extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Admission_Fee_Payment.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    private Fees_Management parentForm;

    CourseDAO dao = new CourseDAO();

    int courseID = 0;
    private Integer studentId;
    private int selectedStudentIds;
    private int selectedEnrollmentId;
    private double admiFee = 0.0;
    String username;
    String role;

    public Admission_Fee_Payment(Window parent, int studentId, Fees_Management parentForm, int selectedStudentIds, int selectedEnrollmentId, double admiFee, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.studentId = studentId;
        this.parentForm = parentForm;
        this.selectedStudentIds = selectedStudentIds;
        this.selectedEnrollmentId = selectedEnrollmentId;
        this.admiFee = admiFee;
        this.username = username;
        this.role = role;
        initComponents();

        ad_admi_payment_date.setDate(new Date());
        ad_admi_total_paid_Textfield.requestFocus();
        jTabbedPane1.setSelectedIndex(0);
        if (jTabbedPane1.getSelectedIndex() == 0) {
            styleDateChooser.applyDarkTheme(ad_admi_payment_date);
            styleDateChooser.applyDarkTheme(ad_admi_cheq_cheque_date);
        } else {
            styleDateChooser.applyDarkTheme(ad_admi_payment_date);
            styleDateChooser.applyDarkTheme(ad_admi_cheq_cheque_date);
        }

        ad_admi_total_paid_Textfield.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        ad_admi_total_paid_Textfield.putClientProperty("JComponent.focusWidth", 2);

        ad_admi_cheq_cheque_number.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        ad_admi_cheq_cheque_number.putClientProperty("JComponent.focusWidth", 2);

        ad_admi_cheq_cheque_amount.putClientProperty("JComponent.outline", new Color(255, 160, 41));
        ad_admi_cheq_cheque_amount.putClientProperty("JComponent.focusWidth", 2);

        styleDateChooser.applyDarkTheme(ad_admi_payment_date);
        styleDateChooser.applyDarkTheme(ad_admi_cheq_cheque_date);

        ad_admi_total_fee_Textfield.setText(GeneralMethods.formatWithComma(admiFee));
        ad_admi_total_paid_Textfield.setText(GeneralMethods.formatWithComma(admiFee));
        ad_admi_total_paid_Textfield.requestFocus();

        ad_admi_cheq_full_fees_Textfield.setText(GeneralMethods.formatWithComma(admiFee));
        ad_admi_cheq_cheque_amount.setText(GeneralMethods.formatWithComma(admiFee));

        updateAdmissionStatusLabel(selectedEnrollmentId, firstName_label8);

        JComboPopulatesBankInfo();

    }

    private void JComboPopulatesBankInfo() {
        // Medicine brand combo
        ad_admi_cheq_cheque_bank.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = ad_admi_cheq_cheque_bank.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(ad_admi_cheq_cheque_bank, "bank_names", "bank_names_srilanka", input);
            }

        });
        setupComboSelectionListener(ad_admi_cheq_cheque_bank, ad_admi_cheq_cheque_branch);

        new ChequeNumberFormatter(ad_admi_cheq_cheque_number, ad_admi_cheq_cheque_bank, ad_admi_cheq_cheque_branch);
        PlainDocument doc = (PlainDocument) ad_admi_cheq_cheque_number.getDocument();
        doc.setDocumentFilter(new NumberOnlyFilter());
    }

    private boolean itemSelectedByUser = false;

    public void setupComboSelectionListener(JComboBox<String> comboBox, JComponent nextFocusComponent) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUser = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (itemSelectedByUser) {
                    Object selected = comboBox.getSelectedItem();
                    if (selected != null) {
                        String selectedValue = selected.toString().trim();
                        if (!selectedValue.isEmpty() && isValueFromList(comboBox, selectedValue)) {
                            nextFocusComponent.requestFocus();
                        }
                    }
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser = false;
            }
        });

        // Detect user selection from keyboard (Enter) or mouse (click)
        comboBox.addActionListener(e -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUser = true;
            }
        });

    }

    private boolean isValueFromList(JComboBox<String> comboBox, String value) {
        ComboBoxModel<String> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            String item = model.getElementAt(i);
            if (item.equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }

    public void saveAdmissionFee(String paymentMethod, String paymentAmont) {

        EntityManager em = HibernateConfig.getEntityManager();
        em.getTransaction().begin();

        try {

            System.out.println("METHOD ADMI - " + admiFee);

            if (selectedStudentIds == 0 || selectedEnrollmentId == 0) {
                JOptionPane.showMessageDialog(null,
                        "Invalid Student or Enrollment selected!");
                em.getTransaction().rollback();
                return;
            }

            // ============================
            // 1. GET ADMISSION FEE
            // ============================
            DefaultTableModel masterModel
                    = (DefaultTableModel) Fees_Management.fm_fees_course_table.getModel();

            //  int selectedRow = Fees_Management.fm_fees_course_table.getSelectedRow();
            // int admissionFee = GeneralMethods.parseCommaNumber(admiFee + "");
            // ============================
            // 2. GET ENTERED AMOUNT
            // ============================
            double paidAmount = GeneralMethods.parseCommaNumber(paymentAmont);

            // ============================
            // 🔥 VALIDATION
            // ============================
            if (paidAmount < admiFee) {
                JOptionPane.showMessageDialog(null,
                        "Cannot pay less than Admission Fee!");
                em.getTransaction().rollback();
                return;
            }

            if (paidAmount > admiFee) {
                JOptionPane.showMessageDialog(null,
                        "Cannot pay more than Admission Fee!");
                em.getTransaction().rollback();
                return;
            }

            // ============================
            // 🔥 CHECK DUPLICATE ADMISSION
            // ============================
            Object result = em.createNativeQuery(
                    "SELECT COUNT(*) FROM student_fee_payments "
                    + "WHERE enrollment_id = ? AND remarks = 'ADMISSION_FEE' AND status = 1"
            )
                    .setParameter(1, selectedEnrollmentId)
                    .getSingleResult();

            int count = ((Number) result).intValue();

            if (count > 0) {
                JOptionPane.showMessageDialog(null,
                        "Admission Fee already paid for this student!");
                em.getTransaction().rollback();
                return;
            }

            // ============================
            // 3. CREATE PAYMENT MASTER
            // ============================
            em.createNativeQuery(
                    "INSERT INTO student_fee_payments "
                    + "(student_id, enrollment_id, total_fee, total_paid, total_balance, "
                    + "payment_type, course_type, payment_status, remarks, created_at, last_mofidied, user, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), NOW(), ?, 1)"
            )
                    .setParameter(1, selectedStudentIds)
                    .setParameter(2, selectedEnrollmentId)
                    .setParameter(3, admiFee)
                    .setParameter(4, paymentMethod.equalsIgnoreCase("CHEQUE") ? 0 : paidAmount)
                    .setParameter(5, admiFee - (paymentMethod.equalsIgnoreCase("CHEQUE") ? 0 : paidAmount))
                    .setParameter(6, "ADMISSION")
                    .setParameter(7, "ADMISSION")
                    .setParameter(8, paymentMethod.equalsIgnoreCase("CHEQUE") ? "PENDING" : "PAID")
                    .setParameter(9, "ADMISSION_FEE")
                    .setParameter(10, username)
                    .executeUpdate();

            // ============================
            // 4. GET LAST INSERT ID
            // ============================
            int studentFeePaymentId = ((Number) em.createNativeQuery(
                    "SELECT LAST_INSERT_ID()"
            ).getSingleResult()).intValue();

            // ============================
            // 5. INSERT INSTALLMENT (SINGLE)
            // ============================
            em.createNativeQuery(
                    "INSERT INTO student_fee_installments "
                    + "(student_fee_payments_id, enrollment_id, installment_no, amount_paid, "
                    + "payment_date, payment_method, payment_type, remarks, status) "
                    + "VALUES (?, ?, 1, ?, NOW(), ?, ?, ?, 1)"
            )
                    .setParameter(1, studentFeePaymentId)
                    .setParameter(2, selectedEnrollmentId)
                    .setParameter(3, paidAmount)
                    .setParameter(4, paymentMethod)
                    .setParameter(5, "ADMISSION")
                    .setParameter(6, "ADMISSION_FEE")
                    .executeUpdate();

            // ============================
            // 6. IF CHEQUE → SAVE DETAILS
            // ============================
            if (paymentMethod.equalsIgnoreCase("CHEQUE")) {

                int installmentId = ((Number) em.createNativeQuery(
                        "SELECT LAST_INSERT_ID()"
                ).getSingleResult()).intValue();

                em.createNativeQuery(
                        "INSERT INTO student_fee_cheque_details "
                        + "(reference_id, reference_type, category, cheque_no, bank, branch, cheque_date, cheque_amount, cheque_status, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', 1)"
                )
                        .setParameter(1, selectedEnrollmentId)
                        .setParameter(2, "ADMISSION")
                        .setParameter(3, "STUDENT")
                        .setParameter(4, ad_admi_cheq_cheque_number.getText())
                        .setParameter(5, ad_admi_cheq_cheque_bank.getEditor().getItem().toString())
                        .setParameter(6, ad_admi_cheq_cheque_branch.getText())
                        .setParameter(7, ad_admi_cheq_cheque_date.getDate())
                        .setParameter(8, paidAmount)
                        .executeUpdate();
            }

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(null, "Admission Fee Saved Successfully!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();   // 🔥 IMPORTANT
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        } finally {
            em.close();   // ✅ always here
        }
    }

    public void updateAdmissionStatusLabel(int enrollmentId, JLabel label) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            // ============================================
            // 1. CASH / CARD (from installments)
            // ============================================
            List<Object[]> cashList = em.createNativeQuery(
                    "SELECT amount_paid, payment_method "
                    + "FROM student_fee_installments "
                    + "WHERE enrollment_id = ? "
                    + "AND payment_type = 'ADMISSION' "
                    + "AND status = 1"
            )
                    .setParameter(1, enrollmentId)
                    .getResultList();

            // ============================================
            // 2. CHEQUE (NEW CORRECT LOGIC)
            // ============================================
            List<Object[]> chequeList = em.createNativeQuery(
                    "SELECT cheque_amount, cheque_status "
                    + "FROM student_fee_cheque_details "
                    + "WHERE reference_type = 'ADMISSION' "
                    + "AND reference_id = ? "
                    + "AND category = 'STUDENT' "
                    + "AND status = 1"
            )
                    .setParameter(1, enrollmentId)
                    .getResultList();

            boolean isPaid = false;
            boolean hasPendingCheque = false;
            String paymentMethodText = "";

            // ============================================
            // 3. CHECK CASH / CARD FIRST
            // ============================================
            for (Object[] row : cashList) {

                int amount = ((Number) row[0]).intValue();
                String method = row[1] != null ? row[1].toString() : "";

                if ((method.equalsIgnoreCase("CASH") || method.equalsIgnoreCase("CARD")) && amount > 0) {
                    isPaid = true;
                    paymentMethodText = method;
                    break;
                }
            }

            // ============================================
            // 4. CHECK CHEQUE
            // ============================================
            if (!isPaid) {

                for (Object[] row : chequeList) {

                    int amount = ((Number) row[0]).intValue();
                    String chequeStatus = row[1] != null ? row[1].toString() : "";

                    if (amount <= 0) {
                        continue;
                    }

                    if ("CLEARED".equalsIgnoreCase(chequeStatus)) {
                        isPaid = true;
                        paymentMethodText = "Cheque";
                        break;
                    } else if ("PENDING".equalsIgnoreCase(chequeStatus)) {
                        hasPendingCheque = true;
                        paymentMethodText = "Cheque";
                    }
                }
            }

            // ============================================
            // 5. FINAL LABEL
            // ============================================
            if (isPaid) {
                label.setText("Admission Paid (" + paymentMethodText + ")");
                label.setForeground(new java.awt.Color(0, 153, 0)); // green
            } else if (hasPendingCheque) {
                label.setText("Admission Pending (Cheque)");
                label.setForeground(new java.awt.Color(204, 153, 0)); // yellow
            } else {
                label.setText("Admission Not Paid");
                label.setForeground(java.awt.Color.RED);
            }

        } catch (Exception e) {
            e.printStackTrace();
            label.setText("Admission Status Unknown");
            label.setForeground(java.awt.Color.GRAY);
        } finally {
            em.close();
        }
    }

//    public void updateAdmissionStatusLabel(int enrollmentId, JLabel label) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//
//            List<Object[]> list = em.createNativeQuery(
//                    "SELECT sfi.amount_paid, sfi.payment_method, scd.cheque_status "
//                    + "FROM student_fee_installments sfi "
//                    + "LEFT JOIN student_fee_cheque_details scd "
//                    + "ON sfi.student_fee_installments_id = scd.student_fee_installments_id "
//                    + "JOIN student_fee_payments sfp "
//                    + "ON sfi.student_fee_payments_id = sfp.student_fee_payments_id "
//                    + "WHERE sfp.enrollment_id = ? "
//                    + "AND sfi.payment_type = 'ADMISSION' "
//                    + "AND sfi.status = 1"
//            )
//                    .setParameter(1, enrollmentId)
//                    .getResultList();
//
//            boolean isPaid = false;
//            boolean hasPendingCheque = false;
//            String paymentMethodText = "";
//
//            for (Object[] row : list) {
//
//                int amount = ((Number) row[0]).intValue();
//                String method = row[1] != null ? row[1].toString() : "";
//                String chequeStatus = row[2] != null ? row[2].toString() : "";
//
//                // ============================
//                // CASH / CARD → PAID
//                // ============================
//                if ((method.equalsIgnoreCase("CASH") || method.equalsIgnoreCase("CARD")) && amount > 0) {
//                    isPaid = true;
//                    paymentMethodText = method; // show Cash or Card
//                    break; // no need to check more if already paid
//                }
//
//                // ============================
//                // CHEQUE LOGIC
//                // ============================
//                if (method.equalsIgnoreCase("CHEQUE")) {
//                    if ("CLEARED".equalsIgnoreCase(chequeStatus)) {
//                        isPaid = true;
//                        paymentMethodText = "Cheque"; // show Cheque
//                        break;
//                    } else if ("PENDING".equalsIgnoreCase(chequeStatus)) {
//                        hasPendingCheque = true;
//                        paymentMethodText = "Cheque";
//                    }
//                    // RETURNED / BOUNCED / CANCELLED → ignore
//                }
//            }
//
//            // ============================
//            // 🔥 SET LABEL TEXT & COLOR
//            // ============================
//            if (isPaid) {
//                label.setText("Admission Paid (" + paymentMethodText + ")");
//                label.setForeground(new java.awt.Color(0, 153, 0)); // green
//            } else if (hasPendingCheque) {
//                label.setText("Admission Pending (" + paymentMethodText + ")");
//                label.setForeground(new java.awt.Color(204, 153, 0)); // yellow
//            } else {
//                label.setText("Admission Not Paid");
//                label.setForeground(java.awt.Color.RED);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            label.setText("Admission Status Unknown");
//            label.setForeground(java.awt.Color.GRAY);
//        } finally {
//            em.close();
//        }
//    }
    public void deleteAdmissionFee(int enrollmentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            // ============================
            // 1. GET PAYMENT ID
            // ============================
            Object paymentIdObj = em.createNativeQuery(
                    "SELECT student_fee_payments_id "
                    + "FROM student_fee_payments "
                    + "WHERE enrollment_id = ? "
                    + "AND remarks = 'ADMISSION_FEE' "
                    + "AND status = 1"
            )
                    .setParameter(1, enrollmentId)
                    .getSingleResult();

            int paymentId = ((Number) paymentIdObj).intValue();

            // ============================
            // 2. UPDATE CHEQUE DETAILS
            // ============================
            em.createNativeQuery(
                    "UPDATE student_fee_cheque_details scd "
                    + "JOIN student_fee_installments sfi "
                    + "ON scd.student_fee_installments_id = sfi.student_fee_installments_id "
                    + "SET scd.status = 0 "
                    + "WHERE sfi.student_fee_payments_id = ?"
            )
                    .setParameter(1, paymentId)
                    .executeUpdate();

            // ============================
            // 3. UPDATE INSTALLMENTS
            // ============================
            em.createNativeQuery(
                    "UPDATE student_fee_installments "
                    + "SET status = 0 "
                    + "WHERE student_fee_payments_id = ?"
            )
                    .setParameter(1, paymentId)
                    .executeUpdate();

            // ============================
            // 4. UPDATE MASTER
            // ============================
            em.createNativeQuery(
                    "UPDATE student_fee_payments "
                    + "SET status = 0 "
                    + "WHERE student_fee_payments_id = ?"
            )
                    .setParameter(1, paymentId)
                    .executeUpdate();

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(null, "Admission Fee Deleted Successfully!");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());

        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        ad_admi_total_paid_Textfield = new javax.swing.JTextField();
        firstName_label7 = new javax.swing.JLabel();
        sup_payment_cash_label = new javax.swing.JLabel();
        ad_admi_total_fee_Textfield = new javax.swing.JTextField();
        buttonGradient2 = new Classes.ButtonGradient();
        jLabel14 = new javax.swing.JLabel();
        ad_admi_payment_method_combo = new javax.swing.JComboBox<>();
        buttonGradient4 = new Classes.ButtonGradient();
        jPanel10 = new javax.swing.JPanel();
        ad_admi_cheq_cheque_number = new javax.swing.JTextField();
        ad_admi_cheq_cheque_bank = new javax.swing.JComboBox<>();
        ad_admi_cheq_cheque_branch = new javax.swing.JTextField();
        ad_admi_cheq_cheque_amount = new javax.swing.JTextField();
        ad_admi_cheq_cheque_date = new com.toedter.calendar.JDateChooser();
        sup_payment_cheque_label = new javax.swing.JLabel();
        ad_admi_cheq_full_fees_Textfield = new javax.swing.JTextField();
        ad_admi_cheq_cheque_status = new javax.swing.JComboBox<>();
        buttonGradient3 = new Classes.ButtonGradient();
        buttonGradient5 = new Classes.ButtonGradient();
        firstName_label8 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        ad_admi_payment_date = new com.toedter.calendar.JDateChooser();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 375, Short.MAX_VALUE)
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Admission Fee Payment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jTabbedPane1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        ad_admi_total_paid_Textfield.setEditable(false);
        ad_admi_total_paid_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_total_paid_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_total_paid_TextfieldActionPerformed(evt);
            }
        });
        ad_admi_total_paid_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ad_admi_total_paid_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_total_paid_TextfieldKeyReleased(evt);
            }
        });

        firstName_label7.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label7.setText("Paid Amount");

        sup_payment_cash_label.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cash_label.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cash_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cash_label.setText("Admission Fee");

        ad_admi_total_fee_Textfield.setEditable(false);
        ad_admi_total_fee_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_total_fee_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_total_fee_TextfieldActionPerformed(evt);
            }
        });
        ad_admi_total_fee_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_total_fee_TextfieldKeyReleased(evt);
            }
        });

        buttonGradient2.setText("DELETE");
        buttonGradient2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("Payment Method");

        ad_admi_payment_method_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        ad_admi_payment_method_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "CARD" }));

        buttonGradient4.setText("SAVE");
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(sup_payment_cash_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ad_admi_total_fee_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(firstName_label7, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ad_admi_total_paid_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGap(27, 27, 27)
                        .addComponent(ad_admi_payment_method_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ad_admi_payment_method_combo)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ad_admi_total_fee_Textfield)
                    .addComponent(sup_payment_cash_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ad_admi_total_paid_Textfield)
                    .addComponent(firstName_label7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 64, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cash / Card", jPanel7);

        ad_admi_cheq_cheque_number.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_cheq_cheque_number.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_cheq_cheque_numberActionPerformed(evt);
            }
        });
        ad_admi_cheq_cheque_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_cheq_cheque_numberKeyReleased(evt);
            }
        });

        ad_admi_cheq_cheque_bank.setEditable(true);
        ad_admi_cheq_cheque_bank.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_cheq_cheque_bank.setMinimumSize(new java.awt.Dimension(83, 30));
        ad_admi_cheq_cheque_bank.setPreferredSize(new java.awt.Dimension(72, 30));
        ad_admi_cheq_cheque_bank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_cheq_cheque_bankActionPerformed(evt);
            }
        });
        ad_admi_cheq_cheque_bank.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_cheq_cheque_bankKeyReleased(evt);
            }
        });

        ad_admi_cheq_cheque_branch.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_cheq_cheque_branch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_cheq_cheque_branchActionPerformed(evt);
            }
        });
        ad_admi_cheq_cheque_branch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_cheq_cheque_branchKeyReleased(evt);
            }
        });

        ad_admi_cheq_cheque_amount.setEditable(false);
        ad_admi_cheq_cheque_amount.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_cheq_cheque_amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_cheq_cheque_amountActionPerformed(evt);
            }
        });
        ad_admi_cheq_cheque_amount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_cheq_cheque_amountKeyReleased(evt);
            }
        });

        ad_admi_cheq_cheque_date.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N

        sup_payment_cheque_label.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cheque_label.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cheque_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cheque_label.setText("Admission Fee");

        ad_admi_cheq_full_fees_Textfield.setEditable(false);
        ad_admi_cheq_full_fees_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_cheq_full_fees_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_cheq_full_fees_TextfieldActionPerformed(evt);
            }
        });
        ad_admi_cheq_full_fees_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_cheq_full_fees_TextfieldKeyReleased(evt);
            }
        });

        ad_admi_cheq_cheque_status.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        ad_admi_cheq_cheque_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending" }));
        ad_admi_cheq_cheque_status.setMinimumSize(new java.awt.Dimension(83, 30));
        ad_admi_cheq_cheque_status.setPreferredSize(new java.awt.Dimension(72, 30));
        ad_admi_cheq_cheque_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ad_admi_cheq_cheque_statusActionPerformed(evt);
            }
        });
        ad_admi_cheq_cheque_status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ad_admi_cheq_cheque_statusKeyReleased(evt);
            }
        });

        buttonGradient3.setText("DELETE");
        buttonGradient3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient3ActionPerformed(evt);
            }
        });

        buttonGradient5.setText("SAVE");
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ad_admi_cheq_cheque_number)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(sup_payment_cheque_label, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ad_admi_cheq_full_fees_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(ad_admi_cheq_cheque_bank, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ad_admi_cheq_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addComponent(ad_admi_cheq_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ad_admi_cheq_cheque_date, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ad_admi_cheq_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ad_admi_cheq_full_fees_Textfield)
                    .addComponent(sup_payment_cheque_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ad_admi_cheq_cheque_number, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ad_admi_cheq_cheque_bank, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ad_admi_cheq_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ad_admi_cheq_cheque_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ad_admi_cheq_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ad_admi_cheq_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(84, 84, 84))
        );

        jTabbedPane1.addTab("Cheque", jPanel10);

        firstName_label8.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label8.setFont(new java.awt.Font("Roboto Medium", 0, 14)); // NOI18N
        firstName_label8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setText("Select Payment Date");

        ad_admi_payment_date.setForeground(new java.awt.Color(204, 204, 204));
        ad_admi_payment_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(firstName_label8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ad_admi_payment_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ad_admi_payment_date, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstName_label8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ad_admi_total_paid_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_total_paid_TextfieldActionPerformed

    }//GEN-LAST:event_ad_admi_total_paid_TextfieldActionPerformed

    private void ad_admi_total_paid_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_total_paid_TextfieldKeyPressed

    }//GEN-LAST:event_ad_admi_total_paid_TextfieldKeyPressed

    private void ad_admi_total_paid_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_total_paid_TextfieldKeyReleased

    }//GEN-LAST:event_ad_admi_total_paid_TextfieldKeyReleased

    private void ad_admi_total_fee_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_total_fee_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_total_fee_TextfieldActionPerformed

    private void ad_admi_total_fee_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_total_fee_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_total_fee_TextfieldKeyReleased

    private void buttonGradient2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient2ActionPerformed

        if (firstName_label8.getText().equalsIgnoreCase("Admission Not Paid")) {
            JOptionPane.showMessageDialog(null, "No admission payment found!");
            return;
        }

        deleteAdmissionFee(selectedEnrollmentId);

        // ✅ LOG: Admission Fee Payment Deletion
        logHelper.log(
                "ADMISSION_PAYMENT",
                selectedEnrollmentId,
                "ADMISSION DELETE",
                "ENROLL_ID: " + selectedEnrollmentId,
                0.0,
                "Admission payment record deleted/reset for Enrollment ID: " + selectedEnrollmentId,
                username
        );
        updateAdmissionStatusLabel(selectedEnrollmentId, firstName_label8);

    }//GEN-LAST:event_buttonGradient2ActionPerformed

    private void ad_admi_cheq_cheque_numberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_numberActionPerformed
        ad_admi_cheq_cheque_amount.requestFocus();
    }//GEN-LAST:event_ad_admi_cheq_cheque_numberActionPerformed

    private void ad_admi_cheq_cheque_numberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_numberKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_numberKeyReleased

    private void ad_admi_cheq_cheque_bankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_bankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_bankActionPerformed

    private void ad_admi_cheq_cheque_bankKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_bankKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_bankKeyReleased

    private void ad_admi_cheq_cheque_branchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_branchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_branchActionPerformed

    private void ad_admi_cheq_cheque_branchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_branchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_branchKeyReleased

    private void ad_admi_cheq_cheque_amountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_amountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_amountActionPerformed

    private void ad_admi_cheq_cheque_amountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_amountKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_amountKeyReleased

    private void ad_admi_cheq_full_fees_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_cheq_full_fees_TextfieldActionPerformed
        ad_admi_cheq_cheque_number.requestFocus();
    }//GEN-LAST:event_ad_admi_cheq_full_fees_TextfieldActionPerformed

    private void ad_admi_cheq_full_fees_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_cheq_full_fees_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_full_fees_TextfieldKeyReleased

    private void ad_admi_cheq_cheque_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_statusActionPerformed

    private void ad_admi_cheq_cheque_statusKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ad_admi_cheq_cheque_statusKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_ad_admi_cheq_cheque_statusKeyReleased

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed

        if (firstName_label8.getText().equalsIgnoreCase("Admission Not Paid")) {
            JOptionPane.showMessageDialog(null, "No admission payment found!");
            return;
        }

        deleteAdmissionFee(selectedEnrollmentId);

        // ✅ LOG: Admission Cheque Deletion
        logHelper.log(
                "ADMISSION_PAYMENT",
                selectedEnrollmentId,
                "ADMISSION DELETE",
                "ENROLL_ID: " + selectedEnrollmentId,
                0.0,
                "Cheque admission payment record was deleted/reversed for Enrollment ID: " + selectedEnrollmentId,
                username
        );

        updateAdmissionStatusLabel(selectedEnrollmentId, firstName_label8);

    }//GEN-LAST:event_buttonGradient3ActionPerformed

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        double admiFee = GeneralMethods.parseCommaNumber(ad_admi_total_fee_Textfield.getText());
        double admiPayFee = GeneralMethods.parseCommaNumber(ad_admi_total_paid_Textfield.getText());

        if (admiPayFee < admiFee || admiPayFee > admiFee) {
            JOptionPane.showMessageDialog(null, "Admission Fee not matched", "Not Matched", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveAdmissionFee(ad_admi_payment_method_combo.getSelectedItem().toString(), ad_admi_total_paid_Textfield.getText());

        // ✅ LOG: Admission Fee Payment
        logHelper.log(
                "ADMISSION_PAYMENT",
                selectedEnrollmentId,
                "ADMISSION PAID",
                ad_admi_payment_method_combo.getSelectedItem().toString(), // Reference is the Mode
                admiPayFee, // The actual amount paid
                String.format("Admission payment received via %s. Total: %.2f",
                        ad_admi_payment_method_combo.getSelectedItem().toString(), admiPayFee),
                username
        );

        updateAdmissionStatusLabel(selectedEnrollmentId, firstName_label8);

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed

        double admiFee = GeneralMethods.parseCommaNumber(ad_admi_cheq_full_fees_Textfield.getText());
        double admiPayFee = GeneralMethods.parseCommaNumber(ad_admi_cheq_cheque_amount.getText());

        if (admiPayFee < admiFee || admiPayFee > admiFee) {
            JOptionPane.showMessageDialog(null, "Admission Fee not matched", "Not Matched", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check mandatory fields
        if (ad_admi_cheq_cheque_number.getText().equals("")
                || ad_admi_cheq_cheque_bank.getEditor().getItem().toString().equals("")
                || ad_admi_cheq_cheque_amount.getText().equalsIgnoreCase("")
                || ad_admi_cheq_cheque_amount.getText().equalsIgnoreCase("0")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty or 0", "Not Found", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (ad_admi_cheq_cheque_date.getDate() == null) {
            JOptionPane.showMessageDialog(null, "Select cheque date", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveAdmissionFee("CHEQUE", ad_admi_cheq_cheque_amount.getText());

        // ✅ LOG: Admission Cheque Payment
        logHelper.log(
                "ADMISSION_PAYMENT",
                selectedEnrollmentId,
                "ADMISSION PAID",
                "Cheque Payment",
                admiPayFee,
                String.format("Cheque Received | Bank: %s | No: %s | Date: %s",
                        ad_admi_cheq_cheque_bank.getEditor().getItem().toString(),
                        ad_admi_cheq_cheque_number.getText(),
                        ad_admi_cheq_cheque_date.getDate()),
                username
        );
        updateAdmissionStatusLabel(selectedEnrollmentId, firstName_label8);

    }//GEN-LAST:event_buttonGradient5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Admission_Fee_Payment dialog
                    = new Admission_Fee_Payment(frame, 0, null, 0, 0, 0, "", "");

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JTextField ad_admi_cheq_cheque_amount;
    public static javax.swing.JComboBox<String> ad_admi_cheq_cheque_bank;
    public static javax.swing.JTextField ad_admi_cheq_cheque_branch;
    public static com.toedter.calendar.JDateChooser ad_admi_cheq_cheque_date;
    public static javax.swing.JTextField ad_admi_cheq_cheque_number;
    public static javax.swing.JComboBox<String> ad_admi_cheq_cheque_status;
    public static javax.swing.JTextField ad_admi_cheq_full_fees_Textfield;
    public static com.toedter.calendar.JDateChooser ad_admi_payment_date;
    public static javax.swing.JComboBox<String> ad_admi_payment_method_combo;
    public static javax.swing.JTextField ad_admi_total_fee_Textfield;
    public static javax.swing.JTextField ad_admi_total_paid_Textfield;
    private Classes.ButtonGradient buttonGradient2;
    private Classes.ButtonGradient buttonGradient3;
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private javax.swing.JLabel firstName_label7;
    private javax.swing.JLabel firstName_label8;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel sup_payment_cash_label;
    private javax.swing.JLabel sup_payment_cheque_label;
    // End of variables declaration//GEN-END:variables

}
