package com.template.server.domain.health;

import com.template.server.global.error.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public Response<Void> healthCheck() {
        return Response.success(200, null);
    }
}
