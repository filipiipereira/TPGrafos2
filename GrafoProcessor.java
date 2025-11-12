import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

// Classe auxiliar para retornar a matriz e o valor de p
class DadosGrafo {
    public final int[][] matrizDistancias;
    public final int k;

    public DadosGrafo(int[][] matrizDistancias, int k) {
        this.matrizDistancias = matrizDistancias;
        this.k = k;
    }
}

public class GrafoProcessor {

    private static final int INFINITO = Integer.MAX_VALUE / 2;

    public DadosGrafo processarDados(String nomeArquivo) throws IOException {
        int numVertices;
        int numArestas;
        int k_centros; 
        
        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            
            String primeiraLinha = br.readLine();
            if (primeiraLinha == null) {
                throw new IOException("Arquivo vazio ou formato inválido.");
            }
            StringTokenizer st = new StringTokenizer(primeiraLinha);
            numVertices = Integer.parseInt(st.nextToken());
            numArestas = Integer.parseInt(st.nextToken());
            k_centros = Integer.parseInt(st.nextToken());

            //System.out.println("--- Parâmetros Lidos ---");
            //System.out.println("Vértices (n): " + numVertices);
            //System.out.println("Arestas Iniciais: " + numArestas);
            //System.out.println("Centros (k): " + k_centros);
            //System.out.println("------------------------");

            int[][] custos = new int[numVertices + 1][numVertices + 1]; 

            for (int i = 1; i <= numVertices; i++) {
                Arrays.fill(custos[i], INFINITO);
                custos[i][i] = 0;
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                
                st = new StringTokenizer(linha);
                try {
                    int i = Integer.parseInt(st.nextToken());
                    int j = Integer.parseInt(st.nextToken());
                    int k = Integer.parseInt(st.nextToken());

                    custos[i][j] = k;
                    custos[j][i] = k;
                } catch (NumberFormatException | java.util.NoSuchElementException e) {
                    System.err.println("Aviso: Linha de aresta inválida ignorada.");
                }
            }

            // --- Algoritmo de Floyd-Warshall ---
            for (int k = 1; k <= numVertices; k++) {
                for (int i = 1; i <= numVertices; i++) {
                    for (int j = 1; j <= numVertices; j++) {
                        if (custos[i][k] != INFINITO && custos[k][j] != INFINITO) {
                            if (custos[i][j] > custos[i][k] + custos[k][j]) {
                                custos[i][j] = custos[i][k] + custos[k][j];
                            }
                        }
                    }
                }
            }
            
            return new DadosGrafo(custos, k_centros); // Retorna a matriz e o valor de p

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + nomeArquivo + " | " + e.getMessage());
            throw e;
        }
    }
}