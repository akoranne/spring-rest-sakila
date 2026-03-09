package com.example.app.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@FieldNameConstants
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HalRelation {
    // others
    private String self;
    private String create;
    private String update;
    private String delete;
    private String details;

    // catalog
    private String actor;
    private String actorList;
    private String film;
    private String filmList;

    // customer
    private String customer;
    private String customerList;

    // location
    private String address;
    private String addressList;
    private String city;
    private String cityList;

    // payment
    private String payment;
    private String paymentList;

    // rental
    private String rental;
    private String rentalList;

    // staff
    private String staff;
    private String staffList;

    // store
    private String store;
    private String storeList;
    private String categorySales;
    private String storeSales;
}
