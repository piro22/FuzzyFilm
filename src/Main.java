import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.*;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.Frame;
import java.awt.Dialog;

public class Main {

    private static final double SOGLIA = 0.2;

    public static void main(String[] args) {

        boolean continuaRicerca = true;

        do {

        // CARICAMENTO FILM DA CSV
        List<Film> databaseFilm = new ArrayList<>();
        //String csvFile = "FuzzyFilm-master/res/movies.csv";
        String csvFile = "res/movies.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {

                if (line.startsWith("Titolo")) continue;

                String[] values = line.trim().split(";");

                databaseFilm.add(new Film(values[0], values[1], Integer.parseInt(values[2]), values[3], values[4], values[5], values[6], values[7]));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore lettura CSV: " + e.getMessage());
            return;
        }

        //Se volgio visualizzare i grafici
        int rispostaGrafici = JOptionPane.showConfirmDialog(
                null,
                "Vuoi visualizzare i grafici della logica Fuzzy?",
                "Debug Grafici",
                JOptionPane.YES_NO_OPTION
        );


        // LOGICA FUZZY PER TONE---------------------------------------------------------------------
        //String fileName = "FuzzyFilm-master/src/tone.fcl";
        String fileName = "src/tone.fcl";
        FIS fisTone = FIS.load(fileName, true);
        if (fisTone == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisTone, "Tono");

        }


        String[] options = {"dark", "serio", "bilanciato", "leggero", "comico", "epico", "inquietante"};
        String TONO_USER = (String) JOptionPane.showInputDialog(
                null,
                "Scegli il tono del film:",
                "Input Tono",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();


        //trasforma input stringa in int
        double tonoNum = numTono(TONO_USER); //c'è gia il metodo in fondo al codice

        //prende input dell'utente e la passa al fuzzy
        fisTone.setVariable("tone", tonoNum);

        String topTone = "";
        Double topT = 0.0;
        //salvo gli score di ogni film
        List<Double> scoreTone = new ArrayList<>();

        for (int i = 0; i < databaseFilm.size(); i++) {
            //recupero il tono di ciascun film
            String tonoFilm = databaseFilm.get(i).getTono();
            //chiamo la funzione che trasforma da stringa a int e poi la passo all'input fuzzy
            fisTone.setVariable("tono_film", numTono(tonoFilm));
            //attivo il fuzzy
            fisTone.evaluate();
            //recupero output e lo salvo
            double idealIndex = fisTone.getVariable("affinita_tono").getValue();
            scoreTone.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score tono: " + idealIndex);
            //per capire il migliore
            if (idealIndex > topT) {
                topT = idealIndex;
                topTone = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base al tono: ");
        System.out.println(topTone + "\n");


        // LOGICA FUZZY PER INTENSITA----------------------------------------------------------------
        //fileName = "FuzzyFilm-master/src/intensita.fcl";
        fileName = "src/intensita.fcl";
        FIS fisIntensita = FIS.load(fileName, true);
        if (fisIntensita == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisIntensita, "Intensita");
        }

        // Input Utente intensita
        String INTENSITA_USER = JOptionPane.showInputDialog("Che intensita vuoi che abbia? (0-10)");

        fisIntensita.setVariable("intensita", Double.parseDouble(INTENSITA_USER));

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();

        String topIntensita = "";
        Double top = 0.0;
        List<Double> scoreIntensita = new ArrayList<>();
        for (int i = 0; i < databaseFilm.size(); i++) {
            String categoria = databaseFilm.get(i).getCategoria();

            fisIntensita.setVariable("categoria_film", numCategoria(categoria));
            fisIntensita.evaluate();
            double idealIndex = fisIntensita.getVariable("affinita_intensita").getValue();
            scoreIntensita.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top) {
                top = idealIndex;
                topIntensita = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base all'intensità: ");
        System.out.println(topIntensita + "\n");


        //LOGICA FUZZY VIOLENZA --------------------------------------------------------------------
        //fileName = "FuzzyFilm-master/src/violenza.fcl";
        fileName = "src/violenza.fcl";
        FIS fisViolenza = FIS.load(fileName, true);
        if (fisViolenza == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisViolenza, "Violenza");
        }

        String[] optionsViolenza = {"perTutti", "lieve", "moderato", "forte", "estremo"};

        String VIOLENZA_USER = (String) JOptionPane.showInputDialog(
                null,
                "Che livello di violenza accetti?",
                "Input Violenza",
                JOptionPane.QUESTION_MESSAGE,
                null,
                optionsViolenza,
                optionsViolenza[0]
        );

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();

        //trasforma input stringa in int
        double violenzaNum = numViolenza(VIOLENZA_USER); //c'è il metodo in fondo al codice


        //prende input dell'utente e la passa al fuzzy
        fisViolenza.setVariable("violenza", violenzaNum);

        String topViolenza = "";
        Double topV = 0.0;
        //salvo gli score di ogni film
        List<Double> scoreViolenza = new ArrayList<>();

        for (int i = 0; i < databaseFilm.size(); i++) {
            String violenzaFilm = databaseFilm.get(i).getContenutiEspliciti();
            //trasformo la stringa che leggo dal db in int e poi collego all'input fuzzy
            fisViolenza.setVariable("violenza_film", numViolenza(violenzaFilm));
            //attivo il fuzzy
            fisViolenza.evaluate();
            //ottengo il risultato
            double idealIndex = fisViolenza.getVariable("affinita_violenza").getValue();
            scoreViolenza.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score violenza: " + idealIndex);
            //serve per capire qual è il rate migliore
            if (idealIndex > topV) {
                topV = idealIndex;
                topViolenza = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base alla violenza: ");
        System.out.println(topViolenza + "\n");


        // LOGICA FUZZY PER TEMPO---------------------------------------------------------------------------------------
        //fileName = "FuzzyFilm-master/src/tempo.fcl";
        fileName = "src/tempo.fcl";
        FIS fisTempo = FIS.load(fileName, true);
        if (fisTempo == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisTempo, "Tempo");
        }

        // Input Utente tempo
        String TEMPO_USER = JOptionPane.showInputDialog("Quanto tempo hai a disposizione? (0-240 minuti)");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();

        fisTempo.setVariable("tempo_a_disposizione", Double.parseDouble(TEMPO_USER));

        String topTempo = "";
        Double top1 = 0.0;
        List<Double> scoreTempo = new ArrayList<>();
        for (int i = 0; i < databaseFilm.size(); i++) {
            int durata = databaseFilm.get(i).getDurata();

            fisTempo.setVariable("durata_film", durata);
            fisTempo.evaluate();
            double idealIndex = fisTempo.getVariable("affinita_tempo").getValue();
            scoreTempo.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top1) {
                top1 = idealIndex;
                topTempo = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base alla durata: ");
        System.out.println(topTempo + "\n");


        // LOGICA FUZZY PER FINALE---------------------------------------------------------------------------------------
        //fileName = "FuzzyFilm-master/src/finale.fcl";
        fileName = "src/finale.fcl";
        FIS fisFinale = FIS.load(fileName, true);
        if (fisFinale == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisFinale, "Finale");
        }

        // Input Utente finale
        String FINALE_USER = JOptionPane.showInputDialog("Quanto emozionante vuoi il finale? (0-10)");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();
        fisFinale.setVariable("intensita_emozioni_post_film", Double.parseDouble(FINALE_USER));

        String topFinale = "";
        Double top2 = 0.0;
        List<Double> scoreFinale = new ArrayList<>();
        for (int i = 0; i < databaseFilm.size(); i++) {
            String finale = databaseFilm.get(i).getFinale();

            fisFinale.setVariable("finale_film", numFinale(finale));
            fisFinale.evaluate();
            double idealIndex = fisFinale.getVariable("affinita_finale").getValue();
            scoreFinale.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top2) {
                top2 = idealIndex;
                topFinale = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base al finale: ");
        System.out.println(topFinale + "\n");


        // LOGICA FUZZY PER COMPLESSITA---------------------------------------------------------------------------------------
        //fileName = "FuzzyFilm-master/src/complessitaNarrativa.fcl";
        fileName = "src/complessitaNarrativa.fcl";
        FIS fisComplessita = FIS.load(fileName, true);
        if (fisComplessita == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisComplessita, "Complessita");
        }

        // Input Utente complessita
        String COMPLESSITA_USER = JOptionPane.showInputDialog("Quanto complessa vuoi la trama? (0-10)");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();
        fisComplessita.setVariable("complessita", Double.parseDouble(COMPLESSITA_USER));

        String topComplessita = "";
        Double top3 = 0.0;
        List<Double> scoreComplessita = new ArrayList<>();
        for (int i = 0; i < databaseFilm.size(); i++) {
            String cmplxty = databaseFilm.get(i).getComplessita();

            fisComplessita.setVariable("complessita_film", numComplessita(cmplxty));
            fisComplessita.evaluate();
            double idealIndex = fisComplessita.getVariable("affinita_complessita").getValue();
            scoreComplessita.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top3) {
                top3 = idealIndex;
                topComplessita = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base alla complessita: ");
        System.out.println(topComplessita + "\n");


        // LOGICA FUZZY PER REALISMO---------------------------------------------------------------------------------------
        //fileName = "FuzzyFilm-master/src/complessitaNarrativa.fcl";
        fileName = "src/realismo.fcl";
        FIS fisRealismo = FIS.load(fileName, true);
        if (fisRealismo == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        if (rispostaGrafici == 0) {
            gestisciGrafici(fisRealismo, "Realismo");
        }

        // Input Utente realismo
        String REALISMO_USER = JOptionPane.showInputDialog("Quanto vuoi fantasioso il film? (0-10)");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        chiudiFinestre();
        fisRealismo.setVariable("fantasia", Double.parseDouble(REALISMO_USER));

        String topRealismo = "";
        Double top4 = 0.0;
        List<Double> scoreRealismo = new ArrayList<>();
        for (int i = 0; i < databaseFilm.size(); i++) {
            String real = databaseFilm.get(i).getRealismo();

            fisRealismo.setVariable("fantasia_film", numRealismo(real));
            fisRealismo.evaluate();
            double idealIndex = fisRealismo.getVariable("affinita_realismo").getValue();
            scoreRealismo.add(idealIndex);
            System.out.println(databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex);
            if (idealIndex > top4) {
                top4 = idealIndex;
                topRealismo = databaseFilm.get(i).getTitolo() + " -> score: " + idealIndex;
            }
        }
        System.out.print("Miglior film in base alla fantasia: ");
        System.out.println(topRealismo + "\n");


        // CALCOLO FILM MIGLIORE
        int indexBest = 0;
        Double bestScore = 0.0;
        for (int i = 0; i < databaseFilm.size(); i++) {
            Double intensitaTemp = scoreIntensita.get(i);
            Double tempoTemp = scoreTempo.get(i);
            Double finaleTemp = scoreFinale.get(i);
            Double complessitaTemp = scoreComplessita.get(i);
            Double realismoTemp = scoreRealismo.get(i);
            Double tonoTemp = scoreTone.get(i);
            Double violenzaTemp = scoreViolenza.get(i);

            if (intensitaTemp < SOGLIA || tempoTemp < SOGLIA || finaleTemp < SOGLIA ||
                    complessitaTemp < SOGLIA || realismoTemp < SOGLIA || tonoTemp < SOGLIA || violenzaTemp < SOGLIA) {
                databaseFilm.get(i).setScore(0.0);
            } else {
                databaseFilm.get(i).setScore((intensitaTemp + tempoTemp + finaleTemp +
                        complessitaTemp + realismoTemp + tonoTemp + violenzaTemp) / 7);
                if (databaseFilm.get(i).getScore() > bestScore) {
                    indexBest = i;
                    bestScore = databaseFilm.get(i).getScore();
                }
            }

            //System.out.println(databaseFilm.get(i).toString());
        }
        //System.out.print("\n\nMiglior film in base a tutti i parametri: " + databaseFilm.get(indexBest).toString());

        Collections.sort(databaseFilm);
        System.out.println("\n--- CLASSIFICA FILM ---");
        int i = 1;
        for (Film f : databaseFilm) {
            if (f.getScore() > 0) { //non mostro quelli con score nullo
                System.out.println(i + ". " + f);
                i++;
            }
        }

        Film BEST = databaseFilm.get(0);
            mostraRisultatoFinale(BEST, databaseFilm);


            creaGraficiBestResult(BEST, TONO_USER, INTENSITA_USER, VIOLENZA_USER, TEMPO_USER, FINALE_USER, COMPLESSITA_USER, REALISMO_USER);

            int risposta = JOptionPane.showConfirmDialog(
                    null,
                    "Vuoi cercare un altro film?",
                    "Nuova Ricerca",
                    JOptionPane.YES_NO_OPTION
            );

            if (risposta == JOptionPane.NO_OPTION) {
                continuaRicerca = false;
                System.out.println("\nBuona visione!");
            } else {

                System.out.println("NUOVA RICERCA");

                chiudiFinestre();
            }

        } while (continuaRicerca);

        chiudiFinestre();
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
                return 3.5; // valore neutro è commedia
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
                return 1.0; // valore neutro tra lieto e triste
        }
    }

    private static double numComplessita(String complessita) {
        if (complessita == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        switch (complessita.toLowerCase()) {
            case "semplice":
                return 1.0;
            case "lineare":
                return 3.0;
            case "intricata":
                return 5.0;
            case "cerebrale":
                return 7.0;
            default:
                return 4.0; // valore neutro è tra lineare e intricato
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
                return 2.0; // valore neutro è tra stilizzato e surreale
        }
    }


    private static double numTono(String tono) {
        if (tono == null) {
            throw new IllegalArgumentException("Tono nullo");
        }

        switch (tono.toLowerCase()) {
            case "dark":
                return 1.0;
            case "serio":
                return 2.5;
            case "bilanciato":
                return 4.0;
            case "leggero":
                return 5.5;
            case "comico":
                return 7.0;
            case "epico":
                return 8.5;
            case "inquietante":
                return 10.0;
            default:
                return 4.0; //valore neutro va bene bilanciato
        }
    }

    private static double numViolenza(String violenza) {
        if (violenza == null) {
            throw new IllegalArgumentException("Violenza nulla");
        }

        switch (violenza.toLowerCase()) {
            case "pertutti":
                return 1.0;
            case "lieve":
                return 3.0;
            case "moderato":
                return 5.0;
            case "esplicito":
                return 7.0;
            case "estremo":
                return 9.0;
            default:
                return 5.0; // valore neutro va bene moderato
        }
    }


    public static void gestisciGrafici(FIS fis, String nomeFis) {
        if (fis == null) return;

        // 2. DOMANDA SPECIFICA
        int sceltaSpecifica = JOptionPane.showConfirmDialog(
                null,
                "Vuoi visualizzare i grafici per: " + nomeFis + "?",
                "Debug: " + nomeFis,
                JOptionPane.YES_NO_OPTION
        );

        // 3. VISUALIZZAZIONE
        if (sceltaSpecifica == JOptionPane.YES_OPTION) {
            // Poiché non possiamo estrarre i grafici singolarmente per unirli,
            // usiamo il metodo standard per mostrare tutto il blocco funzionale.
            // A seconda della versione, potrebbe aprire finestre separate o una unica.
            JFuzzyChart.get().chart(fis);

            // ALTERNATIVA: Se la riga sopra non ti mostra tutto, usa questo ciclo
            // per forzare l'apertura di ogni variabile (Tono, Tono_Film, Affinità):
        /*
        for (net.sourceforge.jFuzzyLogic.rule.Variable var : fis.getFunctionBlock(null).variables()) {
            JFuzzyChart.get().chart(var, var.getDefuzzifier(), true);
        }
        */


        }
    }


    private static void creaGraficiBestResult(Film BEST, String TONO_USER, String INTENSITA_USER, String VIOLENZA_USER, String TEMPO_USER,
                                              String FINALE_USER, String COMPLESSITA_USER, String REALISMO_USER) {

        String fileName = "src/tone.fcl";
        FIS fisTone = FIS.load(fileName, true);
        if (fisTone == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisTone.setVariable("tone", numTono(TONO_USER));
        fisTone.setVariable("tono_film", numTono(BEST.getTono()));
        fisTone.evaluate();

        RuleBlock ruleBlock = fisTone.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Tono ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable = fisTone.getFunctionBlock("calcolo_affinita").getVariable("affinita_tono");
        JFuzzyChart.get().chart(outputVariable, outputVariable.getDefuzzifier(), true);


        fileName = "src/intensita.fcl";
        FIS fisIntensita = FIS.load(fileName, true);
        if (fisIntensita == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisIntensita.setVariable("intensita", Double.parseDouble(INTENSITA_USER));
        fisIntensita.setVariable("categoria_film", numCategoria(BEST.getCategoria()));
        fisIntensita.evaluate();

        ruleBlock = fisIntensita.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Intensita ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable1 = fisIntensita.getFunctionBlock("calcolo_affinita").getVariable("affinita_intensita");
        JFuzzyChart.get().chart(outputVariable1, outputVariable1.getDefuzzifier(), true);


        fileName = "src/violenza.fcl";
        FIS fisViolenza = FIS.load(fileName, true);
        if (fisViolenza == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisViolenza.setVariable("violenza", numViolenza(VIOLENZA_USER));
        fisViolenza.setVariable("violenza_film", numViolenza(BEST.getContenutiEspliciti()));
        fisViolenza.evaluate();

        ruleBlock = fisViolenza.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Violenza ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable3 = fisViolenza.getFunctionBlock("calcolo_affinita").getVariable("affinita_violenza");
        JFuzzyChart.get().chart(outputVariable3, outputVariable3.getDefuzzifier(), true);


        fileName = "src/tempo.fcl";
        FIS fisTempo = FIS.load(fileName, true);
        if (fisTempo == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisTempo.setVariable("tempo_a_disposizione", Double.parseDouble(TEMPO_USER));
        fisTempo.setVariable("durata_film", BEST.getDurata());
        fisTempo.evaluate();

        ruleBlock = fisTempo.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Tempo ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable4 = fisTempo.getFunctionBlock("calcolo_affinita").getVariable("affinita_tempo");
        JFuzzyChart.get().chart(outputVariable4, outputVariable4.getDefuzzifier(), true);


        fileName = "src/finale.fcl";
        FIS fisFinale = FIS.load(fileName, true);
        if (fisFinale == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisFinale.setVariable("intensita_emozioni_post_film", Double.parseDouble(FINALE_USER));
        fisFinale.setVariable("finale_film", numFinale(BEST.getFinale()));
        fisFinale.evaluate();

        ruleBlock = fisFinale.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Finale ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable5 = fisFinale.getFunctionBlock("calcolo_affinita").getVariable("affinita_finale");
        JFuzzyChart.get().chart(outputVariable5, outputVariable5.getDefuzzifier(), true);


        fileName = "src/complessitaNarrativa.fcl";
        FIS fisComplessita = FIS.load(fileName, true);
        if (fisComplessita == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisComplessita.setVariable("complessita", Double.parseDouble(COMPLESSITA_USER));
        fisComplessita.setVariable("complessita_film", numComplessita(BEST.getComplessita()));
        fisComplessita.evaluate();

        ruleBlock = fisComplessita.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Complessita ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable6 = fisComplessita.getFunctionBlock("calcolo_affinita").getVariable("affinita_complessita");
        JFuzzyChart.get().chart(outputVariable6, outputVariable6.getDefuzzifier(), true);


        fileName = "src/realismo.fcl";
        FIS fisRealismo = FIS.load(fileName, true);
        if (fisRealismo == null) {
            System.err.println("Errore caricamento FCL");
            return;
        }

        fisRealismo.setVariable("fantasia", numRealismo(REALISMO_USER));
        fisRealismo.setVariable("fantasia_film", numRealismo(BEST.getRealismo()));
        fisRealismo.evaluate();

        ruleBlock = fisRealismo.getFunctionBlock("calcolo_affinita").getFuzzyRuleBlock("Rules");

        System.out.println("\n--- Analisi Regole Attive Realismo ---");

        for (Rule r : ruleBlock.getRules()) {
            // Il degreeOfSupport è il valore (0.0 - 1.0) che "taglia" il grafico di uscita
            double level = r.getDegreeOfSupport();

            if (level > 0) {
                System.out.println("La regola si è attivata con forza: " + level);
                System.out.println("Regola: " + r);
            }
        }
        Variable outputVariable2 = fisRealismo.getFunctionBlock("calcolo_affinita").getVariable("affinita_realismo");
        JFuzzyChart.get().chart(outputVariable2, outputVariable2.getDefuzzifier(), true);

    }

    //serve per cancellare le finestre create da jfuzzychart perchè non vengono "catturate"
    // le finestre che apre la libreria

    private static void chiudiFinestre() {
        for (Window window : Window.getWindows()) {
            // Controlliamo se la finestra è visibile
            if (window.isDisplayable()) {
                // Se vuoi evitare di chiudere la finestra principale,
                // metti il titolo della tua app qui sotto
                String titolo = "";

                if (window instanceof Frame) {
                    titolo = ((Frame) window).getTitle();
                } else if (window instanceof Dialog) {
                    titolo = ((Dialog) window).getTitle();
                }

                // Chiudi solo le finestre dei grafici JFuzzyChart
                // Queste finestre hanno titoli specifici come "Variable: affinita", "FunctionBlock", etc.
                // NON chiudere i dialog di input che hanno titoli come "Input", "Select", "Message", etc.
                boolean isGraficoFuzzy = titolo.isEmpty(); // Le finestre dei grafici spesso hanno titolo vuoto

                boolean isDialogInput = titolo.contains("Input") ||
                        titolo.contains("Select") ||
                        titolo.contains("Debug") ||
                        titolo.contains("Message");

                // Chiudi solo se è un grafico fuzzy E NON è un dialog di input
                if (isGraficoFuzzy && !isDialogInput) {
                    window.setVisible(false);
                    window.dispose();
                }
            }
        }
    }


    private static void mostraRisultatoFinale(Film best, List<Film> classifica) {
        // Costruisci il messaggio con il film migliore
        StringBuilder messaggio = new StringBuilder();
        messaggio.append("🎬 IL TUO FILM IDEALE È:\n\n");
        messaggio.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        messaggio.append("📽️  ").append(best.getTitolo()).append("\n");
        messaggio.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        messaggio.append("📊 Score: ").append(String.format("%.2f", best.getScore())).append("\n");
        messaggio.append("🎭 Categoria: ").append(best.getCategoria()).append("\n");
        messaggio.append("⏱️  Durata: ").append(best.getDurata()).append(" minuti\n");
        messaggio.append("🎨 Tono: ").append(best.getTono()).append("\n");
        messaggio.append("🎯 Complessità: ").append(best.getComplessita()).append("\n");
        messaggio.append("✨ Realismo: ").append(best.getRealismo()).append("\n");
        messaggio.append("🎬 Finale: ").append(best.getFinale()).append("\n");
        messaggio.append("⚠️  Contenuti: ").append(best.getContenutiEspliciti()).append("\n\n");

        // Aggiungi la top 3
        messaggio.append("🏆 TOP 3 RACCOMANDAZIONI:\n");
        messaggio.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        int count = 1;
        for (Film f : classifica) {
            if (f.getScore() > 0 && count <= 3) {
                String medaglia = count == 1 ? "🥇" : count == 2 ? "🥈" : "🥉";
                messaggio.append(medaglia).append(" ").append(f.getTitolo())
                        .append(" (").append(String.format("%.2f", f.getScore())).append(")\n");
                count++;
            }
        }

        messaggio.append("\n🍿 BUONA VISIONE! 🍿");

        // Mostra il dialog
        JOptionPane.showMessageDialog(
                null,
                messaggio.toString(),
                "🎉 Raccomandazione Film",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}





