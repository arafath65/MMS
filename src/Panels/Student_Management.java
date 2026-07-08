package Panels;

import Additional.LogHelper_new;
import Classes.CameraCapture;
import Classes.GeneralMethods;
import Classes.GeneralMethods.StudentSearchType;
import Classes.HibernateConfig;
import Classes.LogHelper;
import Classes.NICParser;
import Classes.styleDateChooser;
import Entities.Student_Management.Student;
import Entities.Student_Management.StudentLanguages;
import Entities.Student_Management.StudentParents;
import JPA_DAO.Student_Management.StudentDAO;
import JPA_DAO.Student_Management.StudentParentsDAO;
import Panels_SubDialogs.Course_Enrollment;
import Panels_SubDialogs.Eliminate_Student;
import Panels_SubDialogs.Miscellaneous_Issuing;
import Panels_SubDialogs.Siblings_Register;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import java.util.List;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class Student_Management extends javax.swing.JPanel {

    GeneralMethods generalMethods = new GeneralMethods();
    //LogHelper logHelper = new LogHelper();
    LogHelper_new logHelper_new = new LogHelper_new();

    styleDateChooser stDateChooser = new styleDateChooser();

    private File selectedImageFile;

    private int selectedStudentId;

    String username;
    String role;

    public Student_Management(String username, String role) {
        this.username = username;
        this.role = role;
        initComponents();

// Admission Date
        styleDateChooser.applyDarkTheme(stm_ad_admission_date);
        styleDateChooser.applyDarkTheme(stm_ad_student_dob);

        jComboPopulates();

        loadLatestAdmissionNo();

        stm_ad_student_dob.addPropertyChangeListener("date", evt -> {
            NICParser.setAgeFromDOB(stm_ad_student_dob, stm_ad_student_age_text);
        });

        generalMethods.setIntegerOnly(stm_ad_student_contact_text, 10);

        generalMethods.setIntegerOnly(stm_ad_student_mother_contact_text, 10);
        generalMethods.setIntegerOnly(stm_ad_student_mother_whatsapp_text, 10);

        generalMethods.setIntegerOnly(stm_ad_student_father_contact_text, 10);
        generalMethods.setIntegerOnly(stm_ad_student_father_whatsapp_text, 10);

        generalMethods.setIntegerOnly(stm_ad_student_guardian_contact_text, 10);
        generalMethods.setIntegerOnly(stm_ad_student_guardian_whatsapp_text, 10);

    }

    private void jComboPopulates() {

        stm_ad_student_nationality_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = stm_ad_student_nationality_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(stm_ad_student_nationality_combo,
                        "nationality", // columns to show
                        "student", // table
                        input // user input
                );
            }
        });

        stm_ad_district_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = stm_ad_district_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(stm_ad_district_combo,
                        "district", // columns to show
                        "student", // table
                        input // user input
                );
            }
        });

        stm_ad_area_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String input = stm_ad_area_combo.getEditor().getItem().toString();
                generalMethods.loadMatchingComboItems(stm_ad_area_combo,
                        "area", // columns to show
                        "student", // table
                        input // user input
                );
            }
        });

        //*********************
        stm_ad_admission_no_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                String input = stm_ad_admission_no_combo.getEditor().getItem().toString();
                List<Student> list
                        = new StudentDAO().searchStudents(input, "ADMISSION");

                generalMethods.loadStudentCombo(stm_ad_admission_no_combo, list, input, StudentSearchType.ADMISSION);
            }
        });
        setupComboSelectionListeners1(stm_ad_admission_no_combo, stm_ad_form_no_combo, "ADMISSION");

        stm_ad_form_no_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                String input = stm_ad_form_no_combo.getEditor().getItem().toString();

                List<Student> list
                        = new StudentDAO().searchStudents(input, "FORM");

                generalMethods.loadStudentCombo(stm_ad_form_no_combo, list, input, StudentSearchType.FORM);
            }
        });
        setupComboSelectionListeners1(stm_ad_form_no_combo, stm_ad_student_name_combo, "FORM");

        stm_ad_student_name_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                String input = stm_ad_student_name_combo.getEditor().getItem().toString();

                List<Student> list
                        = new StudentDAO().searchStudents(input, "NAME");

                generalMethods.loadStudentCombo(stm_ad_student_name_combo, list, input, StudentSearchType.NAME);
            }
        });
        setupComboSelectionListeners2(stm_ad_student_name_combo, stm_ad_student_nic_combo, "NAME");

        stm_ad_student_nic_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {

                String input = stm_ad_student_nic_combo.getEditor().getItem().toString();

                List<Student> list
                        = new StudentDAO().searchStudents(input, "NIC");

                generalMethods.loadStudentCombo(stm_ad_student_nic_combo, list, input, StudentSearchType.NIC);

            }
        });
        setupComboSelectionListeners1(stm_ad_student_nic_combo, stm_ad_student_address_text, "NIC");

    }

    private boolean itemSelectedByUser1 = false;
    private boolean itemSelectedByUser2 = false;

    public void setupComboSelectionListeners1(JComboBox<String> comboBox, JComponent nextFocusComponent, String type) {
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

                String admis_combo = selectedValue.split(" - ")[0];

                loadStudentDynamic(admis_combo, type);
                comboBox.setSelectedItem(admis_combo);

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

    public void setupComboSelectionListeners2(JComboBox<String> comboBox, JComponent nextFocusComponent, String type) {
        comboBox.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                itemSelectedByUser2 = false;
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (!itemSelectedByUser2) {
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

                String name_combo = selectedValue.split(" - ")[1];

                loadStudentDynamic(name_combo, type);
                comboBox.setSelectedItem(name_combo);

                nextFocusComponent.requestFocus();
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                itemSelectedByUser2 = false;
            }
        });

        // Detect user selection via Enter or mouse click
        comboBox.addActionListener(e -> {
            if (comboBox.isPopupVisible()) {
                itemSelectedByUser2 = true;
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

    public void loadStudentDynamic(String searchText, String type) {

        StudentDAO studentDAO = new StudentDAO();

        try {

            if (searchText == null || searchText.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Please enter Admission No, Form No, NIC or Name",
                        "Warning",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            searchText = searchText.trim();

            // 🔎 Search student (DAO must use JOIN FETCH)
            List<Student> students = studentDAO.searchStudents(searchText, type);

            if (students == null || students.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "No student found for: " + searchText,
                        "Not Found",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Student student = students.get(0);

            // CHECK ELIMINATION BEFORE LOADING DETAILS
            Object[] elimination = studentDAO.getStudentElimination(student.getStudentId());

            if (elimination != null) {

                String eliminateType = elimination[0] != null
                        ? elimination[0].toString() : "";

                String admissionNo = elimination[1] != null
                        ? elimination[1].toString() : "";

                String studentName = elimination[2] != null
                        ? elimination[2].toString() : "";

                String note = elimination[3] != null
                        ? elimination[3].toString() : "";

                String message
                        = "This student is currently marked as "
                        + eliminateType + ".\n\n"
                        + "Admission No : " + admissionNo + "\n"
                        + "Student Name : " + studentName + "\n\n"
                        + "Reason : " + note + "\n\n"
                        + "Do you still want to load this student?";

                int option = JOptionPane.showConfirmDialog(
                        null,
                        message,
                        "Student Elimination Found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

                if (option != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            StudentLanguages lang = student.getStudentLanguages();

            stm_ad_english_chk.setSelected(false);
            stm_ad_sinhala_chk.setSelected(false);
            stm_ad_tamil_chk.setSelected(false);
            stm_ad_arabic_chk.setSelected(false);

            if (lang != null) {

                stm_ad_english_chk.setSelected(
                        Boolean.TRUE.equals(lang.getEnglish()));

                stm_ad_sinhala_chk.setSelected(
                        Boolean.TRUE.equals(lang.getSinhala()));

                stm_ad_tamil_chk.setSelected(
                        Boolean.TRUE.equals(lang.getTamil()));

                stm_ad_arabic_chk.setSelected(
                        Boolean.TRUE.equals(lang.getArabic()));
            }

            // ===============================
            // 1️⃣ STUDENT DETAILS
            // ===============================
            stm_ad_admission_no_combo.getEditor().setItem(student.getAdmissionNo());
            stm_ad_form_no_combo.getEditor().setItem(student.getFormNo());
            stm_ad_admission_date.setDate(student.getAdmissionDate());
            stm_ad_student_bc_text.setText(student.getBcNo());
            stm_ad_student_name_initials_combo.getEditor().setItem(student.getNameInitials());
            stm_ad_student_name_combo.getEditor().setItem(student.getFullName());
            stm_ad_student_nationality_combo.getEditor().setItem(student.getNationality());
            stm_ad_student_nic_combo.getEditor().setItem(student.getNic());
            stm_ad_student_gender_combo.setSelectedItem(student.getGender());
            stm_ad_student_medium_combo.setSelectedItem(student.getMedium());
            stm_ad_student_dob.setDate(student.getDob());
            stm_ad_student_blood_combo.setSelectedItem(student.getBloodGroup());
            stm_ad_student_religion_combo.setSelectedItem(student.getReligion());
            stm_ad_district_combo.getEditor().setItem(student.getDistrict());
            stm_ad_area_combo.getEditor().setItem(student.getArea());
            stm_ad_student_contact_text.setText(student.getContactNo());
            stm_ad_student_email_text.setText(student.getEmail());
            stm_ad_student_address_text.setText(student.getCurrentAddress());
            stm_ad_student_per_address_text.setText(student.getPermanentAddress());
            stm_ad_student_remarks_text.setText(student.getRemarks());
            stm_ad_student_medical_text.setText(student.getMedicalInfo());

            // ===============================
            // 2️⃣ PARENTS DETAILS (CORRECT WAY)
            // ===============================
            StudentParents parents = student.getStudentParents();

            // First clear all parent fields
            stm_ad_student_mother_name_text.setText("");
            stm_ad_student_mother_nic_text.setText("");
            stm_ad_student_mother_contact_text.setText("");
            stm_ad_student_mother_whatsapp_text.setText("");
            stm_ad_student_mother_occupation_combo.setSelectedItem(null);
            stm_ad_student_mother_living_yes_checkbox.setSelected(false);
            stm_ad_student_mother_living_no_checkbox.setSelected(false);
            stm_ad_student_mother_living_combo.setSelectedItem(null);

            stm_ad_student_father_name_text.setText("");
            stm_ad_student_father_nic_text.setText("");
            stm_ad_student_father_contact_text.setText("");
            stm_ad_student_father_whatsapp_text.setText("");
            stm_ad_student_father_occupation_combo.setSelectedItem(null);
            stm_ad_student_father_living_yes_checkbox.setSelected(false);
            stm_ad_student_father_living_no_checkbox.setSelected(false);
            stm_ad_student_father_living_combo.setSelectedItem(null);

            stm_ad_student_guardian_name_text.setText("");
            stm_ad_student_guardian_nic.setText("");
            stm_ad_student_guardian_relationship_combo.setSelectedItem(null);
            stm_ad_student_guardian_address_contact.setText("");
            stm_ad_student_guardian_contact_text.setText("");
            stm_ad_student_guardian_whatsapp_text.setText("");

            if (parents != null) {

                // Mother
                stm_ad_student_mother_name_text.setText(parents.getMotherName());
                stm_ad_student_mother_nic_text.setText(parents.getMotherNic());
                stm_ad_student_mother_contact_text.setText(parents.getMotherContact());
                stm_ad_student_mother_whatsapp_text.setText(parents.getMotherWhatsapp());
                stm_ad_student_mother_occupation_combo.getEditor().setItem(parents.getMotherOccupation());

                if ("YES".equalsIgnoreCase(parents.getMotherLivingWithChild())) {
                    stm_ad_student_mother_living_yes_checkbox.setSelected(true);
                    stm_ad_student_mother_living_no_checkbox.setSelected(false);
                } else if ("NO".equalsIgnoreCase(parents.getMotherLivingWithChild())) {
                    stm_ad_student_mother_living_yes_checkbox.setSelected(false);
                    stm_ad_student_mother_living_no_checkbox.setSelected(true);
                }

                stm_ad_student_mother_living_combo.setSelectedItem(parents.getMotherReason());

                // Father
                stm_ad_student_father_name_text.setText(parents.getFatherName());
                stm_ad_student_father_nic_text.setText(parents.getFatherNic());
                stm_ad_student_father_contact_text.setText(parents.getFatherContact());
                stm_ad_student_father_whatsapp_text.setText(parents.getFatherWhatsapp());
                stm_ad_student_father_occupation_combo.getEditor().setItem(parents.getFatherOccupation());

                if ("YES".equalsIgnoreCase(parents.getFatherLivingWithChild())) {
                    stm_ad_student_father_living_yes_checkbox.setSelected(true);
                    stm_ad_student_father_living_no_checkbox.setSelected(false);
                } else if ("NO".equalsIgnoreCase(parents.getFatherLivingWithChild())) {
                    stm_ad_student_father_living_no_checkbox.setSelected(true);
                    stm_ad_student_father_living_yes_checkbox.setSelected(false);
                } else {
                    stm_ad_student_father_living_no_checkbox.setSelected(false);
                    stm_ad_student_father_living_yes_checkbox.setSelected(false);
                }

                stm_ad_student_father_living_combo.setSelectedItem(parents.getFatherReason());

                // Guardian
                stm_ad_student_guardian_name_text.setText(parents.getGuardianName());
                stm_ad_student_guardian_nic.setText(parents.getGuardianNic());
                stm_ad_student_guardian_relationship_combo.getEditor().setItem(parents.getGuardianRelationship());
                stm_ad_student_guardian_address_contact.setText(parents.getGuardianAddress());
                stm_ad_student_guardian_contact_text.setText(parents.getGuardianContact());
                stm_ad_student_guardian_whatsapp_text.setText(parents.getGuardianWhatsapp());
            }

            // ===============================
            // 3️⃣ Load Student Image
            // ===============================
            try {
                String fileName = student.getAdmissionNo().replaceAll("\\s+", "_") + ".png";
                File imgFile = new File(GeneralMethods.IMAGE_SAVE_BASE_PATH + fileName);

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

            selectedStudentId = student.getStudentId();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error loading student: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    //******************************************************************************************************************************************
    public void updateStudentWithParents(int studentId,
            Student updatedStudent,
            StudentParents updatedParents,
            StudentLanguages updatedLanguages,
            BufferedImage newImage) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {
            em.getTransaction().begin();

            // 1️⃣ Load existing student
            Student existingStudent = em.find(Student.class, studentId);
            if (existingStudent == null) {
                JOptionPane.showMessageDialog(null, "Student not found for update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2️⃣ Update student fields
            existingStudent.setAdmissionNo(updatedStudent.getAdmissionNo());
            existingStudent.setFormNo(updatedStudent.getFormNo());
            existingStudent.setAdmissionDate(updatedStudent.getAdmissionDate());
            existingStudent.setBcNo(updatedStudent.getBcNo());
            existingStudent.setNameInitials(updatedStudent.getNameInitials());
            existingStudent.setFullName(updatedStudent.getFullName());
            existingStudent.setNationality(updatedStudent.getNationality());
            existingStudent.setNic(updatedStudent.getNic());
            existingStudent.setGender(updatedStudent.getGender());
            existingStudent.setMedium(updatedStudent.getMedium());
            existingStudent.setDob(updatedStudent.getDob());
            existingStudent.setBloodGroup(updatedStudent.getBloodGroup());
            existingStudent.setReligion(updatedStudent.getReligion());
            existingStudent.setDistrict(updatedStudent.getDistrict());
            existingStudent.setArea(updatedStudent.getArea());
            existingStudent.setContactNo(updatedStudent.getContactNo());
            existingStudent.setEmail(updatedStudent.getEmail());
            existingStudent.setCurrentAddress(updatedStudent.getCurrentAddress());
            existingStudent.setPermanentAddress(updatedStudent.getPermanentAddress());
            existingStudent.setRemarks(updatedStudent.getRemarks());
            existingStudent.setMedicalInfo(updatedStudent.getMedicalInfo());
            existingStudent.setCurrentStatus(updatedStudent.getCurrentStatus());

            // 3️⃣ Parents
            StudentParents existingParents = existingStudent.getStudentParents(); // parent_id in student table

            if (existingParents != null) {
                // Update existing parent row
                existingParents.setMotherName(updatedParents.getMotherName());
                existingParents.setMotherNic(updatedParents.getMotherNic());
                existingParents.setMotherContact(updatedParents.getMotherContact());
                existingParents.setMotherWhatsapp(updatedParents.getMotherWhatsapp());
                existingParents.setMotherOccupation(updatedParents.getMotherOccupation());
                existingParents.setMotherLivingWithChild(updatedParents.getMotherLivingWithChild());
                existingParents.setMotherReason(updatedParents.getMotherReason());

                existingParents.setFatherName(updatedParents.getFatherName());
                existingParents.setFatherNic(updatedParents.getFatherNic());
                existingParents.setFatherContact(updatedParents.getFatherContact());
                existingParents.setFatherWhatsapp(updatedParents.getFatherWhatsapp());
                existingParents.setFatherOccupation(updatedParents.getFatherOccupation());
                existingParents.setFatherLivingWithChild(updatedParents.getFatherLivingWithChild());
                existingParents.setFatherReason(updatedParents.getFatherReason());

                existingParents.setGuardianName(updatedParents.getGuardianName());
                existingParents.setGuardianNic(updatedParents.getGuardianNic());
                existingParents.setGuardianRelationship(updatedParents.getGuardianRelationship());
                existingParents.setGuardianAddress(updatedParents.getGuardianAddress());
                existingParents.setGuardianContact(updatedParents.getGuardianContact());
                existingParents.setGuardianWhatsapp(updatedParents.getGuardianWhatsapp());

                em.merge(existingParents);

            } else {
                // First time parents
                em.persist(updatedParents); // save parents first
                existingStudent.setStudentParents(updatedParents); // link to student
            }

            // 3️⃣ LANGUAGES
            StudentLanguages existingLanguages = existingStudent.getStudentLanguages();

            if (existingLanguages != null) {

                existingLanguages.setEnglish(updatedLanguages.getEnglish());
                existingLanguages.setSinhala(updatedLanguages.getSinhala());
                existingLanguages.setTamil(updatedLanguages.getTamil());
                existingLanguages.setArabic(updatedLanguages.getArabic());

                em.merge(existingLanguages);

            } else {

                updatedLanguages.setStudent(existingStudent);

                em.persist(updatedLanguages);

                existingStudent.setStudentLanguages(updatedLanguages);
            }

            // 4️⃣ Image Handling
            if (newImage != null) {
                Files.createDirectories(Paths.get(GeneralMethods.IMAGE_SAVE_BASE_PATH));

                String fileName = existingStudent.getAdmissionNo() + ".png";
                File imageFile = new File(GeneralMethods.IMAGE_SAVE_BASE_PATH + fileName);

                if (imageFile.exists()) {
                    imageFile.delete();
                }

                ImageIO.write(newImage, "png", imageFile);
            }

            em.getTransaction().commit();

            JOptionPane.showMessageDialog(null, "Student updated successfully!");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Update failed: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    private void clearForm() {
        // ----------------------
// STUDENT FIELDS CLEAR
// ----------------------
        stm_ad_admission_no_combo.removeAllItems();
        stm_ad_form_no_combo.removeAllItems();
        stm_ad_admission_date.setDate(null);
        stm_ad_student_bc_text.setText("");
        stm_ad_student_name_initials_combo.removeAllItems();
        stm_ad_student_name_combo.removeAllItems();
        stm_ad_student_nationality_combo.removeAllItems();
        stm_ad_student_nic_combo.removeAllItems();
        stm_ad_student_gender_combo.setSelectedIndex(0);
        stm_ad_student_medium_combo.setSelectedIndex(0);
        stm_ad_student_dob.setDate(null);
        stm_ad_student_age_text.setText("");
        stm_ad_student_blood_combo.setSelectedIndex(0);
        stm_ad_student_religion_combo.setSelectedIndex(0);
        stm_ad_district_combo.removeAllItems();
        stm_ad_area_combo.removeAllItems();
        stm_ad_student_contact_text.setText("");
        stm_ad_student_email_text.setText("");
        stm_ad_student_address_text.setText("");
        stm_ad_student_per_address_text.setText("");
        stm_ad_student_remarks_text.setText("");
        stm_ad_student_medical_text.setText("");

        stm_ad_english_chk.setSelected(false);
        stm_ad_sinhala_chk.setSelected(false);
        stm_ad_tamil_chk.setSelected(false);
        stm_ad_arabic_chk.setSelected(false);

// ----------------------
// MOTHER CLEAR
// ----------------------
        stm_ad_student_mother_name_text.setText("");
        stm_ad_student_mother_nic_text.setText("");
        stm_ad_student_mother_contact_text.setText("");
        stm_ad_student_mother_whatsapp_text.setText("");
        stm_ad_student_mother_occupation_combo.removeAllItems();
        stm_ad_student_mother_living_yes_checkbox.setSelected(false);
        stm_ad_student_mother_living_no_checkbox.setSelected(false);
        stm_ad_student_mother_living_combo.setSelectedIndex(-1);
        buttonGroup1.clearSelection();

// ----------------------
// FATHER CLEAR
// ----------------------
        stm_ad_student_father_name_text.setText("");
        stm_ad_student_father_nic_text.setText("");
        stm_ad_student_father_contact_text.setText("");
        stm_ad_student_father_whatsapp_text.setText("");
        stm_ad_student_father_occupation_combo.removeAllItems();
        stm_ad_student_father_living_yes_checkbox.setSelected(false);
        stm_ad_student_father_living_no_checkbox.setSelected(false);
        stm_ad_student_father_living_combo.setSelectedIndex(-1);
        buttonGroup2.clearSelection();

// ----------------------
// GUARDIAN CLEAR
// ----------------------
        stm_ad_student_guardian_name_text.setText("");
        stm_ad_student_guardian_nic.setText("");
        stm_ad_student_guardian_relationship_combo.removeAllItems();
        stm_ad_student_guardian_address_contact.setText("");
        stm_ad_student_guardian_contact_text.setText("");
        stm_ad_student_guardian_whatsapp_text.setText("");

        selectedStudentId = 0;

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
    }

    public boolean isDuplicateStudent(String fullName, int parentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        TypedQuery<Student> q = em.createQuery(
                "SELECT s FROM Student s WHERE "
                + "s.fullName = :name AND "
                + "s.studentParents.studentParentsId = :parentId",
                Student.class
        );

        q.setParameter("name", fullName);
        q.setParameter("parentId", parentId);

        boolean exists = !q.getResultList().isEmpty();
        em.close();

        return exists;
    }

    public void loadParentsDao(String admission_no) {
        StudentDAO studentDAO = new StudentDAO();
        Student student = studentDAO.findByAdmissionNos(admission_no);

        if (student != null) {

            StudentParents parents = student.getStudentParents();

            // Now set parent details
            loadParentDetails(parents);
            System.out.println("PARENTS:-" + parents.getMotherName());
        }
    }

    public void loadParentDetails(StudentParents parents) {

        if (parents == null) {
            return;
        }

        System.out.println("PARENTS:-" + parents.getFatherName());
        // Mother
        stm_ad_student_mother_name_text.setText(parents.getMotherName());
        stm_ad_student_mother_nic_text.setText(parents.getMotherNic());
        stm_ad_student_mother_contact_text.setText(parents.getMotherContact());
        stm_ad_student_mother_whatsapp_text.setText(parents.getMotherWhatsapp());
        stm_ad_student_mother_occupation_combo.getEditor().setItem(parents.getMotherOccupation());

        if ("YES".equalsIgnoreCase(parents.getMotherLivingWithChild())) {
            stm_ad_student_mother_living_yes_checkbox.setSelected(true);
        } else if ("NO".equalsIgnoreCase(parents.getMotherLivingWithChild())) {
            stm_ad_student_mother_living_no_checkbox.setSelected(true);
        }

        stm_ad_student_mother_living_combo.setSelectedItem(parents.getMotherReason());

        // Father
        stm_ad_student_father_name_text.setText(parents.getFatherName());
        stm_ad_student_father_nic_text.setText(parents.getFatherNic());
        stm_ad_student_father_contact_text.setText(parents.getFatherContact());
        stm_ad_student_father_whatsapp_text.setText(parents.getFatherWhatsapp());
        stm_ad_student_father_occupation_combo.getEditor().setItem(parents.getFatherOccupation());

        if ("YES".equalsIgnoreCase(parents.getFatherLivingWithChild())) {
            stm_ad_student_father_living_yes_checkbox.setSelected(true);
        } else if ("NO".equalsIgnoreCase(parents.getFatherLivingWithChild())) {
            stm_ad_student_father_living_no_checkbox.setSelected(true);
        }

        stm_ad_student_father_living_combo.setSelectedItem(parents.getFatherReason());

        // Guardian
        stm_ad_student_guardian_name_text.setText(parents.getGuardianName());
        stm_ad_student_guardian_nic.setText(parents.getGuardianNic());
        stm_ad_student_guardian_relationship_combo.getEditor().setItem(parents.getGuardianRelationship());
        stm_ad_student_guardian_address_contact.setText(parents.getGuardianAddress());
        stm_ad_student_guardian_contact_text.setText(parents.getGuardianContact());
        stm_ad_student_guardian_whatsapp_text.setText(parents.getGuardianWhatsapp());
    }

    public boolean checkStudentElimination(int studentId) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            // =====================================================
            // FETCH ELIMINATION + STUDENT DETAILS
            // =====================================================
            List<Object[]> list = em.createNativeQuery(
                    "SELECT "
                    + "se.eliminate_type, " // 0
                    + "s.admission_no, " // 1
                    + "s.full_name, " // 2
                    + "se.note " // 3
                    + "FROM student_eliminates se "
                    + "INNER JOIN student s ON se.student_id = s.student_id "
                    + "WHERE se.student_id = ? "
                    + "AND se.status = 1 "
                    + "AND s.status = 1 "
                    + "ORDER BY se.student_eliminates_id DESC "
                    + "LIMIT 1"
            )
                    .setParameter(1, studentId)
                    .getResultList();

            // =====================================================
            // IF ELIMINATION RECORD FOUND
            // =====================================================
            if (!list.isEmpty()) {

                Object[] row = list.get(0);

                String eliminateType = row[0] != null ? row[0].toString() : "";
                String admissionNo = row[1] != null ? row[1].toString() : "";
                String studentName = row[2] != null ? row[2].toString() : "";
                String studentNote = row[3] != null ? row[3].toString() : "";

                String message
                        = "Following student is " + eliminateType + "\n\n"
                        + "Admission Number : " + admissionNo + "\n"
                        + "Student Name     : " + studentName + "\n\n"
                        + "Reason           : " + studentNote + "\n\n\n"
                        + "Do you want to change status?";

                String[] options = {"Change Status", "Cancel"};

                int confirm = JOptionPane.showOptionDialog(
                        null,
                        message,
                        "Student Elimination Found",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                // Open dialog only if clicked Change Status
                if (confirm == 0) {

                    String student_name = stm_ad_student_name_combo
                            .getEditor()
                            .getItem()
                            .toString();

                    JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

                    Eliminate_Student dialog = new Eliminate_Student(
                            parentFrame,
                            selectedStudentId,
                            student_name,
                            username,
                            role
                    );

                    GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
                    return true;
                }

                return false;
            }

            // =====================================================
            // IF NO ELIMINATION RECORD FOUND
            // DIRECTLY OPEN DIALOG
            // =====================================================
            String student_name = stm_ad_student_name_combo
                    .getEditor()
                    .getItem()
                    .toString();

            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

            Eliminate_Student dialog = new Eliminate_Student(
                    parentFrame,
                    selectedStudentId,
                    student_name,
                    username,
                    role
            );

            GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);

            return true;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            em.close();
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

    public void loadLatestAdmissionNo() {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            Object admissionObj = em.createNativeQuery(
                    "SELECT admission_no "
                    + "FROM student "
                    + "WHERE status = 1 "
                    + "ORDER BY student_id DESC "
                    + "LIMIT 1"
            ).getSingleResult();

            stm_ad_admission_no_combo.removeAllItems();

            if (admissionObj != null) {
                stm_ad_admission_no_combo.addItem(admissionObj.toString());
                stm_ad_admission_no_combo.setSelectedItem(admissionObj.toString());
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
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        stm_ad_admission_date = new com.toedter.calendar.JDateChooser();
        jLabel4 = new javax.swing.JLabel();
        stm_ad_student_dob = new com.toedter.calendar.JDateChooser();
        stm_ad_student_age_text = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        stm_ad_student_gender_combo = new javax.swing.JComboBox<>();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        stm_ad_student_address_text = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        buttonGradient5 = new Classes.ButtonGradient();
        jLabel15 = new javax.swing.JLabel();
        stm_ad_student_contact_text = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        stm_ad_admission_no_combo = new javax.swing.JComboBox<>();
        stm_ad_form_no_combo = new javax.swing.JComboBox<>();
        stm_ad_student_name_combo = new javax.swing.JComboBox<>();
        stm_ad_student_nic_combo = new javax.swing.JComboBox<>();
        jButton5 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        stm_ad_student_name_initials_combo = new javax.swing.JComboBox<>();
        stm_ad_student_nationality_combo = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        stm_ad_student_medium_combo = new javax.swing.JComboBox<>();
        jLabel16 = new javax.swing.JLabel();
        stm_ad_student_blood_combo = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        stm_ad_student_religion_combo = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();
        stm_ad_student_bc_text = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        stm_ad_district_combo = new javax.swing.JComboBox<>();
        jLabel37 = new javax.swing.JLabel();
        stm_ad_area_combo = new javax.swing.JComboBox<>();
        jLabel38 = new javax.swing.JLabel();
        stm_ad_student_per_address_text = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        stm_ad_english_chk = new javax.swing.JCheckBox();
        stm_ad_sinhala_chk = new javax.swing.JCheckBox();
        stm_ad_tamil_chk = new javax.swing.JCheckBox();
        stm_ad_student_email_text = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        stm_ad_student_remarks_text = new javax.swing.JEditorPane();
        stm_ad_arabic_chk = new javax.swing.JCheckBox();
        jPanel5 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        stm_ad_student_mother_occupation_combo = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        stm_ad_student_mother_nic_text = new javax.swing.JTextField();
        stm_ad_student_mother_name_text = new javax.swing.JTextField();
        stm_ad_student_mother_contact_text = new javax.swing.JTextField();
        stm_ad_student_mother_whatsapp_text = new javax.swing.JTextField();
        stm_ad_student_mother_living_combo = new javax.swing.JComboBox<>();
        jLabel26 = new javax.swing.JLabel();
        stm_ad_student_mother_living_yes_checkbox = new javax.swing.JCheckBox();
        stm_ad_student_mother_living_no_checkbox = new javax.swing.JCheckBox();
        jPanel7 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        stm_ad_student_guardian_relationship_combo = new javax.swing.JComboBox<>();
        stm_ad_student_guardian_name_text = new javax.swing.JTextField();
        stm_ad_student_guardian_contact_text = new javax.swing.JTextField();
        stm_ad_student_guardian_whatsapp_text = new javax.swing.JTextField();
        stm_ad_student_guardian_address_contact = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        stm_ad_student_guardian_nic = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        stm_ad_student_father_occupation_combo = new javax.swing.JComboBox<>();
        jLabel29 = new javax.swing.JLabel();
        stm_ad_student_father_nic_text = new javax.swing.JTextField();
        stm_ad_student_father_name_text = new javax.swing.JTextField();
        stm_ad_student_father_contact_text = new javax.swing.JTextField();
        stm_ad_student_father_whatsapp_text = new javax.swing.JTextField();
        stm_ad_student_father_living_combo = new javax.swing.JComboBox<>();
        jLabel30 = new javax.swing.JLabel();
        stm_ad_student_father_living_yes_checkbox = new javax.swing.JCheckBox();
        stm_ad_student_father_living_no_checkbox = new javax.swing.JCheckBox();
        jPanel8 = new javax.swing.JPanel();
        buttonGradient1 = new Classes.ButtonGradient();
        buttonGradient2 = new Classes.ButtonGradient();
        buttonGradient3 = new Classes.ButtonGradient();
        buttonGradient4 = new Classes.ButtonGradient();
        buttonGradient6 = new Classes.ButtonGradient();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        stm_ad_student_medical_text = new javax.swing.JEditorPane();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "General Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel1.setText("Admission Number");

        jLabel2.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel2.setText("Full Name");

        jLabel3.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel3.setText("App. Form Number");

        stm_ad_admission_date.setForeground(new java.awt.Color(204, 204, 204));
        stm_ad_admission_date.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel4.setText("Date of Birth");

        stm_ad_student_dob.setForeground(new java.awt.Color(255, 255, 255));
        stm_ad_student_dob.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        stm_ad_student_age_text.setEditable(false);
        stm_ad_student_age_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_age_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_age_textActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel5.setText("Age");

        jLabel6.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel6.setText("Gender");

        stm_ad_student_gender_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_gender_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        jLabel7.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel7.setText("NIC");

        jLabel8.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel8.setText("Current Address");

        stm_ad_student_address_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_address_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_address_textActionPerformed(evt);
            }
        });

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

        jLabel15.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel15.setText("Contact");

        stm_ad_student_contact_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_contact_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_contact_textActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel25.setText("Admission Date");

        stm_ad_admission_no_combo.setEditable(true);
        stm_ad_admission_no_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        stm_ad_form_no_combo.setEditable(true);
        stm_ad_form_no_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        stm_ad_student_name_combo.setEditable(true);
        stm_ad_student_name_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        stm_ad_student_nic_combo.setEditable(true);
        stm_ad_student_nic_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

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

        stm_ad_student_name_initials_combo.setEditable(true);
        stm_ad_student_name_initials_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        stm_ad_student_nationality_combo.setEditable(true);
        stm_ad_student_nationality_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel11.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel11.setText("Nationality");

        stm_ad_student_medium_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_medium_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "English", "Sinhala", "Tamil", "Arabic" }));

        jLabel16.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel16.setText("Medium");

        stm_ad_student_blood_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_blood_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-" }));

        jLabel18.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel18.setText("Blood Group");

        jLabel19.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel19.setText("Religion");

        stm_ad_student_religion_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_religion_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Buddhist", "Christian", "Islam", "Hindu", "Burger" }));

        jLabel12.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel12.setText("B/C No");

        stm_ad_student_bc_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_bc_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_bc_textActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel20.setText("District");

        stm_ad_district_combo.setEditable(true);
        stm_ad_district_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel37.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel37.setText("Area");

        stm_ad_area_combo.setEditable(true);
        stm_ad_area_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel38.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel38.setText("Permanent Address");

        stm_ad_student_per_address_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_per_address_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_per_address_textActionPerformed(evt);
            }
        });

        jLabel39.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel39.setText("Languages");

        stm_ad_english_chk.setText("English");

        stm_ad_sinhala_chk.setText("Sinhala");

        stm_ad_tamil_chk.setText("Tamil");

        stm_ad_student_email_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_email_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_email_textActionPerformed(evt);
            }
        });

        jLabel40.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel40.setText("E-Mail");

        jLabel41.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel41.setText("Remarks");

        jScrollPane2.setViewportView(stm_ad_student_remarks_text);

        stm_ad_arabic_chk.setText("Arabic");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(stm_ad_admission_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(stm_ad_form_no_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel25)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(stm_ad_admission_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stm_ad_student_bc_text, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel10)
                                    .addComponent(stm_ad_student_name_initials_combo, 0, 197, Short.MAX_VALUE)
                                    .addComponent(jLabel11)
                                    .addComponent(stm_ad_student_nationality_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stm_ad_student_name_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(stm_ad_student_nic_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel6)
                                            .addComponent(stm_ad_student_gender_combo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel18))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(stm_ad_student_medium_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addGap(6, 6, 6)
                                                        .addComponent(jLabel19))
                                                    .addComponent(jLabel16))
                                                .addGap(0, 0, Short.MAX_VALUE))))))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(stm_ad_district_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(stm_ad_area_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addComponent(jLabel4)
                                                    .addGap(136, 136, 136))
                                                .addGroup(jPanel2Layout.createSequentialGroup()
                                                    .addComponent(stm_ad_student_dob, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addGap(6, 6, 6)))
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel20)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel37)
                                            .addComponent(stm_ad_student_age_text, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(stm_ad_student_blood_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(stm_ad_student_religion_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(stm_ad_student_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel15)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel39)
                                                .addGap(20, 20, 20)
                                                .addComponent(jLabel41)))
                                        .addGap(0, 0, Short.MAX_VALUE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stm_ad_student_email_text)
                            .addComponent(jLabel40)))
                    .addComponent(jLabel8)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel38)
                            .addComponent(stm_ad_student_per_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 386, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_tamil_chk)
                            .addComponent(stm_ad_sinhala_chk)
                            .addComponent(stm_ad_english_chk)
                            .addComponent(stm_ad_arabic_chk))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel3))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(stm_ad_admission_date, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stm_ad_form_no_combo)
                            .addComponent(stm_ad_admission_no_combo)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(stm_ad_student_bc_text, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(stm_ad_student_per_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(17, 17, 17))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(stm_ad_student_name_initials_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(stm_ad_student_name_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(stm_ad_student_nic_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                        .addComponent(stm_ad_student_nationality_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(stm_ad_student_medium_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addGap(41, 41, 41)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(stm_ad_student_gender_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(stm_ad_student_blood_combo)
                                        .addComponent(stm_ad_student_religion_combo, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                                    .addComponent(stm_ad_student_age_text, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(stm_ad_student_dob, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 243, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel20)
                                    .addComponent(jLabel37))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_ad_district_combo))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel15)
                                    .addComponent(jLabel40))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(stm_ad_area_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(stm_ad_student_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(stm_ad_student_email_text, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jLabel39)
                            .addComponent(jLabel41))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(stm_ad_student_address_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel38))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(stm_ad_english_chk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_ad_sinhala_chk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_ad_tamil_chk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(stm_ad_arabic_chk)
                                .addGap(11, 11, 11))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {stm_ad_student_blood_combo, stm_ad_student_religion_combo});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {stm_ad_area_combo, stm_ad_student_contact_text, stm_ad_student_email_text});

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Mother Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel13.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel13.setText("NIC");

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel14.setText("Full Name");

        jLabel21.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel21.setText("Contact");

        jLabel23.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel23.setText("Occupation");

        stm_ad_student_mother_occupation_combo.setEditable(true);
        stm_ad_student_mother_occupation_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel24.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel24.setText("WhatsApp");

        stm_ad_student_mother_nic_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_mother_nic_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_mother_nic_textActionPerformed(evt);
            }
        });

        stm_ad_student_mother_name_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_mother_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_mother_name_textActionPerformed(evt);
            }
        });

        stm_ad_student_mother_contact_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_mother_contact_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_mother_contact_textActionPerformed(evt);
            }
        });

        stm_ad_student_mother_whatsapp_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_mother_whatsapp_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_mother_whatsapp_textActionPerformed(evt);
            }
        });

        stm_ad_student_mother_living_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_mother_living_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Deceased", "Seperated", "Dead" }));

        jLabel26.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel26.setText("Living with child");

        buttonGroup1.add(stm_ad_student_mother_living_yes_checkbox);
        stm_ad_student_mother_living_yes_checkbox.setText("Yes");

        buttonGroup1.add(stm_ad_student_mother_living_no_checkbox);
        stm_ad_student_mother_living_no_checkbox.setText("No");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(stm_ad_student_mother_contact_text, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(stm_ad_student_mother_nic_text))
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(stm_ad_student_mother_name_text)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_mother_occupation_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel23)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_mother_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel24)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel26)
                                .addGap(18, 18, 18)
                                .addComponent(stm_ad_student_mother_living_yes_checkbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_ad_student_mother_living_no_checkbox)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(stm_ad_student_mother_living_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stm_ad_student_mother_nic_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_mother_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_mother_occupation_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_mother_living_yes_checkbox)
                    .addComponent(stm_ad_student_mother_living_no_checkbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stm_ad_student_mother_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_mother_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_mother_living_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Guardian Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel31.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel31.setText("NIC");

        jLabel32.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel32.setText("Full Name");

        jLabel33.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel33.setText("Relationship");

        jLabel34.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel34.setText("Contact");

        jLabel36.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel36.setText("WhatsApp");

        stm_ad_student_guardian_relationship_combo.setEditable(true);
        stm_ad_student_guardian_relationship_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        stm_ad_student_guardian_name_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_guardian_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_guardian_name_textActionPerformed(evt);
            }
        });

        stm_ad_student_guardian_contact_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_guardian_contact_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_guardian_contact_textActionPerformed(evt);
            }
        });

        stm_ad_student_guardian_whatsapp_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_guardian_whatsapp_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_guardian_whatsapp_textActionPerformed(evt);
            }
        });

        stm_ad_student_guardian_address_contact.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_guardian_address_contact.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_guardian_address_contactActionPerformed(evt);
            }
        });

        jLabel35.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel35.setText("Address");

        stm_ad_student_guardian_nic.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_guardian_nic.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_guardian_nicActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_guardian_nic, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(stm_ad_student_guardian_name_text))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stm_ad_student_guardian_relationship_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(stm_ad_student_guardian_address_contact))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_guardian_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel36)
                            .addComponent(stm_ad_student_guardian_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel31)
                        .addComponent(jLabel32))
                    .addComponent(jLabel33, javax.swing.GroupLayout.Alignment.TRAILING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stm_ad_student_guardian_relationship_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_guardian_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_guardian_nic, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(stm_ad_student_guardian_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(stm_ad_student_guardian_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stm_ad_student_guardian_address_contact, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Father Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jLabel17.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel17.setText("NIC");

        jLabel22.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel22.setText("Full Name");

        jLabel27.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel27.setText("Contact");

        jLabel28.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel28.setText("Occupation");

        stm_ad_student_father_occupation_combo.setEditable(true);
        stm_ad_student_father_occupation_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N

        jLabel29.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel29.setText("WhatsApp");

        stm_ad_student_father_nic_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_father_nic_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_father_nic_textActionPerformed(evt);
            }
        });

        stm_ad_student_father_name_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_father_name_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_father_name_textActionPerformed(evt);
            }
        });

        stm_ad_student_father_contact_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_father_contact_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_father_contact_textActionPerformed(evt);
            }
        });

        stm_ad_student_father_whatsapp_text.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_father_whatsapp_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stm_ad_student_father_whatsapp_textActionPerformed(evt);
            }
        });

        stm_ad_student_father_living_combo.setFont(new java.awt.Font("Roboto Light", 0, 14)); // NOI18N
        stm_ad_student_father_living_combo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Deceased", "Seperated", "Dead" }));

        jLabel30.setFont(new java.awt.Font("Roboto Medium", 0, 12)); // NOI18N
        jLabel30.setText("Living with child");

        buttonGroup2.add(stm_ad_student_father_living_yes_checkbox);
        stm_ad_student_father_living_yes_checkbox.setText("Yes");

        buttonGroup2.add(stm_ad_student_father_living_no_checkbox);
        stm_ad_student_father_living_no_checkbox.setText("No");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(stm_ad_student_father_contact_text, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                        .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(stm_ad_student_father_nic_text))
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(stm_ad_student_father_name_text)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_father_occupation_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel28)))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(stm_ad_student_father_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel29)
                            .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addComponent(jLabel30)
                                .addGap(18, 18, 18)
                                .addComponent(stm_ad_student_father_living_yes_checkbox)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stm_ad_student_father_living_no_checkbox)
                                .addGap(0, 20, Short.MAX_VALUE))
                            .addComponent(stm_ad_student_father_living_combo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(jLabel22)
                    .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stm_ad_student_father_nic_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_father_name_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_father_occupation_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_father_living_yes_checkbox)
                    .addComponent(stm_ad_student_father_living_no_checkbox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stm_ad_student_father_contact_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_father_whatsapp_text, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stm_ad_student_father_living_combo, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        buttonGradient1.setText("EDIT");
        buttonGradient1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient1ActionPerformed(evt);
            }
        });

        buttonGradient2.setText("ADD NEW");
        buttonGradient2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient2ActionPerformed(evt);
            }
        });

        buttonGradient3.setText("SAVE (F1)");
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

        buttonGradient6.setText("CLEAR STUDENT");
        buttonGradient6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonGradient6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonGradient6, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonGradient1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient4, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonGradient6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {buttonGradient1, buttonGradient2, buttonGradient3, buttonGradient4});

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 204, 204), new java.awt.Color(102, 102, 102)), "Medical Information", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Roboto", 0, 14))); // NOI18N

        jScrollPane1.setViewportView(stm_ad_student_medical_text);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setBackground(new java.awt.Color(102, 102, 102));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/siblings.png"))); // NOI18N
        jButton1.setToolTipText("Siblings");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(102, 102, 102));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/course_enrolment.png"))); // NOI18N
        jButton2.setToolTipText("Course Enrolment");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(102, 102, 102));
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/misc32.png"))); // NOI18N
        jButton3.setToolTipText("Issue Miscellaneous Items");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(102, 102, 102));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/termination32.png"))); // NOI18N
        jButton4.setToolTipText("Eliminate the student");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE))
                        .addGap(42, 42, 42)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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

    private void stm_ad_student_age_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_age_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_age_textActionPerformed

    private void stm_ad_student_address_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_address_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_address_textActionPerformed

    private void jLabel9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel9MouseClicked
//        File selectedFile = ImageHelper.chooseAndSetImageAutoResizeRemember(jLabel9);
//
//        if (selectedFile != null) {
//            System.out.println("Image selected: " + selectedFile.getAbsolutePath());
//            // The resized image is now stored in ImageHelper.resizedImageToSave
//        } else {
//            System.out.println("No image selected.");
//        }

        showPhotoOptionDialog();

        //   selectedImageFile = GeneralMethods.chooseAndSetImageAutoResizeRemember(jLabel9);

    }//GEN-LAST:event_jLabel9MouseClicked

    private void stm_ad_student_contact_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_contact_textActionPerformed
        stm_ad_student_email_text.requestFocus();
    }//GEN-LAST:event_stm_ad_student_contact_textActionPerformed

    private void stm_ad_student_mother_nic_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_mother_nic_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_mother_nic_textActionPerformed

    private void stm_ad_student_mother_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_mother_name_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_mother_name_textActionPerformed

    private void stm_ad_student_mother_contact_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_mother_contact_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_mother_contact_textActionPerformed

    private void stm_ad_student_mother_whatsapp_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_mother_whatsapp_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_mother_whatsapp_textActionPerformed

    private void stm_ad_student_guardian_nicActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_guardian_nicActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_guardian_nicActionPerformed

    private void stm_ad_student_guardian_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_guardian_name_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_guardian_name_textActionPerformed

    private void stm_ad_student_guardian_contact_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_guardian_contact_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_guardian_contact_textActionPerformed

    private void stm_ad_student_guardian_whatsapp_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_guardian_whatsapp_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_guardian_whatsapp_textActionPerformed

    private void stm_ad_student_father_nic_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_father_nic_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_father_nic_textActionPerformed

    private void stm_ad_student_father_name_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_father_name_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_father_name_textActionPerformed

    private void stm_ad_student_father_contact_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_father_contact_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_father_contact_textActionPerformed

    private void stm_ad_student_father_whatsapp_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_father_whatsapp_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_father_whatsapp_textActionPerformed

    private void stm_ad_student_guardian_address_contactActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_guardian_address_contactActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_guardian_address_contactActionPerformed

    private void buttonGradient3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient3ActionPerformed

        if (stm_ad_admission_no_combo.getEditor().getItem().toString().equalsIgnoreCase("") || stm_ad_student_name_combo.getEditor().getItem().toString().equalsIgnoreCase("")) {
            JOptionPane.showMessageDialog(this, "Please enter student information!");
            return;
        }

        Student student = new Student();
        StudentLanguages langs = new StudentLanguages();

        StudentDAO studentDAO = new StudentDAO();
        student.setAdmissionNo(stm_ad_admission_no_combo.getEditor().getItem().toString().trim());
        student.setFormNo(stm_ad_form_no_combo.getEditor().getItem().toString().trim());
        student.setAdmissionDate(stm_ad_admission_date.getDate()); // Assuming JDateChooser
        student.setBcNo(stm_ad_student_bc_text.getText().trim());
        student.setNameInitials(stm_ad_student_name_initials_combo.getEditor().getItem().toString().trim());
        student.setFullName(stm_ad_student_name_combo.getEditor().getItem().toString().trim());
        student.setNationality(stm_ad_student_nationality_combo.getEditor().getItem().toString().trim());
        student.setNic(stm_ad_student_nic_combo.getEditor().getItem().toString().trim());
        student.setGender(stm_ad_student_gender_combo.getSelectedItem().toString());
        student.setMedium(stm_ad_student_medium_combo.getSelectedItem().toString());
        student.setDob(stm_ad_student_dob.getDate()); // Assuming JDateChooser
        student.setBloodGroup(stm_ad_student_blood_combo.getSelectedItem().toString());
        student.setReligion(stm_ad_student_religion_combo.getSelectedItem().toString());
        student.setDistrict(stm_ad_district_combo.getEditor().getItem().toString().trim());
        student.setArea(stm_ad_area_combo.getEditor().getItem().toString().trim());
        student.setContactNo(stm_ad_student_contact_text.getText().trim());
        student.setEmail(stm_ad_student_email_text.getText().trim());
        student.setCurrentAddress(stm_ad_student_address_text.getText().trim());
        student.setPermanentAddress(stm_ad_student_per_address_text.getText().trim());
        student.setRemarks(stm_ad_student_remarks_text.getText().trim());
        student.setMedicalInfo(stm_ad_student_medical_text.getText().trim());
        student.setCurrentStatus("ACTIVE");
        student.setStatus(true);

        StudentParents parents = new StudentParents();
        parents.setMotherName(stm_ad_student_mother_name_text.getText().trim());
        parents.setMotherNic(stm_ad_student_mother_nic_text.getText().trim());
        parents.setMotherContact(stm_ad_student_mother_contact_text.getText().trim());
        parents.setMotherWhatsapp(stm_ad_student_mother_whatsapp_text.getText().trim());
        parents.setMotherOccupation(stm_ad_student_mother_occupation_combo.getEditor().getItem().toString().trim());
        if (stm_ad_student_mother_living_yes_checkbox.isSelected()) {
            parents.setMotherLivingWithChild("YES");
        } else if (stm_ad_student_mother_living_no_checkbox.isSelected()) {
            parents.setMotherLivingWithChild("NO");
        } else {
            parents.setMotherLivingWithChild(null); // or "" if nothing selected
        }
        parents.setMotherReason(stm_ad_student_mother_living_combo.getSelectedItem().toString().trim());

        parents.setFatherName(stm_ad_student_father_name_text.getText().trim());
        parents.setFatherNic(stm_ad_student_father_nic_text.getText().trim());
        parents.setFatherContact(stm_ad_student_father_contact_text.getText().trim());
        parents.setFatherWhatsapp(stm_ad_student_father_whatsapp_text.getText().trim());
        parents.setFatherOccupation(stm_ad_student_father_occupation_combo.getEditor().getItem().toString().trim());
        if (stm_ad_student_father_living_yes_checkbox.isSelected()) {
            parents.setFatherLivingWithChild("YES");
        } else if (stm_ad_student_father_living_no_checkbox.isSelected()) {
            parents.setFatherLivingWithChild("NO");
        } else {
            parents.setFatherLivingWithChild(null); // or "" if nothing selected
        }
        parents.setFatherReason(stm_ad_student_father_living_combo.getSelectedItem().toString().trim());

        parents.setGuardianName(stm_ad_student_guardian_name_text.getText().trim());
        parents.setGuardianNic(stm_ad_student_guardian_nic.getText().trim());
        parents.setGuardianRelationship(stm_ad_student_guardian_relationship_combo.getEditor().getItem().toString().trim());
        parents.setGuardianAddress(stm_ad_student_guardian_address_contact.getText().trim());
        parents.setGuardianContact(stm_ad_student_guardian_contact_text.getText().trim());
        parents.setGuardianWhatsapp(stm_ad_student_guardian_whatsapp_text.getText().trim());
        parents.setStatus(true);

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

        EntityManager em = HibernateConfig.getEntityManager();
        try {

            String extension = "png";
            String fileName = student.getAdmissionNo() + "." + extension;
            // String relativeImagePath = GeneralMethods.IMAGE_SAVE_BASE_PATH + fileName;
            String fullSavePath = GeneralMethods.IMAGE_SAVE_BASE_PATH + fileName;

            Files.createDirectories(Paths.get(GeneralMethods.IMAGE_SAVE_BASE_PATH));
            File outputPath = new File(fullSavePath);
            ImageIO.write(imageToSave, extension, outputPath);

            em.getTransaction().begin();

            StudentParentsDAO parentsDAO = new StudentParentsDAO();
            StudentParents existingParent = parentsDAO.findExistingParent(parents);
            if (existingParent != null) {

                int option = JOptionPane.showConfirmDialog(
                        this,
                        "This parent/guardian already exists.\n"
                        + "Is this child a sibling?",
                        "Sibling Confirmation",
                        JOptionPane.YES_NO_CANCEL_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {

                    // Check duplicate student with same parent
                    if (isDuplicateStudent(student.getFullName(), existingParent.getStudentParentsId())) {
                        JOptionPane.showMessageDialog(this,
                                "This student already exists under same parent!",
                                "Duplicate",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    student.setStudentParents(existingParent);
                    studentDAO.save(student);

                    // ✅ LOG: Sibling added to existing parent
                    logHelper_new.log(
                            "STUDENT_MANAGEMENT",
                            "NEW_ADMISSION",
                            student.getStudentId(),
                            "INSERT_SIBLING",
                            "Admission: " + student.getAdmissionNo(),
                            String.format("Sibling added to student: %s (Admission No: %s)", student.getFullName(), student.getAdmissionNo()),
                            String.format("Sibling Registered: %s. Linked to existing Parent ID: %d",
                                    student.getFullName(), existingParent.getStudentParentsId()),
                            username
                    );

                    JOptionPane.showMessageDialog(this, "Sibling added successfully!");
                    return;
                }

                if (option == JOptionPane.NO_OPTION) {
                    // continue normal save (new parent)
                }

                if (option == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }

            langs.setEnglish(stm_ad_english_chk.isSelected());
            langs.setSinhala(stm_ad_sinhala_chk.isSelected());
            langs.setTamil(stm_ad_tamil_chk.isSelected());
            langs.setArabic(stm_ad_arabic_chk.isSelected());

            langs.setStudent(student);
            student.setStudentLanguages(langs);

            // Save student
            em.persist(student);

            // Link parents to student
            //           parents.setStudent(student);
            // Save parents
            em.persist(parents);
            //            Save parents first
            em.persist(parents);
            em.flush(); // force ID generation

            // Link parents to student
            student.setStudentParents(parents);

            // Save student
            em.persist(student);

            em.getTransaction().commit();

            // ✅ LOG: Full New Registration
            logHelper_new.log(
                    "STUDENT_MANAGEMENT",
                    "NEW_ADMISSION",
                    student.getStudentId(),
                    "INSERT_STUDENT",
                    "Admission: " + student.getAdmissionNo(),
                    String.format("New student registered: %s (Admission No: %s)", student.getFullName(), student.getAdmissionNo()),
                    "New student profile created successfully.",
                    username
            );

            JOptionPane.showMessageDialog(
                    null,
                    "Student and Parents saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            em.getTransaction().rollback();
            JOptionPane.showMessageDialog(
                    null,
                    "Error saving student: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        } finally {
            em.close();
        }


    }//GEN-LAST:event_buttonGradient3ActionPerformed

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

    private void buttonGradient1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient1ActionPerformed

        if (stm_ad_admission_no_combo.getEditor().getItem().toString().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Admission Number");
            return;
        }

        BufferedImage imageToSave = GeneralMethods.resizedImageToSave;
        Student student = new Student();
        student.setAdmissionNo(stm_ad_admission_no_combo.getEditor().getItem().toString().trim());
        student.setFormNo(stm_ad_form_no_combo.getEditor().getItem().toString().trim());
        student.setAdmissionDate(stm_ad_admission_date.getDate()); // Assuming JDateChooser
        student.setBcNo(stm_ad_student_bc_text.getText().trim());
        student.setNameInitials(stm_ad_student_name_initials_combo.getEditor().getItem().toString().trim());
        student.setFullName(stm_ad_student_name_combo.getEditor().getItem().toString().trim());
        student.setNationality(stm_ad_student_nationality_combo.getEditor().getItem().toString().trim());
        student.setNic(stm_ad_student_nic_combo.getEditor().getItem().toString().trim());
        student.setGender(stm_ad_student_gender_combo.getSelectedItem().toString());
        student.setMedium(stm_ad_student_medium_combo.getSelectedItem().toString());
        student.setDob(stm_ad_student_dob.getDate()); // Assuming JDateChooser
        student.setBloodGroup(stm_ad_student_blood_combo.getSelectedItem().toString());
        student.setReligion(stm_ad_student_religion_combo.getSelectedItem().toString());
        student.setDistrict(stm_ad_district_combo.getEditor().getItem().toString().trim());
        student.setArea(stm_ad_area_combo.getEditor().getItem().toString().trim());
        student.setContactNo(stm_ad_student_contact_text.getText().trim());
        student.setEmail(stm_ad_student_email_text.getText().trim());
        student.setCurrentAddress(stm_ad_student_address_text.getText().trim());
        student.setPermanentAddress(stm_ad_student_per_address_text.getText().trim());
        student.setRemarks(stm_ad_student_remarks_text.getText().trim());
        student.setMedicalInfo(stm_ad_student_medical_text.getText().trim());
        student.setCurrentStatus("ACTIVE");
        student.setStatus(true);

        StudentParents parents = new StudentParents();
        parents.setMotherName(stm_ad_student_mother_name_text.getText().trim());
        parents.setMotherNic(stm_ad_student_mother_nic_text.getText().trim());
        parents.setMotherContact(stm_ad_student_mother_contact_text.getText().trim());
        parents.setMotherWhatsapp(stm_ad_student_mother_whatsapp_text.getText().trim());
        parents.setMotherOccupation(stm_ad_student_mother_occupation_combo.getEditor().getItem().toString().trim());
        if (stm_ad_student_mother_living_yes_checkbox.isSelected()) {
            parents.setMotherLivingWithChild("YES");
        } else if (stm_ad_student_mother_living_no_checkbox.isSelected()) {
            parents.setMotherLivingWithChild("NO");
        } else {
            parents.setMotherLivingWithChild(null); // or "" if nothing selected
        }
        parents.setMotherReason(stm_ad_student_mother_living_combo.getSelectedItem().toString().trim());

        parents.setFatherName(stm_ad_student_father_name_text.getText().trim());
        parents.setFatherNic(stm_ad_student_father_nic_text.getText().trim());
        parents.setFatherContact(stm_ad_student_father_contact_text.getText().trim());
        parents.setFatherWhatsapp(stm_ad_student_father_whatsapp_text.getText().trim());
        parents.setFatherOccupation(stm_ad_student_father_occupation_combo.getEditor().getItem().toString().trim());
        if (stm_ad_student_father_living_yes_checkbox.isSelected()) {
            parents.setFatherLivingWithChild("YES");
        } else if (stm_ad_student_father_living_no_checkbox.isSelected()) {
            parents.setFatherLivingWithChild("NO");
        } else {
            parents.setFatherLivingWithChild(null); // or "" if nothing selected
        }
        parents.setFatherReason(stm_ad_student_father_living_combo.getSelectedItem().toString().trim());

        parents.setGuardianName(stm_ad_student_guardian_name_text.getText().trim());
        parents.setGuardianNic(stm_ad_student_guardian_nic.getText().trim());
        parents.setGuardianRelationship(stm_ad_student_guardian_relationship_combo.getEditor().getItem().toString().trim());
        parents.setGuardianAddress(stm_ad_student_guardian_address_contact.getText().trim());
        parents.setGuardianContact(stm_ad_student_guardian_contact_text.getText().trim());
        parents.setGuardianWhatsapp(stm_ad_student_guardian_whatsapp_text.getText().trim());
        parents.setStatus(true);

        StudentLanguages languages = new StudentLanguages();

        languages.setEnglish(stm_ad_english_chk.isSelected());
        languages.setSinhala(stm_ad_sinhala_chk.isSelected());
        languages.setTamil(stm_ad_tamil_chk.isSelected());
        languages.setArabic(stm_ad_arabic_chk.isSelected());

        updateStudentWithParents(selectedStudentId, student, parents, languages, imageToSave);

        // ✅ LOG: Student & Parent Update
        logHelper_new.log(
                "STUDENT_MANAGEMENT",
                "NEW_ADMISSION",
                student.getStudentId(),
                "UPDATE_STUDENT",
                "Admission: " + student.getAdmissionNo(),
                String.format("Updated details for: %s (Admission No: %s)", student.getFullName(), student.getAdmissionNo()),
                String.format("Profile image %s", (imageToSave != null ? "was updated/changed" : "remained unchanged")),
                username
        );

        JOptionPane.showMessageDialog(this, "Student information updated successfully!");

    }//GEN-LAST:event_buttonGradient1ActionPerformed

    private void buttonGradient4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient4ActionPerformed

        if (stm_ad_admission_no_combo.getEditor().getItem().toString().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Admission Number");
            return;
        }

        String admissionNo = stm_ad_admission_no_combo.getEditor().getItem().toString().trim();

        StudentDAO dao = new StudentDAO();

        // 🔎 Check if student exists and is ACTIVE
        Student student = dao.findByAdmissionNo(admissionNo);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Student not found or already inactive.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to deactivate this student?",
                "Confirm",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {

            dao.softDeleteByAdmissionNo(admissionNo);

            // ✅ LOG: Student Deactivation
//            logHelper.log(
//                    "STUDENT_MANAGEMENT",
//                    student.getStudentId(),
//                    "STUDENT DELETE",
//                    "Admission: " + admissionNo,
//                    0.0,
//                    String.format("STUDENT DEACTIVATED: %s (Adm No: %s).", student.getFullName(), admissionNo),
//                    username
//            );

            JOptionPane.showMessageDialog(this, "Student deactivated successfully.");
        }


    }//GEN-LAST:event_buttonGradient4ActionPerformed

    private void buttonGradient2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient2ActionPerformed
        clearForm();
    }//GEN-LAST:event_buttonGradient2ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Course_Enrollment dialog = new Course_Enrollment(parentFrame, false, selectedStudentId, username, role);
        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void buttonGradient6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonGradient6ActionPerformed
        stm_ad_admission_no_combo.removeAllItems();
        stm_ad_form_no_combo.removeAllItems();
        stm_ad_admission_date.setDate(null);
        stm_ad_student_name_combo.removeAllItems();
        stm_ad_student_nic_combo.removeAllItems();
        stm_ad_student_dob.setDate(null);
        stm_ad_student_gender_combo.setSelectedIndex(-1);
        stm_ad_student_address_text.setText("");
        stm_ad_student_contact_text.setText("");
        stm_ad_student_remarks_text.setText("");
        stm_ad_student_medical_text.setText("");

        stm_ad_english_chk.setSelected(false);
        stm_ad_sinhala_chk.setSelected(false);
        stm_ad_tamil_chk.setSelected(false);
        stm_ad_arabic_chk.setSelected(false);

        selectedStudentId = 0;

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
    }//GEN-LAST:event_buttonGradient6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

//        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//
//        Siblings_Register dialog = new Siblings_Register(parentFrame, false, selectedStudentId);
//        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        String mother_name = stm_ad_student_mother_name_text.getText().trim();
        String mother_nic = stm_ad_student_mother_nic_text.getText().trim();
        String mother_contact = stm_ad_student_mother_contact_text.getText().trim();

        String father_name = stm_ad_student_father_name_text.getText().trim();
        String father_nic = stm_ad_student_father_nic_text.getText().trim();
        String father_contact = stm_ad_student_father_contact_text.getText().trim();

        String guardian_name = stm_ad_student_guardian_name_text.getText().trim();
        String guardian_nic = stm_ad_student_guardian_nic.getText().trim();
        String guardian_contact = stm_ad_student_guardian_contact_text.getText().trim();

        Siblings_Register dialog = new Siblings_Register(
                parentFrame,
                selectedStudentId,
                this // VERY IMPORTANT
        );

        if (!mother_nic.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(0);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(mother_nic);
        } else if (!mother_name.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(1);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(mother_name);
        } else if (!mother_contact.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(2);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(mother_contact);
        } else if (!father_nic.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(3);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(father_nic);
        } else if (!father_name.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(4);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(father_name);
        } else if (!father_contact.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(5);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(father_contact);
        } else if (!guardian_nic.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(6);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(guardian_nic);
        } else if (!guardian_name.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(7);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(guardian_name);
        } else if (!guardian_contact.isEmpty()) {
            Siblings_Register.stm_si_option_combo.setSelectedIndex(8);
            Siblings_Register.stm_si_search_combo.getEditor().setItem(guardian_contact);
        }

        dialog.loadSiblingTable();

        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);


    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        String st_admi = stm_ad_admission_no_combo.getEditor().getItem().toString();
        String st_name = stm_ad_student_name_combo.getEditor().getItem().toString();
        if (st_admi.equals("") || st_name.equals("")) {
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

        Miscellaneous_Issuing dialog = new Miscellaneous_Issuing(parentFrame, selectedStudentId, st_name, username, role);
        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed

        checkStudentElimination(selectedStudentId);
//        String student_name = stm_ad_student_name_combo.getEditor().getItem().toString();
//        if (selectedStudentId == 0 || student_name.equals("")) {
//            return;
//        }
//
//        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
//        Eliminate_Student dialog = new Eliminate_Student(parentFrame, selectedStudentId, student_name, username, role);
//        GeneralMethods.openDialogWithDarkBackground(parentFrame, dialog);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed

        loadLatestAdmissionNo();

    }//GEN-LAST:event_jButton5ActionPerformed

    private void stm_ad_student_bc_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_bc_textActionPerformed
        stm_ad_student_name_initials_combo.requestFocus();
    }//GEN-LAST:event_stm_ad_student_bc_textActionPerformed

    private void stm_ad_student_per_address_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_per_address_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_stm_ad_student_per_address_textActionPerformed

    private void stm_ad_student_email_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stm_ad_student_email_textActionPerformed
        stm_ad_student_address_text.requestFocus();
    }//GEN-LAST:event_stm_ad_student_email_textActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private Classes.ButtonGradient buttonGradient1;
    private Classes.ButtonGradient buttonGradient2;
    private Classes.ButtonGradient buttonGradient3;
    private Classes.ButtonGradient buttonGradient4;
    private Classes.ButtonGradient buttonGradient5;
    private Classes.ButtonGradient buttonGradient6;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
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
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser stm_ad_admission_date;
    private javax.swing.JComboBox<String> stm_ad_admission_no_combo;
    private javax.swing.JCheckBox stm_ad_arabic_chk;
    private javax.swing.JComboBox<String> stm_ad_area_combo;
    private javax.swing.JComboBox<String> stm_ad_district_combo;
    private javax.swing.JCheckBox stm_ad_english_chk;
    private javax.swing.JComboBox<String> stm_ad_form_no_combo;
    private javax.swing.JCheckBox stm_ad_sinhala_chk;
    private javax.swing.JTextField stm_ad_student_address_text;
    private javax.swing.JTextField stm_ad_student_age_text;
    private javax.swing.JTextField stm_ad_student_bc_text;
    private javax.swing.JComboBox<String> stm_ad_student_blood_combo;
    private javax.swing.JTextField stm_ad_student_contact_text;
    private com.toedter.calendar.JDateChooser stm_ad_student_dob;
    private javax.swing.JTextField stm_ad_student_email_text;
    private javax.swing.JTextField stm_ad_student_father_contact_text;
    private javax.swing.JComboBox<String> stm_ad_student_father_living_combo;
    private javax.swing.JCheckBox stm_ad_student_father_living_no_checkbox;
    private javax.swing.JCheckBox stm_ad_student_father_living_yes_checkbox;
    private javax.swing.JTextField stm_ad_student_father_name_text;
    private javax.swing.JTextField stm_ad_student_father_nic_text;
    private javax.swing.JComboBox<String> stm_ad_student_father_occupation_combo;
    private javax.swing.JTextField stm_ad_student_father_whatsapp_text;
    private javax.swing.JComboBox<String> stm_ad_student_gender_combo;
    private javax.swing.JTextField stm_ad_student_guardian_address_contact;
    private javax.swing.JTextField stm_ad_student_guardian_contact_text;
    private javax.swing.JTextField stm_ad_student_guardian_name_text;
    private javax.swing.JTextField stm_ad_student_guardian_nic;
    private javax.swing.JComboBox<String> stm_ad_student_guardian_relationship_combo;
    private javax.swing.JTextField stm_ad_student_guardian_whatsapp_text;
    private javax.swing.JEditorPane stm_ad_student_medical_text;
    private javax.swing.JComboBox<String> stm_ad_student_medium_combo;
    private javax.swing.JTextField stm_ad_student_mother_contact_text;
    private javax.swing.JComboBox<String> stm_ad_student_mother_living_combo;
    private javax.swing.JCheckBox stm_ad_student_mother_living_no_checkbox;
    private javax.swing.JCheckBox stm_ad_student_mother_living_yes_checkbox;
    private javax.swing.JTextField stm_ad_student_mother_name_text;
    private javax.swing.JTextField stm_ad_student_mother_nic_text;
    private javax.swing.JComboBox<String> stm_ad_student_mother_occupation_combo;
    private javax.swing.JTextField stm_ad_student_mother_whatsapp_text;
    private javax.swing.JComboBox<String> stm_ad_student_name_combo;
    private javax.swing.JComboBox<String> stm_ad_student_name_initials_combo;
    private javax.swing.JComboBox<String> stm_ad_student_nationality_combo;
    private javax.swing.JComboBox<String> stm_ad_student_nic_combo;
    private javax.swing.JTextField stm_ad_student_per_address_text;
    private javax.swing.JComboBox<String> stm_ad_student_religion_combo;
    private javax.swing.JEditorPane stm_ad_student_remarks_text;
    private javax.swing.JCheckBox stm_ad_tamil_chk;
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
