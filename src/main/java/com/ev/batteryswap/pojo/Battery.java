package com.ev.batteryswap.pojo;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "batteries")
public class Battery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "serial_number", nullable = false, length = 100)
    private String serialNumber;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "capacity_kwh", nullable = false, precision = 8, scale = 2)
    private BigDecimal capacityKwh;

    @ColumnDefault("0.00")
    @Column(name = "current_charge_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal currentChargePercentage;

    @ColumnDefault("100.00")
    @Column(name = "health_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal healthPercentage;

    @ColumnDefault("0")
    @Column(name = "charge_cycles")
    private Integer chargeCycles;

    @ColumnDefault("'EMPTY'")
    @Column(name = "status")
    private String status;

    @ColumnDefault("0.00")
    @Column(name = "amount", precision = 12, scale = 2)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at")
    private Instant updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getCapacityKwh() {
        return capacityKwh;
    }

    public void setCapacityKwh(BigDecimal capacityKwh) {
        this.capacityKwh = capacityKwh;
    }

    public BigDecimal getCurrentChargePercentage() {
        return currentChargePercentage;
    }

    public void setCurrentChargePercentage(BigDecimal currentChargePercentage) {
        this.currentChargePercentage = currentChargePercentage;
    }

    public BigDecimal getHealthPercentage() {
        return healthPercentage;
    }

    public void setHealthPercentage(BigDecimal healthPercentage) {
        this.healthPercentage = healthPercentage;
    }

    public Integer getChargeCycles() {
        return chargeCycles;
    }

    public void setChargeCycles(Integer chargeCycles) {
        this.chargeCycles = chargeCycles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(LocalDate manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

}