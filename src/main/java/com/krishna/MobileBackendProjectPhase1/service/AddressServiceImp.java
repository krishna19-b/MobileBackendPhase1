package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.AddressRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.AddressResponse;
import com.krishna.MobileBackendProjectPhase1.entity.Address;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.AddressRepository;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImp implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImp(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public AddressResponse createAddress(Long userId, AddressRequest request) {
        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") && !loggedInUser.getId().equals(userId)) {
            throw new AccessDeniedException("Users can create addresses only for themselves");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

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
    @Transactional
    public List<AddressResponse> getAddresses(Long userId) {
        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") && !loggedInUser.getId().equals(userId)) {
            throw new AccessDeniedException("Users can view only their own addresses");
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        return addressRepository.findByUserId(userId)
                .stream()
                .map(AddressResponse::new)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAddress(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + addressId));

        User loggedInUser = getLoggedInUser();

        if (hasRole("USER") &&
                (address.getUser() == null || !address.getUser().getId().equals(loggedInUser.getId()))) {
            throw new AccessDeniedException("Users can delete only their own addresses");
        }

        addressRepository.delete(address);
    }

    private User getLoggedInUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Authentication required");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof User)) {
            throw new RuntimeException("Invalid authenticated user");
        }

        return (User) principal;
    }

    private boolean hasRole(String role) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_" + role));
    }
}