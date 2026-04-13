package util;

public final class PasswordPolicy {

    private PasswordPolicy() {
    }

    public static String validate(String password) {
        if (password == null) {
            return "Password is required.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
        }
        if (!hasUpper || !hasLower || !hasDigit) {
            return "Password must contain uppercase, lowercase, and a digit.";
        }
        return null;
    }
}
