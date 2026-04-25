package Panels_Reports;

import Classes.GeneralMethods;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.TableGradientCell;
import Classes.styleDateChooser;
import Pagination.EventPagination;
import Pagination.PaginationItemRenderStyle1;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Window;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Batch_Class_Student_Dialog extends javax.swing.JDialog {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Batch_Class_Student_Dialog.class.getName());
    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd-MM-yyyy");

    styleDateChooser styleDateChooser = new styleDateChooser();
    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    public Batch_Class_Student_Dialog(Window parent) {
        super(parent, ModalityType.APPLICATION_MODAL);

        initComponents();

        bc_st_table.setDefaultRenderer(Object.class, new TableGradientCell());
        bc_st_table.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "hoverBackground:null;"
                + "pressedBackground:null;"
                + "separatorColor:$TableHeader.background");

        //  loadCourseTable(bc_st_table);
//        pagination1.setPaginationItemRender(new PaginationItemRenderStyle1());
//        pagination1.addEventPagination(new EventPagination() {
//            @Override
//            public void pageChanged(int page) {
//                loaddata(page);
//            }
//        });
        pagination1.setPaginationItemRender(new PaginationItemRenderStyle1());

        pagination1.addEventPagination(new EventPagination() {
            @Override
            public void pageChanged(int page) {
                loadCourseTable(bc_st_table, page);
            }
        });

// first load
        loadCourseTable(bc_st_table, 1);

        // generalMethods.enableTableSearch(ser_misc_table, ser_misc_sort_text);
    }

    public void loadCourseTable(JTable bc_st_table, int page) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            DefaultTableModel model = (DefaultTableModel) bc_st_table.getModel();
            model.setRowCount(0);

            int limit = 10; // rows per page

            // =====================================================
            // TOTAL COUNT
            // =====================================================
            Number totalCountResult = (Number) em.createNativeQuery(
                    "SELECT COUNT(*) "
                    + "FROM course "
                    + "WHERE status = 1"
            ).getSingleResult();

            int count = totalCountResult.intValue();

            // IMPORTANT FIX → use double division
            int totalPage = (int) Math.ceil((double) count / limit);

            pagination1.setPagegination(page, totalPage);

            // =====================================================
            // FETCH PAGINATED DATA
            // =====================================================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT course_id, batch, course_name, "
                    + "enrol_year, enrol_month, "
                    + "comp_year, comp_month "
                    + "FROM course "
                    + "WHERE status = 1 "
                    + "ORDER BY batch ASC "
                    + "LIMIT ? OFFSET ?"
            )
                    .setParameter(1, limit)
                    .setParameter(2, (page - 1) * limit)
                    .getResultList();

            int rowNo = ((page - 1) * limit) + 1;

            for (Object[] row : list) {

                int courseId = ((Number) row[0]).intValue();

                String batch = row[1] != null ? row[1].toString() : "";
                String courseName = row[2] != null ? row[2].toString() : "";

                int startYear = row[3] != null ? ((Number) row[3]).intValue() : 0;
                int startMonth = row[4] != null ? ((Number) row[4]).intValue() : 0;

                int endYear = row[5] != null ? ((Number) row[5]).intValue() : 0;
                int endMonth = row[6] != null ? ((Number) row[6]).intValue() : 0;

                String start = startYear + "-" + String.format("%02d", startMonth);
                String end = endYear + "-" + String.format("%02d", endMonth);

                model.addRow(new Object[]{
                    rowNo++, // #
                    batch, // Batch
                    courseName, // Course Name
                    start, // Start
                    end, // End
                    courseId // Course ID
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

//    public void loadCourseTable(JTable bc_st_table) {
//
//        EntityManager em = HibernateConfig.getEntityManager();
//
//        try {
//            DefaultTableModel model = (DefaultTableModel) bc_st_table.getModel();
//            model.setRowCount(0);
//
//            // =====================================================
//            // FETCH ALL ACTIVE COURSES
//            // =====================================================
//            List<Object[]> list = em.createNativeQuery(
//                    "SELECT course_id, batch, course_name, "
//                    + "enrol_year, enrol_month, "
//                    + "comp_year, comp_month "
//                    + "FROM course "
//                    + "WHERE status = 1 "
//                    + "ORDER BY batch ASC"
//            ).getResultList();
//
//            int rowNo = 1;
//
//            for (Object[] row : list) {
//
//                int courseId = ((Number) row[0]).intValue();
//
//                String batch = row[1] != null ? row[1].toString() : "";
//                String courseName = row[2] != null ? row[2].toString() : "";
//
//                int startYear = row[3] != null ? ((Number) row[3]).intValue() : 0;
//                int startMonth = row[4] != null ? ((Number) row[4]).intValue() : 0;
//
//                int endYear = row[5] != null ? ((Number) row[5]).intValue() : 0;
//                int endMonth = row[6] != null ? ((Number) row[6]).intValue() : 0;
//
//                String start = startYear + "-" + String.format("%02d", startMonth);
//                String end = endYear + "-" + String.format("%02d", endMonth);
//
//                model.addRow(new Object[]{
//                    rowNo++, // #
//                    batch, // Batch
//                    courseName, // Course Name
//                    start, // Start
//                    end, // End
//                    courseId // Course ID
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
//    private void loaddata(int page){
//    
//        DefaultTableModel model = (DefaultTableModel) bc_st_table.getModel();
//        model.setRowCount(0);
//        int limit = 10;
//        String sqlCount = "select count(*) from course";
//        PreparedStatement ps = SingleConnection.getInstance().openConnection().preparedStatement(sqlCount);
//        int cont = 0;
//        ResultSet r = ps.executeQuery();
//        if(r.first()){
//            count = r.getInt(1);
//        }
//        r.close();
//        ps.close();
//        int totalPage = (int) Math.ceil(count / limit);
//        String sql = "Select batch, course_name from course limit "+(page-1)*limit+" ,"+limit;
//    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        panelRound2 = new Classes.PanelRound();
        Main_Lable = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        bc_st_table = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        bc_st_sort_text = new javax.swing.JTextField();
        pagination1 = new Pagination.Pagination();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        panelRound2.setBackground(new java.awt.Color(247, 178, 50));
        panelRound2.setRoundBottomLeft(10);
        panelRound2.setRoundBottomRight(10);
        panelRound2.setRoundTopLeft(10);
        panelRound2.setRoundTopRight(10);

        Main_Lable.setFont(new java.awt.Font("Roboto Black", 3, 14)); // NOI18N
        Main_Lable.setForeground(new java.awt.Color(255, 255, 255));
        Main_Lable.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        Main_Lable.setText("Batch / Course List");

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

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Batch / Courses", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        bc_st_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "#", "Batch", "Course", "Start", "End", "id"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        bc_st_table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bc_st_tableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(bc_st_table);
        if (bc_st_table.getColumnModel().getColumnCount() > 0) {
            bc_st_table.getColumnModel().getColumn(0).setPreferredWidth(50);
            bc_st_table.getColumnModel().getColumn(1).setPreferredWidth(120);
            bc_st_table.getColumnModel().getColumn(2).setPreferredWidth(200);
            bc_st_table.getColumnModel().getColumn(3).setPreferredWidth(100);
            bc_st_table.getColumnModel().getColumn(4).setPreferredWidth(100);
            bc_st_table.getColumnModel().getColumn(5).setMinWidth(0);
            bc_st_table.getColumnModel().getColumn(5).setPreferredWidth(0);
            bc_st_table.getColumnModel().getColumn(5).setMaxWidth(0);
        }

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Sort Table");

        bc_st_sort_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        bc_st_sort_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bc_st_sort_textActionPerformed(evt);
            }
        });
        bc_st_sort_text.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bc_st_sort_textKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bc_st_sort_text)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bc_st_sort_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 358, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 347, Short.MAX_VALUE))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(pagination1, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelRound2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pagination1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(95, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bc_st_tableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bc_st_tableMouseClicked

        try {

            int rowSelected = bc_st_table.getSelectedRow();
            String bat = bc_st_table.getValueAt(rowSelected, 1).toString();
            String cor = bc_st_table.getValueAt(rowSelected, 2).toString();
            int cid = Integer.parseInt(bc_st_table.getValueAt(rowSelected, 5).toString());

            Batch_Class_Student_report.btc_st_batch_combo.setSelectedItem(bat + " [" + cid + "]");
            Batch_Class_Student_report.btc_st_course_text.setText(cor);

            this.dispose();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_bc_st_tableMouseClicked

    private void bc_st_sort_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bc_st_sort_textActionPerformed

    }//GEN-LAST:event_bc_st_sort_textActionPerformed

    private void bc_st_sort_textKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bc_st_sort_textKeyTyped

    }//GEN-LAST:event_bc_st_sort_textKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> {

            JFrame frame = new JFrame();

            Batch_Class_Student_Dialog dialog
                    = new Batch_Class_Student_Dialog(frame);

            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public static javax.swing.JLabel Main_Lable;
    private javax.swing.JTextField bc_st_sort_text;
    public static javax.swing.JTable bc_st_table;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane3;
    private Pagination.Pagination pagination1;
    private Classes.PanelRound panelRound2;
    // End of variables declaration//GEN-END:variables

}
