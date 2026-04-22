package com.gadhub.overseasproduct.dto;

import lombok.Data;

@Data
public class UpdateUserInfoDTO {
    private String newUsername;
    private String oldPassword;
    private String newPassword;
    private String email;
}
