package com.bloodbank.dto.request;

import com.bloodbank.model.UserRole;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class registerRequestDTO {

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 0, max = 100, message = "Password must ne between 8 and 100 characters")
    private String password;

    @Size(max = 20, message = "Phone number must not exceed 20 digits")
    private String phone;

    @NotNull(message = "User role is required")
    private UserRole userRole;
}
