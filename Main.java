import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<String> arquivos = new ArrayList<>();
        for (int i = 1; i <= 40; i++) {
            arquivos.add("pmed" + i + ".txt");
        }

        GrafoProcessor processor = new GrafoProcessor();

        // Listas de resultados
        List<Integer> raiosAprox = new ArrayList<>();
        List<Double> temposAprox = new ArrayList<>();
        List<Double> errosAprox = new ArrayList<>();

        List<Integer> raiosOtim = new ArrayList<>();
        List<Double> temposOtim = new ArrayList<>();
        List<Double> errosOtim = new ArrayList<>();

        List<Integer> raiosExato = new ArrayList<>();
        List<Double> temposExato = new ArrayList<>();

        // ===============================
        // Leitura do gabarito (gab.txt)
        // ===============================
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

        // ============================================
        // ETAPA 1: SOLUÇÃO APROXIMADA (Gonzalez)
        // ============================================
        System.out.println("\n===============================================");
        System.out.println(" ETAPA 1: SOLUÇÃO APROXIMADA (Gonzalez)");
        System.out.println("===============================================");

        for (int i = 0; i < arquivos.size(); i++) {
            String arquivo = arquivos.get(i);
            try {
                DadosGrafo dados = processor.processarDados(arquivo);
                int k = dados.k;

                long inicio = System.nanoTime();
                KCenterAproximado KCenterAprox = new KCenterAproximado(dados.matrizDistancias, k);
                int raio = KCenterAprox.encontrarSolucaoAproximada();
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                double erro = calcularErroPercentual(raio, gabarito.get(i));

                raiosAprox.add(raio);
                temposAprox.add(tempoMs);
                errosAprox.add(erro);

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%%%n", arquivo, raio, tempoMs, erro);
            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);
                raiosAprox.add(-1);
                temposAprox.add(-1.0);
                errosAprox.add(-1.0);
            }
        }

        // ============================================
        // ETAPA 2: SOLUÇÃO EXATA OTIMIZADA
        // ============================================
        System.out.println("\n===============================================");
        System.out.println(" ETAPA 2: SOLUÇÃO EXATA OTIMIZADA");
        System.out.println("===============================================");

        boolean erroMemoria = false;
        for (int i = 0; i < arquivos.size(); i++) {
            if (erroMemoria) {
                System.out.println(" Execução interrompida por falta de memória. Pulando para o Exato completo...");
                break;
            }

            String arquivo = arquivos.get(i);
            try {
                DadosGrafo dados = processor.processarDados(arquivo);
                int k = dados.k;

                long inicio = System.nanoTime();
                KCenterExatoOtim KCenterExatoOtim = new KCenterExatoOtim(dados.matrizDistancias, k);
                Result res = KCenterExatoOtim.encontrarSolucaoExata();
                int raio = res.radius;
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;
                double erro = calcularErroPercentual(raio, gabarito.get(i));

                raiosOtim.add(raio);
                temposOtim.add(tempoMs);
                errosOtim.add(erro);

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%%%n", arquivo, raio, tempoMs, erro);
            } catch (OutOfMemoryError e) {
                System.err.println(" OutOfMemoryError detectado! Abortando etapa Exata Otimizada...");
                erroMemoria = true;
                break;
            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);
                raiosOtim.add(-1);
                temposOtim.add(-1.0);
                errosOtim.add(-1.0);
            }
        }

        // ============================================
        // ETAPA 3: SOLUÇÃO EXATA (Enumeração Completa)
        // ============================================
        System.out.println("\n===============================================");
        System.out.println(" ETAPA 3: SOLUÇÃO EXATA (Enumeração Completa)");
        System.out.println("===============================================");

        for (int i = 0; i < arquivos.size(); i++) {
            String arquivo = arquivos.get(i);
            try {
                DadosGrafo dados = processor.processarDados(arquivo);
                int k = dados.k;

                long inicio = System.nanoTime();
                KCenterExato KCenterExato = new KCenterExato(dados.matrizDistancias, k);
                int raio = KCenterExato.encontrarSolucaoExata();
                long fim = System.nanoTime();

                double tempoMs = (fim - inicio) / 1_000_000.0;

                raiosExato.add(raio);
                temposExato.add(tempoMs);

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms%n", arquivo, raio, tempoMs);
            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);
                raiosExato.add(-1);
                temposExato.add(-1.0);
            }
        }

        // ============================================
        // RELATÓRIO FINAL
        // ============================================
        System.out.println("\n=======================================================");
        System.out.println(" RELATÓRIO FINAL DE DESEMPENHO (k-Centros)");
        System.out.println("=======================================================");
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s%n",
                "Instância", "Aprox (ms)", "Aprox Erro (%)", "ExatoOtim (ms)", "ExatoOtim Erro (%)");
        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < arquivos.size(); i++) {
            System.out.printf("%-10s | %10.3f | %10.2f | %10.3f | %10.2f%n",
                    arquivos.get(i),
                    temposAprox.size() > i ? temposAprox.get(i) : -1,
                    errosAprox.size() > i ? errosAprox.get(i) : -1,
                    temposOtim.size() > i ? temposOtim.get(i) : -1,
                    errosOtim.size() > i ? errosOtim.get(i) : -1);
        }

        System.out.println("=======================================================");
        System.out.println(" Execução concluída com sucesso!");
        System.out.println("=======================================================");
    }

    private static double calcularErroPercentual(int valor, int gabarito) {
        if (gabarito == 0) return 0.0;
        return Math.abs((valor - gabarito) / (double) gabarito) * 100.0;
    }
}
