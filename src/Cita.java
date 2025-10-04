public class Cita {
    private String id;
    private String fechaHora;
    private String motivo;
    private String idDoctor;
    private String idPaciente;

    public Cita(String id, String fechaHora, String motivo, String idDoctor, String idPaciente) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.idDoctor = idDoctor;
        this.idPaciente = idPaciente;
    }

    @Override
    public String toString() {
        return id + "," + fechaHora + "," + motivo + "," + idDoctor + "," + idPaciente;
    }

    public static Cita fromCSV(String line) {
        String[] data = line.split(",");
        return new Cita(data[0], data[1], data[2], data[3], data[4]);
    }
}
