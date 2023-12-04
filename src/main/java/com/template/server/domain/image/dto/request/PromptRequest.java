package com.template.server.domain.image.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PromptRequest implements Serializable {
    @NotBlank
    private String prompt;
    private int option;
}
