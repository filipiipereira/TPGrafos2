import java.util.ArrayList;
import java.util.List;

/**
 * Implementação exata do Problema dos k-Centros utilizando
 * enumeração completa (força bruta com poda).
 *
 * O algoritmo avalia todas as combinações possíveis de K centros
 * dentre N vértices, calculando o raio de cada combinação.
 * O menor raio é considerado a solução ótima.
 *
 * Esta implementação utiliza poda simples para reduzir o espaço
 * de busca quando não é mais possível completar um conjunto de centros.
 */
public class KCenterExato {

    // Matriz de distâncias entre os vértices (1-indexada). 
    private final int[][] distancias;

    // Número total de vértices do grafo.
    private final int N;

    // Número de centros a serem selecionados (K).
    private final int K;

    // Menor raio encontrado até o momento durante a enumeração.
    private int raioMinimo = Integer.MAX_VALUE;

    // Lista de centros correspondente ao menor raio encontrado.
    private List<Integer> melhorCentros = new ArrayList<>();

    /**
     * Construtor da classe.
     *
     * @param matrizDistancias matriz NxN com as distâncias já minimizadas
     *                         (geralmente obtidas pelo Floyd-Warshall).
     *                         Deve ser 1-indexada: posição 0 ignorada.
     * @param k número de centros que devem ser escolhidos.
     */
    public KCenterExato(int[][] matrizDistancias, int k) {
        this.N = matrizDistancias.length - 1;
        this.K = k;
        this.distancias = matrizDistancias;
    }

    /**
     * Inicia o processo de busca exata da solução ótima
     * do Problema dos k-Centros.
     *
     * A função gera todas as combinações possíveis de K centros,
     * calcula o raio de cada combinação e escolhe a combinação com
     * menor raio (distância máxima).
     *
     * @return objeto {@link Resultado} contendo o raio ótimo e os centros
     *         selecionados; retorna {@code null} caso K seja maior que N.
     */
    public Resultado encontrarSolucaoExata() {
        if (K > N) {
            System.err.println("Erro: k não pode ser maior que o número de vértices N.");
            return null;
        }

        // Inicia a enumeração recursiva de centros
        encontrarCombinacaoRecursiva(1, new ArrayList<>());

        return new Resultado(raioMinimo, melhorCentros);
    }

    /**
     * Gera combinações de vértices para selecionar K centros,
     * utilizando recursão e poda.
     *
     * @param proximoCandidato vértice atual sendo considerado (1..N)
     * @param centrosAtuais lista parcial dos centros escolhidos até o momento
     */
    private void encontrarCombinacaoRecursiva(int proximoCandidato, List<Integer> centrosAtuais) {

        // ---- Caso Base: já selecionamos K centros ----
        if (centrosAtuais.size() == K) {
            int raioAtual = calcularRaioKCentros(centrosAtuais);

            // Atualiza a melhor solução encontrada
            if (raioAtual < raioMinimo) {
                raioMinimo = raioAtual;
                melhorCentros = new ArrayList<>(centrosAtuais);
            }
            return;
        }

        // ---- Poda: não há vértices restantes suficientes para completar K ----
        if (proximoCandidato > N ||
            centrosAtuais.size() + (N - proximoCandidato + 1) < K) {
            return;
        }

        // ---- OPÇÃO A: incluir este vértice como centro ----
        centrosAtuais.add(proximoCandidato);
        encontrarCombinacaoRecursiva(proximoCandidato + 1, centrosAtuais);

        // Backtracking
        centrosAtuais.remove(centrosAtuais.size() - 1);

        // ---- OPÇÃO B: não incluir este vértice ----
        encontrarCombinacaoRecursiva(proximoCandidato + 1, centrosAtuais);
    }

    /**
     * Calcula o raio da solução para um conjunto específico de centros.
     *
     * O raio é definido como:
     * Raio = max para cada vértice i ( min distância entre i e qualquer centro j )
     *
     * @param centros lista de vértices escolhidos como centros.
     * @return o raio correspondente à solução testada.
     */
    private int calcularRaioKCentros(List<Integer> centros) {
        int maxDistancia = 0;

        // Para cada vértice i, encontra a menor distância até os centros
        for (int i = 1; i <= N; i++) {
            int minDistancia = Integer.MAX_VALUE;

            for (int centro : centros) {
                minDistancia = Math.min(minDistancia, distancias[i][centro]);
            }

            maxDistancia = Math.max(maxDistancia, minDistancia);
        }
        return maxDistancia;
    }
}
