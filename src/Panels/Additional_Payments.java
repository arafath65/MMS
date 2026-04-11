/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Panels;

import Classes.GeneralMethods;
import Classes.TableGradientCell;
import Classes.ButtonGradientRound;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Entities.Settings.Course;
import Entities.Settings.StudentClass;
import Entities.Student_Management.FeeTypes;
import JPA_DAO.Settings.ClassDAO;
import JPA_DAO.Settings.CourseDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author UNKNOWN_UN
 */
public class Additional_Payments extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    String username;
    String role;

    public Additional_Payments(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        reg_add_paym_table.setDefaultRenderer(Object.class, new TableGradientCell());
        reg_add_paym_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        JComboPopulates();
        loadFeeTypesToTable(reg_add_paym_table);

    }

    private void JComboPopulates() {
        // Medicine brand combo
        reg_add_paym_category_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = reg_add_paym_category_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(reg_add_paym_category_combo, "fee_category", "fee_types", input);
            }

        });
        setupComboSelectionListener(reg_add_paym_category_combo, reg_add_paym_amount_text);
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

    public void loadFeeTypesToTable(JTable table) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            String jpql = "SELECT f.feeTypeId, f.feeName, f.feeCategory, f.defaultAmount "
                    + "FROM FeeTypes f WHERE f.status = 1";

            List<Object[]> list = em.createQuery(jpql).getResultList();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0); // clear table

            int count = 1;

            for (Object[] row : list) {

                Object[] data = {
                    count++, // #
                    row[1], // fee_name
                    row[2], // category
                    GeneralMethods.formatWithComma(GeneralMethods.parseCommaNumber(row[3].toString())), // amount
                    row[0] // fee_type_id (last column hidden or used internally)
                };

                model.addRow(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        reg_add_paym_category_combo = new javax.swing.JComboBox<>();
        reg_add_paym_amount_text = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        reg_add_paym_name_text = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        reg_add_paym_table = new javax.swing.JTable();
        buttonGradientRound1 = new Classes.ButtonGradientRound();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Register Additional Payments", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel12.setText("Payment Name");

        jLabel16.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel16.setText("Category");

        reg_add_paym_category_combo.setEditable(true);
        reg_add_paym_category_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        reg_add_paym_amount_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_add_paym_amount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_add_paym_amount_textActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel21.setText("Amount");

        reg_add_paym_name_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        reg_add_paym_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reg_add_paym_name_textActionPerformed(evt);
            }
        });

        reg_add_paym_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Payment Name", "Category", "Amount", "fee_type_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(reg_add_paym_table);
        if (reg_add_paym_table.getColumnModel().getColumnCount() > 0) {
            reg_add_paym_table.getColumnModel().getColumn(0).setResizable(false);
            reg_add_paym_table.getColumnModel().getColumn(0).setPreferredWidth(30);
            reg_add_paym_table.getColumnModel().getColumn(1).setPreferredWidth(200);
            reg_add_paym_table.getColumnModel().getColumn(2).setPreferredWidth(120);
            reg_add_paym_table.getColumnModel().getColumn(4).setMinWidth(0);
            reg_add_paym_table.getColumnModel().getColumn(4).setPreferredWidth(0);
            reg_add_paym_table.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        buttonGradientRound1.setText("X");
        buttonGradientRound1.setFont(new java.awt.Font("Roboto Black", 0, 17)); // NOI18N
        buttonGradientRound1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradientRound1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reg_add_paym_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel16)
                            .addComponent(reg_add_paym_category_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(reg_add_paym_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(reg_add_paym_category_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(reg_add_paym_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(reg_add_paym_amount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradientRound1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(829, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void reg_add_paym_amount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_add_paym_amount_textActionPerformed

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            String feeName = reg_add_paym_name_text.getText().trim();
            String category = reg_add_paym_category_combo.getEditor().getItem().toString().trim();
            double amount = GeneralMethods.parseCommaNumber(reg_add_paym_amount_text.getText());

            // =========================
            // DUPLICATE CHECK
            // =========================
            Query checkQuery = em.createQuery(
                    "SELECT COUNT(f) FROM FeeTypes f "
                    + "WHERE f.feeName = :name "
                    + "AND f.feeCategory = :cat "
                    + "AND f.defaultAmount = :amt "
                    + "AND f.status = 1"
            );

            checkQuery.setParameter("name", feeName);
            checkQuery.setParameter("cat", category);
            checkQuery.setParameter("amt", amount);

            Long count = (Long) checkQuery.getSingleResult();

            if (count > 0) {
                JOptionPane.showMessageDialog(null, "This payment already exists!");
                return;
            }

            // =========================
            // SAVE
            // =========================
            em.getTransaction().begin();

            FeeTypes fee = new FeeTypes();
            fee.setItemId(0);
            fee.setFeeName(feeName);
            fee.setFeeCategory(category);
            fee.setDefaultAmount(amount);
            fee.setUser(username);
            fee.setStatus(1);

            em.persist(fee);

            em.getTransaction().commit();
            // ✅ LOG: Additional Payment Type Registration
            int generatedId = (fee.getItemId() != null) ? fee.getItemId() : 0;
            logHelper.log(
                    "FEE_CONFIGURATION",
                    generatedId, // The generated primary key
                    "FEE CONFIGURATION CREATE",
                    feeName,
                    amount,
                    String.format("New fee type registered: %s under category: %s", feeName, category),
                    username
            );

            JOptionPane.showMessageDialog(null, "Additional Payment Saved Successfully!");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            JOptionPane.showMessageDialog(null, "Error Saving Additional Payment: " + e.getMessage());
            e.printStackTrace();

        } finally {
            em.close();
        }
    }//GEN-LAST:event_reg_add_paym_amount_textActionPerformed

    private void buttonGradientRound4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound4ActionPerformed

    }//GEN-LAST:event_buttonGradientRound4ActionPerformed

    private void buttonGradientRound1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound1ActionPerformed

        int selectedRow = reg_add_paym_table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Please select a row to delete!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                null,
                "Do you want to delete selected payment?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            em.getTransaction().begin();

            DefaultTableModel model = (DefaultTableModel) reg_add_paym_table.getModel();

            // fee_type_id is last column (index 4)
            int feeTypeId = Integer.parseInt(model.getValueAt(selectedRow, 4).toString());

            // =========================
            // SOFT DELETE
            // =========================
            Query query = em.createQuery(
                    "UPDATE FeeTypes f SET f.status = 0 WHERE f.feeTypeId = :id"
            );

            query.setParameter("id", feeTypeId);

            query.executeUpdate();

            // =========================
            // REMOVE FROM TABLE
            // =========================
            model.removeRow(selectedRow);
            loadFeeTypesToTable(reg_add_paym_table);

            em.getTransaction().commit();

            // ✅ LOG: Additional Payment Deletion
            String deletedFeeName = model.getValueAt(selectedRow, 1).toString(); // Assuming Name is in Column 1

            logHelper.log(
                    "FEE_CONFIGURATION",
                    feeTypeId,
                    "FEE CONFIGURATION DELETED",
                    deletedFeeName,
                    GeneralMethods.parseCommaNumber(model.getValueAt(selectedRow, 3).toString()),
                    "Soft deleted fee type: " + deletedFeeName + " (ID: " + feeTypeId + ")",
                    username
            );

            JOptionPane.showMessageDialog(null, "Deleted successfully!");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());

        } finally {
            em.close();
        }

    }//GEN-LAST:event_buttonGradientRound1ActionPerformed

    private void reg_add_paym_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reg_add_paym_name_textActionPerformed
        reg_add_paym_category_combo.requestFocus();
    }//GEN-LAST:event_reg_add_paym_name_textActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradientRound buttonGradientRound1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField reg_add_paym_amount_text;
    private javax.swing.JComboBox<String> reg_add_paym_category_combo;
    private javax.swing.JTextField reg_add_paym_name_text;
    private javax.swing.JTable reg_add_paym_table;
    // End of variables declaration//GEN-END:variables
}
