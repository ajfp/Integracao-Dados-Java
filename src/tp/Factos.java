package tp;

import java.util.ArrayList;

public class Factos {

    String ISO;
    String capital;
    String cod_telefone;
    String cod_internet;
    String cidade_max;
    String hino;
    String moeda;
    String populacao;
    String area;
    ArrayList<String> idiomas;

    public Factos(String ISO, String cod_telefone, String cod_internet, String capital, String cidade_max, String hino, String moeda, String populacao, String area, ArrayList<String> idiomas) {
        this.ISO = ISO;
        this.cod_telefone = cod_telefone;
        this.cod_internet = cod_internet;
        this.capital = capital;
        this.cidade_max = cidade_max;
        this.hino = hino;
        this.moeda = moeda;
        this.populacao = populacao;
        this.area = area;
        this.idiomas = idiomas;
    }

    public String getISO() {
        return ISO;
    }

    public void setISO(String ISO) {
        this.ISO = ISO;
    }

    public String getCod_telefone() {
        return cod_telefone;
    }

    public void setCod_telefone(String cod_telefone) {
        this.cod_telefone = cod_telefone;
    }

    public String getCod_internet() {
        return cod_internet;
    }

    public void setCod_internet(String cod_internet) {
        this.cod_internet = cod_internet;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getCidade_max() {
        return cidade_max;
    }

    public void setCidade_max(String cidade_max) {
        this.cidade_max = cidade_max;
    }

    public String getHino() {
        return hino;
    }

    public void setHino(String hino) {
        this.hino = hino;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public String getPopulacao() {
        return populacao;
    }

    public void setPopulacao(String populacao) {
        this.populacao = populacao;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public ArrayList<String> getIdiomas() {
        return idiomas;
    }

    public void setIdiomas(ArrayList<String> idiomas) {
        this.idiomas = idiomas;
    }
}
