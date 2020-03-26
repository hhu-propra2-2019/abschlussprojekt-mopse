package mops.persistence.event;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import mops.util.AggregateRoot;
import org.springframework.data.annotation.Id;

/**
 * Represents the latest event id.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@AggregateRoot
public class LatestEventId {

    /**
     * Database Id.
     */
    @Id
    private Long id;
    /**
     * Event Id.
     */
    private long eventId;

    /**
     * Create a new latest event id container.
     *
     * @return new instance
     */
    public static LatestEventId of() {
        return new LatestEventId(null, 0L);
    }
}
