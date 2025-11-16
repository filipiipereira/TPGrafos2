import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Classe auxiliar que armazena a matriz de distâncias calculada
 * e o número K de centros para o problema dos k-centros.
 */
class DadosGrafo {

    /** Matriz de distâncias entre vértices após Floyd-Warshall. */
    public final int[][] matrizDistancias;

    /** Quantidade de centros (k) definida no arquivo de entrada. */
    public final int k;

    /**
     * Construtor da classe DadosGrafo.
     *
     * @param matrizDistancias matriz com todas as distâncias minimizadas.
     * @param k número de centros a serem utilizados.
     */
    public DadosGrafo(int[][] matrizDistancias, int k) {
        this.matrizDistancias = matrizDistancias;
        this.k = k;
    }
}

/**
 * Classe responsável por processar o arquivo de entrada contendo
 * um grafo e calcular a matriz de distâncias usando Floyd-Warshall.
 */
public class GrafoProcessor {

    /** Valor utilizado para representar distância infinita. */
    private static final int INFINITO = Integer.MAX_VALUE / 2;

    /**
     * Lê um arquivo contendo o número de vértices, arestas e valor de k,
     * constrói a matriz de distâncias e aplica o algoritmo de Floyd-Warshall.
     *
     * @param nomeArquivo caminho do arquivo de entrada.
     * @return objeto {@link DadosGrafo} contendo a matriz de distâncias final e o valor de k.
     * @throws IOException caso o arquivo não exista, esteja vazio ou seja inválido.
     */
    public DadosGrafo processarDados(String nomeArquivo) throws IOException {
        int numVertices;
        int numArestas;
        int k_centros;

        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {

            // --- Leitura da primeira linha: V, E e K ---
            String primeiraLinha = br.readLine();
            if (primeiraLinha == null) {
                throw new IOException("Arquivo vazio ou formato inválido.");
            }

            StringTokenizer st = new StringTokenizer(primeiraLinha);
            numVertices = Integer.parseInt(st.nextToken());
            numArestas = Integer.parseInt(st.nextToken());
            k_centros = Integer.parseInt(st.nextToken());

            // Matriz de distâncias (1-indexada)
            int[][] dist = new int[numVertices + 1][numVertices + 1];

            // Inicialização da matriz com INFINITO e diagonal com 0
            for (int i = 1; i <= numVertices; i++) {
                Arrays.fill(dist[i], INFINITO);
                dist[i][i] = 0;
            }

            // --- Leitura das arestas ---
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

            // Retorna matriz final de distâncias e o k solicitado
            return new DadosGrafo(dist, k_centros);

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + nomeArquivo + " | " + e.getMessage());
            throw e;
        }
    }
}
