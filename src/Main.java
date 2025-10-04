import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String FILE_DOCTORES = "doctores.csv";
    private static final String FILE_PACIENTES = "pacientes.csv";
    private static final String FILE_CITAS = "citas.csv";

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private static List<Doctor> doctores = new ArrayList<>();
    private static List<Paciente> pacientes = new ArrayList<>();
    private static List<Cita> citas = new ArrayList<>();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Usuario: ");
        String user = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        if (!user.equals(ADMIN_USER) || !pass.equals(ADMIN_PASS)) {
            System.out.println("Acceso denegado.");
            sc.close();
            return;
        }

        try {
            ensureFilesExist(); // crea los CSV si no existen
        } catch (IOException e) {
            System.out.println("No se pudieron crear/asegurar archivos: " + e.getMessage());
            sc.close();
            return;
        }

        cargarDatos();
        menuPrincipal(sc);
        guardarDatos(); // última salvada por si acaso
        sc.close();
    }

    private static void ensureFilesExist() throws IOException {
        Path working = Paths.get(System.getProperty("user.dir"));
        System.out.println("Directorio de trabajo: " + working.toAbsolutePath());
        for (String nombre : Arrays.asList(FILE_DOCTORES, FILE_PACIENTES, FILE_CITAS)) {
            Path p = Paths.get(nombre);
            if (!Files.exists(p)) {
                Files.createFile(p);
                System.out.println("Archivo creado: " + p.toAbsolutePath());
            }
        }
    }

    private static void menuPrincipal(Scanner sc) {
        int opc;
        do {
            System.out.println("\n=== SISTEMA DE CITAS MÉDICAS ===");
            System.out.println("1. Alta de Doctor");
            System.out.println("2. Alta de Paciente");
            System.out.println("3. Crear Cita");
            System.out.println("4. Mostrar Citas");
            System.out.println("0. Salir");
            System.out.print("Opción: ");

            while (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.print("Introduce un número válido: ");
            }
            opc = sc.nextInt();
            sc.nextLine();

            switch (opc) {
                case 1 -> altaDoctor(sc);
                case 2 -> altaPaciente(sc);
                case 3 -> crearCita(sc);
                case 4 -> mostrarCitas();
                case 0 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opc != 0);
    }

    private static void altaDoctor(Scanner sc) {
        System.out.print("ID del doctor: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) { System.out.println("ID no puede estar vacío."); return; }
        if (doctores.stream().anyMatch(d -> d.getId().equals(id))) {
            System.out.println("Ya existe un doctor con ese ID.");
            return;
        }
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        System.out.print("Especialidad: ");
        String especialidad = sc.nextLine();
        doctores.add(new Doctor(id, nombre, especialidad));
        guardarDatos(); // guardamos inmediatamente
        System.out.println("Doctor agregado correctamente.");
    }

    private static void altaPaciente(Scanner sc) {
        System.out.print("ID del paciente: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) { System.out.println("ID no puede estar vacío."); return; }
        if (pacientes.stream().anyMatch(p -> p.getId().equals(id))) {
            System.out.println("Ya existe un paciente con ese ID.");
            return;
        }
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        pacientes.add(new Paciente(id, nombre));
        guardarDatos(); // guardamos inmediatamente
        System.out.println("Paciente agregado correctamente.");
    }

    private static void crearCita(Scanner sc) {
        if (doctores.isEmpty() || pacientes.isEmpty()) {
            System.out.println("Debe registrar al menos un doctor y un paciente antes de crear citas.");
            return;
        }

        System.out.print("ID de la cita: ");
        String id = sc.nextLine().trim();
        if (id.isEmpty()) { System.out.println("ID no puede estar vacío."); return; }
        if (citas.stream().anyMatch(c -> c.toString().startsWith(id + ","))) {
            System.out.println("Ya existe una cita con ese ID.");
            return;
        }

        System.out.print("Fecha y hora (dd/mm/yyyy hh:mm): ");
        String fechaHora = sc.nextLine();
        System.out.print("Motivo: ");
        String motivo = sc.nextLine();

        System.out.println("Seleccione un doctor (ID disponibles):");
        for (Doctor d : doctores) System.out.println(" - " + d.getId() + " (" + d.getNombre() + ")");
        System.out.print("ID Doctor: ");
        String idDoctor = sc.nextLine().trim();
        if (doctores.stream().noneMatch(d -> d.getId().equals(idDoctor))) {
            System.out.println("Doctor no encontrado. Cancela la creación de la cita.");
            return;
        }

        System.out.println("Seleccione un paciente (ID disponibles):");
        for (Paciente p : pacientes) System.out.println(" - " + p.getId() + " (" + p.getNombre() + ")");
        System.out.print("ID Paciente: ");
        String idPaciente = sc.nextLine().trim();
        if (pacientes.stream().noneMatch(p -> p.getId().equals(idPaciente))) {
            System.out.println("Paciente no encontrado. Cancela la creación de la cita.");
            return;
        }

        citas.add(new Cita(id, fechaHora, motivo, idDoctor, idPaciente));
        guardarDatos(); // guardamos inmediatamente
        System.out.println("Cita creada exitosamente.");
    }

    private static void mostrarCitas() {
        if (citas.isEmpty()) {
            System.out.println("No hay citas registradas.");
            return;
        }
        System.out.println("\n=== LISTADO DE CITAS ===");
        for (Cita c : citas) System.out.println(c.toString());
    }

    private static void guardarDatos() {
        try {
            Files.write(Paths.get(FILE_DOCTORES),
                    doctores.stream().map(Doctor::toString).collect(Collectors.toList()),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Paths.get(FILE_PACIENTES),
                    pacientes.stream().map(Paciente::toString).collect(Collectors.toList()),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Paths.get(FILE_CITAS),
                    citas.stream().map(Cita::toString).collect(Collectors.toList()),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error guardando datos: " + e.getMessage());
        }
    }

    private static void cargarDatos() {
        try {
            List<String> linesDoc = Files.readAllLines(Paths.get(FILE_DOCTORES), StandardCharsets.UTF_8);
            doctores = linesDoc.stream()
                    .filter(s -> !s.isBlank())
                    .map(Doctor::fromCSV)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<String> linesPac = Files.readAllLines(Paths.get(FILE_PACIENTES), StandardCharsets.UTF_8);
            pacientes = linesPac.stream()
                    .filter(s -> !s.isBlank())
                    .map(Paciente::fromCSV)
                    .collect(Collectors.toCollection(ArrayList::new));

            List<String> linesCit = Files.readAllLines(Paths.get(FILE_CITAS), StandardCharsets.UTF_8);
            citas = linesCit.stream()
                    .filter(s -> !s.isBlank())
                    .map(Cita::fromCSV)
                    .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            System.out.println("Error leyendo archivos: " + e.getMessage());
        }
    }
}
