package Classes;

import java.sql.*;
import java.util.List;
import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.SwingUtilities;

public class ChequeNumberFormatter {

    private final JTextField chequeNumberField;
    private final JComboBox<String> bankCombo;
    private final JTextField branchField;
    private boolean isUpdating = false;

    public ChequeNumberFormatter(JTextField chequeNumberField, JComboBox<String> bankCombo, JTextField branchField) {
        this.chequeNumberField = chequeNumberField;
        this.bankCombo = bankCombo;
        this.branchField = branchField;

        addFormatter();
    }

    private void addFormatter() {
        chequeNumberField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                formatAndSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                formatAndSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                // Not used
            }
        });
    }

    private void formatAndSearch() {
        if (isUpdating) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            isUpdating = true;
            try {
                String raw = chequeNumberField.getText().replaceAll("[^0-9]", "");

                // Format: 6 digits + 4 (bank) + 3 (branch) + rest
                StringBuilder formatted = new StringBuilder();
                int[] parts = {6, 4, 3};
                int index = 0;
                for (int part : parts) {
                    if (raw.length() > index) {
                        int end = Math.min(index + part, raw.length());
                        formatted.append(raw, index, end).append(" ");
                        index = end;
                    }
                }
                if (index < raw.length()) {
                    formatted.append(raw.substring(index));
                }

                String formattedText = formatted.toString().trim();

                if (!chequeNumberField.getText().equals(formattedText)) {
                    chequeNumberField.setText(formattedText);
                }

                // Extract and search bank + branch
                if (raw.length() >= 13) {
                    String bankCode = raw.substring(6, 10);
                    String branchCode = raw.substring(10, 13).replaceFirst("^0+(?!$)", ""); // remove leading zeros
                    updateBankAndBranch(bankCode, branchCode);
                } else {
                    bankCombo.setSelectedItem("Select");
                    branchField.setText("");
                }

            } finally {
                isUpdating = false;
            }
        });
    }

    private void updateBankAndBranch(String bankCode, String branchCode) {

        EntityManager em = HibernateConfig.getEntityManager();

        try {

            String sql = "SELECT b.bank_names, br.branch_names "
                    + "FROM bank_names_srilanka b "
                    + "LEFT JOIN bank_branches br ON b.bank_code = br.bank_code "
                    + "WHERE b.bank_code = ? AND br.branch_code = ?";

            List<Object[]> result = em.createNativeQuery(sql)
                    .setParameter(1, bankCode)
                    .setParameter(2, branchCode)
                    .getResultList();

            if (!result.isEmpty()) {

                Object[] row = result.get(0);

                bankCombo.setSelectedItem(row[0] != null ? row[0].toString() : "Select");
                branchField.setText(row[1] != null ? row[1].toString() : "");

            } else {

                bankCombo.setSelectedItem("Select");
                branchField.setText("");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }
}
