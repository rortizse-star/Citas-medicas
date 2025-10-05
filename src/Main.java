import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String DB_FOLDER = "db";
    private static final String FILE_DOCTORES = DB_FOLDER + "/doctores.csv";
    private static final String FILE_PACIENTES = DB_FOLDER + "/pacientes.csv";
    private static final String FILE_CITAS = DB_FOLDER + "/citas.csv";

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "1234";

    private static List<Doctor> doctores = new ArrayList<>();
    private static List<Paciente> pacientes = new ArrayList<>();
    private static List<Cita> citas = new ArrayList<>();

    public static void main(String[] args) {
        crearCarpetaDB(); // 🔹 Crea carpeta si no existe

        Scanner sc = new Scanner(System.in);
        System.out.print("Usuario: ");
        String user = sc.nextLine();
        System.out.print("Contraseña: ");
        String pass = sc.nextLine();

        if (!user.equals(ADMIN_USER) || !pass.equals(ADMIN_PASS)) {
            System.out.println("Acceso denegado.");
            return;
        }

        cargarDatos();
        menuPrincipal(sc);
        guardarDatos();
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
        String id = sc.nextLine();
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        System.out.print("Especialidad: ");
        String especialidad = sc.nextLine();
        doctores.add(new Doctor(id, nombre, especialidad));
        System.out.println("✅ Doctor agregado correctamente.");
    }

    private static void altaPaciente(Scanner sc) {
        System.out.print("ID del paciente: ");
        String id = sc.nextLine();
        System.out.print("Nombre completo: ");
        String nombre = sc.nextLine();
        pacientes.add(new Paciente(id, nombre));
        System.out.println("✅ Paciente agregado correctamente.");
    }

    private static void crearCita(Scanner sc) {
        if (doctores.isEmpty() || pacientes.isEmpty()) {
            System.out.println("⚠️ Debe registrar al menos un doctor y un paciente antes de crear citas.");
            return;
        }

        System.out.print("ID de la cita: ");
        String id = sc.nextLine();
        System.out.print("Fecha y hora (dd/mm/yyyy hh:mm): ");
        String fechaHora = sc.nextLine();
        System.out.print("Motivo: ");
        String motivo = sc.nextLine();

        System.out.println("Seleccione un doctor (ID disponibles):");
        for (Doctor d : doctores) System.out.println(" - " + d.getId() + " (" + d.getNombre() + ")");
        System.out.print("ID Doctor: ");
        String idDoctor = sc.nextLine();

        System.out.println("Seleccione un paciente (ID disponibles):");
        for (Paciente p : pacientes) System.out.println(" - " + p.getId() + " (" + p.getNombre() + ")");
        System.out.print("ID Paciente: ");
        String idPaciente = sc.nextLine();

        citas.add(new Cita(id, fechaHora, motivo, idDoctor, idPaciente));
        System.out.println("✅ Cita creada exitosamente.");
    }

    private static void mostrarCitas() {
        if (citas.isEmpty()) {
            System.out.println("⚠️ No hay citas registradas.");
            return;
        }
        System.out.println("\n=== LISTADO DE CITAS ===");
        for (Cita c : citas) {
            System.out.println(c);
        }
    }

    // ------------------------------
    // 🔹 Métodos para guardar y leer
    // ------------------------------
    private static void guardarDatos() {
        guardarArchivo(FILE_DOCTORES, doctores.stream().map(Doctor::toString).collect(Collectors.toList()));
        guardarArchivo(FILE_PACIENTES, pacientes.stream().map(Paciente::toString).collect(Collectors.toList()));
        guardarArchivo(FILE_CITAS, citas.stream().map(Cita::toString).collect(Collectors.toList()));
    }

    private static void cargarDatos() {
        doctores = new ArrayList<>(leerArchivo(FILE_DOCTORES).stream().map(Doctor::fromCSV).collect(Collectors.toList()));
        pacientes = new ArrayList<>(leerArchivo(FILE_PACIENTES).stream().map(Paciente::fromCSV).collect(Collectors.toList()));
        citas = new ArrayList<>(leerArchivo(FILE_CITAS).stream().map(Cita::fromCSV).collect(Collectors.toList()));
    }

    private static void guardarArchivo(String nombre, List<String> lineas) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombre))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
            System.out.println("💾 Archivo guardado: " + nombre);
        } catch (IOException e) {
            System.out.println("❌ Error al guardar " + nombre + ": " + e.getMessage());
        }
    }

    private static List<String> leerArchivo(String nombre) {
        List<String> lineas = new ArrayList<>();
        File file = new File(nombre);
        if (!file.exists()) return lineas;

        try (BufferedReader br = new BufferedReader(new FileReader(nombre))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("❌ Error al leer " + nombre + ": " + e.getMessage());
        }
        return lineas;
    }

    // ------------------------------
    // 🔹 Crea carpeta db si no existe
    // ------------------------------
    private static void crearCarpetaDB() {
        File folder = new File(DB_FOLDER);
        if (!folder.exists()) {
            boolean creada = folder.mkdirs();
            if (creada) {
                System.out.println("📁 Carpeta 'db' creada correctamente.");
            } else {
                System.out.println("⚠️ No se pudo crear la carpeta 'db'.");
            }
        }
    }
}
