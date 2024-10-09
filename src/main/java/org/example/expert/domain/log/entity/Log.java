package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "log_table")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String action;
    private String info;
    private LocalDateTime createdAt;

    public Log(String action, String info) {
        this.action = action;
        this.info = info;
        this.createdAt = LocalDateTime.now();
    }
}
