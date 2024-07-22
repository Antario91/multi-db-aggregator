package com.multidb.aggregator.user;

public enum UserFields {
    ID("id"), USERNAME("username"), NAME("name"), SURNAME("surname");

    private final String fieldName;

    UserFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
