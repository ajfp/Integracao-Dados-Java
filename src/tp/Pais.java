package tp;

public class Pais {

    public Pais(String ISO, String nome, String continente, String nome_presidente, String link) {
        this.ISO = ISO;
        this.nome = nome;
        this.continente = continente;
        this.nome_presidente = nome_presidente;
        this.link = link;
    }
   String ISO;
   String nome;
   String continente;
   String nome_presidente;
   String link;
   

    public String getISO() {
        return ISO;
    }

    public void setISO(String ISO) {
        this.ISO = ISO;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContinente() {
        return continente;
    }

    public void setContinente(String continente) {
        this.continente = continente;
    }

    public String getNome_presidente() {
        return nome_presidente;
    }

    public void setNome_presidente(String nome_presidente) {
        this.nome_presidente = nome_presidente;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
