package io.alelli.simplehome2.models;

public class Profilo {
    private Long id;
    private String etichetta;
    private String url;
    private String username;
    private String password;

    public Profilo() {
        super();
    }

    public Profilo(String etichetta, String url, String username, String password) {
        super();
        this.etichetta = etichetta;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEtichetta() {
        return etichetta;
    }

    public void setEtichetta(String etichetta) {
        this.etichetta = etichetta;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
