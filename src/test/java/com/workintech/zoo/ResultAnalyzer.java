package com.workintech.zoo;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ResultAnalyzer implements TestWatcher, AfterAllCallback {

    private List<String> results = new ArrayList<>();

    // 🔥 BURAYI DEĞİŞTİR
    private static final String taskId = "308111"; // ← senin projenin ID'si

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        results.add("DISABLED");
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        results.add("SUCCESSFUL");
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        results.add("ABORTED");
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        results.add("FAILED");
    }

    @Override
    public void afterAll(ExtensionContext context) {

        long passed = results.stream().filter(r -> r.equals("SUCCESSFUL")).count();
        long failed = results.stream().filter(r -> r.equals("FAILED")).count();

        try {
            JSONObject json = new JSONObject();
            json.put("taskId", taskId);
            json.put("successCount", passed);
            json.put("failureCount", failed);

            HttpPost request = new HttpPost("https://nextgen.workintech.com/api/results");

            StringEntity params = new StringEntity(json.toString());
            request.addHeader("content-type", "application/json");
            request.setEntity(params);

            HttpClientBuilder.create().build().execute(request);

        } catch (Exception e) {
            System.out.println("Result gönderilemedi");
        }
    }
}