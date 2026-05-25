package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;

public class AdminDashboardController {
    private Persona loggedUser;

    public void setUser(Persona user) {
        this.loggedUser = user;
    }
}
