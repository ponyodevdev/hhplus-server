package kr.hhplus.be.server.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.Objects;

@Entity
@Table(name = "concert")
public class Concert {

    @Id
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private boolean active;

    protected Concert() {
        // JPA 기본 생성자 (무조건 필요)
    }

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
