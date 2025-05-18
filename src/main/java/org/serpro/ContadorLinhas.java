package org.serpro;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class ContadorLinhas {
    private static final List<String> filtros = new ArrayList<>();
    private static int totalLinhas = 0;

    public static void main(String[] args) {
        // Mostrar a ajuda do programa com commons-cli se não for passado
        // nenhum argumento
        var analisador = new DefaultParser();
        var opcoes = new Options();
        opcoes.addOption("h", "help", false, "Mostra essa ajuda");
        opcoes.addOption("v", "versao", false, "Mostra a versão do programa");
        opcoes.addOption("d", "diretorio", true, "Contar linhas de código em um diretório");
        try {
            var cmd = analisador.parse(opcoes, args);
            leArquivoFiltros();
            processarComando(cmd, args);
            System.out.printf("Número total de linhas: %d%n", totalLinhas);
        } catch (ParseException e) {
            System.out.printf("Erro ao analisar os argumentos: %s%n", e.getMessage());
        }
    }

    private static void leArquivoFiltros() {
        // Lê o arquivo de filtros e retorna uma lista de extensões
        try (var scanner = new Scanner(new File("filtros.txt"))) {
            while (scanner.hasNextLine()) {
                String extensao = scanner.nextLine();
                filtros.add(extensao);
            }
        } catch (Exception e) {
            System.out.printf("Erro ao ler o arquivo de filtros: %s%n", e.getMessage());
        }

    }

    private static void processarComando(CommandLine cmd, String[] args) {
        if (cmd.hasOption("h") || args.length == 0) {
            System.out.println("Uso: java -jar ContadorLinhas [opções]");
            System.out.println("Opções:");
            System.out.println("  -h, --help      Mostra essa ajuda");
            System.out.println("  -v, --versao    Mostra a versão do programa");
            System.out.println("  -d, --diretorio <diretorio>   Contar linhas de código em um diretório");
            return;
        }

        if (cmd.hasOption("v")) {
            System.out.println("Contador de Linhas de Código - Versão 1.0");
            return;
        }

        if (cmd.hasOption("d")) {
            String diretorio = cmd.getOptionValue("d");
            System.out.printf("Contando linhas de código no diretório: %s%n", diretorio);
            buscarDiretorio(diretorio);
        }
    }

    private static void buscarDiretorio(String diretorio) {
        var itemDiretorio = new File(diretorio);
        if (!itemDiretorio.exists()) {
            System.out.println("Item informado não existe.");
            return;
        }
        if (!itemDiretorio.isDirectory()) {
            System.out.println("Item informado não é um diretório.");
            return;
        }
        // Inicia busca no diretório
        var topo = new Stack<File>();
        topo.push(itemDiretorio);

        // Enumera os diretórios e arquivos deste diretório informado
        while (!topo.isEmpty()) {
            var item = topo.pop();

            // Se for um diretório, adiciona os arquivos e diretórios
            // encontrados na pilha
            var subArquivos = item.listFiles();
            if (subArquivos != null) {
                for (var subItem : subArquivos) {
                    if (subItem.isDirectory()) {
                        topo.push(subItem);
                    } else {
                        contaLinhas(subItem);
                    }
                }
            }
        }
    }

    private static void contaLinhas(File subItem) {
        // Verifica se o arquivo é um arquivo de código
        if (subItem.isFile()) {
            String nomeArquivo = subItem.getName();
            if (arquivoValido(nomeArquivo)) {
                System.out.printf("Contando linhas do arquivo: %s%n", subItem.getAbsolutePath());

                try (var scanner = new Scanner(subItem)) {
                    int numeroLinhas = 0;
                    while (scanner.hasNextLine()) {
                        scanner.nextLine();
                        numeroLinhas++;
                    }
                    totalLinhas += numeroLinhas;
                    System.out.printf("Número de linhas: %d%n", numeroLinhas);
                } catch (Exception e) {
                    System.out.printf("Erro ao ler o arquivo: %s%n", e.getMessage());
                }
            }
        }

    }

    private static boolean arquivoValido(String nomeArquivo) {
        return filtros.stream().anyMatch(nomeArquivo::endsWith);
    }
}
