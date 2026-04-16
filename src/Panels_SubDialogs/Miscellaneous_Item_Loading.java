package Panels_SubDialogs;

import Classes.ChequeNumberFormatter;
import Classes.DecimalOnlyFilter;
import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NumberOnlyFilter;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import JPA_DAO.Inventory.ItemDAO;
import JPA_DAO.Settings.CourseDAO;
import Panels.Fees_Management;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.PlainDocument;

public class Miscellaneous_Item_Loading extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Miscellaneous_Item_Loading.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    public Miscellaneous_Item_Loading(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);

        initComponents();

        ser_misc_table.setDefaultRenderer(Object.class, new TableGradientCell());
        ser_misc_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        loadServiceAndItems(ser_misc_table);

        // generalMethods.enableTableSearch(ser_misc_table, ser_misc_sort_text);
    }

    public void loadServiceAndItems(JTable table) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            String jpql = "SELECT f.feeTypeId, f.feeName, f.itemId "
                    + "FROM FeeTypes f WHERE f.status = 1";

            List<Object[]> list = em.createQuery(jpql).getResultList();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            int count = 1;

            for (Object[] row : list) {

                int feeTypeId = (int) row[0];
                String name = (String) row[1];
                Integer itemId = (Integer) row[2];

                double stock = 0.0;
                double price = 0.0;

                // =========================
                // IF ITEM LINKED → GET STOCK + PRICE
                // =========================
                if (itemId != null) {

                    // ---------- STOCK IN ----------
                    Query inQuery = em.createQuery(
                            "SELECT SUM(st.quantity) FROM StockTransaction st "
                            + "WHERE st.itemId = :itemId "
                            + "AND st.transactionType = 'IN' "
                            + "AND st.status = 1"
                    );
                    inQuery.setParameter("itemId", itemId);

                    Double inQty = (Double) inQuery.getSingleResult();
                    if (inQty == null) {
                        inQty = 0.0;
                    }

                    // ---------- STOCK OUT ----------
                    Query outQuery = em.createQuery(
                            "SELECT SUM(st.quantity) FROM StockTransaction st "
                            + "WHERE st.itemId = :itemId "
                            + "AND st.transactionType IN ('OUT','SALE','ISSUE') "
                            + "AND st.status = 1"
                    );
                    outQuery.setParameter("itemId", itemId);

                    Double outQty = (Double) outQuery.getSingleResult();
                    if (outQty == null) {
                        outQty = 0.0;
                    }

                    stock = inQty - outQty;

                    // ---------- LATEST PRICE ----------
                    Query priceQuery = em.createQuery(
                            "SELECT gi.unitPrice FROM GrnItems gi "
                            + "WHERE gi.itemId = :itemId AND gi.status = 1 "
                            + "ORDER BY gi.grnItemsId DESC"
                    );

                    priceQuery.setParameter("itemId", itemId);
                    priceQuery.setMaxResults(1);

                    List<Double> priceList = priceQuery.getResultList();

                    if (!priceList.isEmpty()) {
                        price = priceList.get(0);
                    }
                }

                // =========================
                // ADD TO TABLE
                // =========================
                Object[] data = {
                    count++,
                    name,
                    GeneralMethods.formatWithComma(stock),
                    GeneralMethods.formatWithComma(price),
                    itemId
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
        panelRound2 = new Classes.PanelRound();
        Main_Lable = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        ser_misc_table = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        ser_misc_sort_text = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("Issue Miscellaneous Items");

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

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Service / Items List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        ser_misc_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Service/Item", "Stock", "Amount", "itemId"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        ser_misc_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ser_misc_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(ser_misc_table);
        if (ser_misc_table.getColumnModel().getColumnCount() > 0) {
            ser_misc_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            ser_misc_table.getColumnModel().getColumn(1).setPreferredWidth(200);
            ser_misc_table.getColumnModel().getColumn(3).setPreferredWidth(80);
            ser_misc_table.getColumnModel().getColumn(4).setMinWidth(0);
            ser_misc_table.getColumnModel().getColumn(4).setPreferredWidth(0);
            ser_misc_table.getColumnModel().getColumn(4).setMaxWidth(0);
        }

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Sort Table");

        ser_misc_sort_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        ser_misc_sort_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ser_misc_sort_textActionPerformed(evt);
            }
        });
        ser_misc_sort_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ser_misc_sort_textKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ser_misc_sort_text)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ser_misc_sort_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ser_misc_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ser_misc_tableMouseClicked

        try {

            int rowSelected = ser_misc_table.getSelectedRow();
            String itemName = ser_misc_table.getValueAt(rowSelected, 1).toString();
            double qty = GeneralMethods.parseCommaNumber(ser_misc_table.getValueAt(rowSelected, 2).toString());
            double amount = GeneralMethods.parseCommaNumber(ser_misc_table.getValueAt(rowSelected, 3).toString());
            Object value = ser_misc_table.getValueAt(rowSelected, 4);
            String itemIds = (value == null || value.toString().trim().isEmpty()) ? "0" : value.toString();
            //int itemId = ;

            Miscellaneous_Issuing.reg_misc_service_combo.setSelectedItem(itemName + " [" + itemIds + "]");
            Miscellaneous_Issuing.reg_misc_stock_text.setText(GeneralMethods.formatWithComma(qty));
            Miscellaneous_Issuing.reg_misc_amount_text.setText(GeneralMethods.formatWithComma(amount));

            this.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_ser_misc_tableMouseClicked

    private void ser_misc_sort_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ser_misc_sort_textActionPerformed

    }//GEN-LAST:event_ser_misc_sort_textActionPerformed

    private void ser_misc_sort_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ser_misc_sort_textKeyTyped

    }//GEN-LAST:event_ser_misc_sort_textKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Miscellaneous_Item_Loading dialog
                    = new Miscellaneous_Item_Loading(frame);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane3;
    private Classes.PanelRound panelRound2;
    private javax.swing.JTextField ser_misc_sort_text;
    public static javax.swing.JTable ser_misc_table;
    // End of variables declaration//GEN-END:variables

}
