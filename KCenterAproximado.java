import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KCenterAproximado {

    private final int[][] distancias;
    private final int N; // Número total de vértices
    private final int K; // Número de centros a serem escolhidos (k)
    
    // Constante para representar a não alocação inicial, usando um valor seguro.
    private static final int INF = Integer.MAX_VALUE / 2;

    public KCenterAproximado(int[][] matrizDistancias, int k) {
        this.N = matrizDistancias.length - 1; 
        this.K = k;
        this.distancias = matrizDistancias;
    }

    /**
     * Implementa o Algoritmo Guloso de Gonzalez para o Problema dos k-Centros.
     * Seleciona K centros que maximizam a distância em cada passo.
     * * @return O Raio da Solução (distância máxima) aproximada.
     */
    public Resultado encontrarSolucaoAproximada() {
        if (K > N || N <= 0) {
            System.err.println("Erro: Parâmetros de N e K inválidos.");
            return null;
        }


        // Conjunto C: Armazena os vértices escolhidos como centros.
        List<Integer> centrosEscolhidos = new ArrayList<>();
        
        // Array para rastrear a distância de cada vértice i até o centro mais próximo (em C).
        int[] distanciasMinimas = new int[N + 1];
        Arrays.fill(distanciasMinimas, INF);

        // --- Passo 1: Escolher o Primeiro Centro Arbitrariamente (e.g., Vértice 1) ---
        int centroInicial = 1; 
        centrosEscolhidos.add(centroInicial);
        
        // Atualiza as distâncias mínimas para o primeiro centro
        for (int i = 1; i <= N; i++) {
            distanciasMinimas[i] = distancias[i][centroInicial];
        }
        

        // --- Loop Principal: Selecionar k-1 centros restantes ---
        for (int k_iter = 1; k_iter < K; k_iter++) {
            
            int proximoCentro = -1;
            int maxDistanciaAtual = -1; // Rastreia o ponto mais distante
            
            // Itera por todos os vértices para encontrar o ponto 'i' que está
            // mais distante de QUALQUER centro já escolhido (o ponto mais mal-servido).
            for (int i = 1; i <= N; i++) {
                
                // Se o ponto i está mais distante do que o 'maxDistanciaAtual', 
                // ele se torna o novo centro na próxima iteração.
                if (distanciasMinimas[i] > maxDistanciaAtual) {
                    maxDistanciaAtual = distanciasMinimas[i];
                    proximoCentro = i;
                }
            }
            
            // Se proximoCentro for o próprio centro mais distante, e ele ainda não foi escolhido...
            if (proximoCentro != -1) {
                centrosEscolhidos.add(proximoCentro);
                
                // Atualizar as distâncias mínimas para a próxima iteração
                for (int i = 1; i <= N; i++) {
                    distanciasMinimas[i] = Math.min(distanciasMinimas[i], distancias[i][proximoCentro]);
                }
                
            }
        }
        
        // --- Cálculo do Raio Final ---
        // O raio é simplesmente a maior distância mínima encontrada na última iteração.
        // Já temos o maxDistanciaAtual do último loop, mas vamos calcular de novo para garantir.
        int raioFinal = 0;
        for (int i = 1; i <= N; i++) {
            raioFinal = Math.max(raioFinal, distanciasMinimas[i]);
        }

        return new Resultado(raioFinal, centrosEscolhidos);
    }
}