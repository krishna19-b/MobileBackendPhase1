package com.krishna.MobileBackendProjectPhase1.controller;

import com.krishna.MobileBackendProjectPhase1.dto.request.AddressRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.AddressResponse;
import com.krishna.MobileBackendProjectPhase1.service.AddressService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddressService addressService;


    public AddressController(AddressService addressService) {

        this.addressService = addressService;
    }

    @PostMapping("/users/{id}/addresses")
    public ResponseEntity<AddressResponse> createAddress(@PathVariable Long id, @RequestBody AddressRequest request) {
        AddressResponse response = addressService.createAddress(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/users/{id}/addresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddresses(id));
    }


    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}