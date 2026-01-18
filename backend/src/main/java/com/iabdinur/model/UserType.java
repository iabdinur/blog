package com.iabdinur.model;

public enum UserType {
    REA("Reader"),
    AUT("Author");

    private final String description;

    UserType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
