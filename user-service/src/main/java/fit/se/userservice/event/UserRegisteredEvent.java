package fit.se.userservice.event;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisteredEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String eventType;
    private LocalDateTime eventTimestamp;
}
