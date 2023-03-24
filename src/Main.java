import com.fazecast.jSerialComm.SerialPort;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    public static void main(String[] args) {launch(args);} {

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        SerialPort[] serialPorts = SerialPort.getCommPorts();

        for (SerialPort serialPort : serialPorts) {
            System.out.println("Name : " + serialPort.getPortDescription() + " Baud rate : " + serialPort.getBaudRate() +
                    " Descriptive name : " + serialPort.getDescriptivePortName() + " System name : " + serialPort.getSystemPortName() +
                    " Location : " + serialPort.getPortLocation());
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Interface.fxml")));
        primaryStage.setTitle("Design II Hiver 2023");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}