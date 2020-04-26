package it.polimi.ingsw.Utility;

public class Utils {
    /**
     * @param ip ipv4 address to be verified
     *           function taken from https://stackoverflow.com/Questions/5667371/validate-ipv4-address-in-java
     *           should be sufficient to verify correctness of ipv4 address
     * @return true if address is valid, false otherwise
     */
    public static boolean isValidIP(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";

        return ip.matches(PATTERN);
    }
}
