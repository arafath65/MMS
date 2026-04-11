package Classes;

import javax.swing.text.*;
import java.util.regex.Pattern;

public class DecimalOnlyFilter extends DocumentFilter {

    private static final Pattern DECIMAL_PATTERN =
            Pattern.compile("^\\d*(\\.\\d{0,2})?$");

    private boolean isValid(String text) {
        return text.isEmpty() || DECIMAL_PATTERN.matcher(text).matches();
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
            throws BadLocationException {

        if (string == null) return;

        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = new StringBuilder(currentText).insert(offset, string).toString();

        if (isValid(newText)) {
            super.insertString(fb, offset, string, attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
            throws BadLocationException {

        if (text == null) text = "";

        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);

        if (isValid(newText)) {
            super.replace(fb, offset, length, text, attrs);
        }
    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {

        String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
        String newText = currentText.substring(0, offset) + currentText.substring(offset + length);

        if (isValid(newText)) {
            super.remove(fb, offset, length);
        }
    }
}