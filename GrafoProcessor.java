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


            int[][] dist = new int[numVertices + 1][numVertices + 1]; 

            for (int i = 1; i <= numVertices; i++) {
                Arrays.fill(dist[i], INFINITO);
                dist[i][i] = 0;
            }

            String linha;
            while ((linha = br.readLine()) != null) {
                if (linha.trim().isEmpty()) continue;
                
                st = new StringTokenizer(linha);
                try {
                    int i = Integer.parseInt(st.nextToken());
                    int j = Integer.parseInt(st.nextToken());
                    int k = Integer.parseInt(st.nextToken());

                    dist[i][j] = k;
                    dist[j][i] = k;
                } catch (NumberFormatException | java.util.NoSuchElementException e) {
                    System.err.println("Aviso: Linha de aresta inválida ignorada.");
                }
            }

            // --- Algoritmo de Floyd-Warshall ---
            for (int k = 1; k <= numVertices; k++) {
                for (int i = 1; i <= numVertices; i++) {
                    for (int j = 1; j <= numVertices; j++) {
                            if (dist[i][j] > dist[i][k] + dist[k][j]) {
                                dist[i][j] = dist[i][k] + dist[k][j];
                            }
                        
                    }
                }
            }
            
            return new DadosGrafo(dist, k_centros); // Retorna a matriz e o valor de p

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + nomeArquivo + " | " + e.getMessage());
            throw e;
        }
    }
}