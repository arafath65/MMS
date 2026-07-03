/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Classes;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class CameraCapture {

    private final JLabel targetLabel;
    private final Component parent;

    public CameraCapture(Component parent, JLabel targetLabel) {
        this.parent = parent;
        this.targetLabel = targetLabel;
    }

    public void openCamera() {

        try {

            java.util.List<Webcam> webcams = Webcam.getWebcams();

            if (webcams == null || webcams.isEmpty()) {

                JOptionPane.showMessageDialog(
                        parent,
                        "No camera detected."
                );

                return;
            }

            Webcam webcam = webcams.get(0);

            webcam.setViewSize(
                    WebcamResolution.VGA.getSize()
            );

            WebcamPanel webcamPanel = new WebcamPanel(webcam);

            webcamPanel.setFPSDisplayed(true);
            webcamPanel.setMirrored(true);

            Window window = SwingUtilities.getWindowAncestor(parent);

            JDialog dialog = new JDialog(window);
            dialog.setModal(true);

            dialog.setTitle("Take Photo");
            dialog.setSize(700, 550);
            dialog.setLocationRelativeTo(parent);
            dialog.setLayout(new BorderLayout());

            JPanel buttonPanel = new JPanel();

            JButton captureBtn = new JButton("Capture");
            JButton cancelBtn = new JButton("Cancel");

            buttonPanel.add(captureBtn);
            buttonPanel.add(cancelBtn);

            dialog.add(webcamPanel, BorderLayout.CENTER);
            dialog.add(buttonPanel, BorderLayout.SOUTH);

            captureBtn.addActionListener(e -> {

                try {

                    BufferedImage image = webcam.getImage();

                    if (image != null) {

                        BufferedImage resized = GeneralMethods.resizeImage(
                                image,
                                171,
                                171
                        );

                        // Display
                        targetLabel.setIcon(new ImageIcon(resized));

                        // IMPORTANT - remember for saving
                        GeneralMethods.resizedImageToSave = resized;
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                webcam.close();
                dialog.dispose();

            });

//            captureBtn.addActionListener(e -> {
//
//                try {
//
//                    BufferedImage image = webcam.getImage();
//
//                    if (image != null) {
//
//                        Image scaled = image.getScaledInstance(
//                                targetLabel.getWidth(),
//                                targetLabel.getHeight(),
//                                Image.SCALE_SMOOTH
//                        );
//
//                        targetLabel.setIcon(
//                                new ImageIcon(scaled)
//                        );
//
//                    }
//
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//
//                webcam.close();
//                dialog.dispose();
//
//            });
            cancelBtn.addActionListener(e -> {

                webcam.close();
                dialog.dispose();

            });

            dialog.addWindowListener(
                    new java.awt.event.WindowAdapter() {

                @Override
                public void windowClosing(
                        java.awt.event.WindowEvent e) {

                    if (webcam.isOpen()) {
                        webcam.close();
                    }
                }
            });

            dialog.setVisible(true);

        } catch (Exception ex) {

            ex.printStackTrace();

            JOptionPane.showMessageDialog(
                    parent,
                    "Unable to access webcam."
            );
        }
    }
}
