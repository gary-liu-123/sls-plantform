package com.aicoding.proxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

public class ContractWorkOrderQueryByPhoneTest {

    private static final String HOST = "https://servicego.udesk.cn";
    private static final String EMAIL = "ServiceGo.demo@udesk.cn";
    private static final String API_TOKEN = "51c8de11ba94e7a056b832414fdc8b15";
    private static final String OBJECT_API_NAME = "contractWorkOrder";
    private static final String UNIQUE_FIELD_API_NAME = "phone";

    public static void main(String[] args) throws Exception {
        queryContractWorkOrderByPhone("15651818750");
    }

    /**
     * 根据个人电话(phone)查询合同工单
     * 通过接口规范 3.3 的"唯一字段+字段值"定位方式
     */
    public static void queryContractWorkOrderByPhone(String phone) throws Exception {
        long timestamp = Instant.now().getEpochSecond();
        String signInput = EMAIL + "&" + API_TOKEN + "&" + timestamp;
        String sign = sha256(signInput);

        String urlString = HOST + "/api/v1/data?"
                + "objectApiName=" + OBJECT_API_NAME
                + "&uniqueFieldApiName=" + UNIQUE_FIELD_API_NAME
                + "&uniqueFieldValue=" + URLEncoder.encode(phone, StandardCharsets.UTF_8)
                + "&email=" + URLEncoder.encode(EMAIL, StandardCharsets.UTF_8)
                + "&timestamp=" + timestamp
                + "&sign=" + sign;

        System.out.println("=== 按 phone 查询合同工单 ===");
        System.out.println("Phone: " + phone);
        System.out.println("URL: " + urlString);
        System.out.println("Sign input: " + signInput);
        System.out.println("Sign: " + sign);
        System.out.println();

        String response = sendGet(urlString);
        System.out.println("Response: " + response);
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA256计算失败", e);
        }
    }

    public static String sendGet(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);

        int responseCode = conn.getResponseCode();
        System.out.println("HTTP Status: " + responseCode);

        StringBuilder response = new StringBuilder();
        try (var reader = new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream(),
                StandardCharsets.UTF_8)) {
            int ch;
            while ((ch = reader.read()) != -1) {
                response.append((char) ch);
            }
        }

        return response.toString();
    }
}
