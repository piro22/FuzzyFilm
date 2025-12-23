import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import javax.swing.JOptionPane;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // CARICAMENTO FILM DA CSV
        List<Film> databaseFilm = new ArrayList<>();
        String csvFile = "res/movies.csv"; // Assicurati che sia nella root del progetto

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {

                if(line.startsWith("Titolo")) continue;

                String[] values = line.split(";");

                databaseFilm.add(new Film(values[0], values[1], Integer.parseInt(values[2]), values[3], values[4], values[5], values[6], values[7]));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore lettura CSV: " + e.getMessage());
            return;
        }

        // LOGICA FUZZY PER INTENSITA
        String fileName = "src/intensita.fcl";
        FIS fisIntensita = FIS.load(fileName, true);
        if (fisIntensita == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        // Input Utente intensita
        String intensita = JOptionPane.showInputDialog("Che intensita vuoi che abbia? (0-10)");


        fisIntensita.setVariable("intensita", Double.parseDouble(intensita));

        String topIntensita = "";
        Double top = 0.0;
        List<Double> scoreIntensita = new ArrayList<>();
        for (int i = 0; i<databaseFilm.size(); i++) {
            String categoria = databaseFilm.get(i).getCategoria();

            fisIntensita.setVariable("categoria_film", numCategoria(categoria));
            fisIntensita.evaluate();
            double idealIndex = fisIntensita.getVariable("affinita").getValue();
            scoreIntensita.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top) {top = idealIndex; topIntensita = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;}
        }
        System.out.print("Miglior film in base all'intensità: ");
        System.out.println(topIntensita + "\n");


        // LOGICA FUZZY PER TEMPO---------------------------------------------------------------------------------------
        fileName = "src/tempo.fcl";
        FIS fisTempo = FIS.load(fileName, true);
        if (fisTempo == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        // Input Utente tempo
        String tempo = JOptionPane.showInputDialog("Quanto tempo hai a disposizione? (0-240 minuti)");

        fisTempo.setVariable("tempo_a_disposizione", Double.parseDouble(tempo));

        String topTempo = "";
        Double top1 = 0.0;
        List<Double> scoreTempo = new ArrayList<>();
        for (int i = 0; i<databaseFilm.size(); i++) {
            int durata = databaseFilm.get(i).getDurata();

            fisTempo.setVariable("durata_film", durata);
            fisTempo.evaluate();
            double idealIndex = fisTempo.getVariable("affinita").getValue();
            scoreTempo.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top1) {top1 = idealIndex; topTempo = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;}
        }
        System.out.print("Miglior film in base alla durata: ");
        System.out.println(topTempo + "\n");



        // LOGICA FUZZY PER FINALE---------------------------------------------------------------------------------------
        fileName = "src/finale.fcl";
        FIS fisFinale = FIS.load(fileName, true);
        if (fisFinale == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        // Input Utente tempo
        String intensitaPostFilm = JOptionPane.showInputDialog("Quanto emozionante vuoi il finale? (0-10)");
        fisFinale.setVariable("intensita_emozioni_post_film", Double.parseDouble(intensitaPostFilm));

        String topFinale = "";
        Double top2 = 0.0;
        List<Double> scoreFinale = new ArrayList<>();
        for (int i = 0; i<databaseFilm.size(); i++) {
            String finale = databaseFilm.get(i).getFinale();

            fisFinale.setVariable("finale_film", numFinale(finale));
            fisFinale.evaluate();
            double idealIndex = fisFinale.getVariable("affinita").getValue();
            scoreFinale.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top2) {top2 = idealIndex; topFinale = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;}
        }
        System.out.print("Miglior film in base al finale: ");
        System.out.println(topFinale + "\n");




        // LOGICA FUZZY PER COMPLESSITA---------------------------------------------------------------------------------------
        fileName = "src/complessitaNarrativa.fcl";
        FIS fisComplessita = FIS.load(fileName, true);
        if (fisComplessita == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        // Input Utente complessita
        String complessita = JOptionPane.showInputDialog("Quanto complessa vuoi la trama? (0-10)");

        fisComplessita.setVariable("complessita", Double.parseDouble(complessita));

        String topComplessita = "";
        Double top3 = 0.0;
        List<Double> scoreComplessita = new ArrayList<>();
        for (int i = 0; i<databaseFilm.size(); i++) {
            String cmplxty = databaseFilm.get(i).getComplessita();

            fisComplessita.setVariable("complessita_film", numComplessita(cmplxty));
            fisComplessita.evaluate();
            double idealIndex = fisComplessita.getVariable("affinita").getValue();
            scoreComplessita.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top3) {top3 = idealIndex; topComplessita = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;}
        }
        System.out.print("Miglior film in base alla complessita: ");
        System.out.println(topComplessita + "\n");


        // LOGICA FUZZY PER REALISMO---------------------------------------------------------------------------------------
        fileName = "src/realismo.fcl";
        FIS fisRealismo = FIS.load(fileName, true);
        if (fisRealismo == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        // Input Utente tempo
        String realismo = JOptionPane.showInputDialog("Quanto vuoi fantasioso il film? (0-10)");

        fisRealismo.setVariable("fantasia", Double.parseDouble(realismo));

        String topRealismo = "";
        Double top4 = 0.0;
        List<Double> scoreRealismo = new ArrayList<>();
        for (int i = 0; i<databaseFilm.size(); i++) {
            String real = databaseFilm.get(i).getRealismo();

            fisRealismo.setVariable("fantasia_film", numRealismo(real));
            fisRealismo.evaluate();
            double idealIndex = fisRealismo.getVariable("affinita").getValue();
            scoreRealismo.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top4) {top4 = idealIndex; topRealismo = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;}
        }
        System.out.print("Miglior film in base alla fantasia: ");
        System.out.println(topRealismo + "\n");


        // CALCOLO FILM MIGLIORE
        int indexBest = 0;
        Double bestScore = 0.0;
        for (int i = 0; i<databaseFilm.size(); i++) {
            Double intensitaTemp = scoreIntensita.get(i);
            Double tempoTemp = scoreTempo.get(i);
            Double finaleTemp = scoreFinale.get(i);
            Double complessitaTemp = scoreComplessita.get(i);
            Double realismoTemp = scoreRealismo.get(i);

            if(intensitaTemp < 0.2 || tempoTemp < 0.2 || finaleTemp < 0.2 || complessitaTemp < 0.2 || realismoTemp < 0.2) {
                databaseFilm.get(i).setScore(0.0);
            } else {
                databaseFilm.get(i).setScore((intensitaTemp + tempoTemp + finaleTemp + complessitaTemp + realismoTemp) / 5);
                if (databaseFilm.get(i).getScore() > bestScore){ indexBest = i; bestScore = databaseFilm.get(i).getScore();}
            }

            System.out.println(databaseFilm.get(i).toString());
        }
        System.out.print("\n\n Miglior film in base a tutti i parametri: " + databaseFilm.get(indexBest).toString());

    }


    private static double numCategoria(String categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        switch (categoria.toLowerCase()) {
            case "drammatico":
                return 0.5;
            case "animazione":
                return 1.5;
            case "romance":
                return 2.5;
            case "commedia":
                return 3.5;
            case "thriller":
                return 4.5;
            case "horror":
                return 5.5;
            case "azione":
                return 6.5;
            case "avventura":
                return 7.5;
            case "scifi":
                return 8.5;
            case "fantasy":
                return 9.5;
            default:
                return 0.0; // valore neutro se sconosciuta
        }
    }

    private static double numFinale(String categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        switch (categoria.toLowerCase()) {
            case "lieto":
                return 0.5;
            case "triste":
                return 1.5;
            case "malinconico":
                return 2.5;
            case "teso":
                return 3.5;
            default:
                return 0.0; // valore neutro se sconosciuta
        }
    }

    private static double numComplessita(String complessita) {
        if (complessita == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        switch (complessita.toLowerCase()) {
            case "semplice":
                return 0.5;
            case "lineare":
                return 1.5;
            case "intricata":
                return 2.5;
            case "cerebrale":
                return 3.5;
            default:
                return 0.0; // valore neutro se sconosciuta
        }
    }

    private static double numRealismo(String realismo) {
        if (realismo == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        switch (realismo.toLowerCase()) {
            case "realistico":
                return 0.5;
            case "stilizzato":
                return 1.5;
            case "surreale":
                return 2.5;
            case "fantasy":
                return 3.5;
            default:
                return 0.0; // valore neutro se sconosciuta
        }
    }
}