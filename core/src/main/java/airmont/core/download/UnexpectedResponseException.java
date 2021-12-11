package airmont.core.download;

import java.io.IOException;

public class UnexpectedResponseException extends IOException {
    private final int responseCode;
    private final int expectedResponseCode;

    public UnexpectedResponseException(int responseCode, int expectedResponseCode) {
        super("Expected response " + expectedResponseCode + " but got " + responseCode);
        this.responseCode = responseCode;
        this.expectedResponseCode = expectedResponseCode;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getExpectedResponseCode() {
        return expectedResponseCode;
    }
}
