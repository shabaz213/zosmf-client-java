package com.zosmf.rest.client;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;

public class ZosmfRestClient {

    private static final String X_CSRF_ZOSMF_HEADER = "X-CSRF-ZOSMF-HEADER";
    private static final String AUTHORIZATION = "Authorization";
    private static final String GET_REQUEST = "GET";
    private static final String PROTOCOL = "TLS";
    private final String zosmfHost;
    private String user;
    private String password;

    private ZosmfConnectionType zosmfConnectionType;

    public ZosmfRestClient(String zosmfHost) {
        this.zosmfHost = zosmfHost;
    }

    public static ZosmfRestClient createBasicConnection(String zosmfHost, String user, String password) {
        ZosmfRestClient zosmfRestClient = new ZosmfRestClient(zosmfHost);
        zosmfRestClient.setUser(user);
        zosmfRestClient.setPassword(password);
        zosmfRestClient.setConnectionType(ZosmfConnectionType.BASIC);
        return zosmfRestClient;
    }

    private void setConnectionType(ZosmfConnectionType zosmfConnectionType) {
        this.zosmfConnectionType = zosmfConnectionType;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    private void setUser(String user) {
        this.user = user;
    }

    private String getAuthHeader() {
        String credentials = user + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    public ZosmfResponse executeGetRequest(String zosmfApi, Map<String, String> queryParameters, Map<String, String> requestHeaders) throws Exception {
        // Set default SSL socket factory
        HttpsURLConnection.setDefaultSSLSocketFactory(getInsecureSSLContext().getSocketFactory());

        // Disable hostname verification (like --insecure)
        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);

        String endpoint = zosmfHost + "/zosmf/" + zosmfApi;  // Example endpoint

        HttpsURLConnection connection = null;
        try {
            connection = (HttpsURLConnection) new URI(endpoint).toURL().openConnection();
            connection.setRequestMethod(GET_REQUEST);
            setHeaders(connection, requestHeaders);

            ZosmfResponse zosmfResponse = new ZosmfResponse(connection.getResponseCode(), connection.getResponseMessage());
            StringBuilder responseBody = getResponseBody(connection);
            zosmfResponse.setResponseBody(responseBody.toString().trim());
            return zosmfResponse;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void setHeaders(HttpsURLConnection connection, Map<String, String> requestHeaders) {
        if (requestHeaders != null && !requestHeaders.isEmpty()) {
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        connection.setRequestProperty(AUTHORIZATION, getAuthHeader());
        connection.setRequestProperty(X_CSRF_ZOSMF_HEADER, Boolean.TRUE.toString());
    }

    private static StringBuilder getResponseBody(HttpsURLConnection connection) throws IOException {
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                responseBody.append(inputLine).append(System.lineSeparator());
            }
        }

        InputStream errorStream = connection.getErrorStream();
        if (errorStream != null) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(errorStream))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    responseBody.append(inputLine).append(System.lineSeparator());
                }
            }
        }
        return responseBody;
    }

    private static SSLContext getInsecureSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
        };
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.init(null, trustAllCerts, new SecureRandom());
        return sslContext;
    }

    public void executePutRequest() {
        //TODO implementation
    }

    public void executePostRequest() {
        //TODO implementation
    }

    public void executeDeleteRequest() {
        //TODO implementation
    }
}
