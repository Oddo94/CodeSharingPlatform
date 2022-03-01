package platform.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DetailedResponse {
    private String resultCode;
    private HttpStatus httpStatus;
    private String responseMessage;
}
