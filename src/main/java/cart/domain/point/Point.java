package cart.domain.point;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Point {

    private Long id;
    private Integer pointAmount;
    private final LocalDateTime createdAt;
    private final LocalDateTime expiredAt;

    public Point(final Integer pointAmount,
                 final LocalDateTime createdAt, final LocalDateTime expiredAt) {
        this.pointAmount = pointAmount;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public Point(final Long id, final Integer pointAmount,
                 final LocalDateTime createdAt, final LocalDateTime expiredAt) {
        this.id = id;
        this.pointAmount = pointAmount;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public void usedAllPoint() {
        this.pointAmount = 0;
    }

    public void decreasePoint(int point) {
        this.pointAmount -= point;
    }

    public void increasePoint(int point) {
        this.pointAmount += point;
    }

    public boolean isMatchId(Long id) {
        return Objects.equals(this.id, id);
    }

    public boolean isToBeExpired(LocalDateTime compareDateTime) {
        Duration duration = Duration.between(compareDateTime, expiredAt);
        long days = duration.toDays();
        return 0 <= days && days <= 30;
    }

    public int calculatePointByExpired() {
        LocalDateTime now = LocalDateTime.now();
        if (expiredAt == null || expiredAt.isBefore(now)) {
            return 0;
        }
        return pointAmount;
    }

    public Long getId() {
        return id;
    }

    public Integer getPointAmount() {
        return pointAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", pointAmount=" + pointAmount +
                ", createdAt=" + createdAt +
                ", expiredAt=" + expiredAt +
                '}';
    }
}
