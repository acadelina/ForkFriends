package ubb.scs.map.lab6.domain;

import java.time.LocalDateTime;

public class FriendRequest extends Entity<Tuple<Long, Long>> {
    String status;
    LocalDateTime date;

    public FriendRequest() {
        this.date = LocalDateTime.now();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
