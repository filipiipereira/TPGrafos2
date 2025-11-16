import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implementação do algoritmo aproximado de Gonzalez para o
 * Problema dos k-Centros.
 *
 * O objetivo é escolher K centros de forma que a maior distância
 * de qualquer vértice ao centro mais próximo seja minimizada.
 * O algoritmo é guloso e oferece uma solução aproximada de fator 2.
 */
public class KCenterAproximado {

    // Matriz de distâncias entre os vértices (1-indexada). 
    private final int[][] distancias;

    // Número total de vértices do grafo.
    private final int N;

    // Quantidade de centros a serem escolhidos (K).
    private final int K;

    // Valor utilizado para inicialização das distâncias mínimas.
    private static final int INF = Integer.MAX_VALUE / 2;

    /**
     * Construtor da classe.
     *
     * @param matrizDistancias matriz NxN com as distâncias mínimas entre vértices.
     *                         A matriz deve ser 1-indexada (posição 0 ignorada).
     * @param k quantidade de centros a serem escolhidos.
     */
    public KCenterAproximado(int[][] matrizDistancias, int k) {
        this.N = matrizDistancias.length - 1;
        this.K = k;
        this.distancias = matrizDistancias;
    }

    /**
     * Executa o algoritmo guloso de Gonzalez para encontrar uma solução
     * aproximada para o Problema dos k-Centros.
     *
     * O algoritmo funciona da seguinte forma:
     *   -Seleciona arbitrariamente um primeiro centro (vértice 1).
     *   -Para cada iteração, escolhe-se o vértice mais distante do conjunto atual de centros.
     *   -Atualiza-se as distâncias mínimas de cada vértice ao centro mais próximo.
     *
     * @return objeto Resultado contendo:
     *             - O raio aproximado da solução.
     *             - A lista de centros escolhidos.
     *         Retorna {@code null} se os parâmetros forem inválidos (ex.: K > N).
     */
    public Resultado encontrarSolucaoAproximada() {
        if (K > N || N <= 0) {
            System.err.println("Erro: Parâmetros de N e K inválidos.");
            return null;
        }

        // Lista de vértices escolhidos como centros
        List<Integer> centrosEscolhidos = new ArrayList<>();

        // Distância mínima de cada vértice ao centro mais próximo
        int[] distanciasMinimas = new int[N + 1];
        Arrays.fill(distanciasMinimas, INF);

        // --- Passo 1: Escolha arbitrária do primeiro centro ---
        int centroInicial = 1;
        centrosEscolhidos.add(centroInicial);

        // Atualiza distâncias mínimas com base no primeiro centro
        for (int i = 1; i <= N; i++) {
            distanciasMinimas[i] = distancias[i][centroInicial];
        }

        // --- Seleção gulosa dos próximos K-1 centros ---
        for (int k_iter = 1; k_iter < K; k_iter++) {

            int proximoCentro = -1;
            int maxDistanciaAtual = -1;

            // Encontra o vértice mais distante do conjunto de centros já escolhido
            for (int i = 1; i <= N; i++) {
                if (distanciasMinimas[i] > maxDistanciaAtual) {
                    maxDistanciaAtual = distanciasMinimas[i];
                    proximoCentro = i;
                }
            }

            // Adiciona o novo centro, caso válido
            if (proximoCentro != -1) {
                centrosEscolhidos.add(proximoCentro);

                // Atualiza as distâncias mínimas para este novo centro
                for (int i = 1; i <= N; i++) {
                    distanciasMinimas[i] =
                            Math.min(distanciasMinimas[i], distancias[i][proximoCentro]);
                }
            }
        }

        // --- Cálculo do raio final ---
        int raioFinal = 0;
        for (int i = 1; i <= N; i++) {
            raioFinal = Math.max(raioFinal, distanciasMinimas[i]);
        }

        return new Resultado(raioFinal, centrosEscolhidos);
    }
}
