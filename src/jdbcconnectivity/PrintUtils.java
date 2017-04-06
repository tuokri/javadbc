import java.io.PrintStream;

public class PrintUtils {

    public enum Color {

        RESET ("\u001B[0m"),
        BLACK ("\u001B[30m"),
        RED ("\u001B[31m"),
        GREEN ("\u001B[32m"),
        YELLOW ("\u001B[33m"),
        BLUE ("\u001B[34m"),
        PURPLE ("\u001B[35m"),
        CYAN ("\u001B[36m"),
        WHITE ("\u001B[37m"),
        BLACKBG ("\u001B[40m"),
        REDBG ("\u001B[41m"),
        GREENBG ("\u001B[42m"),
        YELLOWBG ("\u001B[43m"),
        BLUEBG ("\u001B[44m"),
        PURPLEBG ("\u001B[45m"),
        CYANBG ("\u001B[46m"),
        WHITEBG ("\u001B[47m");

        private final String _code;

        Color(String code) {

            _code = code;

        }

        public String getCode() {

            return _code;

        }

    }

    private PrintUtils() {

    }

    public static void printlnColor(String msg, Color color, PrintStream out) throws ColorDoesNotExistException {

        out.println(color.getCode() + msg + Color.RESET.getCode());

    }

    public static void printColor(String msg, Color color, PrintStream out) throws ColorDoesNotExistException {

        out.print(color.getCode() + msg + Color.RESET.getCode());

    }
}
