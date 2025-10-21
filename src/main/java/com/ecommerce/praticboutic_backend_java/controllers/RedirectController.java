package com.ecommerce.praticboutic_backend_java.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class RedirectController {

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping("/redirect-handler")
    public void handleRedirect(
            @RequestParam(value = "platform", required = false, defaultValue = "web") String platform,
            @RequestParam(value = "status", required = false, defaultValue = "return") String status,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        switch (platform.toLowerCase()) {
            case "android":
            case "ios":
                if ("refresh".equals(status)) {
                    response.sendRedirect("praticboutic://onboarding-cancelled");
                } else {
                    response.sendRedirect("praticboutic://onboarding-complete");
                }
                break;

            default:
                response.sendRedirect(baseUrl + "/autoclose");
                break;
        }
    }

    @GetMapping("/autoclose")
    public String showAutoclosePage(Model model) {
        return "autoclose"; // => templates/autoclose.html
    }
}
