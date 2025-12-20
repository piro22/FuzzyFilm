import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import javax.swing.JOptionPane;

public class Main {
    public static void main(String[] args) {

        String fileName = "src/film.fcl"; // Controlla il percorso esatto!
        FIS fis = FIS.load(fileName, true);

        if (fis == null) {
            System.err.println("Impossibile caricare il file: '" + fileName + "'");
            return;
        }

        // Input rapido tramite finestre di dialogo (GUI minimale)
        String inputCritica = JOptionPane.showInputDialog("Inserisci voto critica (0-10):");
        String inputPop = JOptionPane.showInputDialog("Inserisci popolarità (0-100):");

        try {
            double critica = Double.parseDouble(inputCritica);
            double popolarita = Double.parseDouble(inputPop);

            // Imposta le variabili
            fis.setVariable("voto_critica", critica);
            fis.setVariable("popolarita", popolarita);

            // Valuta
            fis.evaluate();

            // Ottieni il risultato
            double risultato = fis.getVariable("consiglio").getValue();

            // Mostra il grafico delle variabili (utile per il prof!)
            JFuzzyChart.get().chart(fis.getFunctionBlock("consigliatore").getVariable("consiglio"), true);

            // Output finale
            String verdetto = "Il punteggio di raccomandazione è: " + String.format("%.2f", risultato) + "/10";
            JOptionPane.showMessageDialog(null, verdetto);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Errore nell'inserimento dei dati.");
        }
    }

}