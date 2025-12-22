public class Film {
    String titolo;
    String categoria;
    int durata;
    String finale;
    Double score;


    public Film(String titolo, String categoria, int indice, String finale) {
        this.titolo = titolo;
        this.categoria = categoria;
        this.durata = indice;
        this.finale = finale;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getCategoria() {
        return categoria;
    }

    public int getDurata() {
        return durata;
    }

    public String getFinale() {
        return finale;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Film{" +
                "titolo='" + titolo + '\'' +
                ", categoria='" + categoria + '\'' +
                ", durata=" + durata +
                ", finale='" + finale + '\'' +
                ", score=" + score +
                '}';
    }
}