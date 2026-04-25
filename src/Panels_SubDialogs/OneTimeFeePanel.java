package Panels_SubDialogs;

import Additional.LedgerDAO;
import Classes.ChequeNumberFormatter;
import Classes.GeneralMethods;
import Classes.GradientButton;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.ModernDialog;
import Classes.NumberOnlyFilter;
import Classes.styleDateChooser;
import JPA_DAO.Student_Management.StudentFeeInstallmentsDAO;
import Panels.Fees_Management;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.PlainDocument;

public class OneTimeFeePanel extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    String username;
    String role;

    public OneTimeFeePanel(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        fm_fees_oneTime_table.getColumnModel().getColumn(3).setMinWidth(0);
        fm_fees_oneTime_table.getColumnModel().getColumn(3).setMaxWidth(0);
        fm_fees_oneTime_table.getColumnModel().getColumn(3).setWidth(0);

        fm_fees_oneTime_table.getColumnModel().getColumn(4).setMinWidth(0);
        fm_fees_oneTime_table.getColumnModel().getColumn(4).setMaxWidth(0);
        fm_fees_oneTime_table.getColumnModel().getColumn(4).setWidth(0);

       // fm_fees_oneTime_payment_date.setDate(new Date());
        // fm_fees_oneTime_payment_date.setDate(new Date());

//        styleDateChooser.applyDarkTheme(fm_fees_oneTime_payment_date);
//        styleDateChooser.applyDarkTheme(fm_fees_cheq_cheque_date);
//
//        fm_fees_oneTime_total_paid_Textfield.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        fm_fees_oneTime_total_paid_Textfield.putClientProperty("JComponent.focusWidth", 2);
//
//        fm_fees_cheq_cheque_number.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        fm_fees_cheq_cheque_number.putClientProperty("JComponent.focusWidth", 2);
//
//        fm_fees_cheq_cheque_amount.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        fm_fees_cheq_cheque_amount.putClientProperty("JComponent.focusWidth", 2);
//
//        fm_fees_cheq_cheque_bank.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        fm_fees_cheq_cheque_bank.putClientProperty("JComponent.focusWidth", 2);
//
//        fm_fees_oneTime_payment_method_combo.putClientProperty("JComponent.outline", new Color(255, 160, 41));
//        fm_fees_oneTime_payment_method_combo.putClientProperty("JComponent.focusWidth", 2);

       // JComboPopulatesBankInfo();
    }

//    private void JComboPopulatesBankInfo() {
//        // Medicine brand combo
//        fm_fees_cheq_cheque_bank.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
//            public void keyReleased(KeyEvent e) {
//                String input = fm_fees_cheq_cheque_bank.getEditor().getItem().toString();
//                generalMethods.loadMatchingComboItems(fm_fees_cheq_cheque_bank, "bank_names", "bank_names_srilanka", input);
//            }
//
//        });
//        setupComboSelectionListener(fm_fees_cheq_cheque_bank, fm_fees_cheq_cheque_branch);
//
//        new ChequeNumberFormatter(fm_fees_cheq_cheque_number, fm_fees_cheq_cheque_bank, fm_fees_cheq_cheque_branch);
//        PlainDocument doc = (PlainDocument) fm_fees_cheq_cheque_number.getDocument();
//        doc.setDocumentFilter(new NumberOnlyFilter());
//    }

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

    

    public void deleteOneTimeOrRoundPayment(int enrollmentId, String paymentDate, double amount) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            // ============================
            // 1. FIND INSTALLMENT ID + PAYMENT
            // ============================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT sfi.student_fee_installments_id, "
                    + "sfp.student_fee_payments_id, "
                    + "sfp.total_paid, sfp.total_fee, "
                    + "sfi.payment_method "
                    + "FROM student_fee_installments sfi "
                    + "JOIN student_fee_payments sfp "
                    + "ON sfi.student_fee_payments_id = sfp.student_fee_payments_id "
                    + "WHERE sfp.enrollment_id = ? "
                    + "AND DATE(sfi.payment_date) = ? "
                    + "AND sfi.amount_paid = ? "
                    + "AND sfi.status = 1"
            )
                    .setParameter(1, enrollmentId)
                    .setParameter(2, paymentDate)
                    .setParameter(3, amount)
                    .getResultList();

            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Payment not found!");
                em.getTransaction().rollback();
                return;
            }

            Object[] row = list.get(0);

            int installmentId = ((Number) row[0]).intValue();
            int paymentId = ((Number) row[1]).intValue();
            int totalPaid = ((Number) row[2]).intValue();
            int totalFee = ((Number) row[3]).intValue();
            String paymentMethod = row[4].toString();

            // ============================
            // 2. CHECK CHEQUE ONLY FOR THIS INSTALLMENT
            // ============================
            if ("CHEQUE".equalsIgnoreCase(paymentMethod)) {

                List<Object> chequeList = em.createNativeQuery(
                        "SELECT cheque_status "
                        + "FROM student_fee_cheque_details "
                        + "WHERE student_fee_installments_id = ? "
                        + "AND status = 1"
                )
                        .setParameter(1, installmentId)
                        .getResultList();

                for (Object obj : chequeList) {
                    String chequeStatus = obj.toString();

                    if ("CLEARED".equalsIgnoreCase(chequeStatus)) {
                        JOptionPane.showMessageDialog(null,
                                "Cannot delete! Cheque already CLEARED.");
                        em.getTransaction().rollback();
                        return;
                    }
                }
            }

            // ============================
            // 3. UPDATE MASTER (CONDITIONALLY)
            // ============================
            boolean shouldUpdateAmount = true;

            if ("CHEQUE".equalsIgnoreCase(paymentMethod)) {

                List<Object> chequeList = em.createNativeQuery(
                        "SELECT cheque_status "
                        + "FROM student_fee_cheque_details "
                        + "WHERE student_fee_installments_id = ? "
                        + "AND status = 1"
                )
                        .setParameter(1, installmentId)
                        .getResultList();

                for (Object obj : chequeList) {
                    String chequeStatus = obj.toString();

                    // ❌ If cheque is pending → DO NOT reduce amount
                    if ("PENDING".equalsIgnoreCase(chequeStatus)) {
                        shouldUpdateAmount = false;
                    }

                    // ❌ Already handled earlier → block delete if CLEARED
                }
            }

            // ============================
            // APPLY UPDATE ONLY IF NEEDED
            // ============================
            if (shouldUpdateAmount) {

                double newTotalPaid = totalPaid - amount;
                if (newTotalPaid < 0) {
                    newTotalPaid = 0;
                }

                double newBalance = totalFee - newTotalPaid;

                String paymentStatus;

                // ============================
                // 🔥 STATUS LOGIC
                // ============================
                if (newTotalPaid == 0) {
                    paymentStatus = "ACTIVE";   // nothing paid
                } else if (newTotalPaid < totalFee) {
                    paymentStatus = "ACTIVE";  // partially paid (optional but recommended)
                } else {
                    paymentStatus = "COMPLETED";     // fully paid
                }

                em.createNativeQuery(
                        "UPDATE student_fee_payments "
                        + "SET total_paid = ?, total_balance = ?, payment_status = ? "
                        + "WHERE student_fee_payments_id = ?"
                )
                        .setParameter(1, newTotalPaid)
                        .setParameter(2, newBalance)
                        .setParameter(3, paymentStatus)
                        .setParameter(4, paymentId)
                        .executeUpdate();
            }

            // ============================
            // 4. DELETE CHEQUE (ONLY THIS INSTALLMENT)
            // ============================
            em.createNativeQuery(
                    "UPDATE student_fee_cheque_details "
                    + "SET status = 0 "
                    + "WHERE student_fee_installments_id = ?"
            )
                    .setParameter(1, installmentId)
                    .executeUpdate();

            // ============================
            // 5. DELETE INSTALLMENT (ONLY THIS ROW)
            // ============================
            em.createNativeQuery(
                    "UPDATE student_fee_installments "
                    + "SET status = 0 "
                    + "WHERE student_fee_installments_id = ?"
            )
                    .setParameter(1, installmentId)
                    .executeUpdate();

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(null, "Payment Deleted Successfully!");

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
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fm_fees_oneTime_table = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        firstName_label9 = new javax.swing.JLabel();
        firstName_label10 = new javax.swing.JLabel();
        fm_fees_oneTime_total_fee_text = new javax.swing.JTextField();
        fm_fees_oneTime_total_paid_text = new javax.swing.JTextField();
        firstName_label13 = new javax.swing.JLabel();
        fm_fees_oneTime_cheque_amount_text = new javax.swing.JTextField();
        fm_fees_oneTime_final_balance_text = new javax.swing.JTextField();
        firstName_label14 = new javax.swing.JLabel();
        firstName_label15 = new javax.swing.JLabel();
        fm_fees_oneTime_balance_text = new javax.swing.JTextField();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Payment Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        fm_fees_oneTime_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Installmet Seq", "Payment Date", "Paid Amount", "Payment Method", "Cheque Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fm_fees_oneTime_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fm_fees_oneTime_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(fm_fees_oneTime_table);
        if (fm_fees_oneTime_table.getColumnModel().getColumnCount() > 0) {
            fm_fees_oneTime_table.getColumnModel().getColumn(0).setPreferredWidth(120);
            fm_fees_oneTime_table.getColumnModel().getColumn(1).setPreferredWidth(150);
            fm_fees_oneTime_table.getColumnModel().getColumn(2).setPreferredWidth(150);
        }

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Total Paid", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        firstName_label9.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label9.setText("Total Fee");

        firstName_label10.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label10.setText("Paid Amount");

        fm_fees_oneTime_total_fee_text.setEditable(false);
        fm_fees_oneTime_total_fee_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_total_fee_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_total_fee_textActionPerformed(evt);
            }
        });
        fm_fees_oneTime_total_fee_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_fee_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_fee_textKeyReleased(evt);
            }
        });

        fm_fees_oneTime_total_paid_text.setEditable(false);
        fm_fees_oneTime_total_paid_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_total_paid_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_total_paid_textActionPerformed(evt);
            }
        });
        fm_fees_oneTime_total_paid_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_paid_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_paid_textKeyReleased(evt);
            }
        });

        firstName_label13.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label13.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label13.setText("Cheque (Pending)");

        fm_fees_oneTime_cheque_amount_text.setEditable(false);
        fm_fees_oneTime_cheque_amount_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_cheque_amount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_cheque_amount_textActionPerformed(evt);
            }
        });
        fm_fees_oneTime_cheque_amount_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_cheque_amount_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_cheque_amount_textKeyReleased(evt);
            }
        });

        fm_fees_oneTime_final_balance_text.setEditable(false);
        fm_fees_oneTime_final_balance_text.setFont(new java.awt.Font("Roboto Condensed Light", 1, 14)); // NOI18N
        fm_fees_oneTime_final_balance_text.setForeground(new java.awt.Color(255, 51, 102));
        fm_fees_oneTime_final_balance_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_final_balance_textActionPerformed(evt);
            }
        });
        fm_fees_oneTime_final_balance_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_final_balance_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_final_balance_textKeyReleased(evt);
            }
        });

        firstName_label14.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label14.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label14.setText("Final Balance");

        firstName_label15.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label15.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label15.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label15.setText("Balance Amount");

        fm_fees_oneTime_balance_text.setEditable(false);
        fm_fees_oneTime_balance_text.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_balance_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_balance_textActionPerformed(evt);
            }
        });
        fm_fees_oneTime_balance_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_balance_textKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_balance_textKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fm_fees_oneTime_balance_text, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fm_fees_oneTime_final_balance_text, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                            .addComponent(firstName_label9, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(firstName_label13)
                            .addComponent(firstName_label14)
                            .addComponent(fm_fees_oneTime_cheque_amount_text)
                            .addComponent(fm_fees_oneTime_total_fee_text)
                            .addComponent(firstName_label10)
                            .addComponent(firstName_label15, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fm_fees_oneTime_total_paid_text))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(firstName_label9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_oneTime_total_fee_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(firstName_label10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_oneTime_total_paid_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(firstName_label15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_oneTime_balance_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(firstName_label13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_oneTime_cheque_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(firstName_label14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_oneTime_final_balance_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(662, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fm_fees_oneTime_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_tableMouseClicked

    }//GEN-LAST:event_fm_fees_oneTime_tableMouseClicked

    private void fm_fees_oneTime_total_fee_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_fee_textActionPerformed

    }//GEN-LAST:event_fm_fees_oneTime_total_fee_textActionPerformed

    private void fm_fees_oneTime_total_fee_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_fee_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_fee_textKeyPressed

    private void fm_fees_oneTime_total_fee_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_fee_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_fee_textKeyReleased

    private void fm_fees_oneTime_total_paid_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_paid_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_paid_textActionPerformed

    private void fm_fees_oneTime_total_paid_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_paid_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_paid_textKeyPressed

    private void fm_fees_oneTime_total_paid_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_paid_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_paid_textKeyReleased

    private void fm_fees_oneTime_cheque_amount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_cheque_amount_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_cheque_amount_textActionPerformed

    private void fm_fees_oneTime_cheque_amount_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_cheque_amount_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_cheque_amount_textKeyPressed

    private void fm_fees_oneTime_cheque_amount_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_cheque_amount_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_cheque_amount_textKeyReleased

    private void fm_fees_oneTime_final_balance_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_final_balance_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_final_balance_textActionPerformed

    private void fm_fees_oneTime_final_balance_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_final_balance_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_final_balance_textKeyPressed

    private void fm_fees_oneTime_final_balance_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_final_balance_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_final_balance_textKeyReleased

    private void fm_fees_oneTime_balance_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_balance_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_balance_textActionPerformed

    private void fm_fees_oneTime_balance_textKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_balance_textKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_balance_textKeyPressed

    private void fm_fees_oneTime_balance_textKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_balance_textKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_balance_textKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel firstName_label10;
    private javax.swing.JLabel firstName_label13;
    private javax.swing.JLabel firstName_label14;
    private javax.swing.JLabel firstName_label15;
    private javax.swing.JLabel firstName_label9;
    public static javax.swing.JTextField fm_fees_oneTime_balance_text;
    public static javax.swing.JTextField fm_fees_oneTime_cheque_amount_text;
    public static javax.swing.JTextField fm_fees_oneTime_final_balance_text;
    public static javax.swing.JTable fm_fees_oneTime_table;
    public static javax.swing.JTextField fm_fees_oneTime_total_fee_text;
    public static javax.swing.JTextField fm_fees_oneTime_total_paid_text;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
private int showRoundPaymentDialog(int studentId, int enrollmentId, double overpaidAmount) {
        Window parent = SwingUtilities.getWindowAncestor(this);

        ModernDialog dialog = new ModernDialog((Frame) parent, 480, 300);
        JPanel panel = dialog.getContentPanel();

        panel.setLayout(null);
        panel.setBackground(new Color(45, 45, 50));

        // ===== TITLE =====
        JLabel title = new JLabel("Overpayment Detected");
        title.setBounds(30, 20, 420, 30);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        panel.add(title);

        // ===== MESSAGE =====
        JLabel message = new JLabel("<html>Payment exceeds the balance by: <b>" + overpaidAmount + "</b><br>"
                + "Do you want to apply excess to other courses?</html>");
        message.setBounds(30, 60, 420, 50);
        message.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        message.setForeground(Color.WHITE);
        panel.add(message);

        // ===== BUTTONS =====
        GradientButton roundBtn = new GradientButton(
                "Round Payment",
                new Color(0, 153, 102),
                new Color(0, 204, 153)
        );
        roundBtn.setBounds(30, 150, 140, 42);

        GradientButton singleBtn = new GradientButton(
                "Pay This Course Only",
                new Color(0, 102, 204),
                new Color(0, 180, 255)
        );
        singleBtn.setBounds(190, 150, 180, 42);

        GradientButton cancelBtn = new GradientButton(
                "Cancel",
                Color.decode("#F09819"),
                Color.decode("#FF512F")
        );
        cancelBtn.setBounds(380, 150, 80, 42);

        panel.add(roundBtn);
        panel.add(singleBtn);
        panel.add(cancelBtn);

        final int[] choice = {-1}; // -1 = cancel, 1 = round, 2 = single

        roundBtn.addActionListener(e -> {
            choice[0] = 1;
            dialog.dispose();
        });

        singleBtn.addActionListener(e -> {
            choice[0] = 2;
            dialog.dispose();
        });

        cancelBtn.addActionListener(e -> {
            choice[0] = -1;
            dialog.dispose();
        });

        // ===== SUM BALANCES IF MULTIPLE COURSES =====
        int courseCount = Fees_Management.fm_fees_course_table.getRowCount();
        if (courseCount > 1) {
            double totalBalance = 0.0;
            for (int i = 0; i < courseCount; i++) {
                totalBalance += GeneralMethods.parseCommaNumber(
                        Fees_Management.fm_fees_course_table.getValueAt(i, 9).toString()
                );
            }
            message.setText("<html>Payment exceeds the balance by: <b>" + overpaidAmount + "</b><br>"
                    + "Total balance across courses: <b>" + totalBalance + "</b><br>"
                    + "Do you want to apply excess to other courses?</html>");
        }

        dialog.setVisible(true);

        return choice[0];
    }
}
