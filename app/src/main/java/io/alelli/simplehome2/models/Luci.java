package io.alelli.simplehome2.models;

public class Luci {
    private Integer id;
    private String nome;
    private Boolean stato;

    public Luci(Integer id, String nome, Boolean stato) {
        this.stato = stato;
        this.id = id;
        this.nome = nome;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getStato() {
        return stato;
    }

    public void setStato(Boolean stato) {
        this.stato = stato;
    }
}
