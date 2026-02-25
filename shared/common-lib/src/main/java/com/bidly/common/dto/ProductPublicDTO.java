package com.bidly.common.dto;

import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPublicDTO implements Serializable {
    private UUID id;
    private String title;
    private String description;
}