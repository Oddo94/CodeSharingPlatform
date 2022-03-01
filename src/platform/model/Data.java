package platform.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name= "code_snippets")
@JsonIgnoreProperties(value = {"id","UUID","expiryDate","isExpired", "hasInitialViewsRestriction"})
public class Data {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="snippet_id")
    @JsonProperty("id")
    private long id;

    @JsonProperty("UUID")
    private String UUID;

    @Column(name="code")
    private String code;

    @Column(name="creation_date")
    private LocalDateTime date;

    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss.SSS")
    @JsonProperty("expiryDate")
    private LocalDateTime expiryDate;

    @JsonProperty("isExpired")
    private boolean isExpired;

    @JsonProperty("hasInitialViewsRestriction")
    private boolean hasInitialViewsRestriction;

    private long time;

    private long views;
}
