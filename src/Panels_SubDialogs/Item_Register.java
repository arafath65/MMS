package Panels_SubDialogs;

import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Inventory.Item;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Settings.CourseDAO;
import Panels.Student_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

public class Item_Register extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Item_Register.class.getName());
    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    String upd = "";
    String username;
    String role;

    public Item_Register(Window parent, String username, String role) {
        super(parent, ModalityType.APPLICATION_MODAL);
        this.username = username;
        this.role = role;
        initComponents();

        inv_reg_add_table.setDefaultRenderer(Object.class, new TableGradientCell());
        inv_reg_add_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadItemTable(inv_reg_add_table);

        jComboPopulates();

    }

    private void jComboPopulates() {

        inv_reg_add_category_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = inv_reg_add_category_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(inv_reg_add_category_combo, "category", "items", input);
            }

        });
        setupComboSelectionListeners(inv_reg_add_category_combo, inv_reg_add_unit_price_text);

    }

    private boolean itemSelectedByUser1 = false;

    public void setupComboSelectionListeners(JComboBox<String> comboBox, JComponent nextFocusComponent) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUser1 = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (!itemSelectedByUser1) {
                    return;
                }

                Object selected = comboBox.getSelectedItem();
                if (selected == null) {
                    return;
                }

                String selectedValue = selected.toString().trim();
                if (selectedValue.isEmpty() || !isValueFromList(comboBox, selectedValue)) {
                    return;
                }

                // String admis_combo = selectedValue.split(" - ")[0];
                comboBox.setSelectedItem(selectedValue);

                nextFocusComponent.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser1 = false;
            }
        });

        // Detect user selection via Enter or mouse click
        comboBox.addActionListener(e -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUser1 = true;
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

    public void loadItemTable(JTable table) {

        ItemDAO dao = new ItemDAO();
        List<Item> list = dao.findAll();

        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        int count = 1;

        for (Item i : list) {

            Object[] row = {
                count++,
                i.getItemName(),
                i.getCategory(),
                i.getUnits(),
                GeneralMethods.formatWithComma(i.getUnitPrice()),
                i.getDescription(),
                i.getItemId()
            };

            model.addRow(row);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        inv_reg_add_item_name_text = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        inv_reg_add_description_textpane = new javax.swing.JEditorPane();
        jLabel5 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        inv_reg_add_table = new javax.swing.JTable();
        buttonGradient4 = new Classes.ButtonGradient();
        buttonGradient5 = new Classes.ButtonGradient();
        buttonGradient6 = new Classes.ButtonGradient();
        buttonGradient7 = new Classes.ButtonGradient();
        inv_reg_add_category_combo = new javax.swing.JComboBox<>();
        inv_reg_add_unit_combo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        inv_reg_add_unit_price_text = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Register New Item", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Item Name");

        inv_reg_add_item_name_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_reg_add_item_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_reg_add_item_name_textActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Category");

        jScrollPane1.setViewportView(inv_reg_add_description_textpane);

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Description");

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Item List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        inv_reg_add_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Name", "Category", "Unit", "Unit Price", "Description", "id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        inv_reg_add_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inv_reg_add_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(inv_reg_add_table);
        if (inv_reg_add_table.getColumnModel().getColumnCount() > 0) {
            inv_reg_add_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            inv_reg_add_table.getColumnModel().getColumn(1).setPreferredWidth(200);
            inv_reg_add_table.getColumnModel().getColumn(2).setPreferredWidth(130);
            inv_reg_add_table.getColumnModel().getColumn(3).setPreferredWidth(80);
            inv_reg_add_table.getColumnModel().getColumn(4).setPreferredWidth(100);
            inv_reg_add_table.getColumnModel().getColumn(6).setMinWidth(0);
            inv_reg_add_table.getColumnModel().getColumn(6).setPreferredWidth(0);
            inv_reg_add_table.getColumnModel().getColumn(6).setMaxWidth(0);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
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

        inv_reg_add_category_combo.setEditable(true);
        inv_reg_add_category_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        inv_reg_add_unit_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_reg_add_unit_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unit", "KG", "Litre" }));

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("Unit");

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Unit Price");

        inv_reg_add_unit_price_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_reg_add_unit_price_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_reg_add_unit_price_textActionPerformed(evt);
            }
        });
        inv_reg_add_unit_price_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inv_reg_add_unit_price_textKeyTyped(evt);
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
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(176, 176, 176)
                                .addComponent(jLabel3)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(buttonGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(inv_reg_add_item_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inv_reg_add_category_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(inv_reg_add_unit_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(inv_reg_add_unit_price_text, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {buttonGradient4, buttonGradient5, buttonGradient6, buttonGradient7});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3))
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(inv_reg_add_unit_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inv_reg_add_category_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(inv_reg_add_item_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inv_reg_add_unit_price_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
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

    private void inv_reg_add_item_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_reg_add_item_name_textActionPerformed
        inv_reg_add_category_combo.requestFocus();
    }//GEN-LAST:event_inv_reg_add_item_name_textActionPerformed

    private void inv_reg_add_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inv_reg_add_tableMouseClicked

        upd = "update";
    }//GEN-LAST:event_inv_reg_add_tableMouseClicked

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        try {

            if (upd.equalsIgnoreCase("")) {

                if (inv_reg_add_item_name_text.getText().equals("") || inv_reg_add_category_combo.getEditor().getItem().toString().equals("")) {
                    JOptionPane.showMessageDialog(this, "Fields cannot be empty");
                    return;
                }

                double val = 0.0;
                if (!inv_reg_add_unit_price_text.getText().equals("")) {
                    val = GeneralMethods.parseCommaNumber(inv_reg_add_unit_price_text.getText());
                }

                Item item = new Item();

                item.setItemName(inv_reg_add_item_name_text.getText());
                item.setCategory(inv_reg_add_category_combo.getEditor().getItem().toString());
                item.setUnits(inv_reg_add_unit_combo.getSelectedItem().toString());
                item.setUnitPrice(val);
                item.setDescription(inv_reg_add_description_textpane.getText());
                item.setUser(username);
                item.setStatus(1);

                ItemDAO dao = new ItemDAO();
                dao.save(item);

                // LOG
                logHelper.log("ITEM_REGISTER", item.getItemId(), "ITEM CREATE", "", val, "Item registration worth - " + val, username);

                loadItemTable(inv_reg_add_table);

                JOptionPane.showMessageDialog(this, "Saved an item successfully");
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

                int row = inv_reg_add_table.getSelectedRow();

                if (row != -1) {

                    double unitPrice = GeneralMethods.parseCommaNumber(inv_reg_add_table.getValueAt(row, 4).toString()); // hidden ID column
                    int itemId = (int) inv_reg_add_table.getValueAt(row, 6); // hidden ID column

                    ItemDAO dao = new ItemDAO();
                    dao.softDelete(itemId);

                    loadItemTable(inv_reg_add_table); // refresh table

                    // LOG
                    logHelper.log("ITEM_REGISTER", itemId, "ITEM DELETE", "", unitPrice, "Deleted an item worth - " + unitPrice, username);

                    JOptionPane.showMessageDialog(this, "Deleted the item successfully");
                    upd = "";

                } else {
                    JOptionPane.showMessageDialog(null, "Please select an item");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void buttonGradient6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient6ActionPerformed

        inv_reg_add_table.clearSelection();
        upd = "";
    }//GEN-LAST:event_buttonGradient6ActionPerformed

    private void buttonGradient7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient7ActionPerformed

        inv_reg_add_item_name_text.setText("");
        inv_reg_add_category_combo.removeAllItems();
        inv_reg_add_unit_combo.setSelectedIndex(0);
        inv_reg_add_unit_price_text.setText("");
        inv_reg_add_description_textpane.setText("");

        upd = "";

    }//GEN-LAST:event_buttonGradient7ActionPerformed

    private void inv_reg_add_unit_price_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_reg_add_unit_price_textActionPerformed
        inv_reg_add_description_textpane.requestFocus();
    }//GEN-LAST:event_inv_reg_add_unit_price_textActionPerformed

    private void inv_reg_add_unit_price_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inv_reg_add_unit_price_textKeyTyped
        ((AbstractDocument) inv_reg_add_unit_price_text.getDocument())
                .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_inv_reg_add_unit_price_textKeyTyped

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
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Item_Register dialog
                    = new Item_Register(frame, null, null);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private Classes.ButtonGradient buttonGradient6;
    private Classes.ButtonGradient buttonGradient7;
    private javax.swing.JComboBox<String> inv_reg_add_category_combo;
    private javax.swing.JEditorPane inv_reg_add_description_textpane;
    private javax.swing.JTextField inv_reg_add_item_name_text;
    public static javax.swing.JTable inv_reg_add_table;
    private javax.swing.JComboBox<String> inv_reg_add_unit_combo;
    private javax.swing.JTextField inv_reg_add_unit_price_text;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

}
