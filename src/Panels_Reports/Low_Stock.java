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

public class Low_Stock extends javax.swing.JPanel {

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    styleDateChooser stDateChooser = new styleDateChooser();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    private String currentSelectedClass = "";
    String username;
    String role;

    public Low_Stock(String username, String role) {
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
                loadLowStock(stm_tm_table, page);
            }
        });

    }

    public void loadLowStock(JTable table, int page) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int limit = 15;
            int offset = (page - 1) * limit;

            // =====================================================
            // 🔥 GET FILTER QTY
            // =====================================================
            double filterQty = -1;

            if (!lst_qty_text.getText().trim().isEmpty()) {
                filterQty = Double.parseDouble(lst_qty_text.getText().trim());
            }

            // =====================================================
            // 🔥 COUNT QUERY
            // =====================================================
            String countSql = "SELECT COUNT(*) FROM ( "
                    + "SELECT i.item_id, "
                    + "IFNULL(SUM(CASE WHEN st.transaction_type='IN' THEN st.quantity ELSE 0 END),0) - "
                    + "IFNULL(SUM(CASE WHEN st.transaction_type='OUT' THEN st.quantity ELSE 0 END),0) AS current_qty "
                    + "FROM items i "
                    + "LEFT JOIN stock_transactions st "
                    + "ON i.item_id = st.item_id AND st.status=1 "
                    + "WHERE i.status=1 "
                    + "GROUP BY i.item_id ";

            // 🔥 FILTER ONLY IF TEXT EXISTS
            if (filterQty >= 0) {
                countSql += "HAVING current_qty <= ? ";
            }

            countSql += ") x";

            Query countQuery = em.createNativeQuery(countSql);

            if (filterQty >= 0) {
                countQuery.setParameter(1, filterQty);
            }

            int totalRows = ((Number) countQuery.getSingleResult()).intValue();

            lbl_total_rows.setText("Total : " + totalRows + " Records");

            int totalPages = (int) Math.ceil((double) totalRows / limit);
            pagination1.setPagegination(page, totalPages);

            // =====================================================
            // 🔥 DATA QUERY
            // =====================================================
            String dataSql = "SELECT "
                    + "i.item_id, "
                    + "i.item_name, "
                    + "MAX(s.supplier_name), "
                    + "IFNULL(SUM(CASE WHEN st.transaction_type='IN' THEN st.quantity ELSE 0 END),0) - "
                    + "IFNULL(SUM(CASE WHEN st.transaction_type='OUT' THEN st.quantity ELSE 0 END),0) AS current_qty, "
                    + "MAX(CASE WHEN st.transaction_type='IN' THEN st.transaction_date END) AS last_in, "
                    + "MAX(CASE WHEN st.transaction_type='OUT' THEN st.transaction_date END) AS last_out "
                    + "FROM items i "
                    + "LEFT JOIN stock_transactions st "
                    + "ON i.item_id = st.item_id AND st.status=1 "
                    + "LEFT JOIN suppliers s "
                    + "ON st.suppliers_id = s.suppliers_id "
                    + "WHERE i.status=1 "
                    + "GROUP BY i.item_id ";

            // 🔥 FILTER ONLY IF TEXT EXISTS
            if (filterQty >= 0) {
                dataSql += "HAVING current_qty <= ? ";
            }

            dataSql += "ORDER BY current_qty ASC "
                    + "LIMIT ? OFFSET ?";

            Query dataQuery = em.createNativeQuery(dataSql);

            int paramIndex = 1;

            if (filterQty >= 0) {
                dataQuery.setParameter(paramIndex++, filterQty);
            }

            dataQuery.setParameter(paramIndex++, limit);
            dataQuery.setParameter(paramIndex, offset);

            List<Object[]> list = dataQuery.getResultList();

            int rowNo = offset + 1;

            // =====================================================
            // 🔥 LOAD TABLE
            // =====================================================
            for (Object[] row : list) {

                String itemName = row[1] != null ? row[1].toString() : "";
                String supplierName = row[2] != null ? row[2].toString() : "-";

                double currentQty = row[3] != null
                        ? ((Number) row[3]).doubleValue()
                        : 0;

                // =====================================================
                // 🔥 DATE FORMAT
                // =====================================================
                String lastIn = "";
                if (row[4] != null) {
                    lastIn = row[4].toString().split(" ")[0];
                }

                String lastOut = "";
                if (row[5] != null) {
                    lastOut = row[5].toString().split(" ")[0];
                }

                // =====================================================
                // 🔥 STATUS
                // =====================================================
                String status;

                if (currentQty <= 0) {
                    status = "OUT OF STOCK 🔴";
                } else if (currentQty < 5) {
                    status = "LOW STOCK > 5 🟡";
                } else {
                    status = "Available 🟢";
                }

                model.addRow(new Object[]{
                    rowNo++,
                    supplierName,
                    itemName,
                    GeneralMethods.formatWithComma(currentQty),
                    status,
                    lastIn,
                    lastOut
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

//    public void loadLowStock(JTable table, int page) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) table.getModel();
//            model.setRowCount(0);
//
//            int limit = 15;
//            int offset = (page - 1) * limit;
//
//            // =====================================================
//            // 🔥 COUNT QUERY (TOTAL ITEMS)
//            // =====================================================
//            String countSql = "SELECT COUNT(*) FROM items WHERE status=1";
//
//            int totalRows = ((Number) em.createNativeQuery(countSql)
//                    .getSingleResult()).intValue();
//
//            lbl_total_rows.setText("Total : " + totalRows + " Records");
//
//            int totalPages = (int) Math.ceil((double) totalRows / limit);
//            pagination1.setPagegination(page, totalPages);
//
//            // =====================================================
//            // 🔥 DATA QUERY WITH PAGINATION
//            // =====================================================
//            String dataSql = "SELECT "
//                    + "i.item_id, "
//                    + "i.item_name, "
//                    + "MAX(s.supplier_name), "
//                    + "IFNULL(SUM(CASE WHEN st.transaction_type='IN' THEN st.quantity ELSE 0 END),0) - "
//                    + "IFNULL(SUM(CASE WHEN st.transaction_type='OUT' THEN st.quantity ELSE 0 END),0) AS current_qty, "
//                    + "MAX(CASE WHEN st.transaction_type='IN' THEN st.transaction_date END) AS last_in, "
//                    + "MAX(CASE WHEN st.transaction_type='OUT' THEN st.transaction_date END) AS last_out "
//                    + "FROM items i "
//                    + "LEFT JOIN stock_transactions st ON i.item_id = st.item_id AND st.status=1 "
//                    + "LEFT JOIN suppliers s ON st.suppliers_id = s.suppliers_id "
//                    + "WHERE i.status=1 "
//                    + "GROUP BY i.item_id "
//                    + "ORDER BY current_qty ASC "
//                    + "LIMIT ? OFFSET ?";
//
//            List<Object[]> list = em.createNativeQuery(dataSql)
//                    .setParameter(1, limit)
//                    .setParameter(2, offset)
//                    .getResultList();
//
//            int rowNo = offset + 1;
//
//            // =====================================================
//            // 🔥 LOAD TABLE
//            // =====================================================
//            for (Object[] row : list) {
//
//                String itemName = row[1] != null ? row[1].toString() : "";
//                String supplierName = row[2] != null ? row[2].toString() : "-";
//
//                double currentQty = row[3] != null ? ((Number) row[3]).doubleValue() : 0;
//
//                // ✅ DATE FORMAT (yyyy-mm-dd only)
//                String lastIn = "";
//                if (row[4] != null) {
//                    lastIn = row[4].toString().split(" ")[0];
//                }
//
//                String lastOut = "";
//                if (row[5] != null) {
//                    lastOut = row[5].toString().split(" ")[0];
//                }
//
//                // =====================================================
//                // 🔥 STATUS LOGIC
//                // =====================================================
//                String status;
//
//                if (currentQty <= 0) {
//                    status = "OUT OF STOCK 🔴";
//                } else if (currentQty < 5) {
//                    status = "LOW STOCK 🟡";
//                } else {
//                    status = "GOOD 🟢";
//                }
//
//                model.addRow(new Object[]{
//                    rowNo++,
//                    supplierName,
//                    itemName,
//                    GeneralMethods.formatWithComma(currentQty),
//                    status,
//                    lastIn,
//                    lastOut
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
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
        jButton6 = new javax.swing.JButton();
        lst_qty_text = new javax.swing.JTextField();
        pagination1 = new Pagination.Pagination();
        lbl_total_rows = new javax.swing.JLabel();

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Low Stock Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

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
                "#", "Supplier Name", "Item Name", "Currenty Qty", "Status", "Last IN", "Last OUT"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, false
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
            stm_tm_table.getColumnModel().getColumn(0).setPreferredWidth(1);
            stm_tm_table.getColumnModel().getColumn(1).setPreferredWidth(200);
            stm_tm_table.getColumnModel().getColumn(2).setPreferredWidth(250);
        }

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Enter the qty");

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

        lst_qty_text.setFont(new java.awt.Font("Roboto Light", 1, 14)); // NOI18N
        lst_qty_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lst_qty_textActionPerformed(evt);
            }
        });
        lst_qty_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lst_qty_textKeyTyped(evt);
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
                                .addComponent(lst_qty_text, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addComponent(lst_qty_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
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
        loadLowStock(stm_tm_table, 1);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void lst_qty_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lst_qty_textActionPerformed
        jButton6.doClick();
    }//GEN-LAST:event_lst_qty_textActionPerformed

    private void lst_qty_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lst_qty_textKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_lst_qty_textKeyTyped


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
    private javax.swing.JTextField lst_qty_text;
    private Pagination.Pagination pagination1;
    private javax.swing.JTable stm_tm_table;
    // End of variables declaration//GEN-END:variables

}
