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
    ANSI_WHITE("\u001B[37m"),
    ANSI_WHITE_BG("\u001B[47m"),
    ANSI_CYAN_BG("\u001b[46"),
    ANSI_LEVEL1("\u001B[2680m");

    public static final String RESET = "\u001B[0m";
    public static final String BOLD = "\u001b[1m";

    private final String escape;

    Color(String escape)
    {
        this.escape= escape;
    }
    public String getEscape()
    {
        return escape;
    }
        @Override
        public String toString()
        {
             return escape;
        }
}