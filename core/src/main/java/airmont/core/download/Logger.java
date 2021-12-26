package airmont.core.download;

public record Logger(String prefix) {

    public Logger(String prefix) {
        this.prefix = truncateIfNeeded(prefix);
    }

    private String truncateIfNeeded(String text) {
        int length = text.length();
        if (length > 40) {
            return text.substring(0, 15) + "..." + text.substring(length - 20, length);
        }
        return text;
    }

    public void out(String message) {
        System.out.println(getPrefixInBrackets() + message);
    }

    public void err(String message) {
        System.err.println(getPrefixInBrackets() + message);
    }

    public void err(Throwable e) {
        System.err.println(getPrefixInBrackets() + e.getMessage());
        e.printStackTrace(System.err);
    }

    private String getPrefixInBrackets() {
        return "[" + prefix + "] ";
    }
}
