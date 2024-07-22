package com.multidb.aggregator.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DataSource {
    private String name;
    private String strategy;
    private String url;
    private String table;
    private String user;
    private String password;
    private ColumnMapping mapping;
}
