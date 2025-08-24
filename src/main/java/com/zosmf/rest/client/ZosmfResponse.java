package com.zosmf.rest.client;

public class ZosmfResponse {
    private final int responseCode;
    private final String statusMessage;
    private String responseBody;

    ZosmfResponse(int responseCode, String statusMessage) {
        this.responseCode = responseCode;
        this.statusMessage = statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
