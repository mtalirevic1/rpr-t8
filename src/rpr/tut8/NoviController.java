package rpr.tut8;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class NoviController {
    public TextField postanskiBroj;
    public TextField ime;
    public TextField adresa;
    public TextField grad;
    public Button closeBtn;
    public SimpleStringProperty text;
    private Thread thread1;

    public NoviController() {
        text = new SimpleStringProperty("");
    }

    public boolean validanPostanskiBroj(String s) throws Exception {

        URL url = new URL("http://c9.etf.unsa.ba/proba/postanskiBroj.php?postanskiBroj=" + postanskiBroj.getText());
        BufferedReader ulaz = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        String json = "", line = null;
        while ((line = ulaz.readLine()) != null)
            json = json + line;
        if (json.equals("OK")) return true;
        return false;
    }

    @FXML
    public void initialize() {

            postanskiBroj.textProperty().bindBidirectional(text);
            postanskiBroj.getStyleClass().add("poljeNeispravno");
           /* thread1 = new Thread(r1);
        thread1.start();*/
           dodajListenere();
    }

   /* Runnable r1 = () -> {
        postanskiBroj.focusedProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue) {
                    if (validanPostanskiBroj(postanskiBroj.getText())) {
                        postanskiBroj.getStyleClass().removeAll("poljeNeispravno");
                        postanskiBroj.getStyleClass().add("poljeIspravno");
                    } else {
                        postanskiBroj.getStyleClass().removeAll("poljeIspravno");
                        postanskiBroj.getStyleClass().add("poljeNeispravno");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    };*/

    private void dodajListenere() {
        postanskiBroj.focusedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (aBoolean && !t1) {
               // validator.setBroj(brojField.getText());

                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {

                        return validanPostanskiBroj(postanskiBroj.getText());
                    }
                };

                task.setOnSucceeded(workerStateEvent -> {
                    Boolean value = task.getValue();
                    if (value) {
                        postanskiBroj.getStyleClass().removeAll("poljeNijeIspravno");
                        postanskiBroj.getStyleClass().add("poljeIspravno");
                    } else {
                        postanskiBroj.getStyleClass().removeAll("poljeIspravno");
                        postanskiBroj.getStyleClass().add("poljeNijeIspravno");
                    }
                });

               thread1= new Thread(task);
               thread1.start();

            }
        });

    }

public void clickOnCloseButton(ActionEvent actionEvent) {
    Node n = (Node) actionEvent.getSource();
    Stage stage = (Stage) n.getScene().getWindow();
    stage.close();
}
}