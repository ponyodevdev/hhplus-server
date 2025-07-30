package kr.hhplus.be.server.domain.model;

import java.util.Objects;

public class Concert {

    private final Long id;
    private final String title;
    private final String description;
    private final boolean active;

    public Concert(Long id, String title, String description, boolean active) {
        if (Objects.isNull(title) || title.isBlank()) {
            throw new IllegalArgumentException("공연 제목은 필수입니다.");
        }
        this.id = id;
        this.title = title;
        this.description = description;
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }


}
