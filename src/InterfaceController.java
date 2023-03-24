import javafx.fxml.FXML;
import javafx.scene.control.*;

import com.fazecast.jSerialComm.SerialPort;
import java.lang.*;

import java.io.IOException;
import java.text.DecimalFormat;

public class InterfaceController {
    public ScrollBar freqSlider;
    public Label freqLabel;
    public CheckBox checkBoxHarmonique;
    private boolean deuxiemeHarmonique;
    private SerialPort serialPort;


    @FXML
    /*
    Initialisation des différents attributs
    Applique un listener à checkBoxHarmonique pour lui permettre de changer le système en mode deuxième harmonique
    Applique un listener à freqSlider pour changer le texte de freqLabel selon la valeur du slider
     */
    public void initialize() {
        DecimalFormat decimalFormat = new DecimalFormat("#.0");
        freqLabel.setText("90.0 Hz");
        deuxiemeHarmonique = false;

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

    /*
    Lorsque le bouton démarrer est appuyé, envoye la fréquence commandée vers le port série
    Appele les fonction connexion() et deconnexion()
    Envoye deux fois la valeur car l'arduino ne reçoit pas la fréquence lors du premier envois
     */
    public void demarrerSysteme() throws IOException, InterruptedException {
        double frequence = freqSlider.getValue();
        System.out.println("Frequence : " + frequence);

        Integer freqByte = (int) (10 * frequence);

        if (connexion()) {                                              //Si la connexion est réussie,
            serialPort.getOutputStream().write(freqByte.byteValue());   // écrit la valeur de la fréquence en octets
            serialPort.getOutputStream().flush();                       // vers l'arduino deux fois. Attend 1 seconde
            Thread.sleep(1000);                                    // à chaque envoie. Déconnecte après l'envoie
            serialPort.getOutputStream().write(freqByte.byteValue());
            serialPort.getOutputStream().flush();
            System.out.println("Sent number: " + freqByte);
            Thread.sleep(1000);

            deconnexion();

        }
    }

    public void arreterSysteme() throws IOException, InterruptedException {

        byte signal = 10;

        if (connexion()) {

            serialPort.getOutputStream().write(signal);
            serialPort.getOutputStream().flush();
            Thread.sleep(1000);
            serialPort.getOutputStream().write(signal);
            serialPort.getOutputStream().flush();
            Thread.sleep(1000);

            deconnexion();
        }
    }

    public void setFaDiezeButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(185);
            freqLabel.setText("185.0 Hz");
        }
        else {
            freqSlider.setValue(92.5);
            freqLabel.setText("92.5 Hz");
        }
    }

    public void setSolButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(196);
            freqLabel.setText("196.0 Hz");
        }
        else {
            freqSlider.setValue(98);
            freqLabel.setText("98.0 Hz");
        }
    }

    public void setSolDiezeButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(207.7);
            freqLabel.setText("207.7 Hz");
        }
        else {
            freqSlider.setValue(103.8);
            freqLabel.setText("103.8 Hz");
        }
    }

    public void setLaButton() {
        if (deuxiemeHarmonique) {
            freqSlider.setValue(220);
            freqLabel.setText("220.0 Hz");
        }
        else {
            freqSlider.setValue(110);
            freqLabel.setText("110.0 Hz");
        }
    }

    public void setLaDiezeButton(){
        if (deuxiemeHarmonique) {
            freqSlider.setValue(233.1);
            freqLabel.setText("233.1 Hz");
        }
        else {
            freqSlider.setValue(116.5);
            freqLabel.setText("116.5 Hz");
        }
    }

    public boolean connexion() {

        SerialPort[] serialPorts = SerialPort.getCommPorts();
        String arduino = "Arduino Mega 2560";
        boolean connected = false;

        for (SerialPort sp :serialPorts) {
            if (sp.getPortDescription().length() > 16) {
                if (sp.getPortDescription().substring(0, 17).equals(arduino)) {
                    serialPort = SerialPort.getCommPort(sp.getSystemPortName());
                    serialPort.setComPortParameters(sp.getBaudRate(), 8, 1, 0);
                    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0);
                    connected = true;
                    break;
                }
            }
        }

        if (!connected) {
            System.out.println("Failed to open port");
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Arduino Mega 2560 non connecté");
            alert.show();
            return false;
        }

        if (serialPort.openPort()) {
            System.out.println("Port open");
            return true;
        }

        System.out.println("Failed to open port");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Échec de la connexion au port série");
        alert.show();
        return false;
    }

    public void deconnexion() {
        if (serialPort.closePort()) {
            System.out.println("Port is closed :)");
        }

        else {
            System.out.println("Failed to close port :(");
        }
    }
}
