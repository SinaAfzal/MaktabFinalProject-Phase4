package ir.maktabsharif.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validation {


    public static boolean isPathValid(String path) {
        // Check if the path is empty or null
        if (path == null || path.isEmpty()) {
            return false;
        }

        // Try to convert the path to a Path object
        try {
            Path filePath = Paths.get(path);

            // Check if the file exists
            if (Files.exists(filePath)) {
                return true;
            }
        } catch (Exception e) {
            // If any exception occurs while checking the path, return false
            return false;
        }
        return false;
    }

    public static boolean isImageValid(String path) throws IOException {
        //Validate image path
        if (!isPathValid(path))
            return false;
        // Validate image format
        String mimeType = Files.probeContentType(Paths.get(path));
        if (((!path.toLowerCase().endsWith(".jpg")) &&
                (!path.toLowerCase().endsWith(".jpg\\")) &&
                (!path.toLowerCase().endsWith(".jpg/"))) ||
                !mimeType.equals("image/jpeg"))
            return false;
        // Validate image size (less than 300 KB)
        File imageFile = new File(path);
        if (imageFile.length() > 300 * 1024)  // 1024 bytes = 1 KB
            return false;
        return true;
    }

    public static boolean isPasswordValid(String password) {
        Pattern pattern =
                Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");//at least one upper-case, one lower-case and one digit and at least 8 characters
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern = Pattern.compile("^(\\S+)@([A-Za-z]\\w+)[.]([A-Za-z]+)$");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isWebsiteValid(String website) {
        Pattern pattern = Pattern.compile("[Ww][Ww][Ww]\\.[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = pattern.matcher(website);
        return matcher.matches();
    }

    public static boolean isPhoneNumberValid(String phoneNumber) {
        Pattern pattern = Pattern.compile("^[1-9]\\d{6,9}$");//7 to 10 digit number not starting with zero
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static boolean isNationalCodeValid(String nationalCode) {
        Pattern pattern = Pattern.compile("^[1-9]\\d{9}$");//10 digit number not starting with zero
        Matcher matcher = pattern.matcher(nationalCode);
        return matcher.matches();
    }

}
