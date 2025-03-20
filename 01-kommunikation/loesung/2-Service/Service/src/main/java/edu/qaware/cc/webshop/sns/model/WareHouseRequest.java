package edu.qaware.cc.webshop.sns.model;

import java.util.List;

public record WareHouseRequest(
        String id,
        List<Item> items,
        Address address) {}
