package fit.se.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEvent implements Serializable {
    private String eventType; // "USER_REGISTERED"
    private String username;
    private String email;
    private String fullName;
}