import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.*;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.awt.Window;

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
                JOptionPane.showMessageDialog(null, "Error reading CSV file: " + e.getMessage());
                return;
            }

            //Chiedo se volgio visualizzare i grafici
            int rispostaGrafici = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to visualize the fuzzy logic graphs?",
                    "Graphs",
                    JOptionPane.YES_NO_OPTION
            );


            // LOGICA FUZZY PER TONE------------------------------------------------------------------------------------
            String fileName = "src/tone.fcl";
            FIS fisTone = FIS.load(fileName, true);
            if (fisTone == null) {
                System.err.println("Error loading FCL file:" + fileName);
                return;
            }

            //Chiedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisTone, "Tono");

            }


            //Input utente Tono
            String[] options = {"dark", "serious", "balanced", "lighthearted", "comic", "epic", "disturbing"};
            String TONO_USER = "";

            while(!Arrays.asList(options).contains(TONO_USER)){
                TONO_USER = (String) JOptionPane.showInputDialog(
                        null,
                        "Choose the preferred film tone:",
                        "Input Tone",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[0]
                );

                if (TONO_USER == null) {
                    System.out.println("Operazione annullata dall'utente.");
                    break; // O System.exit(0);
                }
            }


            callSleep();
            chiudiFinestre();

            double tonoNum = numTono(TONO_USER);
            fisTone.setVariable("tone", tonoNum);

            // Per ogni film svolgo evaluate del fuzzy
            String topTone = "";
            double topT = 0.0;
            List<Double> scoreTone = new ArrayList<>();

            for (Film film : databaseFilm) {
                String tonoFilm = film.getTono();
                fisTone.setVariable("tono_film", numTono(tonoFilm));
                fisTone.evaluate();

                double idealIndex = fisTone.getVariable("affinita_tono").getValue();
                scoreTone.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score tone: " + idealIndex);

                if (idealIndex > topT) {
                    topT = idealIndex;
                    topTone = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on tone: ");
            System.out.println(topTone + "\n");


            // LOGICA FUZZY PER INTENSITA-------------------------------------------------------------------------------
            fileName = "src/intensita.fcl";
            FIS fisIntensita = FIS.load(fileName, true);
            if (fisIntensita == null) {
                System.err.println("Error loading FCL file");
                return;
            }

            //chiedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisIntensita, "Intensita");
            }

            // Input Utente intensita
            Double INTENSITA_USER = richiediInput("How intense do you want the movie to be?", 10.0);
            if(INTENSITA_USER == null) break;
            else fisIntensita.setVariable("intensita", INTENSITA_USER);

            callSleep();
            chiudiFinestre();

            // Per ogni film svolgo evaluate del fuzzy
            String topIntensita = "";
            double top = 0.0;
            List<Double> scoreIntensita = new ArrayList<>();

            for (Film film : databaseFilm) {
                String categoria = film.getCategoria();
                fisIntensita.setVariable("categoria_film", numCategoria(categoria));
                fisIntensita.evaluate();

                double idealIndex = fisIntensita.getVariable("affinita_intensita").getValue();
                scoreIntensita.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score: " + idealIndex);

                if (idealIndex > top) {
                    top = idealIndex;
                    topIntensita = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on intensità: ");
            System.out.println(topIntensita + "\n");


            //LOGICA FUZZY VIOLENZA ------------------------------------------------------------------------------------
            fileName = "src/violenza.fcl";
            FIS fisViolenza = FIS.load(fileName, true);
            if (fisViolenza == null) {
                System.err.println("Error loading FCL file");
                return;
            }

            //chiedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisViolenza, "Violenza");
            }

            //Input utente violenza
            String[] optionsViolenza = {"forEverybody", "low", "moderate", "explicit", "extreme"};
            String VIOLENZA_USER = "";

            while(!Arrays.asList(optionsViolenza).contains(VIOLENZA_USER)){
                VIOLENZA_USER = (String) JOptionPane.showInputDialog(
                        null,
                        "How violent do you want the movie to be?",
                        "Input Violence",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        optionsViolenza,
                        optionsViolenza[0]
                );

                if (VIOLENZA_USER == null) {
                    System.out.println("Operazione annullata dall'utente.");
                    break; // O System.exit(0);
                }
            }

            callSleep();
            chiudiFinestre();

            double violenzaNum = numViolenza(VIOLENZA_USER);
            fisViolenza.setVariable("violenza", violenzaNum);

            // Per ogni film svolgo evaluate del fuzzy
            String topViolenza = "";
            double topV = 0.0;
            List<Double> scoreViolenza = new ArrayList<>();

            for (Film film : databaseFilm) {
                String violenzaFilm = film.getContenutiEspliciti();
                fisViolenza.setVariable("violenza_film", numViolenza(violenzaFilm));
                fisViolenza.evaluate();

                double idealIndex = fisViolenza.getVariable("affinita_violenza").getValue();
                scoreViolenza.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score violenza: " + idealIndex);

                if (idealIndex > topV) {
                    topV = idealIndex;
                    topViolenza = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on violence: ");
            System.out.println(topViolenza + "\n");


            // LOGICA FUZZY PER TEMPO-----------------------------------------------------------------------------------
            fileName = "src/tempo.fcl";
            FIS fisTempo = FIS.load(fileName, true);
            if (fisTempo == null) {
                System.err.println("Error loading FCL file");
                return;
            }

            //chedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisTempo, "Tempo");
            }

            // Input Utente tempo
            Double TEMPO_USER = richiediInput("How much time do you have available? (in minutes)", 240.0);
            if (TEMPO_USER == null) break;
            else fisTempo.setVariable("tempo_a_disposizione", TEMPO_USER);

            callSleep();
            chiudiFinestre();

            // Per ogni film svolgo evaluate del fuzzy
            String topTempo = "";
            double top1 = 0.0;
            List<Double> scoreTempo = new ArrayList<>();

            for (Film film : databaseFilm) {
                int durata = film.getDurata();
                fisTempo.setVariable("durata_film", durata);
                fisTempo.evaluate();

                double idealIndex = fisTempo.getVariable("affinita_tempo").getValue();
                scoreTempo.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score: " + idealIndex);

                if (idealIndex > top1) {
                    top1 = idealIndex;
                    topTempo = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on duration: ");
            System.out.println(topTempo + "\n");


            // LOGICA FUZZY PER FINALE----------------------------------------------------------------------------------
            fileName = "src/finale.fcl";
            FIS fisFinale = FIS.load(fileName, true);
            if (fisFinale == null) {
                System.err.println("Error loading FCL file");
                return;
            }

            //chiedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisFinale, "Finale");
            }

            // Input Utente finale
            Double FINALE_USER = richiediInput("How emotional do you want the movie ending to be?", 10.0);
            if (FINALE_USER == null) break;
            else fisFinale.setVariable("intensita_emozioni_post_film", FINALE_USER);

            callSleep();
            chiudiFinestre();

            // Per ogni film svolgo evaluate del fuzzy
            String topFinale = "";
            double top2 = 0.0;
            List<Double> scoreFinale = new ArrayList<>();

            for (Film film : databaseFilm) {
                String finale = film.getFinale();
                fisFinale.setVariable("finale_film", numFinale(finale));
                fisFinale.evaluate();

                double idealIndex = fisFinale.getVariable("affinita_finale").getValue();
                scoreFinale.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score: " + idealIndex);

                if (idealIndex > top2) {
                    top2 = idealIndex;
                    topFinale = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on film ending: ");
            System.out.println(topFinale + "\n");


            // LOGICA FUZZY PER COMPLESSITA-----------------------------------------------------------------------------
            fileName = "src/complessitaNarrativa.fcl";
            FIS fisComplessita = FIS.load(fileName, true);
            if (fisComplessita == null) {
                System.err.println("Error loading FCL file");
                return;
            }

            //chiedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisComplessita, "Complessita");
            }

            // Input Utente complessita
            Double COMPLESSITA_USER = richiediInput("How complex do you want the movie plot to be?", 10.0);
            if(COMPLESSITA_USER == null) break;
            else fisComplessita.setVariable("complessita", COMPLESSITA_USER);

            callSleep();
            chiudiFinestre();

            // Per ogni film svolgo evaluate del fuzzy
            String topComplessita = "";
            double top3 = 0.0;
            List<Double> scoreComplessita = new ArrayList<>();

            for (Film film : databaseFilm) {
                String cmplxty = film.getComplessita();
                fisComplessita.setVariable("complessita_film", numComplessita(cmplxty));
                fisComplessita.evaluate();

                double idealIndex = fisComplessita.getVariable("affinita_complessita").getValue();
                scoreComplessita.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score: " + idealIndex);

                if (idealIndex > top3) {
                    top3 = idealIndex;
                    topComplessita = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on plot complexity: ");
            System.out.println(topComplessita + "\n");


            // LOGICA FUZZY PER REALISMO--------------------------------------------------------------------------------
            fileName = "src/realismo.fcl";
            FIS fisRealismo = FIS.load(fileName, true);
            if (fisRealismo == null) {
                System.err.println("Error loading FCL file");
                return;
            }

            //chiedo per grafici
            if (rispostaGrafici == 0) {
                gestisciGrafici(fisRealismo, "Realismo");
            }

            // Input Utente realismo
            Double REALISMO_USER = richiediInput("How fantastical do you want the movie to be?", 10.0);
            if(REALISMO_USER == null) break;
            else fisRealismo.setVariable("fantasia", REALISMO_USER);

            callSleep();
            chiudiFinestre();

            // Per ogni film svolgo evaluate del fuzzy
            String topRealismo = "";
            double top4 = 0.0;
            List<Double> scoreRealismo = new ArrayList<>();

            for (Film film : databaseFilm) {
                String real = film.getRealismo();
                fisRealismo.setVariable("fantasia_film", numRealismo(real));
                fisRealismo.evaluate();

                double idealIndex = fisRealismo.getVariable("affinita_realismo").getValue();
                scoreRealismo.add(idealIndex);
                System.out.println(film.getTitolo() + " -> score: " + idealIndex);

                if (idealIndex > top4) {
                    top4 = idealIndex;
                    topRealismo = film.getTitolo() + " -> score: " + idealIndex;
                }
            }
            System.out.print("Best film based on fantasy: ");
            System.out.println(topRealismo + "\n");


            // CALCOLO FILM MIGLIORE TRAMITE TUTTI GLI SCORE
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
                        bestScore = databaseFilm.get(i).getScore();
                    }
                }
            }

            //ORDINO DATABASE E STAMPO CLASSIFICA
            Collections.sort(databaseFilm);
            System.out.println("\n--- FILM RANKING ---");
            int i = 1;
            for (Film f : databaseFilm) {
                if (f.getScore() > 0) { //non mostro quelli con score nullo
                    System.out.println(i + ". " + f);
                    i++;
                }
            }

            Film BEST = databaseFilm.getFirst();
            mostraRisultatoFinale(BEST, databaseFilm);
            creaGraficiBestResult(BEST, TONO_USER, INTENSITA_USER, VIOLENZA_USER, TEMPO_USER, FINALE_USER, COMPLESSITA_USER, REALISMO_USER);

            //Cercare altro film
            int risposta = JOptionPane.showConfirmDialog(
                    null,
                    "Do you want to repeat the process?",
                    "New Research",
                    JOptionPane.YES_NO_OPTION
            );

            if (risposta == JOptionPane.NO_OPTION) {
                continuaRicerca = false;
                System.out.println("\n Enjoy the movie!");
                chiudiFinestre();
            } else {
                System.out.println("NEW RESEARCH");
                chiudiFinestre();
            }

        } while (continuaRicerca);

        chiudiFinestre();

    }


//METODI----------------------------------------------------------------------------------------------------------------


    //sleep generale
    private static void callSleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            System.err.println("Sleep interrupted.");
        }
    }



    private static double numCategoria(String categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        return switch (categoria.toLowerCase()) {
            case "dramatic" -> 0.5;
            case "animation" -> 1.5;
            case "romance" -> 2.5;
            case "commedy" -> 3.5;
            case "thriller" -> 4.5;
            case "horror" -> 5.5;
            case "action" -> 6.5;
            case "adventure" -> 7.5;
            case "scifi" -> 8.5;
            case "fantasy" -> 9.5;
            default -> 3.5; // valore neutro è commedia
        };
    }



    private static double numFinale(String categoria) {
        if (categoria == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        return switch (categoria.toLowerCase()) {
            case "happy" -> 0.5;
            case "sad" -> 1.5;
            case "melancholic" -> 2.5;
            case "tense" -> 3.5;
            default -> 1.0; // valore neutro tra happy e sad
        };
    }



    private static double numComplessita(String complessita) {
        if (complessita == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        return switch (complessita.toLowerCase()) {
            case "simple" -> 1.0;
            case "linear" -> 3.0;
            case "intricate" -> 5.0;
            case "cerebral" -> 7.0;
            default -> 4.0; // valore neutro è tra linear e intricate
        };
    }



    private static double numRealismo(String realismo) {
        if (realismo == null) {
            throw new IllegalArgumentException("Categoria nulla");
        }

        return switch (realismo.toLowerCase()) {
            case "realistic" -> 0.5;
            case "stylized" -> 1.5;
            case "surreal" -> 2.5;
            case "fantasy" -> 3.5;
            default -> 2.0; // valore neutro è tra stylized e surreal
        };
    }



    private static double numTono(String tono) {
        if (tono == null) {
            throw new IllegalArgumentException("Tono nullo");
        }

        return switch (tono.toLowerCase()) {
            case "dark" -> 1.0;
            case "serious" -> 2.5;
            case "balanced" -> 4.0;
            case "lighthearted" -> 5.5;
            case "comic" -> 7.0;
            case "epic" -> 8.5;
            case "disturbing" -> 10.0;
            default -> 4.0; //valore neutro va bene balanced
        };
    }



    private static double numViolenza(String violenza) {
        if (violenza == null) {
            throw new IllegalArgumentException("Violenza nulla");
        }

        return switch (violenza.toLowerCase()) {
            case "foreverybody" -> 1.0;
            case "low" -> 3.0;
            case "moderate" -> 5.0;
            case "explicit" -> 7.0;
            case "extreme" -> 9.0;
            default -> 5.0; // valore neutro va bene moderate
        };
    }



    public static void gestisciGrafici(FIS fis, String nomeFis) {
        if (fis == null) return;

        // 2. DOMANDA SPECIFICA
        int sceltaSpecifica = JOptionPane.showConfirmDialog(
                null,
                "Do you want to visualize " + nomeFis + " graphs?",
                "Debug: " + nomeFis,
                JOptionPane.YES_NO_OPTION
        );

        // 3. VISUALIZZAZIONE
        if (sceltaSpecifica == JOptionPane.YES_OPTION) {
            JFuzzyChart.get().chart(fis);
        }
    }



    private static void creaGraficiBestResult(Film BEST, String TONO_USER, double INTENSITA_USER, String VIOLENZA_USER, double TEMPO_USER,
                                              double FINALE_USER, double COMPLESSITA_USER, double REALISMO_USER) {

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

        fisIntensita.setVariable("intensita", INTENSITA_USER);
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

        fisTempo.setVariable("tempo_a_disposizione", TEMPO_USER);
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

        fisFinale.setVariable("intensita_emozioni_post_film", FINALE_USER);
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

        fisComplessita.setVariable("complessita", COMPLESSITA_USER);
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

        fisRealismo.setVariable("fantasia", REALISMO_USER);
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



//forzo la chiusura delle finestre dei grafici
    private static void chiudiFinestre() {
        // Usiamo invokeLater per evitare il deadlock
        SwingUtilities.invokeLater(() -> {
            for (Window window : Window.getWindows()) {
                if (window.isDisplayable()) {
                    // La rendiamo invisibile subito (così sparisce alla vista istantaneamente)
                    window.setVisible(false);
                    // Liberiamo le risorse
                    window.dispose();
                }
            }
        });
    }



    private static void mostraRisultatoFinale(Film best, List<Film> classifica) {
        // Costruisco il messaggio con il film migliore
        StringBuilder messaggio = new StringBuilder();
        messaggio.append("🎬 YOUR IDEAL MOVIE IS:\n\n");
        messaggio.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        messaggio.append("📽️  ").append(best.getTitolo()).append("\n");
        messaggio.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n");
        messaggio.append("📊 Score: ").append(String.format("%.2f", best.getScore())).append("\n");
        messaggio.append("🎭 Category: ").append(best.getCategoria()).append("\n");
        messaggio.append("⏱️ Duration: ").append(best.getDurata()).append(" minutes\n");
        messaggio.append("🎨 Tone: ").append(best.getTono()).append("\n");
        messaggio.append("🎯 Complexity: ").append(best.getComplessita()).append("\n");
        messaggio.append("✨ Realism: ").append(best.getRealismo()).append("\n");
        messaggio.append("🎬 Ending: ").append(best.getFinale()).append("\n");
        messaggio.append("⚠️ Explicit Content: ").append(best.getContenutiEspliciti()).append("\n\n");

        // Aggiungo la top
        messaggio.append("🏆 TOP PICKS:\n");
        messaggio.append("━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        int i = 1;
        for (Film f : classifica) {
            if (f.getScore() > 0 && i <= 3) {
                messaggio.append(" ").append(f.getTitolo())
                        .append(" (").append(String.format("%.2f", f.getScore())).append(")\n");
                i++;
            }
        }

        messaggio.append("\n🍿 ENJOY THE MOVIE! 🍿");

        // Mostra il dialog
        JOptionPane.showMessageDialog(
                null,
                messaggio.toString(),
                "🎉 TOP PICKS",
                JOptionPane.INFORMATION_MESSAGE
        );
    }



    private static Double richiediInput(String messaggio, double max) {
        while (true) {
            String inputUser = JOptionPane.showInputDialog(messaggio + " (" + 0.0 + " - " + max + ")");

            //operazione annullata
            if (inputUser == null) {
                System.out.println("Operazione annullata per: " + messaggio);
                return null; // Ritorniamo null per segnalare l'uscita
            }

            try {
                double valore = Double.parseDouble(inputUser);

                if (valore >= 0.0 && valore <= max) {
                    return valore; // Input valido, lo ritorniamo
                } else {
                    System.err.println("Number out of Range (" + 0.0 + "-" + max + ")");
                }

            } catch (NumberFormatException e) {
                System.err.println("Not a Number: " + inputUser);
            }
        }
    }

}