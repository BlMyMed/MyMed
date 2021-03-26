package com.example.mymed;

public class BookingUser {
    String data_prenotazione;
    String stato;
    String id_utente;
    String ora;

    public String getStudio_selezionato() {
        return studio_selezionato;
    }

    public void setStudio_selezionato(String studio_selezionato) {
        this.studio_selezionato = studio_selezionato;
    }

    String studio_selezionato;
    String id_prenotazione;

    public String getCodice_utente() {
        return codice_utente;
    }

    public void setCodice_utente(String codice_utente) {
        this.codice_utente = codice_utente;
    }

    String codice_utente;
    public String getId_prenotazione() {
        return id_prenotazione;
    }

    public void setId_prenotazione(String id_prenotazione) {
        this.id_prenotazione = id_prenotazione;
    }



    public BookingUser(){}
    public BookingUser(String data_prenotazione, String stato, String id_utente,String ora,String studio_selezionato) {
        this.data_prenotazione = data_prenotazione;
        this.stato = stato;
        this.id_utente = id_utente;
        this.ora = ora;
        this.studio_selezionato = studio_selezionato;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String turno_selezionato) {
        this.ora = turno_selezionato;
    }

    public String getData_prenotazione() {
        return data_prenotazione;
    }

    public void setData_prenotazione(String data_prenotazione) {
        this.data_prenotazione = data_prenotazione;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public String getId_utente() {
        return id_utente;
    }

    public void setId_utente(String id_utente) {
        this.id_utente = id_utente;
    }
}
