package com.grupo10.epicentrogourmet.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // ADMINISTRADOR, RESPONSABLE, EMPLEADO

    @OneToOne
    @JoinColumn(name = "staff_id")
    private Personal staff; // 1 vincula 1 con Staff. Puede ser Cocinero, Cajero, etc.

    public User() {}

    public User(String email, String passwordHash, UserRole role, Personal staff) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.staff = staff;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public Personal getStaff() { return staff; }
    public void setStaff(Personal staff) { this.staff = staff; }
}