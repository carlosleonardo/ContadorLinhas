package org.serpro;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

public class ContadorLinhas {
    public static void main(String[] args) {
        // Mostrar a ajuda do programa com commons-cli se não for passado
        // nenhum argumento
        var analisador = new DefaultParser();
        var opcoes = new Options();
        opcoes.addOption("h", "help", false, "Mostra essa ajuda");
        opcoes.addOption("v", "versao", false, "Mostra a versão do programa");
    }
}
