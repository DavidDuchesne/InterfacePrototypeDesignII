import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.fazecast.jSerialComm.SerialPort;

import java.lang.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;

public class InterfaceController {
    public ScrollBar freqSlider;
    public Label freqLabel;
    public CheckBox checkBoxHarmonique;
    private boolean deuxiemeHarmonique;
    private SerialPort serialPort;
    private boolean connected;

    /**
    Initialisation des différents attributs
    Applique un DecimalFormat au label de fréquence pour n'avoir que 2 décimales
    Applique un listener à checkBoxHarmonique pour lui permettre de changer le système en mode deuxième harmonique
    Applique un listener à freqSlider pour changer le texte de freqLabel selon la valeur du slider
     */
    @FXML
    public void initialize() {
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        freqLabel.setText("90.00 Hz");
        deuxiemeHarmonique = false;
        connected = false;

        checkBoxHarmonique.selectedProperty().addListener((observable, oldValue, newValue) -> {
            double freqInter;
            if (newValue) {                                             // Double les valeurs maximales et minimales
                freqInter = freqSlider.getValue() * 2;                  // du slider pour passer en deuxième
                freqSlider.setMax(freqSlider.getMax() * 2);             // harmonique lorsque la checkBox est checked
                freqSlider.setMin(freqSlider.getMin() * 2);
                deuxiemeHarmonique = true;
            }

            else {                                                      // Divise par deux les valeurs maximales et
                freqInter = freqSlider.getValue() / 2;                  // minimales du slider pour passer en première
                freqSlider.setMin(freqSlider.getMin()/2);               // harmonique lorsque la checkBox est unchecked
                freqSlider.setMax(freqSlider.getMax()/2);
                deuxiemeHarmonique = false;
            }
            freqSlider.setValue(freqInter);                             // Modifie la valeur du slider et du label pour
            freqLabel.setText(decimalFormat.format(freqInter) + " Hz"); // pour suivre le changement d'harmonique
        });

        freqSlider.valueProperty().addListener((observable, oldValue, newValue) -> // Modifie le texte du label dès qu'il
                freqLabel.setText(decimalFormat.format(newValue) + " Hz"));        // y a un changement au slider
    }

    /**
    Lorsque le bouton démarrer est appuyé, envoye la fréquence commandée vers le port série
    Appele les fonction connexion() et deconnexion()
    Envoye deux fois la valeur car l'arduino ne reçoit pas la fréquence lors du premier envoi
     */
    public void demarrerSysteme() throws IOException, InterruptedException {
        float freqFloat = (float) (freqSlider.getValue());


        if (!connected) {                                               // Si non connecté, tente de se connecter à
            connexion();                                                // l'arduino.
        }

        System.out.println("Sent number: " + freqFloat);
        serialPort.getOutputStream().write(Float.toString(freqFloat).getBytes(StandardCharsets.US_ASCII));
        serialPort.getOutputStream().flush();
        serialPort.getOutputStream().write(("\n").getBytes(StandardCharsets.US_ASCII));
        serialPort.getOutputStream().flush();
    }

    /**
    Lorsque le bouton arrêter est appuyé, envoye un signal vers l'arduino pour lui indiquer
    de mettre fin à la vibration.
    Appelle connexion() et deconnexion()
     */
    public void arreterSysteme() throws IOException, InterruptedException {
        if (!connected) {                                               // Se connecte a l'arduino si non connecté
            connexion();
        }

        serialPort.getOutputStream().write(("10").getBytes(StandardCharsets.US_ASCII));
        serialPort.getOutputStream().flush();
        serialPort.getOutputStream().write(0);
        serialPort.getOutputStream().flush();

        deconnexion();
    }

    /**
    Modifie la valeur du slider et du label pour correspondre à la fréquence liée à la note selon l'harmonique
     */
    public void setFaDiezeButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(185);
            freqLabel.setText("185.00 Hz");
        }
        else {
            freqSlider.setValue(92.5);
            freqLabel.setText("92.50 Hz");
        }
    }

    public void setSolButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(196);
            freqLabel.setText("196.00 Hz");
        }
        else {
            freqSlider.setValue(98);
            freqLabel.setText("98.00 Hz");
        }
    }

    public void setSolDiezeButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(207.66);
            freqLabel.setText("207.66 Hz");
        }
        else {
            freqSlider.setValue(103.83);
            freqLabel.setText("103.83 Hz");
        }
    }

    public void setLaButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(220);
            freqLabel.setText("220.00 Hz");
        }
        else {
            freqSlider.setValue(110);
            freqLabel.setText("110.00 Hz");
        }
    }

    public void setLaDiezeButton(){
        if (deuxiemeHarmonique) {
            freqSlider.setValue(233.08);
            freqLabel.setText("233.08 Hz");
        }
        else {
            freqSlider.setValue(116.54);
            freqLabel.setText("116.54 Hz");
        }
    }

    /**
     * Analyse chaque port de communication en utilisation lors de l'appel de la fonction et se connecte
     * à celui relié à l'arduino. Modifie la valeur de connected a true si la connexion est réussie.
     */
    private void connexion() throws InterruptedException {

        SerialPort[] serialPorts = SerialPort.getCommPorts();
        String arduino = "Arduino Mega 2560";

        //Vérifie si le nom d'une des connexion série contient "Arduion Mega 2560". S'y connecte si oui.

        for (SerialPort sp :serialPorts) {
            if (sp.getPortDescription().contains(arduino)) {
                    serialPort = SerialPort.getCommPort(sp.getSystemPortName());
                    serialPort.setComPortParameters(57600, 8, 1, 0);
                    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                    connected = true;
                    break;

            }
        }

        if (!connected) {                                                   // S'il n'y avait pas de connexion à un arduino,
            System.out.println("Failed to open port");                      // affiche une alerte et return false.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Arduino Mega 2560 non connecté");
            alert.show();
        }

        else if (serialPort.openPort()) {                                    // Tente d'ouvrir le port série. Return true
            System.out.println("Port open");                                 // s'il s'y connecte.

        }

        else {
            System.out.println("Failed to open port");                      // Si la connexion au port série a échoué,
            Alert alert = new Alert(Alert.AlertType.ERROR);                 // affiche une alerte l'indiquant.
            alert.setContentText("Échec de la connexion au port série");
            alert.show();
        }

        Thread.sleep(1000);
    }

    /**
     * Ferme la connexion avec l'arduino
     */
    public void deconnexion() {
        if (serialPort.closePort()) {                                   // Tente de fermer la connexion. Affiche à la
            System.out.println("Port is closed :)");                    // si la déconnexion est réussie
            connected = false;
        }

        else {
            System.out.println("Failed to close port :(");              // Affiche à la console si la déconnexion
        }                                                               // échoue.
    }
}
