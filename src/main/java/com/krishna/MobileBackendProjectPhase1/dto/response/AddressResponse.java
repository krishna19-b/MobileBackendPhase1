package com.krishna.MobileBackendProjectPhase1.dto.response;

import com.krishna.MobileBackendProjectPhase1.entity.Address;

public class AddressResponse {

    private Long id;
    private String houseNumber;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private Long userId;


    public AddressResponse(Address address) {

        this.id = address.getId();
        this.houseNumber = address.getHouseNumber();
        this.street = address.getStreet();
        this.city = address.getCity();
        this.state = address.getState();
        this.pincode = address.getPincode();

        if (address.getUser() != null) {
            this.userId = address.getUser().getId();
        }
    }


    public Long getId() {
        return id;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getPincode() {
        return pincode;
    }

    public Long getUserId() {
        return userId;
    }
}