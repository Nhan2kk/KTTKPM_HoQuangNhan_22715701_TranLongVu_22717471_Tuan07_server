package fit.se.userservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/users") // QUAN TRỌNG: Phải khớp với Path trong Gateway
public class UserController {

    @GetMapping("/test")
    public Map<String, String> testGateway() {
        return Map.of(
                "status", "Success",
                "message", "Chào bạn! Data này được gửi từ User Service (Port 8084)",
                "gateway_status", "Connected"
        );
    }
}
