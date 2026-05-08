package os.infinitytech.os_front_infinitytech;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Carrega o arquivo FXML
        Parent root = FXMLLoader.load(getClass().getResource("/os_front_infinitytech/fxml/login.fxml"));

        // Cria a cena
        Scene scene = new Scene(root);

        // Configurações da Janela
        stage.setTitle("Infinity Tech - Login");

        // Opcional: Remove a barra de tarefas padrão do Windows para um visual mais limpo (opcional)
        //
        //stage.initStyle(StageStyle.UNDECORATED);

        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
