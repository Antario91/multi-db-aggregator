package com.multidb.aggregator.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    private String id;
    private String username;
    private String name;
    private String surname;
}
