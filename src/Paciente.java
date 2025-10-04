import java.io.Serializable;

public class Paciente implements Serializable {
    private String id;
    private String nombre;

    public Paciente(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return id + "," + nombre;
    }

    public static Paciente fromCSV(String line) {
        String[] data = line.split(",");
        return new Paciente(data[0], data[1]);
    }
}

