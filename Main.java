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
        List<List<Integer>> centrosAprox = new ArrayList<>();

        List<Integer> raiosExato = new ArrayList<>();
        List<Double> temposExato = new ArrayList<>();
        List<Double> errosExato = new ArrayList<>();
        List<List<Integer>> centrosExato = new ArrayList<>();

        // Leitura do gabarito (gab.txt)
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

        
        // SOLUÇÃO APROXIMADA (Gonzalez)
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

                raiosAprox.add(raio);
                temposAprox.add(tempoMs);
                errosAprox.add(erro);
                centrosAprox.add(centros);

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%% | Centros = %s%n",
                    arquivo, raio, tempoMs, erro, centros);
            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);
                raiosAprox.add(-1);
                temposAprox.add(-1.0);
                errosAprox.add(-1.0);
                centrosAprox.add(new ArrayList<>());
            }
        }

        // SOLUÇÃO EXATA (Enumeração Completa)
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

                raiosExato.add(raio);
                temposExato.add(tempoMs);
                errosExato.add(erro);
                centrosExato.add(centros);

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%% | Centros = %s%n",
                    arquivo, raio, tempoMs, erro, centros);
            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);
                raiosExato.add(-1);
                temposExato.add(-1.0);
                errosExato.add(-1.0);
                centrosExato.add(new ArrayList<>());
            }
        }

        // RELATÓRIO FINAL
        System.out.println("\n=======================================================");
        System.out.println(" RELATÓRIO FINAL DE DESEMPENHO (k-Centros)");
        System.out.println("=======================================================");
        System.out.printf("%-10s | %-15s | %-15s | %-15s | %-15s%n",
                "Instância", "Aprox (ms)", "Aprox Erro (%)", "Exato (ms)", "Exato Erro (%)");
        System.out.println("--------------------------------------------------------------------------");

        for (int i = 0; i < arquivos.size(); i++) {
            System.out.printf("%-10s | %10.3f | %10.2f | %10.3f | %10.2f%n",
                    arquivos.get(i),
                    temposAprox.size() > i ? temposAprox.get(i) : -1.0,
                    errosAprox.size() > i ? errosAprox.get(i) : -1.0,
                    temposExato.size() > i ? temposExato.get(i) : -1.0,
                    errosExato.size() > i ? errosExato.get(i) : -1.0);
        }

        // Escrever resultados em arquivo
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("resultados.txt"))) {
            bw.write("Instancia,Aprox_Raio,Aprox_Tempo_ms,Aprox_Erro_pct,Aprox_Centros,Exato_Raio,Exato_Tempo_ms,Exato_Erro_pct,Exato_Centros\n");
            for (int i = 0; i < arquivos.size(); i++) {
                String inst = arquivos.get(i);
                int raioA = raiosAprox.size() > i ? raiosAprox.get(i) : -1;
                double tempoA = temposAprox.size() > i ? temposAprox.get(i) : -1.0;
                double erroA = errosAprox.size() > i ? errosAprox.get(i) : -1.0;
                List<Integer> cA = centrosAprox.size() > i ? centrosAprox.get(i) : new ArrayList<>();

                int raioE = raiosExato.size() > i ? raiosExato.get(i) : -1;
                double tempoE = temposExato.size() > i ? temposExato.get(i) : -1.0;
                double erroE = errosExato.size() > i ? errosExato.get(i) : -1.0;
                List<Integer> cE = centrosExato.size() > i ? centrosExato.get(i) : new ArrayList<>();

                bw.write(String.format("%s,%d,%.3f,%.3f,%s,%d,%.3f,%.3f,%s\n",
                        inst,
                        raioA,
                        tempoA,
                        erroA,
                        cA.toString(),
                        raioE,
                        tempoE,
                        erroE,
                        cE.toString()));
            }
        } catch (IOException e) {
            System.err.println("ERRO ao gravar resultados.txt: " + e.getMessage());
        }
    }

    private static double calcularErroPercentual(int valor, int gabarito) {
        if (gabarito == 0) return 0.0;
        return Math.abs((valor - gabarito) / (double) gabarito) * 100.0;
    }
}
