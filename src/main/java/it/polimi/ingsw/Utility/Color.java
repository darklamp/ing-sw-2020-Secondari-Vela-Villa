package it.polimi.ingsw.Utility;

/**
 * Colors for CLI
 */
public enum Color {
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_CYAN("\u001B[36m"),
    ANSI_WHITE("\u001B[37m");

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001b[1m";

    private final String escape;

    Color(String escape) {
        this.escape = escape;
    }

    public Color next() {
        if (this.ordinal() == Color.values().length - 1) return Color.values()[0];
        else return Color.values()[this.ordinal() + 1];
    }

    public static String Rainbow(String s) {
        StringBuilder out = new StringBuilder();
        Color cur = ANSI_RED;
        short count = 0;
        for (char c : s.toCharArray()) {
            if (c != ' ') {
                out.append(cur).append(c);
                if (count == (short) 3) {
                    cur = cur.next();
                    count = 0;
                } else count += 1;
            } else out.append(' ');
        }
        out.append(Color.RESET);
        return out.toString();
    }

    @Override
    public String toString() {
        return escape;
    }
}