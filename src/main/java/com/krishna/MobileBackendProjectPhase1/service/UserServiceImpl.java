package com.krishna.MobileBackendProjectPhase1.service;

import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserRequest;
import com.krishna.MobileBackendProjectPhase1.dto.request.userRequest.UserUpdateRequest;
import com.krishna.MobileBackendProjectPhase1.dto.response.UserResponse;
import com.krishna.MobileBackendProjectPhase1.entity.User;
import com.krishna.MobileBackendProjectPhase1.exception.DuplicateUserException;
import com.krishna.MobileBackendProjectPhase1.exception.UserNotFoundException;
import com.krishna.MobileBackendProjectPhase1.repository.UserRepository;
import com.krishna.MobileBackendProjectPhase1.specification.UserSpecification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl
        implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    // Add User

    @Override
    @Transactional
    public UserResponse createUser(UserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {

            throw new DuplicateUserException("Email already registered: " + request.getEmail());
        }

        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {

            throw new DuplicateUserException("Mobile number already registered: " + request.getMobileNumber());
        }

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setMobileNumber(request.getMobileNumber());
        user.setPassword(request.getPassword());

        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser);
    }

    //Get All Users

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(int page, int size, String sort) {

        Pageable pageable = createPageable(page, size, sort);

        Page<User> users = userRepository.findAll(pageable);

        return users.map(UserResponse::new);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return new UserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {

            throw new DuplicateUserException("Email already registered: " + request.getEmail());
        }

        if (userRepository.existsByMobileNumberAndIdNot(request.getMobileNumber(), id)) {

            throw new DuplicateUserException("Mobile number already registered: " + request.getMobileNumber());
        }

        user.setFirstName(request.getFirstName());

        user.setLastName(request.getLastName());

        user.setEmail(request.getEmail());

        user.setMobileNumber(request.getMobileNumber());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(request.getPassword());
        }

        return new UserResponse(userRepository.save(user));
    }

    // Delete User

    @Override
    @Transactional
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {

            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String name, int page, int size, String sort) {

        Pageable pageable = createPageable(page, size, sort);

        Specification<User> specification = UserSpecification.nameContains(name);

        Page<User> users = userRepository.findAll(specification,pageable);

        return users.map(UserResponse::new);
    }

    private Pageable createPageable(int page, int size, String sort) {

        String[] sortParts = sort.split(",");

        String property = sortParts[0];

        Sort.Direction direction = Sort.Direction.ASC;

        if (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")) {

            direction = Sort.Direction.DESC;
        }

        return PageRequest.of(page, size, Sort.by(direction, property));
    }
}