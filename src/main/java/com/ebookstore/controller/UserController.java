package com.ebookstore.controller;

import com.ebookstore.dto.AddressRequest;
import com.ebookstore.dto.AddressResponse;
import com.ebookstore.dto.UserProfileResponse;
import com.ebookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Authentication & User", description = "User profile and address management")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieves user profile and current gift points balance")
    public ResponseEntity<UserProfileResponse> getProfile(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        UserProfileResponse profile = userService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/addresses")
    @Operation(summary = "List saved addresses", description = "Retrieves all delivery addresses saved by the user")
    public ResponseEntity<List<AddressResponse>> getAddresses(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId
    ) {
        List<AddressResponse> addresses = userService.getUserAddresses(userId);
        return ResponseEntity.ok(addresses);
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add delivery address", description = "Saves a new delivery address for the user")
    public ResponseEntity<AddressResponse> addAddress(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse address = userService.addAddress(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(address);
    }

    @GetMapping("/addresses/{id}")
    @Operation(summary = "Get address by ID")
    public ResponseEntity<AddressResponse> getAddressById(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long id
    ) {
        AddressResponse address = userService.getAddressById(userId, id);
        return ResponseEntity.ok(address);
    }

    @PutMapping("/addresses/{id}")
    @Operation(summary = "Update delivery address")
    public ResponseEntity<AddressResponse> updateAddress(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long id,
        @Valid @RequestBody AddressRequest request
    ) {
        AddressResponse address = userService.updateAddress(userId, id, request);
        return ResponseEntity.ok(address);
    }

    @DeleteMapping("/addresses/{id}")
    @Operation(summary = "Delete delivery address")
    public ResponseEntity<Void> deleteAddress(
        @Parameter(description = "Authenticated User ID", required = true)
        @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
        @PathVariable Long id
    ) {
        userService.deleteAddress(userId, id);
        return ResponseEntity.noContent().build();
    }
}
