package com.ev.batteryswap.pojo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "swap_transactions")
public class SwapTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "battery_out_id", nullable = false)
    private Battery batteryOut;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "battery_in_id", nullable = false)
    private Battery batteryIn;

    @ColumnDefault("0.00")
    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Lob
    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Lob
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Lob
    @Column(name = "notes")
    private String notes;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;


}