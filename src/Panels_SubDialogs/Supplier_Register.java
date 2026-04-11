package Panels_SubDialogs;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Inventory.Supplier;
import JPA_DAO.Inventory.SupplierDAO;
import JPA_DAO.Settings.CourseDAO;
import Panels.Student_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Window;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Supplier_Register extends javax.swing.JDialog {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    String upd = "";
    String username;
    String role;

    public Supplier_Register(Window parent, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.username = username;
        this.role = role;
        initComponents();

        sup_add_supplier_table.setDefaultRenderer(Object.class, new TableGradientCell());
        sup_add_supplier_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        generalMethods.setIntegerOnly(sup_add_supplier_contact_text, 10);

        loadSupplierTable(sup_add_supplier_table, "");

    }

    public void loadSupplierTable(JTable table, String searchText) {

        SupplierDAO dao = new SupplierDAO();
        List<Supplier> list = dao.search(searchText);

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // clear table

        int count = 1;

        for (Supplier s : list) {

            Object[] row = {
                count++, // #
                s.getSupplierName(), // Name
                s.getCompany(), // Company
                s.getPhone(), // Contact
                s.getAddress(), // Address
                s.getSuppliersId() // hidden ID
            };

            model.addRow(row);
        }

//        // 🔥 Hide ID column
//        table.getColumnModel().getColumn(5).setMinWidth(0);
//        table.getColumnModel().getColumn(5).setMaxWidth(0);
//        table.getColumnModel().getColumn(5).setWidth(0);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        sup_add_supplier_name_text = new javax.swing.JTextField();
        sup_add_supplier_company_text = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        sup_add_supplier_contact_text = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        sup_add_supplier_address = new javax.swing.JEditorPane();
        jLabel5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sup_add_supplier_table = new javax.swing.JTable();
        buttonGradient4 = new Classes.ButtonGradient();
        buttonGradient5 = new Classes.ButtonGradient();
        buttonGradient6 = new Classes.ButtonGradient();
        buttonGradient7 = new Classes.ButtonGradient();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Register New Supplier", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Name");

        sup_add_supplier_name_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        sup_add_supplier_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sup_add_supplier_name_textActionPerformed(evt);
            }
        });

        sup_add_supplier_company_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        sup_add_supplier_company_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sup_add_supplier_company_textActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Company");

        sup_add_supplier_contact_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        sup_add_supplier_contact_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sup_add_supplier_contact_textActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Contact");

        jScrollPane1.setViewportView(sup_add_supplier_address);

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Address");

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Suppliers", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        sup_add_supplier_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Name", "Company", "Contact", "Address", "supplier_id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        sup_add_supplier_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sup_add_supplier_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(sup_add_supplier_table);
        if (sup_add_supplier_table.getColumnModel().getColumnCount() > 0) {
            sup_add_supplier_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            sup_add_supplier_table.getColumnModel().getColumn(1).setPreferredWidth(130);
            sup_add_supplier_table.getColumnModel().getColumn(2).setPreferredWidth(130);
            sup_add_supplier_table.getColumnModel().getColumn(3).setPreferredWidth(100);
            sup_add_supplier_table.getColumnModel().getColumn(5).setMinWidth(0);
            sup_add_supplier_table.getColumnModel().getColumn(5).setPreferredWidth(0);
            sup_add_supplier_table.getColumnModel().getColumn(5).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                .addContainerGap())
        );

        buttonGradient4.setText("SAVE");
        buttonGradient4.setToolTipText("Save new record");
        buttonGradient4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

        buttonGradient5.setText("DELETE");
        buttonGradient5.setToolTipText("Clear All / Add New");
        buttonGradient5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        buttonGradient6.setText("C.S.");
        buttonGradient6.setToolTipText("Clear table selection");
        buttonGradient6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient6ActionPerformed(evt);
            }
        });

        buttonGradient7.setText("NEW");
        buttonGradient7.setToolTipText("Add new record / Clear All");
        buttonGradient7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(sup_add_supplier_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(sup_add_supplier_company_text, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(sup_add_supplier_contact_text)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonGradient4, buttonGradient5, buttonGradient6, buttonGradient7});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(41, 41, 41))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sup_add_supplier_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sup_add_supplier_company_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sup_add_supplier_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonGradient7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonGradient4, buttonGradient5, buttonGradient6, buttonGradient7});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void sup_add_supplier_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sup_add_supplier_name_textActionPerformed
        sup_add_supplier_company_text.requestFocus();
    }//GEN-LAST:event_sup_add_supplier_name_textActionPerformed

    private void sup_add_supplier_company_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sup_add_supplier_company_textActionPerformed
        sup_add_supplier_contact_text.requestFocus();
    }//GEN-LAST:event_sup_add_supplier_company_textActionPerformed

    private void sup_add_supplier_contact_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sup_add_supplier_contact_textActionPerformed
        sup_add_supplier_address.requestFocus();
    }//GEN-LAST:event_sup_add_supplier_contact_textActionPerformed

    private void sup_add_supplier_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_sup_add_supplier_tableMouseClicked

        upd = "update";
    }//GEN-LAST:event_sup_add_supplier_tableMouseClicked

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        try {

            if (upd.equalsIgnoreCase("")) {

                if (sup_add_supplier_name_text.getText().equals("") || sup_add_supplier_company_text.getText().toString().equals("")) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be empty");
                    return;
                }

                Supplier supplier = new Supplier();

                supplier.setSupplierName(sup_add_supplier_name_text.getText());
                supplier.setCompany(sup_add_supplier_company_text.getText());
                supplier.setPhone(sup_add_supplier_contact_text.getText());
                supplier.setAddress(sup_add_supplier_address.getText());
                supplier.setRemarks("");
                supplier.setUser(username); // or logged user
                supplier.setStatus(1); // ACTIVE

                SupplierDAO dao = new SupplierDAO();
                dao.save(supplier);

                // LOG
                logHelper.log("SUPPLIER_REGISTER", supplier.getSuppliersId(), "SUPPLIER CREATE", "", 0.0, "New supplier registered (" + supplier.getSupplierName() + ")", username);

                DefaultTableModel model = (DefaultTableModel) sup_add_supplier_table.getModel();
                int count = sup_add_supplier_table.getRowCount() + 1;

                Object[] row = {
                    count, // #
                    supplier.getSupplierName(), // Name
                    supplier.getCompany(), // Company
                    supplier.getPhone(), // Contact
                    supplier.getAddress(), // Address
                    supplier.getSuppliersId() // hidden ID
                };

                model.addRow(row);

                JOptionPane.showMessageDialog(this, "Saved supplier successfully");
                buttonGradient7.doClick();

            } else {

                JOptionPane.showMessageDialog(this, "Clear table selection (C.S)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed

        try {

            if (upd.equalsIgnoreCase("update")) {

                EntityManager em = HibernateConfig.getEntityManager();
                em.getTransaction().begin();

                Query query = em.createQuery(
                        "UPDATE Supplier s SET s.status = 0 WHERE s.suppliersId = :id"
                );
                query.setParameter("id", Integer.parseInt(sup_add_supplier_table.getValueAt(sup_add_supplier_table.getSelectedRow(), 5).toString()));
                query.executeUpdate();

                // LOG
                logHelper.log("SUPPLIER_REGISTER", Integer.parseInt(sup_add_supplier_table.getValueAt(sup_add_supplier_table.getSelectedRow(), 5).toString()),
                        "SUPPLIER DELETED", "", 0.0, "New supplier registered (" + sup_add_supplier_table.getValueAt(sup_add_supplier_table.getSelectedRow(), 1).toString() + ")", username);

                em.getTransaction().commit();
                em.close();

                loadSupplierTable(sup_add_supplier_table, "");
                upd = "";
                JOptionPane.showMessageDialog(this, "Deleted supplier successfully");

            } else {
                JOptionPane.showMessageDialog(this, "Please select the supplier to delete");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void buttonGradient6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient6ActionPerformed

        sup_add_supplier_table.clearSelection();
        upd = "";

    }//GEN-LAST:event_buttonGradient6ActionPerformed

    private void buttonGradient7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient7ActionPerformed

        sup_add_supplier_name_text.setText("");
        sup_add_supplier_company_text.setText("");
        sup_add_supplier_contact_text.setText("");
        sup_add_supplier_address.setText("");
        upd = "";

        loadSupplierTable(sup_add_supplier_table, "");

    }//GEN-LAST:event_buttonGradient7ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {

        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Supplier_Register dialog
                    = new Supplier_Register(frame, null, null);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private Classes.ButtonGradient buttonGradient6;
    private Classes.ButtonGradient buttonGradient7;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JEditorPane sup_add_supplier_address;
    private javax.swing.JTextField sup_add_supplier_company_text;
    private javax.swing.JTextField sup_add_supplier_contact_text;
    private javax.swing.JTextField sup_add_supplier_name_text;
    public static javax.swing.JTable sup_add_supplier_table;
    // End of variables declaration//GEN-END:variables

}
