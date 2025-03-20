package edu.qaware.cc.webshop.sns.model;

public record Address(
        String country,
        String state,
        String city,
        String zip,
        String address1,
        String address2,
        String address3) {}
