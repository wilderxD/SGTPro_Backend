package com.example.sgtpro.SGTPRO.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Integer idUsuario;
    private RolDTO rol;
    private String nombreCompleto;
    private String correo;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
