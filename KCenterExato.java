import java.util.ArrayList;
import java.util.List;

public class KCenterExato {

    private final int[][] distancias;
    private final int N; // Número total de vértices
    private final int K; // Número de centros a serem escolhidos (k)
    
    // Variáveis para rastrear a melhor solução
    private int raioMinimo = Integer.MAX_VALUE;
    private List<Integer> melhorCentros = new ArrayList<>();

    public KCenterExato(int[][] matrizDistancias, int k) {
        // A matriz é indexada a partir de 1, então o tamanho é length - 1
        this.N = matrizDistancias.length - 1; 
        this.K = k;
        this.distancias = matrizDistancias;
    }
    
    /**
     * Inicia o processo de enumeração exaustiva para encontrar o Raio Ótimo.
     * @return O raio ótimo (mínimo da distância máxima) encontrado.
     */
    public int encontrarSolucaoExata() {
        if (K > N) {
            System.err.println("Erro: k não pode ser maior que o número de vértices N.");
            return -1;
        }

        System.out.println("\n--- Iniciando Solução Exata k-Centros (Enumeração) ---");
        
        // Chama a função recursiva para gerar combinações
        encontrarCombustaoRecursiva(1, new ArrayList<>());
        
        System.out.println("--- Enumeração Concluída ---");
        System.out.println("Centros Ótimos: " + melhorCentros);
        System.out.println("Raio Mínimo Ótimo (Distância Máxima): " + raioMinimo);
        
        return raioMinimo;
    }

    private void encontrarCombustaoRecursiva(int proximoCandidato, List<Integer> centrosAtuais) {
        
        // 1. Caso Base: Se já selecionamos K centros
        if (centrosAtuais.size() == K) {
            // Calculamos o Raio (Custo Máximo) para esta combinação
            int raioAtual = calcularRaioKCentros(centrosAtuais);

            // Verificamos se é a melhor solução (menor raio) encontrada até agora
            if (raioAtual < raioMinimo) {
                raioMinimo = raioAtual;
                melhorCentros = new ArrayList<>(centrosAtuais); // Clonar a lista para salvar
            }
            return;
        }

        // 2. Condição de Poda (Pruning): Se não há vértices suficientes para completar K
        if (proximoCandidato > N || centrosAtuais.size() + (N - proximoCandidato + 1) < K) {
            return;
        }

        // 3. Passo Recursivo: 
        
        // OPÇÃO A: Incluir o vértice 'proximoCandidato' como centro
        centrosAtuais.add(proximoCandidato);
        encontrarCombustaoRecursiva(proximoCandidato + 1, centrosAtuais);
        
        // Backtracking
        centrosAtuais.remove(centrosAtuais.size() - 1); 

        // OPÇÃO B: Não incluir o vértice 'proximoCandidato' como centro
        encontrarCombustaoRecursiva(proximoCandidato + 1, centrosAtuais);
    }
    
    /**
     * Calcula o Raio da Solução (Distância Máxima) para um dado conjunto de centros.
     * Raio = Max( para todo i em V, da min distância de i até um centro em C )
     * @param centros O conjunto C de centros escolhidos.
     * @return O Raio da Solução.
     */
    private int calcularRaioKCentros(List<Integer> centros) {
        int maxDistancia = 0; // O Raio da Solução
        
        // Percorre todos os vértices de demanda (i = 1 até N)
        for (int i = 1; i <= N; i++) {
            int minDistancia = Integer.MAX_VALUE;
            
            // Encontra o centro mais próximo para o vértice i
            for (int j : centros) {
                if (distancias[i][j] < minDistancia) {
                    minDistancia = distancias[i][j];
                }
            }
            
            // O Raio é a MAIOR (Máximo) das distâncias mínimas
            if (minDistancia > maxDistancia) {
                maxDistancia = minDistancia;
            }
        }
        return maxDistancia;
    }
}