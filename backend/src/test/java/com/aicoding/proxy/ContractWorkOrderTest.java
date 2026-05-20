package com.aicoding.proxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

public class ContractWorkOrderTest {

    private static final String HOST = "https://servicego.udesk.cn";
    private static final String EMAIL = "ServiceGo.demo@udesk.cn";
    private static final String API_TOKEN = "51c8de11ba94e7a056b832414fdc8b15";
    private static final String OBJECT_API_NAME = "contractWorkOrder";
    private static final int FILTER_ID = 241618; // 过滤器ID，需要根据实际配置

    public static void main(String[] args) throws Exception {
        // 参数
        int pageNum = 1;
        int pageSize = 10;

        // 生成时间戳（秒）
        long timestamp = Instant.now().getEpochSecond();

        // 计算签名 SHA256(email&api_token&timestamp)
        String signInput = EMAIL + "&" + API_TOKEN + "&" + timestamp;
        String sign = sha256(signInput);

        // 构建URL - 使用 /v1/datas 接口
        String urlString = HOST + "/api/v1/datas?"
                + "objectApiName=" + OBJECT_API_NAME
                + "&filterId=" + FILTER_ID
                + "&pageNum=" + pageNum
                + "&pageSize=" + pageSize
                + "&email=" + URLEncoder.encode(EMAIL, StandardCharsets.UTF_8)
                + "&timestamp=" + timestamp
                + "&sign=" + sign;

        System.out.println("URL: " + urlString);
        System.out.println("Sign input: " + signInput);
        System.out.println("Sign: " + sign);
        System.out.println();

        // 发送请求
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