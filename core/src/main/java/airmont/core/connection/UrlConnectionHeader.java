package airmont.core.connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record UrlConnectionHeader(Map<String, List<String>> headerFields) {

    private static final String HEADER_ENTRY_CONTENT_LENGTH = "Content-Length";
    private static final String HEADER_ENTRY_CONTENT_DISPOSITION = "Content-Disposition";

    public UrlConnectionHeader(Map<String, List<String>> headerFields) {
        this.headerFields = new HashMap<>(headerFields);
    }

    public String getFileName() {
        String contentDisposition = getEntry(headerFields, HEADER_ENTRY_CONTENT_DISPOSITION);
        if (contentDisposition == null || contentDisposition.isEmpty()) {
            return null;
        }
        String[] attributes = contentDisposition.split(";");
        for (String attribute : attributes) {
            if (attribute.toLowerCase().contains("filename")) {
                try {
                    return attribute.substring(attribute.indexOf('\"') + 1, attribute.lastIndexOf('\"'));
                } catch (Exception e) {
                    return attribute.substring(attribute.indexOf('=') + 1);
                }
            }
        }
        return null;
    }

    public long getFileSize() {
        String fullFileSizeText = getEntry(headerFields, HEADER_ENTRY_CONTENT_LENGTH);
        return fullFileSizeText == null ? 0 : Long.parseLong(fullFileSizeText);
    }

    private static String getEntry(Map<String, List<String>> headerFields, String entry) {
        List<String> fullFileSizes = headerFields.get(entry);
        if (fullFileSizes == null || fullFileSizes.isEmpty()) {
            return null;
        }
        return fullFileSizes.get(0);
    }

    @Override
    public String toString() {
        return headerFields.entrySet().stream()
                .map(entry -> entry.getKey() + " = " + String.join(", ", entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

}
