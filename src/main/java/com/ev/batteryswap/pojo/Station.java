package com.ev.batteryswap.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "stations")
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Lob
    @Column(name = "address", nullable = false)
    private String address;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quan", nullable = false)
    private Quanhuyen quan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "province", nullable = false)
    private Tinhthanh province;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phuongxa")
    private Phuongxa phuongxa;

    @Lob
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

}
