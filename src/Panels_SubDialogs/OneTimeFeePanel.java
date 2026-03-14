package Panels_SubDialogs;

import Additional.LedgerDAO;
import Classes.ChequeNumberFormatter;
import Classes.GeneralMethods;
import Classes.GradientButton;
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
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    String username = "";

    public OneTimeFeePanel() {
        initComponents();

        fm_fees_oneTime_payment_date.setDate(new Date());
        // fm_fees_oneTime_payment_date.setDate(new Date());

        styleDateChooser.applyDarkTheme(fm_fees_oneTime_payment_date);
        styleDateChooser.applyDarkTheme(fm_fees_cheq_cheque_date);

        JComboPopulatesBankInfo();
    }

    private void JComboPopulatesBankInfo() {
        // Medicine brand combo
        fm_fees_cheq_cheque_bank.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = fm_fees_cheq_cheque_bank.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(fm_fees_cheq_cheque_bank, "bank_names", "bank_names_srilanka", input);
            }

        });
        setupComboSelectionListener(fm_fees_cheq_cheque_bank, fm_fees_cheq_cheque_branch);

        new ChequeNumberFormatter(fm_fees_cheq_cheque_number, fm_fees_cheq_cheque_bank, fm_fees_cheq_cheque_branch);
        PlainDocument doc = (PlainDocument) fm_fees_cheq_cheque_number.getDocument();
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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        fm_fees_oneTime_total_paid_Textfield = new javax.swing.JTextField();
        firstName_label7 = new javax.swing.JLabel();
        fm_fees_oneTime_total_balance_Textfield = new javax.swing.JTextField();
        firstName_label8 = new javax.swing.JLabel();
        sup_payment_cash_label = new javax.swing.JLabel();
        fm_fees_oneTime_total_fee_Textfield = new javax.swing.JTextField();
        buttonGradient2 = new Classes.ButtonGradient();
        jLabel14 = new javax.swing.JLabel();
        fm_fees_oneTime_payment_method_combo = new javax.swing.JComboBox<>();
        jPanel6 = new javax.swing.JPanel();
        firstName_label9 = new javax.swing.JLabel();
        firstName_label10 = new javax.swing.JLabel();
        fm_fees_oneTime_enter_amount_Textfield = new javax.swing.JTextField();
        fm_fees_oneTime_show_bal_Textfield = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        fm_fees_cheq_cheque_number = new javax.swing.JTextField();
        fm_fees_cheq_cheque_bank = new javax.swing.JComboBox<>();
        fm_fees_cheq_cheque_branch = new javax.swing.JTextField();
        fm_fees_cheq_cheque_amount = new javax.swing.JTextField();
        fm_fees_cheq_cheque_date = new com.toedter.calendar.JDateChooser();
        sup_payment_cheque_label = new javax.swing.JLabel();
        fm_fees_cheq_full_fees_Textfield = new javax.swing.JTextField();
        fm_fees_cheq_cheque_status = new javax.swing.JComboBox<>();
        fm_fees_cheq_cheque_remaining = new javax.swing.JTextField();
        buttonGradient3 = new Classes.ButtonGradient();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fm_fees_oneTime_table = new javax.swing.JTable();
        jLabel13 = new javax.swing.JLabel();
        fm_fees_oneTime_payment_date = new com.toedter.calendar.JDateChooser();

        jTabbedPane1.setFont(new java.awt.Font("Roboto", 0, 14)); // NOI18N

        fm_fees_oneTime_total_paid_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_total_paid_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_total_paid_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_oneTime_total_paid_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_paid_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_paid_TextfieldKeyReleased(evt);
            }
        });

        firstName_label7.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label7.setText("Total Paid");

        fm_fees_oneTime_total_balance_Textfield.setEditable(false);
        fm_fees_oneTime_total_balance_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_total_balance_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_total_balance_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_oneTime_total_balance_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_balance_TextfieldKeyReleased(evt);
            }
        });

        firstName_label8.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        firstName_label8.setText("Remaining Balance");

        sup_payment_cash_label.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cash_label.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cash_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cash_label.setText("Total Fee");

        fm_fees_oneTime_total_fee_Textfield.setEditable(false);
        fm_fees_oneTime_total_fee_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_total_fee_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_total_fee_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_oneTime_total_fee_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_total_fee_TextfieldKeyReleased(evt);
            }
        });

        buttonGradient2.setText("F1");
        buttonGradient2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient2ActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("Select Payment Method");

        fm_fees_oneTime_payment_method_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        fm_fees_oneTime_payment_method_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "CASH", "CARD" }));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        firstName_label9.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstName_label9.setText("Enter Amount");

        firstName_label10.setBackground(new java.awt.Color(33, 33, 33));
        firstName_label10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        firstName_label10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        firstName_label10.setText("Balance");

        fm_fees_oneTime_enter_amount_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_enter_amount_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_enter_amount_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_oneTime_enter_amount_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_enter_amount_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_enter_amount_TextfieldKeyReleased(evt);
            }
        });

        fm_fees_oneTime_show_bal_Textfield.setEditable(false);
        fm_fees_oneTime_show_bal_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_oneTime_show_bal_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_oneTime_show_bal_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_oneTime_show_bal_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_show_bal_TextfieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_oneTime_show_bal_TextfieldKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(firstName_label9, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                    .addComponent(fm_fees_oneTime_enter_amount_Textfield))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fm_fees_oneTime_show_bal_Textfield)
                    .addComponent(firstName_label10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(firstName_label9)
                    .addComponent(firstName_label10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(fm_fees_oneTime_show_bal_Textfield, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                        .addGap(7, 7, 7))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(fm_fees_oneTime_enter_amount_Textfield)
                        .addContainerGap())))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(sup_payment_cash_label, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_oneTime_total_fee_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(firstName_label7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_oneTime_total_paid_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(fm_fees_oneTime_payment_method_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(firstName_label8, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_oneTime_total_balance_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fm_fees_oneTime_payment_method_combo)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fm_fees_oneTime_total_fee_Textfield)
                    .addComponent(sup_payment_cash_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fm_fees_oneTime_total_paid_Textfield)
                    .addComponent(firstName_label7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fm_fees_oneTime_total_balance_Textfield)
                    .addComponent(firstName_label8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cash / Card", jPanel7);

        fm_fees_cheq_cheque_number.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_cheque_number.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_cheque_numberActionPerformed(evt);
            }
        });
        fm_fees_cheq_cheque_number.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_cheque_numberKeyReleased(evt);
            }
        });

        fm_fees_cheq_cheque_bank.setEditable(true);
        fm_fees_cheq_cheque_bank.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_cheque_bank.setMinimumSize(new java.awt.Dimension(83, 30));
        fm_fees_cheq_cheque_bank.setPreferredSize(new java.awt.Dimension(72, 30));
        fm_fees_cheq_cheque_bank.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_cheque_bankActionPerformed(evt);
            }
        });
        fm_fees_cheq_cheque_bank.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_cheque_bankKeyReleased(evt);
            }
        });

        fm_fees_cheq_cheque_branch.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_cheque_branch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_cheque_branchActionPerformed(evt);
            }
        });
        fm_fees_cheq_cheque_branch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_cheque_branchKeyReleased(evt);
            }
        });

        fm_fees_cheq_cheque_amount.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_cheque_amount.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_cheque_amountActionPerformed(evt);
            }
        });
        fm_fees_cheq_cheque_amount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_cheque_amountKeyReleased(evt);
            }
        });

        fm_fees_cheq_cheque_date.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N

        sup_payment_cheque_label.setBackground(new java.awt.Color(33, 33, 33));
        sup_payment_cheque_label.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        sup_payment_cheque_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        sup_payment_cheque_label.setText("Total Fee");

        fm_fees_cheq_full_fees_Textfield.setEditable(false);
        fm_fees_cheq_full_fees_Textfield.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_full_fees_Textfield.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_full_fees_TextfieldActionPerformed(evt);
            }
        });
        fm_fees_cheq_full_fees_Textfield.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_full_fees_TextfieldKeyReleased(evt);
            }
        });

        fm_fees_cheq_cheque_status.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_cheque_status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Pending", "Cleared" }));
        fm_fees_cheq_cheque_status.setMinimumSize(new java.awt.Dimension(83, 30));
        fm_fees_cheq_cheque_status.setPreferredSize(new java.awt.Dimension(72, 30));
        fm_fees_cheq_cheque_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_cheque_statusActionPerformed(evt);
            }
        });
        fm_fees_cheq_cheque_status.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_cheque_statusKeyReleased(evt);
            }
        });

        fm_fees_cheq_cheque_remaining.setEditable(false);
        fm_fees_cheq_cheque_remaining.setFont(new java.awt.Font("Roboto Condensed Light", 0, 14)); // NOI18N
        fm_fees_cheq_cheque_remaining.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fm_fees_cheq_cheque_remainingActionPerformed(evt);
            }
        });
        fm_fees_cheq_cheque_remaining.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fm_fees_cheq_cheque_remainingKeyReleased(evt);
            }
        });

        buttonGradient3.setText("F1");
        buttonGradient3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fm_fees_cheq_cheque_number)
                    .addComponent(fm_fees_cheq_cheque_bank, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(sup_payment_cheque_label, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fm_fees_cheq_full_fees_Textfield, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fm_fees_cheq_cheque_amount)
                            .addComponent(fm_fees_cheq_cheque_branch))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(fm_fees_cheq_cheque_status, 0, 134, Short.MAX_VALUE)
                            .addComponent(fm_fees_cheq_cheque_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(fm_fees_cheq_cheque_remaining)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fm_fees_cheq_full_fees_Textfield)
                    .addComponent(sup_payment_cheque_label, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_cheq_cheque_number, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_cheq_cheque_bank, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(fm_fees_cheq_cheque_branch, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fm_fees_cheq_cheque_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fm_fees_cheq_cheque_status, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fm_fees_cheq_cheque_amount, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fm_fees_cheq_cheque_remaining, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Cheque", jPanel10);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Payment Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        fm_fees_oneTime_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Installmet Seq", "Payment Date", "Paid Amount"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setText("Select Payment Mode");

        fm_fees_oneTime_payment_date.setForeground(new java.awt.Color(204, 204, 204));
        fm_fees_oneTime_payment_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fm_fees_oneTime_payment_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(jTabbedPane1))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(494, 494, 494))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fm_fees_oneTime_payment_date, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTabbedPane1))
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void fm_fees_oneTime_total_paid_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_paid_TextfieldActionPerformed

        try {

//            int bal_fee = GeneralMethods.parseCommaNumber(fm_fees_oneTime_total_balance_Textfield.getText());
//            int paid_fee = GeneralMethods.parseCommaNumber(fm_fees_oneTime_total_paid_Textfield.getText());
//
//            fm_fees_oneTime_total_balance_Textfield.setText(GeneralMethods.formatWithComma(bal_fee - paid_fee));
            fm_fees_oneTime_enter_amount_Textfield.requestFocus();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_fm_fees_oneTime_total_paid_TextfieldActionPerformed

    private void fm_fees_oneTime_total_paid_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_paid_TextfieldKeyPressed

    }//GEN-LAST:event_fm_fees_oneTime_total_paid_TextfieldKeyPressed

    private void fm_fees_oneTime_total_paid_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_paid_TextfieldKeyReleased
        if (fm_fees_oneTime_total_paid_Textfield.getText().equals("")) {
            fm_fees_oneTime_enter_amount_Textfield.setText("");
            fm_fees_oneTime_show_bal_Textfield.setText("");
        }
    }//GEN-LAST:event_fm_fees_oneTime_total_paid_TextfieldKeyReleased

    private void fm_fees_oneTime_total_balance_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_balance_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_balance_TextfieldActionPerformed

    private void fm_fees_oneTime_total_balance_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_balance_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_balance_TextfieldKeyReleased

    private void fm_fees_oneTime_total_fee_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_fee_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_fee_TextfieldActionPerformed

    private void fm_fees_oneTime_total_fee_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_total_fee_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_total_fee_TextfieldKeyReleased

    private void fm_fees_cheq_cheque_numberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_numberActionPerformed
        fm_fees_cheq_cheque_amount.requestFocus();
    }//GEN-LAST:event_fm_fees_cheq_cheque_numberActionPerformed

    private void fm_fees_cheq_cheque_numberKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_numberKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_numberKeyReleased

    private void fm_fees_cheq_cheque_bankActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_bankActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_bankActionPerformed

    private void fm_fees_cheq_cheque_bankKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_bankKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_bankKeyReleased

    private void fm_fees_cheq_cheque_branchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_branchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_branchActionPerformed

    private void fm_fees_cheq_cheque_branchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_branchKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_branchKeyReleased

    private void fm_fees_cheq_cheque_amountActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_amountActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_amountActionPerformed

    private void fm_fees_cheq_cheque_amountKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_amountKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_amountKeyReleased

    private void fm_fees_cheq_full_fees_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_full_fees_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_full_fees_TextfieldActionPerformed

    private void fm_fees_cheq_full_fees_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_full_fees_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_full_fees_TextfieldKeyReleased

    private void fm_fees_cheq_cheque_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_statusActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_statusActionPerformed

    private void fm_fees_cheq_cheque_statusKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_statusKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_statusKeyReleased

    private void fm_fees_cheq_cheque_remainingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_remainingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_remainingActionPerformed

    private void fm_fees_cheq_cheque_remainingKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_cheq_cheque_remainingKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_cheq_cheque_remainingKeyReleased

    private void buttonGradient2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient2ActionPerformed

        try {

            int st_id = Fees_Management.selectedStudentIds;
            int en_id = Fees_Management.selectedEnrollmentId;

            int amount_paid = GeneralMethods.parseCommaNumber(fm_fees_oneTime_total_paid_Textfield.getText());
            System.out.println("PAID AMOU1 - " + fm_fees_oneTime_total_paid_Textfield.getText());
            Date utilDate = fm_fees_oneTime_payment_date.getDate();
            java.sql.Date paymentDate = new java.sql.Date(utilDate.getTime());
            String pay_method = fm_fees_oneTime_payment_method_combo.getSelectedItem().toString();

            int balance = GeneralMethods.parseCommaNumber(fm_fees_oneTime_total_balance_Textfield.getText());
            StudentFeeInstallmentsDAO dao = new StudentFeeInstallmentsDAO();
            LedgerDAO ledgerDAO = new LedgerDAO();

            if (amount_paid > balance) {
                int choice = showRoundPaymentDialog(st_id, en_id, amount_paid - balance);
                switch (choice) {
                    case 1:

                        dao.processRoundPayment(st_id, en_id, amount_paid, paymentDate, pay_method, username);
                        Fees_Management.updateMasterTableRows(st_id);
                        int paymentId = dao.getPaymentIdByStudentAndEnrollment(st_id, en_id);
                        ledgerDAO.saveLedgerEntry(paymentDate, "INCOME", amount_paid, "Student Fee Payment", "Student Fees - Round", paymentId, pay_method, "Student Management", username);
                        break;

                    case 2:
                        // Pay this course only: leave excess in this course
                        DefaultTableModel instModel = (DefaultTableModel) fm_fees_oneTime_table.getModel();
                        dao.saveInstallment(st_id, en_id, balance, paymentDate, pay_method, "", username);
                        Fees_Management.updateMasterTableRows(st_id);

                        int paymentIds = dao.getPaymentIdByStudentAndEnrollment(st_id, en_id);
                        int lastInstallmentNo = dao.getLastInstallmentNo(paymentIds);
                        instModel.addRow(new Object[]{lastInstallmentNo, paymentDate, GeneralMethods.formatWithComma(balance)});

                        ledgerDAO.saveLedgerEntry(paymentDate, "INCOME", amount_paid, "Student Fee Payment", "Student Fees - Full", paymentIds, pay_method, "Student Management", username);

                        break;
                    default:
                        // Cancel: do nothing
                        return;
                }
            } else {

                DefaultTableModel instModel = (DefaultTableModel) fm_fees_oneTime_table.getModel();
                dao.saveInstallment(st_id, en_id, amount_paid, paymentDate, pay_method, "", username);
                Fees_Management.updateMasterTableRows(st_id);

                int paymentId = dao.getPaymentIdByStudentAndEnrollment(st_id, en_id);
                int nextInstallmentNo = dao.getNextInstallmentNo(paymentId);

                instModel.addRow(new Object[]{
                    nextInstallmentNo,
                    paymentDate,
                    GeneralMethods.formatWithComma(amount_paid)
                });

                ledgerDAO.saveLedgerEntry(paymentDate, "INCOME", amount_paid, "Student Fee Payment", "Student Fees - Full", paymentId, pay_method, "Student Management", username);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_buttonGradient2ActionPerformed

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_buttonGradient3ActionPerformed

    private void fm_fees_oneTime_enter_amount_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_enter_amount_TextfieldActionPerformed
        try {

            int amount_type = GeneralMethods.parseCommaNumber(fm_fees_oneTime_enter_amount_Textfield.getText());
            int paid_fee = GeneralMethods.parseCommaNumber(fm_fees_oneTime_total_paid_Textfield.getText());

            fm_fees_oneTime_show_bal_Textfield.setText(GeneralMethods.formatWithComma(amount_type - paid_fee));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_fm_fees_oneTime_enter_amount_TextfieldActionPerformed

    private void fm_fees_oneTime_enter_amount_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_enter_amount_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_enter_amount_TextfieldKeyPressed

    private void fm_fees_oneTime_enter_amount_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_enter_amount_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_enter_amount_TextfieldKeyReleased

    private void fm_fees_oneTime_show_bal_TextfieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_show_bal_TextfieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_show_bal_TextfieldActionPerformed

    private void fm_fees_oneTime_show_bal_TextfieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_show_bal_TextfieldKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_show_bal_TextfieldKeyPressed

    private void fm_fees_oneTime_show_bal_TextfieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fm_fees_oneTime_show_bal_TextfieldKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_fm_fees_oneTime_show_bal_TextfieldKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient2;
    private Classes.ButtonGradient buttonGradient3;
    private javax.swing.JLabel firstName_label10;
    private javax.swing.JLabel firstName_label7;
    private javax.swing.JLabel firstName_label8;
    private javax.swing.JLabel firstName_label9;
    public static javax.swing.JTextField fm_fees_cheq_cheque_amount;
    public static javax.swing.JComboBox<String> fm_fees_cheq_cheque_bank;
    public static javax.swing.JTextField fm_fees_cheq_cheque_branch;
    private com.toedter.calendar.JDateChooser fm_fees_cheq_cheque_date;
    public static javax.swing.JTextField fm_fees_cheq_cheque_number;
    public static javax.swing.JTextField fm_fees_cheq_cheque_remaining;
    public static javax.swing.JComboBox<String> fm_fees_cheq_cheque_status;
    public static javax.swing.JTextField fm_fees_cheq_full_fees_Textfield;
    public static javax.swing.JTextField fm_fees_oneTime_enter_amount_Textfield;
    public static com.toedter.calendar.JDateChooser fm_fees_oneTime_payment_date;
    public static javax.swing.JComboBox<String> fm_fees_oneTime_payment_method_combo;
    public static javax.swing.JTextField fm_fees_oneTime_show_bal_Textfield;
    public static javax.swing.JTable fm_fees_oneTime_table;
    public static javax.swing.JTextField fm_fees_oneTime_total_balance_Textfield;
    public static javax.swing.JTextField fm_fees_oneTime_total_fee_Textfield;
    public static javax.swing.JTextField fm_fees_oneTime_total_paid_Textfield;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel sup_payment_cash_label;
    private javax.swing.JLabel sup_payment_cheque_label;
    // End of variables declaration//GEN-END:variables
private int showRoundPaymentDialog(int studentId, int enrollmentId, int overpaidAmount) {
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
            int totalBalance = 0;
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
