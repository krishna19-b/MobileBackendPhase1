package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.AddressRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.AddressResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Address;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.AddressRepository;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImp implements AddressService {
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImp(
            AddressRepository addressRepository,
            UserRepository userRepository) {

        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Address address = new Address();
        address.setHouseNumber(request.getHouseNumber());

        address.setStreet(request.getStreet());

        address.setCity(request.getCity());

        address.setState(request.getState());

        address.setPincode(request.getPincode());

        address.setUser(user);

        Address saved = addressRepository.save(address);

        return new AddressResponse(saved);
    }

    @Override
    public List<AddressResponse> getAddresses(Long userId) {

        if (!userRepository.existsById(userId)) {

            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return addressRepository.findByUserId(userId).stream().map(AddressResponse::new).toList();
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }
}