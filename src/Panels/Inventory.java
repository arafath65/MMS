package Panels;

import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.GeneralMethods.StudentSearchType;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Entities.Inventory.Grn;
import Entities.Inventory.GrnItems;
import Entities.Inventory.Item;
import Entities.Inventory.StockTransaction;
import Entities.Student_Management.FeeTypes;
import Entities.Student_Management.Student;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Inventory.SupplierDAO;
import JPA_DAO.Student_Management.StudentDAO;
import Panels_SubDialogs.Item_Register;
import Panels_SubDialogs.Supplier_Register;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

public class Inventory extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");

    String upd = "";
    String username;
    String role;

    public Inventory(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        inv_grn_table.setDefaultRenderer(Object.class, new TableGradientCell());
        inv_grn_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        inv_grn_date.setDate(new Date());
        styleDateChooser.applyDarkTheme(inv_grn_date);

        jComboPopulates();

    }

    private void jComboPopulates() {

        inv_grn_supplier_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = inv_grn_supplier_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(inv_grn_supplier_combo, "suppliers_id", "supplier_name", "suppliers", input);
            }

        });
        setupComboSelectionListeners1(inv_grn_supplier_combo, inv_grn_invoice_no_text);

        inv_grn_item_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = inv_grn_item_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(inv_grn_item_combo, "item_id", "item_name", "items", input);
            }

        });
        setupComboSelectionListeners1(inv_grn_item_combo, inv_grn_unit_price_text);

    }

    private boolean itemSelectedByUser1 = false;

    public void setupComboSelectionListeners1(JComboBox<String> comboBox, JComponent nextFocusComponent) {
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

                int itemId = generalMethods.extractIdFromCombo(selectedValue);
                Object[] data = getItemUnitsAndPrice(itemId);

                if (data != null) {
                    String units = (String) data[0];
                    double price = (double) data[1];

                    inv_grn_unit_combo.setSelectedItem(units);
                    inv_grn_unit_price_text.setText(price + "");
                }

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

//    public int extractIdFromCombo(String combo) {
//
//        Object selectedObj = combo;
//
//        if (selectedObj == null) {
//            JOptionPane.showMessageDialog(null, "Please select value");
//            return -1;
//        }
//
//        String text = selectedObj.toString().trim();
//
//        // check valid format
//        if (!text.contains("[") || !text.contains("]")) {
//            JOptionPane.showMessageDialog(null, "Please select from dropdown list");
//            return -1;
//        }
//
//        try {
//            int start = text.lastIndexOf("[") + 1;
//            int end = text.lastIndexOf("]");
//
//            if (start >= end) {
//                throw new Exception();
//            }
//
//            return Integer.parseInt(text.substring(start, end));
//
//        } catch (Exception e) {
//            JOptionPane.showMessageDialog(null, "Invalid selection format");
//            return -1;
//        }
//    }

    public void loadGrnToTable(JTable table, int supplierId, String invoiceNo) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            String sql = "SELECT g.grn_date, gi.item_id, i.item_name, gi.units, gi.unit_price, "
                    + "gi.quantity, gi.discount_amount, gi.line_total "
                    + "FROM grn g "
                    + "JOIN grn_items gi ON g.grn_id = gi.grn_id "
                    + "JOIN items i ON gi.item_id = i.item_id "
                    + "WHERE g.suppliers_id = ? "
                    + "AND g.invoice_no = ? "
                    + "AND g.status = 1 "
                    + "AND gi.status = 1";

            List<Object[]> list = em.createNativeQuery(sql)
                    .setParameter(1, supplierId)
                    .setParameter(2, invoiceNo)
                    .getResultList();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int count = 1;

            for (Object[] row : list) {

                double price = Double.parseDouble(row[4].toString());
                double qty = Double.parseDouble(row[5].toString());
                double discount = Double.parseDouble(row[6].toString());

                // 1. Sub total (price * qty)
                double subTotal = price * qty;

                // 2. Final total (subTotal - discount)
                double lineTotal = subTotal - discount;

                Object[] data = {
                    count++,
                    row[0], // GRN DATE

                    inv_grn_supplier_combo.getSelectedItem().toString(),
                    inv_grn_invoice_no_text.getText(),
                    row[2] + " [" + row[1] + "]", // item name + id

                    row[3], // units

                    GeneralMethods.formatWithComma(price), // unit price
                    qty, // quantity

                    GeneralMethods.formatWithComma(subTotal), // row[6] = price * qty

                    GeneralMethods.formatWithComma(discount), // discount

                    GeneralMethods.formatWithComma(lineTotal) // row[8] = subTotal - discount
                };

                model.addRow(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void deleteGrnItemOrInvoice() {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            DefaultTableModel model = (DefaultTableModel) inv_grn_table.getModel();
            int selectedRow = inv_grn_table.getSelectedRow();

            String invoiceNo = inv_grn_invoice_no_text.getText();
            int supId = generalMethods.extractIdFromCombo(inv_grn_supplier_combo.getSelectedItem().toString());

            // =========================
            // CASE 1: ROW SELECTED
            // =========================
            if (selectedRow != -1) {

                int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Do you want to delete selected item?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirm != JOptionPane.YES_OPTION) {
                    em.getTransaction().rollback();
                    return;
                }

                int itemId = generalMethods.extractIdFromCombo(model.getValueAt(selectedRow, 4).toString());

                // GRN ITEM SOFT DELETE
                Query q1 = em.createQuery(
                        "UPDATE GrnItems gi SET gi.status = 0 "
                        + "WHERE gi.itemId = :itemId "
                        + "AND gi.grnId IN (SELECT g.grnId FROM Grn g "
                        + "WHERE g.suppliersId = :supId AND g.invoiceNo = :inv)"
                );

                q1.setParameter("itemId", itemId);
                q1.setParameter("supId", supId);
                q1.setParameter("inv", invoiceNo);

                q1.executeUpdate();

                // STOCK TRANSACTION SOFT DELETE (ITEM ONLY)
                Query q2 = em.createQuery(
                        "UPDATE StockTransaction st SET st.status = 0 "
                        + "WHERE st.itemId = :itemId "
                        + "AND st.suppliersId = :supId "
                        + "AND st.invoiceNo = :inv"
                );

                q2.setParameter("itemId", itemId);
                q2.setParameter("supId", supId);
                q2.setParameter("inv", invoiceNo);

                q2.executeUpdate();

                // ✅ LOG: Single Item Delete from GRN
                logHelper.log(
                        "INVENTORY_GRN",
                        itemId,
                        "GRN DELETE ITEM",
                        "Invoice No: " + invoiceNo,
                        GeneralMethods.parseCommaNumber(inv_grn_table.getValueAt(inv_grn_table.getSelectedRow(), 10).toString()),
                        String.format("Removed Item ID: %d from Invoice: %s (Supplier ID: %d)", itemId, invoiceNo, supId),
                        username
                );

                model.removeRow(selectedRow);

                JOptionPane.showMessageDialog(null, "Item deleted successfully!");

            } // =========================
            // CASE 2: NO ROW SELECTED
            // =========================
            else {

                int confirmAll = JOptionPane.showConfirmDialog(
                        null,
                        "No item selected.\nDo you want to delete entire invoice?",
                        "Confirm Invoice Delete",
                        JOptionPane.YES_NO_OPTION
                );

                if (confirmAll != JOptionPane.YES_OPTION) {
                    em.getTransaction().rollback();
                    return;
                }

                // GRN ITEMS
                Query q1 = em.createQuery(
                        "UPDATE GrnItems gi SET gi.status = 0 "
                        + "WHERE gi.grnId IN (SELECT g.grnId FROM Grn g "
                        + "WHERE g.suppliersId = :supId AND g.invoiceNo = :inv)"
                );

                q1.setParameter("supId", supId);
                q1.setParameter("inv", invoiceNo);

                q1.executeUpdate();

                // GRN HEADER
                Query q2 = em.createQuery(
                        "UPDATE Grn g SET g.status = 0 "
                        + "WHERE g.suppliersId = :supId AND g.invoiceNo = :inv"
                );

                q2.setParameter("supId", supId);
                q2.setParameter("inv", invoiceNo);

                q2.executeUpdate();

                // STOCK TRANSACTIONS
                Query q3 = em.createQuery(
                        "UPDATE StockTransaction st SET st.status = 0 "
                        + "WHERE st.suppliersId = :supId AND st.invoiceNo = :inv"
                );

                q3.setParameter("supId", supId);
                q3.setParameter("inv", invoiceNo);

                q3.executeUpdate();

                // ✅ LOG: Entire GRN Invoice Delete
                logHelper.log(
                        "INVENTORY_GRN",
                        supId,
                        "GRN DELETE INVOICE",
                        "Invoice No: " + invoiceNo,
                        GeneralMethods.parseCommaNumber(inv_grn_invoice_total_text.getText()),
                        String.format("FULL DELETE: Entire Invoice %s for Supplier %d was soft-deleted.", invoiceNo, supId),
                        username
                );

                model.setRowCount(0);

                JOptionPane.showMessageDialog(null, "Invoice deleted successfully!");
            }

            em.getTransaction().commit();

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

    private void calculateInvoiceTotal() {

        DefaultTableModel model = (DefaultTableModel) inv_grn_table.getModel();

        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {

            Object value = GeneralMethods.parseCommaNumber(model.getValueAt(i, 10).toString()); // column index 10

            if (value != null && !value.toString().isEmpty()) {
                total += Double.parseDouble(String.valueOf(value));
            }
        }

        inv_grn_invoice_total_text.setText(GeneralMethods.formatWithComma(total));
    }

    public Object[] getItemUnitsAndPrice(int itemId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            Query query = em.createQuery(
                    "SELECT i.units, i.unitPrice FROM Item i WHERE i.itemId = :id AND i.status = 1"
            );

            query.setParameter("id", itemId);

            List<Object[]> list = query.getResultList();

            if (!list.isEmpty()) {
                return list.get(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        inv_grn_item_combo = new javax.swing.JComboBox<>();
        inv_grn_unit_combo = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        inv_grn_unit_price_text = new javax.swing.JTextField();
        inv_grn_qty_text = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        inv_grn_discount_text = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        inv_grn_line_total_text = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        buttonGradient4 = new Classes.ButtonGradient();
        buttonGradient6 = new Classes.ButtonGradient();
        buttonGradient5 = new Classes.ButtonGradient();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        inv_grn_table = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        inv_grn_invoice_no_text = new javax.swing.JTextField();
        inv_grn_date = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        inv_grn_supplier_combo = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        inv_grn_invoice_total_text = new javax.swing.JTextField();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Add GRN ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Unit Price");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("Item");

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("Unit");

        inv_grn_item_combo.setEditable(true);
        inv_grn_item_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        inv_grn_unit_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_unit_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Unit", "KG", "Litre" }));

        jButton6.setBackground(new java.awt.Color(102, 102, 102));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("+");
        jButton6.setToolTipText("Course Enrolment");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        inv_grn_unit_price_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_unit_price_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_grn_unit_price_textActionPerformed(evt);
            }
        });
        inv_grn_unit_price_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inv_grn_unit_price_textKeyTyped(evt);
            }
        });

        inv_grn_qty_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_qty_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_grn_qty_textActionPerformed(evt);
            }
        });
        inv_grn_qty_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inv_grn_qty_textKeyTyped(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Qty");

        inv_grn_discount_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_discount_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_grn_discount_textActionPerformed(evt);
            }
        });
        inv_grn_discount_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inv_grn_discount_textKeyTyped(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setText("Discount Amount");

        inv_grn_line_total_text.setEditable(false);
        inv_grn_line_total_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_line_total_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_grn_line_total_textActionPerformed(evt);
            }
        });
        inv_grn_line_total_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inv_grn_line_total_textKeyTyped(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel8.setText("Line Total");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(102, 102, 102))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(inv_grn_item_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(inv_grn_unit_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(inv_grn_unit_price_text, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(inv_grn_qty_text, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(inv_grn_discount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel8)
                    .addComponent(inv_grn_line_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(41, 41, 41))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inv_grn_line_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(inv_grn_discount_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(inv_grn_unit_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(inv_grn_unit_price_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(inv_grn_item_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                    .addGap(1, 1, 1)
                                    .addComponent(jLabel4)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(inv_grn_qty_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        buttonGradient4.setText("DELETE");
        buttonGradient4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

        buttonGradient6.setText("NEW");
        buttonGradient6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient6ActionPerformed(evt);
            }
        });

        buttonGradient5.setText("SAVE");
        buttonGradient5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Inventory List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        inv_grn_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Date", "Supplier Name", "Invoice No", "Item Name", "Unit", "Unit Price", "Qty", "Total", "Discount", "Line Total"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        inv_grn_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                inv_grn_tableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(inv_grn_table);
        if (inv_grn_table.getColumnModel().getColumnCount() > 0) {
            inv_grn_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            inv_grn_table.getColumnModel().getColumn(1).setPreferredWidth(100);
            inv_grn_table.getColumnModel().getColumn(2).setPreferredWidth(200);
            inv_grn_table.getColumnModel().getColumn(4).setPreferredWidth(250);
            inv_grn_table.getColumnModel().getColumn(6).setPreferredWidth(100);
            inv_grn_table.getColumnModel().getColumn(8).setPreferredWidth(120);
            inv_grn_table.getColumnModel().getColumn(10).setPreferredWidth(120);
        }

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setToolTipText("Course Enrolment");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Supplier Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jButton4.setBackground(new java.awt.Color(102, 102, 102));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("+");
        jButton4.setToolTipText("Course Enrolment");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Supplier");

        inv_grn_invoice_no_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_invoice_no_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_grn_invoice_no_textActionPerformed(evt);
            }
        });

        inv_grn_date.setForeground(new java.awt.Color(204, 204, 204));
        inv_grn_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Invoice No");

        inv_grn_supplier_combo.setEditable(true);
        inv_grn_supplier_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel25.setText("Date");

        jButton5.setBackground(new java.awt.Color(102, 102, 102));
        jButton5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton5.setToolTipText("Course Enrolment");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel25)
                    .addComponent(inv_grn_date, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1))
                    .addComponent(inv_grn_supplier_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(inv_grn_invoice_no_text, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inv_grn_supplier_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel5)
                                    .addGap(41, 41, 41))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGap(22, 22, 22)
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(inv_grn_invoice_no_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(inv_grn_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jLabel9.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel9.setText("Invoice Total");

        inv_grn_invoice_total_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        inv_grn_invoice_total_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inv_grn_invoice_total_textActionPerformed(evt);
            }
        });
        inv_grn_invoice_total_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                inv_grn_invoice_total_textKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inv_grn_invoice_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(inv_grn_invoice_total_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        DefaultTableModel model = (DefaultTableModel) inv_grn_table.getModel();
        if (inv_grn_supplier_combo.getEditor().getItem().toString().equals("") || inv_grn_invoice_no_text.getText().equals("") || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Fields or Table cannot be empty");
            return;
        }

        deleteGrnItemOrInvoice();

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed


    }//GEN-LAST:event_jButton2ActionPerformed

    private void buttonGradient6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient6ActionPerformed

        inv_grn_date.setDate(new Date());
        inv_grn_supplier_combo.removeAllItems();
        inv_grn_invoice_no_text.setText("");
        inv_grn_item_combo.removeAllItems();
        inv_grn_unit_combo.setSelectedIndex(0);
        inv_grn_unit_price_text.setText("");
        inv_grn_qty_text.setText("");
        inv_grn_discount_text.setText("");
        inv_grn_line_total_text.setText("");
        inv_grn_invoice_total_text.setText("");
        inv_grn_supplier_combo.requestFocus();

        DefaultTableModel model = (DefaultTableModel) inv_grn_table.getModel();
        model.setRowCount(0);

    }//GEN-LAST:event_buttonGradient6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Supplier_Register dialog = new Supplier_Register(
                parentFrame,
                username,
                role // VERY IMPORTANT
        );

        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Item_Register dialog = new Item_Register(
                parentFrame,
                username,
                role // VERY IMPORTANT
        );

        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void inv_grn_unit_price_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_grn_unit_price_textActionPerformed
        inv_grn_qty_text.requestFocus();
    }//GEN-LAST:event_inv_grn_unit_price_textActionPerformed

    private void inv_grn_qty_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_grn_qty_textActionPerformed
        inv_grn_discount_text.requestFocus();
    }//GEN-LAST:event_inv_grn_qty_textActionPerformed

    private void inv_grn_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_inv_grn_tableMouseClicked


    }//GEN-LAST:event_inv_grn_tableMouseClicked

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed

        EntityManager em = HibernateConfig.getEntityManager();
        int supp_id = generalMethods.extractIdFromCombo(inv_grn_supplier_combo.getEditor().getItem().toString());

        String invoiceNo = inv_grn_invoice_no_text.getText();

        // =========================
        // CHECK DUPLICATE FIRST
        // =========================
        Query checkQuery = em.createQuery(
                "SELECT COUNT(g) FROM Grn g WHERE g.suppliersId = :supId AND g.invoiceNo = :inv AND g.status = 1"
        );

        checkQuery.setParameter("supId", supp_id);
        checkQuery.setParameter("inv", invoiceNo);

        Long count = (Long) checkQuery.getSingleResult();

        if (count > 0) {
            JOptionPane.showMessageDialog(null, "GRN already exists for this supplier and invoice!");
            return;
        }

        try {
            em.getTransaction().begin();

            // =========================
            // 1. CREATE & SAVE GRN
            // =========================
            int sup_id = generalMethods.extractIdFromCombo(inv_grn_supplier_combo.getEditor().getItem().toString());

            Grn grn = new Grn();
            grn.setSuppliersId(sup_id); // from UI
            grn.setInvoiceNo(invoiceNo);
            grn.setGrnDate(new Date());
            grn.setRemarks("");
            grn.setUser(username);
            grn.setStatus(1);

            em.persist(grn);
            em.flush(); // generate grn_id

            int grnId = grn.getGrnId();

            // =========================
            // 2. LOOP TABLE ITEMS
            // =========================
            DefaultTableModel model = (DefaultTableModel) inv_grn_table.getModel();

            for (int i = 0; i < model.getRowCount(); i++) {

                GrnItems item = new GrnItems();
                int itemId = generalMethods.extractIdFromCombo(model.getValueAt(i, 4).toString());
                // int itemId = itemDAO.getItemIdByName(model.getValueAt(i, 4).toString());

                // int itemId = selectedItemId;
                String units = (String) model.getValueAt(i, 5);
                double unitPrice = GeneralMethods.parseCommaNumber(model.getValueAt(i, 6).toString());
                double qty = GeneralMethods.parseCommaNumber(model.getValueAt(i, 7).toString());
                double discount = GeneralMethods.parseCommaNumber(model.getValueAt(i, 9).toString());

                double lineTotal = GeneralMethods.parseCommaNumber(model.getValueAt(i, 10).toString());

                // =========================
                // GRN ITEM SAVE
                // =========================
                item.setGrnId(grnId);
                item.setItemId(itemId);
                item.setUnits(units);
                item.setUnitPrice(unitPrice);
                item.setQuantity(qty);
                item.setDiscountAmount(discount);
                item.setLineTotal(lineTotal);
                item.setRemarks("");
                item.setUser(username);
                item.setStatus(1);

                em.persist(item);

                // =========================
                // FEE TYPE SAVE (AUTO)
                // =========================

                Query feeQuery = em.createQuery(
                        "SELECT f FROM FeeTypes f WHERE f.itemId = :itemId AND f.status = 1"
                );

                feeQuery.setParameter("itemId", itemId);

                List<FeeTypes> feeList = feeQuery.getResultList();

                if (feeList.isEmpty()) {

                    // =========================
                    // INSERT NEW
                    // =========================
                    FeeTypes fee = new FeeTypes();
                    fee.setItemId(itemId);
                    fee.setFeeName(model.getValueAt(i, 4).toString().split("\\[")[0].trim());
                    fee.setFeeCategory("INVENTORY");
                    fee.setDefaultAmount(unitPrice);
                    fee.setUser(username);
                    fee.setStatus(1);

                    em.persist(fee);

                } else {

                    // =========================
                    // UPDATE EXISTING
                    // =========================
                    FeeTypes fee = feeList.get(0);

                    fee.setDefaultAmount(unitPrice); // update latest price
                    fee.setUser(username);

                    em.merge(fee);
                }

                // =========================
                // STOCK IN SAVE
                // =========================
                StockTransaction st = new StockTransaction();
                st.setItemId(itemId);
                st.setSuppliersId(sup_id);
                st.setInvoiceNo(inv_grn_invoice_no_text.getText());
                st.setQuantity(qty);
                st.setTransactionType("IN");
                st.setTransactionDate(new Date());
                st.setRemarks("GRN IN");
                st.setUser(username);
                st.setStatus(1);

                em.persist(st);
            }

            // =========================
            // COMMIT ALL
            // =========================
            em.getTransaction().commit();
            // ✅ LOG: GRN Header Created
            logHelper.log(
                    "INVENTORY_GRN",
                    grnId,
                    "GRN CREATE",
                    "Invoice No: " + invoiceNo,
                    GeneralMethods.parseCommaNumber(inv_grn_invoice_total_text.getText()), // Or you can calculate the total net amount of the table
                    "New GRN created for Supplier ID: " + sup_id + " | Invoice: " + invoiceNo,
                    username
            );

            JOptionPane.showMessageDialog(null, "GRN Saved Successfully!");

        } catch (Exception e) {

            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            JOptionPane.showMessageDialog(null, "Error Saving GRN: " + e.getMessage());
            e.printStackTrace();

        } finally {
            em.close();
        }


    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void inv_grn_invoice_no_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_grn_invoice_no_textActionPerformed
        inv_grn_item_combo.requestFocus();

    }//GEN-LAST:event_inv_grn_invoice_no_textActionPerformed

    private void inv_grn_unit_price_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inv_grn_unit_price_textKeyTyped
        ((AbstractDocument) inv_grn_unit_price_text.getDocument())
                .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_inv_grn_unit_price_textKeyTyped

    private void inv_grn_qty_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inv_grn_qty_textKeyTyped
        ((AbstractDocument) inv_grn_qty_text.getDocument())
                .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_inv_grn_qty_textKeyTyped

    private void inv_grn_discount_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_grn_discount_textActionPerformed
        try {

            if (inv_grn_supplier_combo.getEditor().getItem().toString().equals("") || inv_grn_item_combo.getEditor().getItem().toString().equals("")
                    || inv_grn_invoice_no_text.getText().equals("") || inv_grn_unit_price_text.getText().equals("") || inv_grn_qty_text.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Fields cannot be empty");
                return;
            }

            double dis = 0.0;
            if (!inv_grn_discount_text.getText().equals("") || inv_grn_discount_text.getText().equals("0")) {
                dis = GeneralMethods.parseCommaNumber(inv_grn_discount_text.getText());
            }

            DefaultTableModel model = (DefaultTableModel) inv_grn_table.getModel();

            double up = GeneralMethods.parseCommaNumber(inv_grn_unit_price_text.getText());
            double qty = GeneralMethods.parseCommaNumber(inv_grn_qty_text.getText());
            double lineTot = (up * qty) - dis;

            int count = 1;

            Object[] row = {
                count++,
                dft.format(inv_grn_date.getDate()),
                inv_grn_supplier_combo.getEditor().getItem().toString(),
                inv_grn_invoice_no_text.getText(),
                inv_grn_item_combo.getEditor().getItem().toString(),
                inv_grn_unit_combo.getSelectedItem().toString(),
                GeneralMethods.formatWithComma(up),
                GeneralMethods.formatWithComma(qty),
                GeneralMethods.formatWithComma(up * qty),
                GeneralMethods.formatWithComma(dis),
                GeneralMethods.formatWithComma(lineTot)

            };

            model.addRow(row);
            calculateInvoiceTotal();

            inv_grn_item_combo.removeAllItems();
            inv_grn_unit_combo.setSelectedIndex(0);
            inv_grn_unit_price_text.setText("");
            inv_grn_qty_text.setText("");
            inv_grn_discount_text.setText("");
            inv_grn_line_total_text.setText("");
            inv_grn_item_combo.requestFocus();

        } catch (Exception e) {
        }

    }//GEN-LAST:event_inv_grn_discount_textActionPerformed

    private void inv_grn_discount_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inv_grn_discount_textKeyTyped
        ((AbstractDocument) inv_grn_discount_text.getDocument())
        .setDocumentFilter(new DecimalOnlyFilter());
    }//GEN-LAST:event_inv_grn_discount_textKeyTyped

    private void inv_grn_line_total_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_grn_line_total_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inv_grn_line_total_textActionPerformed

    private void inv_grn_line_total_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inv_grn_line_total_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_inv_grn_line_total_textKeyTyped

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        if (inv_grn_supplier_combo.getEditor().getItem().toString().equals("") || inv_grn_invoice_no_text.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Supplier and Invoice No fields cannot be empty");
            return;
        }

        int sup_id = generalMethods.extractIdFromCombo(inv_grn_supplier_combo.getEditor().getItem().toString());
        loadGrnToTable(inv_grn_table, sup_id, inv_grn_invoice_no_text.getText());
        calculateInvoiceTotal();

    }//GEN-LAST:event_jButton5ActionPerformed

    private void inv_grn_invoice_total_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inv_grn_invoice_total_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_inv_grn_invoice_total_textActionPerformed

    private void inv_grn_invoice_total_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inv_grn_invoice_total_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_inv_grn_invoice_total_textKeyTyped


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private Classes.ButtonGradient buttonGradient6;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private com.toedter.calendar.JDateChooser inv_grn_date;
    private javax.swing.JTextField inv_grn_discount_text;
    private javax.swing.JTextField inv_grn_invoice_no_text;
    private javax.swing.JTextField inv_grn_invoice_total_text;
    private javax.swing.JComboBox<String> inv_grn_item_combo;
    private javax.swing.JTextField inv_grn_line_total_text;
    private javax.swing.JTextField inv_grn_qty_text;
    private javax.swing.JComboBox<String> inv_grn_supplier_combo;
    public static javax.swing.JTable inv_grn_table;
    private javax.swing.JComboBox<String> inv_grn_unit_combo;
    private javax.swing.JTextField inv_grn_unit_price_text;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

}
