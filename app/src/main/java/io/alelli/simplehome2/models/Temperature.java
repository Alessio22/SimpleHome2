package io.alelli.simplehome2.models;

/**
 * Created by Alessio on 04/12/2015.
 */
public class Temperature {
    private Long id;
    private String temperatura;
    private String setPoint;
    private String txtTemp;

    public Temperature() {}

    public Temperature(String temperatura, String setPoint, String txtTemp) {
        this.temperatura = temperatura;
        this.setPoint = setPoint;
        this.txtTemp = txtTemp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(String temperatura) {
        this.temperatura = temperatura;
    }

    public String getSetPoint() {
        return setPoint;
    }

    public void setSetPoint(String setPoint) {
        this.setPoint = setPoint;
    }

    public String getTxtTemp() {
        return txtTemp;
    }

    public void setTxtTemp(String txtTemp) {
        this.txtTemp = txtTemp;
    }
}
