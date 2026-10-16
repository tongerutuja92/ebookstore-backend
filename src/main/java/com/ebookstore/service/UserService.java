package com.ebookstore.service;

import com.ebookstore.dto.*;
import com.ebookstore.exception.BadRequestException;
import com.ebookstore.exception.ResourceNotFoundException;
import com.ebookstore.model.Address;
import com.ebookstore.model.Cart;
import com.ebookstore.model.User;
import com.ebookstore.repository.AddressRepository;
import com.ebookstore.repository.CartRepository;
import com.ebookstore.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;

    public UserService(UserRepository userRepository, AddressRepository addressRepository, CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
    }

    public AuthResponse register(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BadRequestException("User with email '" + request.email() + "' already exists");
        }

        // New accounts start with 100 bonus gift points
        User user = new User(
            request.email(),
            request.password(),
            request.fullName(),
            request.phoneNumber(),
            100
        );
        User savedUser = userRepository.save(user);

        // Auto-create shopping cart for user
        Cart cart = new Cart(savedUser);
        cartRepository.save(cart);

        String token = "jwt-token-" + UUID.randomUUID();
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(), savedUser.getFullName(), savedUser.getGiftPoints());
    }

    public AuthResponse login(UserLoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (!user.getPassword().equals(request.password())) {
            throw new BadRequestException("Invalid email or password");
        }

        String token = "jwt-token-" + UUID.randomUUID();
        return new AuthResponse(token, user.getId(), user.getEmail(), user.getFullName(), user.getGiftPoints());
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(Long userId) {
        User user = getUserOrThrow(userId);
        return new UserProfileResponse(
            user.getId(),
            user.getEmail(),
            user.getFullName(),
            user.getPhoneNumber(),
            user.getGiftPoints(),
            user.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(Long userId) {
        getUserOrThrow(userId);
        return addressRepository.findByUserId(userId).stream()
            .map(this::mapToAddressResponse)
            .toList();
    }

    public AddressResponse addAddress(Long userId, AddressRequest request) {
        User user = getUserOrThrow(userId);

        if (Boolean.TRUE.equals(request.isDefault())) {
            resetDefaultAddress(userId);
        }

        Address address = new Address(
            user,
            request.recipientName(),
            request.street(),
            request.city(),
            request.state(),
            request.postalCode(),
            request.country(),
            request.isDefault()
        );

        Address saved = addressRepository.save(address);
        return mapToAddressResponse(saved);
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        return mapToAddressResponse(address);
    }

    public AddressResponse updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));

        if (Boolean.TRUE.equals(request.isDefault())) {
            resetDefaultAddress(userId);
        }

        address.setRecipientName(request.recipientName());
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        if (request.country() != null) {
            address.setCountry(request.country());
        }
        if (request.isDefault() != null) {
            address.setIsDefault(request.isDefault());
        }

        Address updated = addressRepository.save(address);
        return mapToAddressResponse(updated);
    }

    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + addressId));
        addressRepository.delete(address);
    }

    private void resetDefaultAddress(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        for (Address addr : addresses) {
            if (Boolean.TRUE.equals(addr.getIsDefault())) {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            }
        }
    }

    public User getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    public AddressResponse mapToAddressResponse(Address address) {
        return new AddressResponse(
            address.getId(),
            address.getUser().getId(),
            address.getRecipientName(),
            address.getStreet(),
            address.getCity(),
            address.getState(),
            address.getPostalCode(),
            address.getCountry(),
            address.getIsDefault()
        );
    }
}
