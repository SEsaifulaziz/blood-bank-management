package com.bloodbank.dto.request;

import com.bloodbank.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDTO {

    @NotBlank(message = "Email is required.")
    @Email(message = "Invalid email format.")
    @Size(max = 100, message = "Email cannot exceed 100 characters.")
    private String email;

    @NotBlank(message = "Password is required for registration.")
    @Size(min = 6, max = 100,
            message = "Password must be between 6 and 100 characters.")
    private String password;
    
    @NotBlank(message = "Full name is required.")
    @Size(max = 100, message = "Full name cannot exceed 100 characters.")
    private String fullName;

    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number is invalid.")
    private String phone;

    @NotNull(message = "User role is required.")
    private UserRole userRole;
}
