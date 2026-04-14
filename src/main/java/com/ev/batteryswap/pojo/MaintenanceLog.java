package com.ev.batteryswap.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "maintenance_logs.html")
@EntityListeners(AuditingEntityListener.class)
public class MaintenanceLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "battery_id", nullable = false)
    private Battery battery;

    @Lob
    @Column(name = "maintenance_type", nullable = false)
    private String maintenanceType;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @ColumnDefault("0.00")
    @Column(name = "cost", precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "technician", length = 100)
    private String technician;

    @Column(name = "maintenance_date", nullable = false)
    private LocalDate maintenanceDate;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

}
