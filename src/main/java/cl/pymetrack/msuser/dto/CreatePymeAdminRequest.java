package cl.pymetrack.msuser.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreatePymeAdminRequest {

    @NotBlank
    private String nombrePyme;

    @NotBlank
    private String rutPyme;

    @NotBlank
    @Email
    private String emailContactoPyme;

    private String telefonoContactoPyme;
    private String direccionSucursalPyme;
    private String comunaSucursalPyme;
    private String regionSucursalPyme;

    @NotBlank
    private String nombreRepresentante;

    @NotBlank
    private String apellidoRepresentante;

    @NotBlank
    @Email
    private String emailRepresentante;

    @NotBlank
    @Size(min = 8)
    private String password;

    public String getNombrePyme() { return nombrePyme; }
    public void setNombrePyme(String nombrePyme) { this.nombrePyme = nombrePyme; }

    public String getRutPyme() { return rutPyme; }
    public void setRutPyme(String rutPyme) { this.rutPyme = rutPyme; }

    public String getEmailContactoPyme() { return emailContactoPyme; }
    public void setEmailContactoPyme(String emailContactoPyme) { this.emailContactoPyme = emailContactoPyme; }

    public String getTelefonoContactoPyme() { return telefonoContactoPyme; }
    public void setTelefonoContactoPyme(String telefonoContactoPyme) { this.telefonoContactoPyme = telefonoContactoPyme; }

    public String getDireccionSucursalPyme() { return direccionSucursalPyme; }
    public void setDireccionSucursalPyme(String direccionSucursalPyme) { this.direccionSucursalPyme = direccionSucursalPyme; }

    public String getComunaSucursalPyme() { return comunaSucursalPyme; }
    public void setComunaSucursalPyme(String comunaSucursalPyme) { this.comunaSucursalPyme = comunaSucursalPyme; }

    public String getRegionSucursalPyme() { return regionSucursalPyme; }
    public void setRegionSucursalPyme(String regionSucursalPyme) { this.regionSucursalPyme = regionSucursalPyme; }

    public String getNombreRepresentante() { return nombreRepresentante; }
    public void setNombreRepresentante(String nombreRepresentante) { this.nombreRepresentante = nombreRepresentante; }

    public String getApellidoRepresentante() { return apellidoRepresentante; }
    public void setApellidoRepresentante(String apellidoRepresentante) { this.apellidoRepresentante = apellidoRepresentante; }

    public String getEmailRepresentante() { return emailRepresentante; }
    public void setEmailRepresentante(String emailRepresentante) { this.emailRepresentante = emailRepresentante; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}