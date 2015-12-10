package io.alelli.simplehome2.models;

public class Allarme {
    private Integer id;
    private boolean stato;
    private boolean statoP1;
    private boolean statoP2;

    public Allarme() {}

    public Allarme(Integer id, boolean stato, boolean statoP1, boolean statoP2) {
        this.id = id;
        this.stato = stato;
        this.statoP1 = statoP1;
        this.statoP2 = statoP2;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean getStato() {
        return stato;
    }

    public void setStato(boolean stato) {
        this.stato = stato;
    }

    public boolean getStatoP1() {
        return statoP1;
    }

    public void setStatoP1(boolean statoP1) {
        this.statoP1 = statoP1;
    }

    public boolean getStatoP2() {
        return statoP2;
    }

    public void setStatoP2(boolean statoP2) {
        this.statoP2 = statoP2;
    }
}
