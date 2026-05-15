package com.catering.ui.customer;

public class Customer {
    private String name;
    private String phone;
    private String email;

    public Customer() {}

    public Customer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        String n = name == null ? "" : name.trim();
        String p = phone == null ? "" : phone.trim();
        if (!n.isEmpty() && !p.isEmpty()) {
            return n + " (" + p + ")";
        }
        if (!n.isEmpty()) {
            return n;
        }
        if (!p.isEmpty()) {
            return p;
        }
        return "Customer";
    }
}
