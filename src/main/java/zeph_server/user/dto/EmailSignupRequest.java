package zeph_server.user.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmailSignupRequest(
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        @NotBlank
        @JsonAlias("nickname")
        String name
) {}
