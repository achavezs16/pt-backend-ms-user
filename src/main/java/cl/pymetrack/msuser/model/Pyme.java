package cl.pymetrack.msuser.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pymes")
public class Pyme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_pyme", nullable = false, length = 100)
    private String nombrePyme;

    @Column(name = "rut_pyme", nullable = false, unique = true, length = 20)
    private String rutPyme;

    @Column(name = "email_contacto_pyme", nullable = false, length = 100)
    private String emailContactoPyme;

    @Column(name = "telefono_contacto_pyme", length = 20)
    private String telefonoContactoPyme;

    @Column(name = "direccion_sucursal_pyme", columnDefinition = "TEXT")
    private String direccionSucursalPyme;

    @Column(name = "comuna_sucursal_pyme", length = 50)
    private String comunaSucursalPyme;

    @Column(name = "region_sucursal_pyme", length = 50)
    private String regionSucursalPyme;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    public Pyme() {}

    public Pyme(String nombrePyme, String rutPyme, String emailContactoPyme) {
        this.nombrePyme = nombrePyme;
        this.rutPyme = rutPyme;
        this.emailContactoPyme = emailContactoPyme;
        this.activo = true;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (creadoEn == null) {
            creadoEn = now;
        }
        actualizadoEn = now;
        if (activo == null) {
            activo = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombrePyme() {
        return nombrePyme;
    }

    public void setNombrePyme(String nombrePyme) {
        this.nombrePyme = nombrePyme;
    }

    public String getRutPyme() {
        return rutPyme;
    }

    public void setRutPyme(String rutPyme) {
        this.rutPyme = rutPyme;
    }

    public String getEmailContactoPyme() {
        return emailContactoPyme;
    }

    public void setEmailContactoPyme(String emailContactoPyme) {
        this.emailContactoPyme = emailContactoPyme;
    }

    public String getTelefonoContactoPyme() {
        return telefonoContactoPyme;
    }

    public void setTelefonoContactoPyme(String telefonoContactoPyme) {
        this.telefonoContactoPyme = telefonoContactoPyme;
    }

    public String getDireccionSucursalPyme() {
        return direccionSucursalPyme;
    }

    public void setDireccionSucursalPyme(String direccionSucursalPyme) {
        this.direccionSucursalPyme = direccionSucursalPyme;
    }

    public String getComunaSucursalPyme() {
        return comunaSucursalPyme;
    }

    public void setComunaSucursalPyme(String comunaSucursalPyme) {
        this.comunaSucursalPyme = comunaSucursalPyme;
    }

    public String getRegionSucursalPyme() {
        return regionSucursalPyme;
    }

    public void setRegionSucursalPyme(String regionSucursalPyme) {
        this.regionSucursalPyme = regionSucursalPyme;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public LocalDateTime getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(LocalDateTime actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }
}
