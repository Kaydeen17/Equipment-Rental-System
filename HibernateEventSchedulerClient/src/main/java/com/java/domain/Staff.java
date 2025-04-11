package com.java.domain;


public class Staff {
    private int staffId;
    private String name;
    private String email;
    private String phone;
    private String role; // Role can be something like 'Manager', 'Cashier', etc.
    private String password; // New password field

    // Constructor
    public Staff(int staffId, String name, String email, String phone, String role, String password) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.password = password;
    }

    // Getters and Setters
    public int getStaffId() {
        return staffId;
    }

    public void setStaffId(int staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Optional: Override toString method to make it easier to display in UI (e.g., JComboBox)
    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
