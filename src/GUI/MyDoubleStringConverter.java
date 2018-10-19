package GUI;

import javafx.util.converter.NumberStringConverter;

public class MyDoubleStringConverter extends NumberStringConverter {
    @Override
    public Number fromString(String value) {
        value = value.trim();
        value = value.replace(" ", "");
        value = value.replace(",", ".");
        System.out.println("value: " + value);
        if (value.isEmpty() || !isNumber(value)) {
            return null;
        } else {
            return Double.valueOf(value);
        }

    }
    private boolean isNumber(String value) {
        int size = value.length();
        value = value.trim();
        int numberOfDots = 0;
        for (int i = 0; i < size; i++) {
            if (value.charAt(i) == '.') {
                numberOfDots++;
                if (numberOfDots > 1) {
                    return false;
                }
                continue;
            }
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return size > 0;
    }
}