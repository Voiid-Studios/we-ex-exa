package voiidstudios.wex.utils;

public final class TextUtils {
    private TextUtils() {
    }

    public static String toLegacy(String message) {
        if (message == null) {
            return "";
        }
        return message.replace("&", "\u00A7");
    }
}
