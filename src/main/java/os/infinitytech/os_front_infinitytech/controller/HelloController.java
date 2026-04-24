package os.infinitytech.os_front_infinitytech.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String senha = txtSenha.getText();

        // Lógica simples de teste
        if (usuario.equals("admin") && senha.equals("1234")) {
            System.out.println("Acesso concedido à In-finity Tech!");
            // Aqui você carregaria a próxima tela (o Dashboard que você já tem)
        } else {
            exibirErro("Erro de Autenticação", "Usuário ou senha incorretos.");
        }
    }

    @FXML
    private void handleEsqueceuSenha() {
        System.out.println("Redirecionando para recuperação de senha...");
    }

    @FXML
    private void handleCriarConta() {
        System.out.println("Abrindo tela de cadastro...");
    }

    private void exibirErro(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
