package cl.pymetrack.msuser.model;

public enum Role {
    ADMIN("Administrador del sistema"),
    PYME("Propietario de PYME"),
    REPARTIDOR("Repartidor de pedidos");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
