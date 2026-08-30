package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.AddressRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.AddressResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface AddressService {

    AddressResponse createAddress(Long userId, AddressRequest request);

    List<AddressResponse> getAddresses(Long userId);

    void deleteAddress(Long addressId);
}