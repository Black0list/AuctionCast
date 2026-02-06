package com.bidly.common.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPublicDTO implements Serializable {
    private String id;
    private String firstName;
    private String lastName;
}