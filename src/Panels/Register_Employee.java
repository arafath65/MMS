/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package Panels;

import Classes.GeneralMethods;
import Classes.TableGradientCell;
import Classes.ButtonGradientRound;
import Classes.CameraCapture;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.styleDateChooser;
import Entities.Employee.Employee;
import Entities.Employee.EmployeeCareerHistory;
import Entities.Madhrasa_Profile.MadhrasaProfile;
import Entities.Settings.Course;
import Entities.Settings.StudentClass;
import Entities.Student_Management.FeeTypes;
import JPA_DAO.Employee.EmployeeCareerHistoryDAO;
import JPA_DAO.Employee.EmployeeDAO;
import JPA_DAO.Employee.EmployeeLanguagesDAO;
import JPA_DAO.Madhrasa_Profile.MadhrasaProfileDAO;
import JPA_DAO.Settings.ClassDAO;
import JPA_DAO.Settings.CourseDAO;
import Panels_SubDialogs.Employee_Career_History;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.ComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;

public class Register_Employee extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    LogHelper logHelper = new LogHelper();

    private File selectedImageFile;

    int selectedEmployeeId = 0;
    String username;
    String role;

    public Register_Employee(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

        styleDateChooser.applyDarkTheme(emp_dob_date);
        styleDateChooser.applyDarkTheme(emp_joined_date);

        JComboPopulates();

    }

    private void JComboPopulates() {
        // 
        emp_emp_no_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_emp_no_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(emp_emp_no_combo, "employee_id", "employee_no", "employee", input);
            }

        });
        setupComboSelectionListener(emp_emp_no_combo, emp_file_no_combo);

        emp_file_no_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_file_no_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(emp_file_no_combo, "employee_id", "file_no", "employee", input);
            }

        });
        setupComboSelectionListener(emp_file_no_combo, emp_nic_combo);

        emp_nic_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_nic_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(emp_nic_combo, "employee_id", "nic", "employee", input);
            }

        });
        setupComboSelectionListener(emp_nic_combo, emp_name_with_initials_combo);

        emp_name_with_initials_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_name_with_initials_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(emp_name_with_initials_combo, "employee_id", "name_with_initials", "employee", input);
            }

        });
        setupComboSelectionListener(emp_name_with_initials_combo, emp_full_name_combo);

        emp_full_name_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_full_name_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItemswithID(emp_full_name_combo, "employee_id", "full_name", "employee", input);
            }

        });
        setupComboSelectionListener(emp_full_name_combo, emp_nationality_combo);

        emp_nationality_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_nationality_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_nationality_combo, "nationality", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_nationality_combo, emp_gender_combo);

        emp_area_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_area_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_area_combo, "area", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_area_combo, emp_contact_text);

        emp_category_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_category_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_category_combo, "employee_category", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_category_combo, emp_job_title_combo);

        emp_job_title_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_job_title_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_job_title_combo, "job_title", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_job_title_combo, emp_department_combo);

        emp_department_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_department_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_department_combo, "department", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_department_combo, emp_employment_type_combo);

        emp_bank_name_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_bank_name_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_bank_name_combo, "bank_name", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_bank_name_combo, emp_branch_combo);

        emp_branch_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = emp_branch_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(emp_branch_combo, "bank_branch", "employee", input);
            }

        });
        setupComboSelectionListener2(emp_branch_combo, emp_account_number_text);

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

                            int emp_id = generalMethods.extractIdFromCombo(selectedValue);
                            selectedEmployeeId = emp_id;

                            loadEmployee(selectedEmployeeId);
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

    public void setupComboSelectionListener2(JComboBox<String> comboBox, JComponent nextFocusComponent) {
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

                            int emp_id = generalMethods.extractIdFromCombo(selectedValue);
                            selectedEmployeeId = emp_id;

                            loadEmployee(selectedEmployeeId);
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

    private void showPhotoOptionDialog() {

        Window parent = SwingUtilities.getWindowAncestor(this);

        JDialog dialog = new JDialog(parent);
        dialog.setUndecorated(true);
        dialog.setSize(420, 220);
        dialog.setLayout(null);
        dialog.setBackground(new Color(0, 0, 0, 0));
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(null) {

            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                g2.setColor(new Color(0, 0, 0, 120));
                g2.fillRoundRect(10, 10, getWidth() - 10,
                        getHeight() - 10, 30, 30);

                g2.setColor(Color.decode("#2B2B2B"));
                g2.fillRoundRect(0, 0,
                        getWidth() - 10,
                        getHeight() - 10,
                        30, 30);

                g2.dispose();
            }
        };

        panel.setOpaque(false);
        panel.setBounds(0, 0, 420, 220);

        JLabel title = new JLabel("Select Photo Option");
        title.setBounds(30, 25, 250, 30);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JButton closeBtn = new JButton("X") {
            @Override
            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                Color color = getModel().isRollover()
                        ? new Color(190, 30, 45)
                        : new Color(220, 53, 69);

                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth("X")) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;

                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString("X", x, y);

                g2.dispose();
            }
        };

        closeBtn.setBounds(372, 14, 26, 26);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());

        panel.add(title);
        panel.add(closeBtn);

        JButton takePhotoBtn = createAnimatedGradientButton(
                "TAKE PHOTO",
                new Color(0, 153, 102),
                new Color(0, 204, 153)
        );

        takePhotoBtn.setBounds(40, 110, 150, 45);

        JButton uploadBtn = createAnimatedGradientButton(
                "UPLOAD",
                new Color(0, 102, 204),
                new Color(0, 180, 255)
        );

        uploadBtn.setBounds(220, 110, 150, 45);

        panel.add(takePhotoBtn);
        panel.add(uploadBtn);

        dialog.add(panel);

        uploadBtn.addActionListener(e -> {

            selectedImageFile
                    = GeneralMethods.chooseAndSetImageAutoResizeRemember(jLabel9);

            dialog.dispose();

        });

        takePhotoBtn.addActionListener(e -> {

            dialog.dispose();

            openStudentCamera();

        });

        dialog.setVisible(true);
    }

    private void openStudentCamera() {

        CameraCapture camera
                = new CameraCapture(this, jLabel9);

        camera.openCamera();
    }

    private void saveEmployeeLanguages(Employee employee) {

        EmployeeLanguagesDAO dao2 = new EmployeeLanguagesDAO();

        dao2.saveOrUpdateLanguage(employee, "English",
                emp_lang_english_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Sinhala",
                emp_lang_sinhala_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Tamil",
                emp_lang_tamil_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Arabic",
                emp_lang_arabic_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Hindi",
                emp_lang_hindi_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "French",
                emp_lang_french_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Malay",
                emp_lang_malay_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Japanese",
                emp_lang_japanese_checkbox.isSelected(), username);

        dao2.saveOrUpdateLanguage(employee, "Korean",
                emp_lang_korean_checkbox.isSelected(), username);

    }

    public void loadEmployee(int employeeId) {

        EmployeeDAO dao = new EmployeeDAO();

        Employee employee = dao.getEmployeeById(employeeId);

        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Employee not found.");
            return;
        }

        selectedEmployeeId = employee.getEmployeeId();

        //====================================================
        // BASIC DETAILS
        //====================================================
        emp_emp_no_combo.getEditor().setItem(employee.getEmployeeNo());
        emp_file_no_combo.getEditor().setItem(employee.getFileNo());
        emp_nic_combo.getEditor().setItem(employee.getNic());

        emp_name_with_initials_combo.getEditor().setItem(employee.getNameWithInitials());
        emp_full_name_combo.getEditor().setItem(employee.getFullName());
        emp_nationality_combo.getEditor().setItem(employee.getNationality());
        emp_gender_combo.setSelectedItem(employee.getGender());
        emp_dob_date.setDate(employee.getDob());
        emp_religion_combo.setSelectedItem(employee.getReligion());
        emp_blood_group_combo.setSelectedItem(employee.getBloodGroup());
        emp_maritial_status_combo.setSelectedItem(employee.getMaritialStatus());
        emp_area_combo.getEditor().setItem(employee.getArea());
        emp_contact_text.setText(employee.getContactNo());
        emp_whatsapp_text.setText(employee.getWhatsapp());
        emp_email_text.setText(employee.getEmail());
        emp_current_address_text.setText(employee.getCurrentAddress());
        emp_permanent_address_text.setText(employee.getPermanentAddress());
        emp_joined_date.setDate(employee.getJoinedDate());
        emp_category_combo.setSelectedItem(employee.getEmployeeCategory());
        emp_job_title_combo.getEditor().setItem(employee.getJobTitle());

        emp_basic_salary_text.setText(
                GeneralMethods.formatWithComma(employee.getBasicSalary())
        );

        emp_department_combo.getEditor().setItem(employee.getDepartment());
        emp_employment_type_combo.setSelectedItem(employee.getEmploymentType());
        emp_current_status_combo.setSelectedItem(employee.getCurrentStatus());
        emp_medium_combo.setSelectedItem(employee.getMedium());
        emp_bank_name_combo.getEditor().setItem(employee.getBankName());
        emp_branch_combo.getEditor().setItem(employee.getBankBranch());
        emp_account_number_text.setText(employee.getAccountNumber());
        emp_remarks_textpane.setText(employee.getRemarks());

        //====================================================
        // CLEAR LANGUAGE CHECKBOXES
        //====================================================
        emp_lang_english_checkbox.setSelected(false);
        emp_lang_sinhala_checkbox.setSelected(false);
        emp_lang_tamil_checkbox.setSelected(false);
        emp_lang_arabic_checkbox.setSelected(false);
        emp_lang_hindi_checkbox.setSelected(false);
        emp_lang_french_checkbox.setSelected(false);
        emp_lang_malay_checkbox.setSelected(false);
        emp_lang_japanese_checkbox.setSelected(false);
        emp_lang_korean_checkbox.setSelected(false);

        //====================================================
        // LOAD LANGUAGES
        //====================================================
        EmployeeLanguagesDAO dao2 = new EmployeeLanguagesDAO();

        List<String> languages = dao2.getActiveLanguagesByEmployeeId(employeeId);

        for (String lang : languages) {

            switch (lang.toUpperCase()) {

                case "ENGLISH" ->
                    emp_lang_english_checkbox.setSelected(true);

                case "SINHALA" ->
                    emp_lang_sinhala_checkbox.setSelected(true);

                case "TAMIL" ->
                    emp_lang_tamil_checkbox.setSelected(true);

                case "ARABIC" ->
                    emp_lang_arabic_checkbox.setSelected(true);

                case "HINDI" ->
                    emp_lang_hindi_checkbox.setSelected(true);

                case "FRENCH" ->
                    emp_lang_french_checkbox.setSelected(true);

                case "MALAY" ->
                    emp_lang_malay_checkbox.setSelected(true);

                case "JAPANESE" ->
                    emp_lang_japanese_checkbox.setSelected(true);

                case "KOREAN" ->
                    emp_lang_korean_checkbox.setSelected(true);
            }
        }

        // ===============================
        // 3️⃣ Load Student Image
        // ===============================
        try {
            String employeeNo = employee.getEmployeeNo();

            // Remove [id] part if present
            if (employeeNo.contains("[")) {
                employeeNo = employeeNo.substring(0, employeeNo.indexOf("[")).trim();
            }

            String fileName = employeeNo + ".png";
            File imgFile = new File(GeneralMethods.IMAGE_SAVE_BASE_PATH_EMPLOYEE + fileName);

            BufferedImage img = null;

            if (imgFile.exists()) {
                img = ImageIO.read(imgFile);
            }

            if (img == null) {
                InputStream is = getClass().getResourceAsStream("/images/student_logo.png");
                if (is != null) {
                    img = ImageIO.read(is);
                    is.close();
                }
            }

            if (img != null) {
                jLabel9.setIcon(new ImageIcon(
                        GeneralMethods.resizeImage(img, 171, 163)
                ));
            } else {
                jLabel9.setIcon(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            jLabel9.setIcon(null);
        }
    }

    private void saveEmployeeCareerHistory(Employee employee) {

        EmployeeCareerHistory history = new EmployeeCareerHistory();

        history.setEmployee(employee);
        history.setEffectiveDate(employee.getJoinedDate());
        history.setDesignation(employee.getJobTitle());
        history.setSalary(employee.getBasicSalary());
        history.setChangeType("JOINED");
        history.setRemarks("Initial appointment");
        history.setUser(username);
        history.setStatus(1);

        new EmployeeCareerHistoryDAO().save(history);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        buttonGradient5 = new Classes.ButtonGradient();
        emp_emp_no_combo = new javax.swing.JComboBox<>();
        emp_file_no_combo = new javax.swing.JComboBox<>();
        emp_full_name_combo = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        emp_name_with_initials_combo = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        emp_nic_combo = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        emp_nationality_combo = new javax.swing.JComboBox<>();
        jLabel6 = new javax.swing.JLabel();
        emp_gender_combo = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        emp_dob_date = new com.toedter.calendar.JDateChooser();
        jLabel19 = new javax.swing.JLabel();
        emp_religion_combo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        emp_maritial_status_combo = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        emp_blood_group_combo = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        emp_contact_text = new javax.swing.JTextField();
        emp_whatsapp_text = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        emp_email_text = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        emp_current_address_text = new javax.swing.JTextField();
        emp_permanent_address_text = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        emp_area_combo = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        buttonGradient2 = new Classes.ButtonGradient();
        buttonGradient3 = new Classes.ButtonGradient();
        buttonGradient4 = new Classes.ButtonGradient();
        jPanel5 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        emp_joined_date = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        emp_category_combo = new javax.swing.JComboBox<>();
        jButton6 = new javax.swing.JButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        emp_department_combo = new javax.swing.JComboBox<>();
        jLabel22 = new javax.swing.JLabel();
        emp_basic_salary_text = new javax.swing.JTextField();
        emp_job_title_combo = new javax.swing.JComboBox<>();
        jLabel25 = new javax.swing.JLabel();
        emp_employment_type_combo = new javax.swing.JComboBox<>();
        emp_current_status_combo = new javax.swing.JComboBox<>();
        jLabel28 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        emp_medium_combo = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jButton7 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        emp_bank_name_combo = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        emp_branch_combo = new javax.swing.JComboBox<>();
        jLabel27 = new javax.swing.JLabel();
        emp_account_number_text = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        emp_lang_english_checkbox = new javax.swing.JCheckBox();
        emp_lang_sinhala_checkbox = new javax.swing.JCheckBox();
        emp_lang_tamil_checkbox = new javax.swing.JCheckBox();
        emp_lang_arabic_checkbox = new javax.swing.JCheckBox();
        emp_lang_hindi_checkbox = new javax.swing.JCheckBox();
        emp_lang_malay_checkbox = new javax.swing.JCheckBox();
        emp_lang_french_checkbox = new javax.swing.JCheckBox();
        emp_lang_japanese_checkbox = new javax.swing.JCheckBox();
        emp_lang_korean_checkbox = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        emp_remarks_textpane = new javax.swing.JEditorPane();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "General Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Emp No");

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Full Name");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("File Number");

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Image", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 0, 14))); // NOI18N

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/student_logo.png"))); // NOI18N
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel9MouseClicked(evt);
            }
        });

        buttonGradient5.setText("RESET");
        buttonGradient5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                    .addComponent(buttonGradient5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        emp_emp_no_combo.setEditable(true);
        emp_emp_no_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        emp_file_no_combo.setEditable(true);
        emp_file_no_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        emp_full_name_combo.setEditable(true);
        emp_full_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

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

        jLabel10.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel10.setText("Name with initials");

        emp_name_with_initials_combo.setEditable(true);
        emp_name_with_initials_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setText("NIC");

        emp_nic_combo.setEditable(true);
        emp_nic_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel11.setText("Nationality");

        emp_nationality_combo.setEditable(true);
        emp_nationality_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setText("Gender");

        emp_gender_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_gender_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Date of Birth");

        emp_dob_date.setForeground(new java.awt.Color(255, 255, 255));
        emp_dob_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel19.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel19.setText("Religion");

        emp_religion_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_religion_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buddhist", "Christian", "Islam", "Hindu", "Burger" }));

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("Maritial Status");

        emp_maritial_status_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_maritial_status_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Married", "Single", "Divorced", "Widow", "Seperated" }));

        jLabel18.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel18.setText("Blood Group");

        emp_blood_group_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_blood_group_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-" }));

        jLabel29.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 51, 51));
        jLabel29.setText("*");

        jLabel30.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 51, 51));
        jLabel30.setText("*");

        jLabel31.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 51, 51));
        jLabel31.setText("*");

        jLabel32.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 51, 51));
        jLabel32.setText("*");

        jLabel33.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 51, 51));
        jLabel33.setText("*");

        jLabel34.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 51, 51));
        jLabel34.setText("*");

        jLabel35.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 51, 51));
        jLabel35.setText("*");

        jLabel36.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 51, 51));
        jLabel36.setText("*");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(emp_emp_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel29)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel30)
                                        .addGap(60, 60, 60)
                                        .addComponent(jLabel13)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel31))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(emp_file_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(emp_nic_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel10)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel32))
                                    .addComponent(emp_name_with_initials_combo, 0, 197, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel34))
                                    .addComponent(emp_nationality_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel33))
                                    .addComponent(emp_full_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel6)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel35))
                                            .addComponent(emp_gender_combo, 0, 130, Short.MAX_VALUE)
                                            .addComponent(jLabel18)
                                            .addComponent(emp_blood_group_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jLabel36))
                                            .addComponent(emp_dob_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jLabel7)
                                            .addComponent(emp_maritial_status_combo, 0, 183, Short.MAX_VALUE)))))
                            .addComponent(emp_religion_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel19)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel13)
                                    .addComponent(jLabel29)
                                    .addComponent(jLabel30)
                                    .addComponent(jLabel31))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(emp_emp_no_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel32)
                                    .addComponent(jLabel33)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emp_file_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emp_nic_combo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(24, 24, 24)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(emp_name_with_initials_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emp_full_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel34))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emp_nationality_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                                        .addComponent(jLabel36))
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel35)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emp_dob_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emp_gender_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                                .addGroup(jPanel2Layout.createSequentialGroup()
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(8, 8, 8)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(emp_religion_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(emp_blood_group_combo)
                                .addComponent(emp_maritial_status_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))))
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/career.png"))); // NOI18N
        jButton1.setToolTipText("Add Career History");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Contact Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Contact");

        emp_contact_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_contact_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_contact_textActionPerformed(evt);
            }
        });

        emp_whatsapp_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_whatsapp_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_whatsapp_textActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("WhatsApp");

        jLabel17.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel17.setText("E-Mail");

        emp_email_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_email_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_email_textActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel20.setText("Current Address");

        emp_current_address_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_current_address_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_current_address_textActionPerformed(evt);
            }
        });

        emp_permanent_address_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_permanent_address_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_permanent_address_textActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel21.setText("Permenant Address");

        jLabel37.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(255, 51, 51));
        jLabel37.setText("*");

        jLabel38.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 51, 51));
        jLabel38.setText("*");

        jLabel39.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(255, 51, 51));
        jLabel39.setText("*");

        jLabel46.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel46.setText("Area");

        emp_area_combo.setEditable(true);
        emp_area_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(emp_current_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(emp_area_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel37))
                            .addComponent(emp_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emp_permanent_address_text)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel38)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(emp_whatsapp_text))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(emp_email_text, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(jLabel14)
                            .addComponent(jLabel38)
                            .addComponent(jLabel5)
                            .addComponent(jLabel37))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(emp_email_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(emp_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(emp_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_area_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel20)
                            .addComponent(jLabel39))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_current_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_permanent_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        buttonGradient2.setText("ADD NEW");
        buttonGradient2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient2ActionPerformed(evt);
            }
        });

        buttonGradient3.setText("SAVE / UPDATE");
        buttonGradient3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient3ActionPerformed(evt);
            }
        });

        buttonGradient4.setText("DELETE");
        buttonGradient4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Employment Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel12.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel12.setText("Joined Date");

        emp_joined_date.setForeground(new java.awt.Color(255, 255, 255));
        emp_joined_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel8.setText("Category");

        emp_category_combo.setEditable(true);
        emp_category_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

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

        jLabel15.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel15.setText("Job Title");

        jLabel16.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel16.setText("Employment Type");

        emp_department_combo.setEditable(true);
        emp_department_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel22.setText("Basic Salary");

        emp_basic_salary_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_basic_salary_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_basic_salary_textActionPerformed(evt);
            }
        });

        emp_job_title_combo.setEditable(true);
        emp_job_title_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel25.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel25.setText("Department");

        emp_employment_type_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_employment_type_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FULL TIME", "PART TIME", "GUEST LECTURER", "INTERN", "CONTRACTUAL" }));

        emp_current_status_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_current_status_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ACTIVE", "INACTIVE", "RESIGNED", "RETIRED", "TERMINATED", "DECEASED" }));

        jLabel28.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel28.setText("Current Status");

        jLabel23.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel23.setText("Medium");

        emp_medium_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_medium_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "English", "Sinhala", "Tamil", "Arabic" }));

        jLabel40.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(255, 51, 51));
        jLabel40.setText("*");

        jLabel41.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(255, 51, 51));
        jLabel41.setText("*");

        jLabel42.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(255, 51, 51));
        jLabel42.setText("*");

        jLabel43.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel43.setForeground(new java.awt.Color(255, 51, 51));
        jLabel43.setText("*");

        jLabel44.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel44.setForeground(new java.awt.Color(255, 51, 51));
        jLabel44.setText("*");

        jButton7.setBackground(new java.awt.Color(102, 102, 102));
        jButton7.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/closed_eye24.png"))); // NOI18N
        jButton7.setToolTipText("Course Enrolment");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
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
                        .addComponent(emp_joined_date, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_category_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_job_title_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel40)
                        .addGap(64, 64, 64)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel41)
                        .addGap(118, 118, 118)
                        .addComponent(jLabel15)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emp_department_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addComponent(emp_current_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel28)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel42)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(emp_employment_type_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel43))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel16)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel44)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel22)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(emp_basic_salary_text)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(emp_medium_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(6, 6, 6)))
                        .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel15))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addContainerGap()
                                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel12)
                                        .addComponent(jLabel40)))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel41))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emp_joined_date, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(emp_job_title_combo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(emp_category_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel16)
                        .addComponent(jLabel22)
                        .addComponent(jLabel44))
                    .addComponent(jLabel25))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(emp_department_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(emp_basic_salary_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(emp_employment_type_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel28)
                            .addComponent(jLabel42))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(emp_current_status_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(emp_medium_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(jLabel43))
                        .addGap(41, 41, 41)))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Bank Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        emp_bank_name_combo.setEditable(true);
        emp_bank_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel24.setText("Bank Name");

        jLabel26.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel26.setText("Branch");

        emp_branch_combo.setEditable(true);
        emp_branch_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel27.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel27.setText("Account Number");

        emp_account_number_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        emp_account_number_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                emp_account_number_textActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(102, 102, 102));
        jButton8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/closed_eye24.png"))); // NOI18N
        jButton8.setToolTipText("Course Enrolment");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emp_bank_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(emp_branch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel27)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(emp_account_number_text, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel27)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(emp_account_number_text)
                            .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_branch_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel24)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emp_bank_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Languages Known", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        emp_lang_english_checkbox.setText("English");
        emp_lang_english_checkbox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                emp_lang_english_checkboxMouseClicked(evt);
            }
        });

        emp_lang_sinhala_checkbox.setText("Sinhala");

        emp_lang_tamil_checkbox.setText("Tamil");

        emp_lang_arabic_checkbox.setText("Arabic");

        emp_lang_hindi_checkbox.setText("Hindi");

        emp_lang_malay_checkbox.setText("Malay");

        emp_lang_french_checkbox.setText("French");

        emp_lang_japanese_checkbox.setText("Japanese");

        emp_lang_korean_checkbox.setText("Korean");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(emp_lang_english_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_sinhala_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_tamil_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_arabic_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_hindi_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_malay_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_french_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_japanese_checkbox)
                .addGap(18, 18, 18)
                .addComponent(emp_lang_korean_checkbox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emp_lang_english_checkbox)
                    .addComponent(emp_lang_sinhala_checkbox)
                    .addComponent(emp_lang_tamil_checkbox)
                    .addComponent(emp_lang_arabic_checkbox)
                    .addComponent(emp_lang_hindi_checkbox)
                    .addComponent(emp_lang_malay_checkbox)
                    .addComponent(emp_lang_french_checkbox)
                    .addComponent(emp_lang_japanese_checkbox)
                    .addComponent(emp_lang_korean_checkbox))
                .addContainerGap())
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Remarks", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jScrollPane3.setViewportView(emp_remarks_textpane);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void buttonGradientRound4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradientRound4ActionPerformed

    }//GEN-LAST:event_buttonGradientRound4ActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
        showPhotoOptionDialog();
    }//GEN-LAST:event_jLabel9MouseClicked

    private void buttonGradient5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient5ActionPerformed
        try {

            InputStream imgStream = getClass().getResourceAsStream("/images/student_logo.png");
            if (imgStream == null) {
                System.out.println("Image resource not found!");
                return;
            }

            // Read the image
            BufferedImage img = ImageIO.read(imgStream);
            jLabel9.setIcon(new ImageIcon(img));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_buttonGradient5ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Employee_Career_History dialog = new Employee_Career_History(parentFrame, selectedEmployeeId, username, role);
        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

    }//GEN-LAST:event_jButton5ActionPerformed

    private void emp_contact_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_contact_textActionPerformed
        emp_whatsapp_text.requestFocus();
    }//GEN-LAST:event_emp_contact_textActionPerformed

    private void buttonGradient2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient2ActionPerformed

        emp_emp_no_combo.removeAllItems();
        emp_file_no_combo.removeAllItems();
        emp_nic_combo.removeAllItems();
        emp_name_with_initials_combo.removeAllItems();
        emp_full_name_combo.removeAllItems();
        emp_nationality_combo.removeAllItems();
        emp_gender_combo.setSelectedIndex(0);
        emp_dob_date.setDate(null);
        emp_religion_combo.setSelectedIndex(0);
        emp_blood_group_combo.setSelectedIndex(0);
        emp_maritial_status_combo.setSelectedIndex(0);

        emp_area_combo.removeAllItems();
        emp_contact_text.setText("");
        emp_whatsapp_text.setText("");
        emp_email_text.setText("");
        emp_current_address_text.setText("");
        emp_permanent_address_text.setText("");

        emp_joined_date.setDate(null);
        emp_category_combo.removeAllItems();
        emp_job_title_combo.removeAllItems();
        emp_basic_salary_text.setText("");
        emp_department_combo.removeAllItems();
        emp_employment_type_combo.setSelectedIndex(0);
        emp_current_status_combo.setSelectedIndex(0);
        emp_medium_combo.setSelectedIndex(0);

        emp_bank_name_combo.removeAllItems();
        emp_branch_combo.removeAllItems();
        emp_account_number_text.setText("");

        emp_remarks_textpane.setText("");

        emp_lang_english_checkbox.setSelected(false);
        emp_lang_sinhala_checkbox.setSelected(false);
        emp_lang_tamil_checkbox.setSelected(false);
        emp_lang_arabic_checkbox.setSelected(false);
        emp_lang_hindi_checkbox.setSelected(false);
        emp_lang_french_checkbox.setSelected(false);
        emp_lang_malay_checkbox.setSelected(false);
        emp_lang_japanese_checkbox.setSelected(false);
        emp_lang_korean_checkbox.setSelected(false);

        try {
            InputStream imgStream = getClass().getResourceAsStream("/images/student_logo.png");
            if (imgStream == null) {
                System.out.println("Image resource not found!");
                return;
            }

            // Read the image
            BufferedImage img = ImageIO.read(imgStream);
            jLabel9.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            e.printStackTrace();
        }

        selectedEmployeeId = 0;

    }//GEN-LAST:event_buttonGradient2ActionPerformed

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed

        try {

            EmployeeDAO dao = new EmployeeDAO();
            Employee employee;

            // =====================================================
            // SAVE OR UPDATE
            // =====================================================
            if (selectedEmployeeId == 0) {

                employee = new Employee();

            } else {

                employee = dao.findById(selectedEmployeeId);

                if (employee == null) {
                    JOptionPane.showMessageDialog(this, "Employee not found.");
                    return;
                }
            }

            // =====================================================
            // EMPLOYEE DETAILS
            // =====================================================
            employee.setEmployeeNo(emp_emp_no_combo.getEditor().getItem().toString());
            employee.setFileNo(emp_file_no_combo.getEditor().getItem().toString());
            employee.setNic(emp_nic_combo.getEditor().getItem().toString());
            employee.setNameWithInitials(emp_name_with_initials_combo.getEditor().getItem().toString());
            employee.setFullName(emp_full_name_combo.getEditor().getItem().toString());
            employee.setNationality(emp_nationality_combo.getEditor().getItem().toString());
            employee.setGender(emp_gender_combo.getSelectedItem().toString());
            employee.setDob(emp_dob_date.getDate());
            employee.setReligion(emp_religion_combo.getEditor().getItem().toString());
            employee.setBloodGroup(emp_blood_group_combo.getSelectedItem().toString());
            employee.setMaritialStatus(emp_maritial_status_combo.getSelectedItem().toString());

            employee.setArea(emp_area_combo.getEditor().getItem().toString());
            employee.setContactNo(emp_contact_text.getText());
            employee.setWhatsapp(emp_whatsapp_text.getText());
            employee.setEmail(emp_email_text.getText());
            employee.setCurrentAddress(emp_current_address_text.getText());
            employee.setPermanentAddress(emp_permanent_address_text.getText());

            employee.setJoinedDate(emp_joined_date.getDate());
            employee.setEmployeeCategory(emp_category_combo.getSelectedItem().toString());
            employee.setJobTitle(emp_job_title_combo.getEditor().getItem().toString());
            employee.setBasicSalary(GeneralMethods.parseCommaNumber(emp_basic_salary_text.getText()));
            employee.setDepartment(emp_department_combo.getEditor().getItem().toString());
            employee.setEmploymentType(emp_employment_type_combo.getSelectedItem().toString());
            employee.setCurrentStatus(emp_current_status_combo.getSelectedItem().toString());
            employee.setMedium(emp_medium_combo.getSelectedItem().toString());

            employee.setBankName(emp_bank_name_combo.getEditor().getItem().toString());
            employee.setBankBranch(emp_branch_combo.getEditor().getItem().toString());
            employee.setAccountNumber(emp_account_number_text.getText());

            employee.setRemarks(emp_remarks_textpane.getText());
            employee.setStatus(1);

            // Image handling
            BufferedImage imageToSave = GeneralMethods.resizedImageToSave;
            if (imageToSave == null) {
                imageToSave = GeneralMethods.getDefaultImage();
                GeneralMethods.resizedImageToSave = imageToSave;
            }

            if (imageToSave == null) {
                JOptionPane.showMessageDialog(this, "Failed to load image.");
                return;
            }

            String extension = "png";
            String fileName = generalMethods.extractBoxBracket(employee.getEmployeeNo()) + "." + extension;
            String fullSavePath = GeneralMethods.IMAGE_SAVE_BASE_PATH_EMPLOYEE + fileName;

            // =====================================================
            // SAVE / UPDATE
            // =====================================================
            boolean success;

            if (selectedEmployeeId == 0) {

                success = dao.save(employee);
                saveEmployeeLanguages(employee);

            } else {

                employee.setEmployeeNo(generalMethods.extractBoxBracket(employee.getEmployeeNo()));
                employee.setFileNo(generalMethods.extractBoxBracket(employee.getFileNo()));
                employee.setNic(generalMethods.extractBoxBracket(employee.getNic()));
                employee.setNameWithInitials(generalMethods.extractBoxBracket(employee.getNameWithInitials()));
                employee.setFullName(generalMethods.extractBoxBracket(employee.getFullName()));

                success = dao.update(employee);
                saveEmployeeLanguages(employee);

            }

            if (success) {

                Files.createDirectories(Paths.get(GeneralMethods.IMAGE_SAVE_BASE_PATH_EMPLOYEE));
                File outputPath = new File(fullSavePath);

                // Delete old image if exists
                if (outputPath.exists()) {
                    outputPath.delete();
                }

                ImageIO.write(imageToSave, extension, outputPath);
                saveEmployeeCareerHistory(employee);

                JOptionPane.showMessageDialog(
                        this,
                        selectedEmployeeId == 0
                                ? "Employee saved successfully."
                                : "Employee updated successfully."
                );

                selectedEmployeeId = employee.getEmployeeId();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Operation failed."
                );
            }

        } catch (Exception e) {
            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage()
            );
        }

    }//GEN-LAST:event_buttonGradient3ActionPerformed

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        try {

            if (selectedEmployeeId == 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select an employee.",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String empNo = emp_emp_no_combo.getEditor().getItem().toString();
            String empName = emp_full_name_combo.getEditor().getItem().toString();
            String jobTitle = emp_job_title_combo.getEditor().getItem().toString();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this employee?\n\n"
                    + "Employee No : " + empNo + "\n"
                    + "Employee Name : " + empName + "\n"
                    + "Job Title : " + jobTitle + "\n\n"
                    + "This employee will be marked as inactive.",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            EmployeeDAO dao = new EmployeeDAO();

            if (dao.softDelete(selectedEmployeeId)) {

                JOptionPane.showMessageDialog(
                        this,
                        "Employee deleted successfully."
                );
                selectedEmployeeId = 0;

                // Optional
                // clearFields();
                // loadEmployees();
            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to delete employee.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void emp_whatsapp_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_whatsapp_textActionPerformed
        emp_email_text.requestFocus();
    }//GEN-LAST:event_emp_whatsapp_textActionPerformed

    private void emp_email_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_email_textActionPerformed
        emp_current_address_text.requestFocus();
    }//GEN-LAST:event_emp_email_textActionPerformed

    private void emp_current_address_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_current_address_textActionPerformed
        emp_permanent_address_text.requestFocus();
    }//GEN-LAST:event_emp_current_address_textActionPerformed

    private void emp_permanent_address_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_permanent_address_textActionPerformed
        emp_category_combo.requestFocus();
    }//GEN-LAST:event_emp_permanent_address_textActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton6ActionPerformed

    private void emp_basic_salary_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_basic_salary_textActionPerformed
        emp_current_status_combo.requestFocus();
    }//GEN-LAST:event_emp_basic_salary_textActionPerformed

    private void emp_account_number_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_emp_account_number_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_emp_account_number_textActionPerformed

    private void emp_lang_english_checkboxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_emp_lang_english_checkboxMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_emp_lang_english_checkboxMouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton8ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient2;
    private Classes.ButtonGradient buttonGradient3;
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField emp_account_number_text;
    private javax.swing.JComboBox<String> emp_area_combo;
    private javax.swing.JComboBox<String> emp_bank_name_combo;
    private javax.swing.JTextField emp_basic_salary_text;
    private javax.swing.JComboBox<String> emp_blood_group_combo;
    private javax.swing.JComboBox<String> emp_branch_combo;
    private javax.swing.JComboBox<String> emp_category_combo;
    private javax.swing.JTextField emp_contact_text;
    private javax.swing.JTextField emp_current_address_text;
    private javax.swing.JComboBox<String> emp_current_status_combo;
    private javax.swing.JComboBox<String> emp_department_combo;
    private com.toedter.calendar.JDateChooser emp_dob_date;
    private javax.swing.JTextField emp_email_text;
    private javax.swing.JComboBox<String> emp_emp_no_combo;
    private javax.swing.JComboBox<String> emp_employment_type_combo;
    private javax.swing.JComboBox<String> emp_file_no_combo;
    private javax.swing.JComboBox<String> emp_full_name_combo;
    private javax.swing.JComboBox<String> emp_gender_combo;
    private javax.swing.JComboBox<String> emp_job_title_combo;
    private com.toedter.calendar.JDateChooser emp_joined_date;
    private javax.swing.JCheckBox emp_lang_arabic_checkbox;
    private javax.swing.JCheckBox emp_lang_english_checkbox;
    private javax.swing.JCheckBox emp_lang_french_checkbox;
    private javax.swing.JCheckBox emp_lang_hindi_checkbox;
    private javax.swing.JCheckBox emp_lang_japanese_checkbox;
    private javax.swing.JCheckBox emp_lang_korean_checkbox;
    private javax.swing.JCheckBox emp_lang_malay_checkbox;
    private javax.swing.JCheckBox emp_lang_sinhala_checkbox;
    private javax.swing.JCheckBox emp_lang_tamil_checkbox;
    private javax.swing.JComboBox<String> emp_maritial_status_combo;
    private javax.swing.JComboBox<String> emp_medium_combo;
    private javax.swing.JComboBox<String> emp_name_with_initials_combo;
    private javax.swing.JComboBox<String> emp_nationality_combo;
    private javax.swing.JComboBox<String> emp_nic_combo;
    private javax.swing.JTextField emp_permanent_address_text;
    private javax.swing.JComboBox<String> emp_religion_combo;
    private javax.swing.JEditorPane emp_remarks_textpane;
    private javax.swing.JTextField emp_whatsapp_text;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
private JButton createAnimatedGradientButton(String text, Color c1, Color c2) {

        JButton btn = new JButton(text) {

            private float scale = 1f;

            protected void paintComponent(Graphics g) {

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int width = (int) (getWidth() * scale);
                int height = (int) (getHeight() * scale);

                int x = (getWidth() - width) / 2;
                int y = (getHeight() - height) / 2;

                GradientPaint gp = new GradientPaint(
                        0, 0, c1,
                        getWidth(), getHeight(), c2
                );

                g2.setPaint(gp);
                g2.fillRoundRect(x, y, width, height, 20, 20);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));

        // Hover Animation
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                btn.setSize(btn.getWidth() + 2, btn.getHeight() + 2);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setSize(btn.getWidth() - 2, btn.getHeight() - 2);
            }
        });

        return btn;
    }

}
