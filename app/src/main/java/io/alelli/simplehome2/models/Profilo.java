package io.alelli.simplehome2.models;

public class Profilo {
    private Long id;
    private String etichetta;
    private String url;
    private String username;
    private String password;

    private String urlCam;
    private String usernameCam;
    private String passwordCam;

    public Profilo() {
        super();
    }

    public Profilo(String etichetta, String url, String username, String password,
                   String urlCam, String usernameCam, String passwordCam) {
        super();
        this.etichetta = etichetta;
        this.url = url;
        this.username = username;
        this.password = password;
        this.urlCam = urlCam;
        this.usernameCam = usernameCam;
        this.passwordCam = passwordCam;
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

    public String getUrlCam() {
        return urlCam;
    }

    public void setUrlCam(String urlCam) {
        this.urlCam = urlCam;
    }

    public String getUsernameCam() {
        return usernameCam;
    }

    public void setUsernameCam(String usernameCam) {
        this.usernameCam = usernameCam;
    }

    public String getPasswordCam() {
        return passwordCam;
    }

    public void setPasswordCam(String passwordCam) {
        this.passwordCam = passwordCam;
    }

}
