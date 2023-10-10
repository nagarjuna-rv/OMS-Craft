package com.intuit.order.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusDto {

    @NotNull
    private Long orderId;

    @NotBlank
    @Pattern(regexp = "^(DELIVERED|CANCELLED)$", message = "Delivery status must be 'DELIVERED' or 'CANCELLED'")
    private String status;

}
