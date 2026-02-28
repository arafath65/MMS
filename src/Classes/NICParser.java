package Classes;

import com.toedter.calendar.JDateChooser;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class NICParser {

    public static class NICDetails {

        public LocalDate dob;
        public String ageText;
        public String gender;

        public NICDetails(LocalDate dob, String ageText, String gender) {
            this.dob = dob;
            this.ageText = ageText;
            this.gender = gender;
        }
    }

    public static NICDetails parseNIC(String nic) {

        if (nic == null || nic.trim().isEmpty()) {
            return null;
        }

        nic = nic.trim().toUpperCase();

        try {
            LocalDate dob;
            boolean isFemale = false;

            // OLD NIC (9 digits + V/X)
            if (nic.length() == 10 && (nic.endsWith("V") || nic.endsWith("X"))) {

                String numbers = nic.substring(0, 9);

                int yearPart = Integer.parseInt(numbers.substring(0, 2));
                int year = (yearPart <= 30) ? 2000 + yearPart : 1900 + yearPart;

                int dayOfYear = Integer.parseInt(numbers.substring(2, 5));

                if (dayOfYear > 500) {
                    isFemale = true;
                    dayOfYear -= 500;
                }

                dob = LocalDate.ofYearDay(year, dayOfYear);
            } // NEW NIC (12 digits)
            else if (nic.length() == 12 && nic.matches("\\d{12}")) {

                int year = Integer.parseInt(nic.substring(0, 4));
                int dayOfYear = Integer.parseInt(nic.substring(4, 7));

                if (dayOfYear > 500) {
                    isFemale = true;
                    dayOfYear -= 500;
                }

                dob = LocalDate.ofYearDay(year, dayOfYear);
            } else {
                throw new IllegalArgumentException("Invalid NIC format");
            }

            // Calculate Age
            LocalDate today = LocalDate.now();
            int years = Period.between(dob, today).getYears();

            String ageText = numberToWordsEnglish(years) + " years (" + years + ")";
            String gender = isFemale ? "Female" : "Male";

            return new NICDetails(dob, ageText, gender);

        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid NIC format");
        }
    }

    public static Date localDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.of("Asia/Colombo")).toInstant());
    }

    public static void setNICDetailsToFields(String nic,
            JDateChooser dateChooser,
            javax.swing.JTextField ageField,
            javax.swing.JTextField genderField) {
        try {
            NICDetails details = parseNIC(nic);

            if (details != null) {
                dateChooser.setDate(localDateToDate(details.dob));
                ageField.setText(details.ageText);
                genderField.setText(details.gender);
            } else {
                clearFields(dateChooser, ageField, genderField);
            }

        } catch (Exception e) {
            clearFields(dateChooser, ageField, genderField);
        }
    }

    private static void clearFields(JDateChooser dateChooser,
            javax.swing.JTextField ageField,
            javax.swing.JTextField genderField) {
        dateChooser.setDate(null);
        ageField.setText("");
        genderField.setText("");
    }

    // ------------------------------
    // English Number to Words
    // ------------------------------
    private static String numberToWordsEnglish(int num) {

        String[] belowTwenty = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
            "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen",
            "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
        };

        String[] tens = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty",
            "Seventy", "Eighty", "Ninety"
        };

        if (num < 20) {
            return belowTwenty[num];
        }

        if (num < 100) {
            int t = num / 10;
            int o = num % 10;
            return tens[t] + (o > 0 ? " " + belowTwenty[o] : "");
        }

        int h = num / 100;
        int r = num % 100;

        return belowTwenty[h] + " Hundred"
                + (r > 0 ? " " + numberToWordsEnglish(r) : "");
    }

    public static String calculateAgeFromDOB(LocalDate dob) {

        if (dob == null) {
            return "";
        }

        LocalDate today = LocalDate.now();
        int years = Period.between(dob, today).getYears();

        return numberToWordsEnglish(years) + " years (" + years + ")";
    }

    public static void setAgeFromDOB(JDateChooser dateChooser,
            javax.swing.JTextField ageField) {

        if (dateChooser.getDate() == null) {
            ageField.setText("");
            return;
        }

        LocalDate dob = dateChooser.getDate()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        String ageText = calculateAgeFromDOB(dob);
        ageField.setText(ageText);
    }

}
