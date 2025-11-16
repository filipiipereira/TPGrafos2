import java.io.*;
import java.util.*;

/**
 * Classe principal responsável pela execução dos experimentos do Problema dos k-Centros,
 * aplicando tanto o algoritmo aproximado de Gonzalez quanto o método exato por enumeração.
 *
 * <p>O programa processa 40 instâncias no formato "pmedX.txt", compara os resultados
 * com os valores ótimos presentes no arquivo "gab.txt" e exporta os resultados em dois
 * arquivos: {@code resultados_aprox.txt} e {@code resultados_exato.txt}.</p>
 *
 * <p>Cada linha dos arquivos de saída contém: Instância, Raio obtido, Tempo de execução,
 * Erro percentual em relação ao gabarito e os centros escolhidos.</p>
 */
public class Main {

    /**
     * Método principal. Controla toda a leitura das instâncias, processamento dos algoritmos,
     * cálculo dos erros e geração dos arquivos de saída.
     *
     * @param args não utilizado.
     */
    public static void main(String[] args) {

        // Lista de nomes de arquivos das 40 instâncias
        List<String> arquivos = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            arquivos.add("pmed" + i + ".txt");
        }

        GrafoProcessor processor = new GrafoProcessor();

        // ===================== LEITURA DO GABARITO =====================
        List<Integer> gabarito = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("gab.txt"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                gabarito.add(Integer.parseInt(linha.trim()));
            }
        } catch (IOException e) {
            System.err.println("ERRO ao ler gab.txt. Certifique-se de que o arquivo existe e tem 40 linhas.");
            return;
        }

        // Arquivos de saída
        BufferedWriter bwAprox, bwExato;
        try {
            bwAprox = new BufferedWriter(new FileWriter("resultados_aprox.txt"));
            bwExato = new BufferedWriter(new FileWriter("resultados_exato.txt"));

            bwAprox.write("Instancia,Raio,Tempo_ms,Erro_pct,Centros\n");
            bwExato.write("Instancia,Raio,Tempo_ms,Erro_pct,Centros\n");

            bwAprox.flush();
            bwExato.flush();
        } catch (IOException e) {
            System.err.println("ERRO ao abrir arquivos de resultados.");
            return;
        }

        // ===================== SOLUÇÃO APROXIMADA =====================
        System.out.println("\n===============================================");
        System.out.println(" SOLUÇÃO APROXIMADA (Gonzalez)");
        System.out.println("===============================================");

        for (int i = 0; i < arquivos.size(); i++) {
            String arquivo = arquivos.get(i);

            try {
                DadosGrafo dados = processor.processarDados(arquivo);
                int k = dados.k;

                long inicio = System.nanoTime();
                KCenterAproximado KCenterAprox = new KCenterAproximado(dados.matrizDistancias, k);
                Resultado resultadoAprox = KCenterAprox.encontrarSolucaoAproximada();
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                int raio = (resultadoAprox != null) ? resultadoAprox.raio : -1;
                List<Integer> centros = (resultadoAprox != null) ? resultadoAprox.centros : new ArrayList<>();
                double erro = (raio >= 0) ? calcularErroPercentual(raio, gabarito.get(i)) : -1.0;

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%%%n",
                        arquivo, raio, tempoMs, erro);

                bwAprox.write(String.format(Locale.US, "%s,%d,%.3f,%.3f,%s\n",
                        arquivo, raio, tempoMs, erro, centros));
                bwAprox.flush();

            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);

                try {
                    bwAprox.write(String.format(Locale.US, "%s,-1,-1,-1,[]\n", arquivo));
                    bwAprox.flush();
                } catch (IOException ignored) {}
            }
        }

        // ===================== SOLUÇÃO EXATA =====================
        System.out.println("\n===============================================");
        System.out.println(" SOLUÇÃO EXATA (Enumeração Completa)");
        System.out.println("===============================================");

        for (int i = 0; i < arquivos.size(); i++) {
            String arquivo = arquivos.get(i);

            try {
                DadosGrafo dados = processor.processarDados(arquivo);
                int k = dados.k;

                long inicio = System.nanoTime();
                KCenterExato KCenterExato = new KCenterExato(dados.matrizDistancias, k);
                Resultado resultadoExato = KCenterExato.encontrarSolucaoExata();
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                int raio = (resultadoExato != null) ? resultadoExato.raio : -1;
                List<Integer> centros = (resultadoExato != null) ? resultadoExato.centros : new ArrayList<>();
                double erro = (raio >= 0) ? calcularErroPercentual(raio, gabarito.get(i)) : -1.0;

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%%%n",
                        arquivo, raio, tempoMs, erro);

                bwExato.write(String.format(Locale.US, "%s,%d,%.3f,%.3f,%s\n",
                        arquivo, raio, tempoMs, erro, centros));
                bwExato.flush();

            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);

                try {
                    bwExato.write(String.format(Locale.US, "%s,-1,-1,-1,[]\n", arquivo));
                    bwExato.flush();
                } catch (IOException ignored) {}
            }
        }

        // FECHAR ARQUIVOS
        try {
            bwAprox.close();
            bwExato.close();
        } catch (IOException e) {
            System.err.println("ERRO ao fechar arquivos de resultados.");
        }

        System.out.println("\nArquivos resultados_aprox.txt e resultados_exato.txt salvos com sucesso.");
    }

    /**
     * Calcula o erro percentual entre um valor obtido e seu valor ótimo.
     *
     * <p>Erro(%) = |valor - gabarito| / gabarito * 100</p>
     *
     * @param valor valor obtido pelo algoritmo.
     * @param gabarito valor ótimo esperado.
     * @return erro percentual; retorna 0 se o gabarito for zero.
     */
    private static double calcularErroPercentual(int valor, int gabarito) {
        if (gabarito == 0) return 0.0;
        return Math.abs((valor - gabarito) / (double) gabarito) * 100.0;
    }
}
