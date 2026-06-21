package os.infinitytech.os_front_infinitytech.util;

import javafx.scene.control.TableCell;
import java.text.Normalizer;

/**
 * Classe universal para estilização de status baseada em CSS.
 * O 'T' permite que esta classe seja usada por qualquer Model (ProduModel, ServiceModel, etc).
 */
public class StatusColor<T> extends TableCell<T, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);

        // Define a lista de estilos para facilitar a manutenção
        String[] todosEstilos = {
                "status-andamento", "status-concluido", "status-aguardando",
                "status-autorizado", "status-disponivel", "status-indisponivel", "status-padrao"
        };

        if (empty || item == null) {
            setText(null);
            getStyleClass().removeAll(todosEstilos);
        } else {
            setText(item);
            // Limpeza total antes de reaplicar o novo estilo
            getStyleClass().removeAll(todosEstilos);

            // Normaliza a string: remove acentos e joga para maiúsculo
            String statusNormalizado = removerAcentos(item.toUpperCase());

            String styleClass = switch (statusNormalizado) {
                case "ANDAMENTO"    -> "status-andamento";
                case "CONCLUIDO"    -> "status-concluido";
                case "AGUARDANDO"   -> "status-aguardando";
                case "AUTORIZADO"   -> "status-autorizado";
                case "DISPONIVEL"   -> "status-disponivel";
                case "INDISPONIVEL" -> "status-indisponivel";
                default             -> "status-padrao";
            };

            getStyleClass().add(styleClass);
        }
    }

    /**
     * Remove acentos de uma string para garantir que a comparação no switch funcione.
     */
    private String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "");
    }
}