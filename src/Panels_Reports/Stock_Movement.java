package Panels_Reports;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Pagination.EventPagination;
import Pagination.PaginationItemRenderStyle1;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Stock_Movement extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String currentSelectedClass = "";
    String username;
    String role;

    public Stock_Movement(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        stm_tm_table.setDefaultRenderer(Object.class, new TableGradientCell());
        stm_tm_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");
        stm_tm_table.setRowHeight(30);

        stm_tm_table.getTableHeader().setPreferredSize(
                new Dimension(
                        stm_tm_table.getTableHeader().getPreferredSize().width,
                        35
                )
        );

        pagination1.setPaginationItemRender(new PaginationItemRenderStyle1());
        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                loadStockTransactions(stm_tm_table, stm_option_combo, page);
            }
        });

    }

    public void loadStockTransactions(JTable table,
            JComboBox<String> stm_option_combo,
            int page) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int limit = 15;
            int offset = (page - 1) * limit;

            // =====================================================
            // 🔥 FILTER TYPE
            // =====================================================
            String selectedOption = stm_option_combo.getSelectedItem() != null
                    ? stm_option_combo.getSelectedItem().toString()
                    : "ALL STOCK";

            boolean isStockIn = selectedOption.equalsIgnoreCase("STOCK IN");
            boolean isStockOut = selectedOption.equalsIgnoreCase("STOCK OUT");

            // =====================================================
            // 🔥 COUNT QUERY
            // =====================================================
            String countSql = "SELECT COUNT(*) FROM stock_transactions st WHERE st.status = 1 ";

            if (isStockIn) {
                countSql += "AND st.transaction_type = 'IN' ";
            } else if (isStockOut) {
                countSql += "AND st.transaction_type = 'OUT' ";
            }

            Query countQuery = em.createNativeQuery(countSql);

            int totalRows = ((Number) countQuery.getSingleResult()).intValue();

            lbl_total_rows.setText("Total : " + totalRows + " Records");

            int totalPages = (int) Math.ceil((double) totalRows / limit);
            pagination1.setPagegination(page, totalPages);

            // =====================================================
            // 🔥 DATA QUERY (JOIN EVERYTHING HERE 🔥)
            // =====================================================
            String dataSql = "SELECT "
                    + "DATE(st.transaction_date), "
                    + "st.transaction_type, "
                    + "st.invoice_no, "
                    + "st.quantity, "
                    + "s.supplier_name, "
                    + "stu.full_name, "
                    + "stu.admission_no, "
                    + "i.item_name "
                    + "FROM stock_transactions st "
                    + "LEFT JOIN suppliers s ON st.suppliers_id = s.suppliers_id "
                    + "LEFT JOIN student stu ON st.student_id = stu.student_id "
                    + "LEFT JOIN items i ON st.item_id = i.item_id "
                    + "WHERE st.status = 1 ";

            if (isStockIn) {
                dataSql += "AND st.transaction_type = 'IN' ";
            } else if (isStockOut) {
                dataSql += "AND st.transaction_type = 'OUT' ";
            }

            dataSql += "ORDER BY st.transaction_date DESC LIMIT ? OFFSET ?";

            Query dataQuery = em.createNativeQuery(dataSql);

            dataQuery.setParameter(1, limit);
            dataQuery.setParameter(2, offset);

            List<Object[]> list = dataQuery.getResultList();

            int rowNo = offset + 1;

            // =====================================================
            // 🔥 LOAD TABLE
            // =====================================================
            for (Object[] row : list) {

                String date = row[0] != null ? row[0].toString() : "";
                String type = row[1] != null ? row[1].toString() : "";
                String invoiceNo = row[2] != null ? row[2].toString() : "";
                double qty = row[3] != null ? ((Number) row[3]).doubleValue() : 0;

                String supplierName = row[4] != null ? row[4].toString() : null;
                String studentName = row[5] != null ? row[5].toString() : null;
                String admissionNo = row[6] != null ? row[6].toString() : "";
                String itemName = row[7] != null ? row[7].toString() : "";

                // =====================================================
                // 🔥 SUPPLIER / STUDENT LOGIC
                // =====================================================
                String displayName;
                String displayInvoice;

                if (supplierName != null && !supplierName.isEmpty()) {
                    displayName = supplierName;
                    displayInvoice = invoiceNo;
                } else {
                    displayName = studentName != null ? studentName : "";
                    displayInvoice = admissionNo;
                }

                model.addRow(new Object[]{
                    rowNo++,
                    date,
                    type,
                    displayInvoice,
                    displayName,
                    itemName,
                    GeneralMethods.formatWithComma(qty)
                });
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        stm_tm_table = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        stm_option_combo = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        pagination1 = new Pagination.Pagination();
        lbl_total_rows = new javax.swing.JLabel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Stock Movements", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/printblue32.png"))); // NOI18N
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/xlsx32.png"))); // NOI18N
        jButton2.setToolTipText("Siblings");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        stm_tm_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Date", "Transaction", "Invoice/Admission", "Supplier/Student Name", "Item Name", "qty"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        stm_tm_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                stm_tm_tableMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(stm_tm_table);
        if (stm_tm_table.getColumnModel().getColumnCount() > 0) {
            stm_tm_table.getColumnModel().getColumn(3).setPreferredWidth(150);
            stm_tm_table.getColumnModel().getColumn(4).setPreferredWidth(200);
            stm_tm_table.getColumnModel().getColumn(5).setPreferredWidth(200);
        }

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Transaction");

        stm_option_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_option_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "STOCK IN", "STOCK OUT", "ALL STOCK" }));

        jButton6.setBackground(new java.awt.Color(102, 102, 102));
        jButton6.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/search16.png"))); // NOI18N
        jButton6.setToolTipText("Course Enrolment");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(stm_option_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1330, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stm_option_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 499, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );

        pagination1.setOpaque(false);

        lbl_total_rows.setFont(new java.awt.Font("Roboto Medium", 3, 14)); // NOI18N
        lbl_total_rows.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_total_rows.setText("Total : 0 Records");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(pagination1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(440, 440, 440)
                        .addComponent(lbl_total_rows, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbl_total_rows, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pagination1, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE))
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void stm_tm_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_stm_tm_tableMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_tm_tableMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        loadStockTransactions(stm_tm_table, stm_option_combo, 1);
    }//GEN-LAST:event_jButton6ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lbl_total_rows;
    private Pagination.Pagination pagination1;
    private javax.swing.JComboBox<String> stm_option_combo;
    private javax.swing.JTable stm_tm_table;
    // End of variables declaration//GEN-END:variables

}
