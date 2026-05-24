package co.edu.uniquindio.eventos.pfeventosp2.Model;

import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;

import static co.edu.uniquindio.eventos.pfeventosp2.Model.RoleUser.ADMIN;

public class PruebaLectura {
    public static void main(String[] args) {
        UserRepository repositorio = UserRepository.getInstance();
        if (repositorio.getPersonas().isEmpty()) {
            System.out.println("La lista está vacía.");
        } else {
            System.out.println("--- LISTA DE USUARIOS CARGADOS DESDE TXT ---");
            for (Persona p : repositorio.getPersonas()) {
                System.out.println(p.toString());
            }
            System.out.println("--------------------------------------------");
        }
        Admin nuevoAdmin = new Admin(ADMIN, "Carlos Admin", "admin123", "carlos@admin.com", "12345");
        repositorio.addPersona(nuevoAdmin);
        for (Persona p : repositorio.getPersonas()) {
            System.out.println(p.toString());
        }
    }
}

