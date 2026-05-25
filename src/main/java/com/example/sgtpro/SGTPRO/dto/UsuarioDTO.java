package com.example.sgtpro.SGTPRO.dto;

import com.example.sgtpro.SGTPRO.entity.Rol;

public class UsuarioDTO {
    
    private Integer idUsuario;
    private Rol rol;
    private String nombreCompleto;
    private String correo;
    private String password;

    public UsuarioDTO() {
    }

    private UsuarioDTO(Integer idUsuario, Rol rol, String nombreCompleto, String correo, String password) {
        this.idUsuario = idUsuario;
        this.rol = rol;
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.password = password;
    }
    
    

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }
    
    public static UsuarioDTOBuilder builder(){
        return new UsuarioDTOBuilder();
    }
    
    public static class UsuarioDTOBuilder{
        private Integer idUsuario;
        private Rol rol;
        private String nombreCompleto;
        private String correo;
        private String password;
        
        public UsuarioDTOBuilder idUsuario(Integer idUsuario){
            this.idUsuario = idUsuario;
            return this;
        }
        
        public UsuarioDTOBuilder rol(Rol rol){
            this.rol = rol;
            return this;
        }
        
        public UsuarioDTOBuilder nombreCompleto(String nombreCompleto){
            this.nombreCompleto = nombreCompleto;
            return this;
        }
        
        public UsuarioDTOBuilder correo(String correo){
            this.correo = correo;
            return this;
        }
        
        public UsuarioDTOBuilder password(String password){
            this.password = password;
            return this;
        }
        
        public UsuarioDTO build(){
            return new UsuarioDTO(idUsuario, rol, nombreCompleto, correo, password);
        }
        
    }
    
    
    
}
