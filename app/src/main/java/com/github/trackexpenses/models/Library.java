package com.github.trackexpenses.models;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Library {
    String name;
    String license;
    String url;
}
