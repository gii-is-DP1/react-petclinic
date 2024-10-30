package org.springframework.samples.learning;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class Customer {
    private String firstName;
    private String sureName;
    private String address;
}
