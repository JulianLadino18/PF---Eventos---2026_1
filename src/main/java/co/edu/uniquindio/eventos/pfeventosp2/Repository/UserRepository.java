package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Admin;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Usuario;

import java.io.*;
import java.util.ArrayList;

public class UserRepository {
    //atributos
    private String rutaUsers = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/users.txt";
    private static UserRepository instancia;
    private static ArrayList<Persona> personas;

    //método para que cada que se inicialice el repositorio se lea el archivo txt
    private UserRepository() {
        personas = new ArrayList<>();
        readArchive();
    }

    //patron singleton
    public static UserRepository getInstance() {
        if (instancia == null) {
            instancia = new UserRepository();
        }
        return instancia;
    }

    public ArrayList<Persona> getPersonas() {
        return personas;
    }

    private void readArchive() {
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaUsers))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] bloques = linea.split(";");
                String tipo = bloques[0];
                String id = bloques[1];
                String nombre = bloques[2];
                String correo = bloques[3];
                String password = bloques[4];

                if (tipo.equals("ADMIN")) {
                    personas.add(new Admin(id, nombre, correo, password));
                } else if (tipo.equals("CLIENTE")) {
                    String telefono = bloques[5];
                    Usuario u = new Usuario(id, nombre, correo, password, telefono);
                    if (bloques.length > 6 && !bloques[6].isEmpty()) {
                        String[] pagos = bloques[6].split(",");
                        for (String pago : pagos) {
                            u.getMetodosPago().add(pago);
                        }
                    }
                    personas.add(u);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado. Se creará uno nuevo al guardar.");
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public void updateArchive() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaUsers))) {
            for (Persona p : personas) {
                StringBuilder sb = new StringBuilder();
                if (p instanceof Admin) {
                    sb.append("ADMIN;").append(p.getId()).append(";")
                            .append(p.getNombre()).append(";").append(p.getCorreo()).append(";")
                            .append(p.getPassword());
                } else if (p instanceof Usuario) {
                    Usuario u = (Usuario) p;
                    sb.append("CLIENTE;").append(u.getId()).append(";")
                            .append(u.getNombre()).append(";").append(u.getCorreo()).append(";")
                            .append(u.getPassword()).append(";").append(u.getTelefono()).append(";");
                    if (!u.getMetodosPago().isEmpty()) {
                        String pagosString = String.join(",", u.getMetodosPago());
                        sb.append(pagosString);
                    }
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error al guardar en el archivo: " + e.getMessage());
        }
    }

    public void addPersona(Persona p) {
        personas.add(p);
        updateArchive();
    }

    public Persona login(String correo, String password) {
        for (Persona p : personas) {
            if (p.getCorreo().equals(correo) && p.getPassword().equals(password)) {
                return p;
            }
        }
        return null;
    }
}
