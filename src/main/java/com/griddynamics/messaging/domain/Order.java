package com.griddynamics.messaging.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@AllArgsConstructor
@Data
public class Order implements Serializable {
    private int id;
    private String name;
}
