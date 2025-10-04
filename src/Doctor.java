import java.io.Serializable;

public class Doctor implements Serializable {
    private String id;
    private String nombre;
    private String especialidad;

    public Doctor(String id, String nombre, String especialidad) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }

    @Override
    public String toString() {
        return id + "," + nombre + "," + especialidad;
    }

    public static Doctor fromCSV(String line) {
        String[] data = line.split(",");
        return new Doctor(data[0], data[1], data[2]);
    }
}
