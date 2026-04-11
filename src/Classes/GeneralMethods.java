/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import Entities.Student_Management.Student;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DateFormatter;

/**
 *
 * @author UNKNOWN_UN
 */
public class GeneralMethods {

    public static JFormattedTextField createDateField() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setLenient(false);

        DateFormatter df = new DateFormatter(sdf);
        JFormattedTextField tf = new JFormattedTextField(df);

        tf.setColumns(10);
        tf.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);

        return tf;
    }

    public static int getMonthNumber(String month) {

        if (month == null || month.trim().isEmpty()) {
            return 0;
        }

        String m = month.trim().toLowerCase();

        // normalize short names to full names
        switch (m) {
            case "jan":
            case "january":
                return 1;
            case "feb":
            case "february":
                return 2;
            case "mar":
            case "march":
                return 3;
            case "apr":
            case "april":
                return 4;
            case "may":
                return 5;
            case "jun":
            case "june":
                return 6;
            case "jul":
            case "july":
                return 7;
            case "aug":
            case "august":
                return 8;
            case "sep":
            case "september":
                return 9;
            case "oct":
            case "october":
                return 10;
            case "nov":
            case "november":
                return 11;
            case "dec":
            case "december":
                return 12;
            default:
                return 0;
        }
    }

    public static String getMonthName(int month) {
        switch (month) {
            case 1:
                return "January";
            case 2:
                return "February";
            case 3:
                return "March";
            case 4:
                return "April";
            case 5:
                return "May";
            case 6:
                return "June";
            case 7:
                return "July";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "October";
            case 11:
                return "November";
            case 12:
                return "December";
            default:
                return "";
        }
    }

    // AUTO-RESIZE IMAGE AND SET TO LABEL
    public static File lastImageDirectory = new File(System.getProperty("user.home"));
    public static final String IMAGE_SAVE_BASE_PATH = "C:/MMS/students/";
    public static final String IMAGE_SAVE_BASE_PATH_LOGO = "C:/InnovexPOS_Images/";

    // You’ll store the resized image to this variable from outside
    public static BufferedImage resizedImageToSave = null;

    public static File chooseAndSetImageAutoResizeRemember(JLabel label) {
        JFileChooser fileChooser = new JFileChooser(lastImageDirectory);
        fileChooser.setDialogTitle("Select Image");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            lastImageDirectory = selectedFile.getParentFile();

            try {
                BufferedImage originalImage = ImageIO.read(selectedFile);

                if (originalImage == null) {
                    JOptionPane.showMessageDialog(null, "Invalid image file.");
                    return null;
                }

                BufferedImage resized = resizeImage(originalImage, 171, 171);
                label.setIcon(new ImageIcon(resized));
                resizedImageToSave = resized; // save resized image to be stored later

                return selectedFile;

            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error loading image.");
            }
        }
        return null;
    }

    // Resizing utility
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(resultingImage, 0, 0, null);
        g2d.dispose();
        return outputImage;
    }

    public static BufferedImage getDefaultImage() {
        try {
            InputStream is = GeneralMethods.class.getResourceAsStream("/images/student_logo.png");
            if (is != null) {
                BufferedImage defaultImage = ImageIO.read(is);
                return resizeImage(defaultImage, 171, 171); // also resize default
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum StudentSearchType {
        ADMISSION,
        FORM,
        NIC,
        NAME
    }

    public static void loadStudentCombo(JComboBox<String> combo,
            List<Student> students,
            String typedText,
            StudentSearchType type) {

        combo.removeAllItems();

        for (Student s : students) {

            String display;

            switch (type) {

                case ADMISSION:
                    display = s.getAdmissionNo() + " - " + s.getFullName();
                    break;

                case FORM:
                    display = s.getFormNo() + " - " + s.getFullName();
                    break;

                case NIC:
                    display = s.getNic() + " - " + s.getFullName();
                    break;

                case NAME:
                    display = s.getAdmissionNo() + " - " + s.getFullName();
                    break;

                default:
                    display = s.getAdmissionNo() + " - " + s.getFullName();
            }

            combo.addItem(display);
        }

        combo.getEditor().setItem(typedText);

        if (!students.isEmpty()) {
            styleLightPopup(combo);
            combo.showPopup();
        } else {
            combo.hidePopup();
        }
    }

    private static void styleLightPopup(JComboBox<?> combo) {

        Object comp = combo.getUI().getAccessibleChild(combo, 0);

        if (comp instanceof JPopupMenu) {

            JPopupMenu popup = (JPopupMenu) comp;
            popup.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            Component scroll = popup.getComponent(0);

            if (scroll instanceof JScrollPane) {

                JScrollPane scrollPane = (JScrollPane) scroll;
                JList<?> list = (JList<?>) scrollPane.getViewport().getView();

                list.setBackground(Color.WHITE);
                list.setForeground(new Color(40, 40, 40));
                list.setSelectionBackground(new Color(230, 240, 255));
                list.setSelectionForeground(Color.BLACK);
                list.setFixedCellHeight(28);

                list.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            }
        }
    }

    public static void openDialogWithDarkBackground(JFrame parentFrame, JDialog dialog) {
        JLayeredPane layeredPane = parentFrame.getLayeredPane();

        JPanel darkBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(0, 0, 0, 230));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        darkBackground.setOpaque(false);
        darkBackground.setBounds(0, 0, parentFrame.getWidth(), parentFrame.getHeight());

        layeredPane.add(darkBackground, JLayeredPane.MODAL_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();

        // Center dialog relative to parent
        dialog.setLocationRelativeTo(parentFrame);

        // Add listener to clean up overlay when dialog closes
        dialog.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                layeredPane.remove(darkBackground);
                layeredPane.revalidate();
                layeredPane.repaint();
                parentFrame.setEnabled(true);
                parentFrame.toFront();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                layeredPane.remove(darkBackground);
                layeredPane.revalidate();
                layeredPane.repaint();
                parentFrame.setEnabled(true);
                parentFrame.toFront();
            }
        });

        // ✅ Make the dialog modal temporarily
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        // Show dialog
        dialog.setVisible(true);
    }

    public static String formatWithComma(double amount) {
        return String.format("%,.2f", amount);
    }

    public static double parseCommaNumber(String text) {

        if (text == null || text.trim().isEmpty()) {
            return 0;
        }

        return Double.parseDouble(text.replace(",", "").trim());
    }

    public void setIntegerOnly(JTextField textField, int maxLength) {
        // Apply Roboto font (clean look for numbers)
        textField.setFont(new Font("Roboto", Font.PLAIN, 14));

        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();

                // Convert any localized digit to English 0–9
                if (Character.isDigit(c)) {
                    int numericValue = Character.getNumericValue(c);
                    if (numericValue >= 0 && numericValue <= 9) {
                        if (textField.getText().length() < maxLength) {
                            e.setKeyChar((char) ('0' + numericValue)); // replace with English digit
                        } else {
                            e.consume(); // exceed max
                        }
                    } else {
                        e.consume(); // invalid numeric symbol
                    }
                } else {
                    e.consume(); // block non-digits
                }
            }
        });
    }

    // This method loads and shows matching items from the database into a combo box
    public void loadMatchingComboItems(JComboBox<String> comboBox,
            String column,
            String table,
            String input) {

        SwingUtilities.invokeLater(() -> {

            EntityManager em = HibernateConfig.getEntityManager();

            try {

                String sql = "SELECT DISTINCT " + column
                        + " FROM " + table
                        + " WHERE status = 1 AND " + column + " LIKE ?";

                List<String> results = em.createNativeQuery(sql)
                        .setParameter(1, "%" + input + "%")
                        .getResultList();

                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                boolean found = false;

                for (String value : results) {
                    model.addElement(value);
                    found = true;
                }

                comboBox.setModel(model);
                comboBox.setSelectedItem(input);

                if (found) {
                    comboBox.setPopupVisible(true);
                } else {
                    comboBox.hidePopup();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }

        });
    }

    public void loadMatchingComboItemswithID(JComboBox<String> comboBox,
            String idColumn,
            String nameColumn,
            String table,
            String input) {

        SwingUtilities.invokeLater(() -> {

            EntityManager em = HibernateConfig.getEntityManager();

            try {

                String sql = "SELECT DISTINCT CONCAT(" + nameColumn + ", ' [', " + idColumn + ", ']') "
                        + "FROM " + table + " "
                        + "WHERE " + nameColumn + " LIKE ? "
                        + "AND status = 1";

                List<String> results = em.createNativeQuery(sql)
                        .setParameter(1, "%" + input + "%")
                        .getResultList();

                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
                boolean found = false;

                for (String value : results) {
                    model.addElement(value);
                    found = true;
                }

                comboBox.setModel(model);
                comboBox.setSelectedItem(input);

                if (found) {
                    comboBox.setPopupVisible(true);
                } else {
                    comboBox.hidePopup();
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                em.close();
            }

        });
    }

}
