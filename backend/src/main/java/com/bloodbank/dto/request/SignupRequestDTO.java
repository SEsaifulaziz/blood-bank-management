package com.bloodbank.dto.request;

import com.bloodbank.model.UserRole;
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
public class SignupRequestDTO {

    @NotBlank(message = "Email is required for registration.")
    @Email(message = "Invalid email format.")
    @Size(max = 100, message = "Email cannot exceed 100 characters.")
    private String email;

    @NotBlank(message = "Password is required for registration.")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters long.")
    private String password;

    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name must not exceed 100 characters.")
    private String fullName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters.")
    private String phone;

    @NotNull(message = "User role specification is required.")
    private UserRole userRole;
}
