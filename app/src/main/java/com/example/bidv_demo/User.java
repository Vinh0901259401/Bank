package com.example.bidv_demo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class User {
    private int id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String avatar;
    private Date birthYear;
    private boolean isLocked;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String firstName;
    private String lastName;
    private BigDecimal balance;
    private String description;

    // Constructor
    public User(int id, String username, String email, String phone, String address, String password, String avatar,
                Date birthYear, boolean isLocked, Timestamp createdAt, Timestamp updatedAt,
                String firstName, String lastName, BigDecimal balance, String description) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
        this.avatar = avatar;
        this.birthYear = birthYear;
        this.isLocked = isLocked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.firstName = firstName;
        this.lastName = lastName;
        this.balance = balance;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getBirthYear() {
        return birthYear;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getDescription() {
        return description;
    }
}
