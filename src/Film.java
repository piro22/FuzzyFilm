public class Film {
    String titolo;
    String categoria;
    int durata;
    String finale;
    String complessita;
    String realismo;
    String contenutiEspliciti;
    String tono;
    Double score;


    public Film(String titolo, String categoria, int indice, String finale, String complessita, String realismo, String contenutiEspliciti, String tono) {
        this.titolo = titolo;
        this.categoria = categoria;
        this.durata = indice;
        this.finale = finale;
        this.complessita = complessita;
        this.realismo = realismo;
        this.contenutiEspliciti = contenutiEspliciti;
        this.tono = tono;
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

    public String getComplessita() {
        return complessita;
    }

    public String getRealismo() {
        return realismo;
    }

    public String getContenutiEspliciti() {
        return contenutiEspliciti;
    }

    public String getTono() {
        return tono;
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