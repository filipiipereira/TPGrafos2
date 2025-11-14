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

        // Leitura do gabarito
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

        
        // SOLUÇÃO APROXIMADA
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

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%%%n",
                        arquivo, raio, tempoMs, erro);

                // --- ESCREVER RESULTADOS APROXIMADOS ---
                bwAprox.write(String.format("%s,%d,%.3f,%.3f,%s\n",
                        arquivo, raio, tempoMs, erro, centros));
                bwAprox.flush();

            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);

                raiosAprox.add(-1);
                temposAprox.add(-1.0);
                errosAprox.add(-1.0);
                centrosAprox.add(new ArrayList<>());

                try {
                    bwAprox.write(String.format("%s,-1,-1,-1,[]\n", arquivo));
                    bwAprox.flush();
                } catch (IOException ignored) {}
            }
        }

        // SOLUÇÃO EXATA
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

                System.out.printf(" %s -> Raio = %d | Tempo = %.3f ms | Erro = %.2f%%%n",
                        arquivo, raio, tempoMs, erro);

                // --- ESCREVER RESULTADOS EXATOS ---
                bwExato.write(String.format("%s,%d,%.3f,%.3f,%s\n",
                        arquivo, raio, tempoMs, erro, centros));
                bwExato.flush();

            } catch (IOException e) {
                System.err.printf(" ERRO ao processar %s%n", arquivo);

                raiosExato.add(-1);
                temposExato.add(-1.0);
                errosExato.add(-1.0);
                centrosExato.add(new ArrayList<>());

                try {
                    bwExato.write(String.format("%s,-1,-1,-1,[]\n", arquivo));
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

    private static double calcularErroPercentual(int valor, int gabarito) {
        if (gabarito == 0) return 0.0;
        return Math.abs((valor - gabarito) / (double) gabarito) * 100.0;
    }
}
