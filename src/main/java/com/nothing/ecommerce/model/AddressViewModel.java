package com.nothing.ecommerce.model;

import com.nothing.ecommerce.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressViewModel {

    private int id;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private int main;

    public AddressViewModel(Address address) {
        this.id = address.getId();
        this.streetAddress = address.getStreetAddress();
        this.city = address.getCity();
        this.state = address.getState();
        this.postalCode = address.getPostalCode();
        this.country = address.getCountry();
        this.main = address.getMain();
    }

    public Boolean isMain() {
        return this.main != 0 ? true : false;
    }
}
