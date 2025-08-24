package com.zosmf.rest.client;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ZosmfRestClientTest {
    private static final String ZOSMF_URL = System.getenv("ZOSMF_URL"); // z/OSMF URL
    private static final String USER = System.getenv("ZOSMF_USER");
    private static final String PASSWORD = System.getenv("ZOSMF_PASSWORD");

    @Test
    public void ZosmfInfoTest() throws Exception {
        ZosmfRestClient zosmfRestClient = ZosmfRestClient.createBasicConnection(ZOSMF_URL, USER, PASSWORD);
        Map<String, String> parms = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        ZosmfResponse zosmfResponse = zosmfRestClient.executeGetRequest("info", parms, headers);
        System.out.println("Response Code: " + zosmfResponse.getResponseCode());
        System.out.println("Status Message: " + zosmfResponse.getStatusMessage());
        System.out.println("Response Body: " + zosmfResponse.getResponseBody());
    }
}