package com.phonebook_system.contact_service.model.request;

import com.phonebook_system.contact_service.model.ContactTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateContactInfoRequest {
    @NotNull(message = "Contact type is required")
    private ContactTypeEnum type;

    @NotBlank(message = "Contact value is required")
    private String value;
}
