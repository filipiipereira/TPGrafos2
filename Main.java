import java.io.IOException;

public class Main {
    // --- Classe principal para teste  ---
    public static void main(String[] args) {
        String arquivoTeste = "pmed1.txt"; 
        
        System.out.println("Iniciando processamento para o arquivo: " + arquivoTeste);
        
        try {
            GrafoProcessor processor = new GrafoProcessor();
            
            // 1. Pré-processamento
            // ... (Medição de tempo do Pré-processamento) ...
            DadosGrafo dados = processor.processarDados(arquivoTeste);
            // ... (Impressão do tempo do Pré-processamento) ...
            
            int k = dados.k; 
            int n = dados.matrizDistancias.length - 1;
            int raioOtimo = -1;
            double tempoExatoKCenterMs = -1;
            
            System.out.println("\n--- MEDINDO TEMPO DA SOLUÇÃO EXATA k-CENTROS ---");
            long inicioExato = System.nanoTime();
            
            KCenterExato KCenterExato = new KCenterExato(dados.matrizDistancias, k);
            raioOtimo = KCenterExato.encontrarSolucaoExata(); 
            
            long fimExato = System.nanoTime();
            tempoExatoKCenterMs = (fimExato - inicioExato) / 1_000_000.0;

            // 3. Solução Aproximada k-Centros (Gonzalez)
            System.out.println("\n--- MEDINDO TEMPO DA SOLUÇÃO APROXIMADA k-CENTROS ---");
            long inicioAproximado = System.nanoTime();
            
            KCenterAproximado KCenterAprox = new KCenterAproximado(dados.matrizDistancias, k);
            int raioAproximado = KCenterAprox.encontrarSolucaoAproximada(); 
            
            long fimAproximado = System.nanoTime();
            double tempoAproximadoKCenterMs = (fimAproximado - inicioAproximado) / 1_000_000.0;

            // 4. Relatório de Desempenho
            System.out.println("\n=======================================================");
            System.out.println("      RELATÓRIO DE DESEMPENHO (k-Centros)");
            System.out.println("=======================================================");
            System.out.printf("Instância: %s (N=%d, K=%d)\n", arquivoTeste, n, k);
            System.out.println("-------------------------------------------------------");
            
            if (tempoExatoKCenterMs != -1) {
                double erroAprox = (double) (raioAproximado - raioOtimo) / raioOtimo * 100.0;
                
                System.out.printf(" EXATO (Enumeração): Raio = %d | Tempo = %.3f ms\n", raioOtimo, tempoExatoKCenterMs);
                System.out.printf(" APROXIMADO (Gonzalez): Raio = %d | Tempo = %.3f ms | Erro: %.2f%%\n", raioAproximado, tempoAproximadoKCenterMs, erroAprox);
            } else {
                 System.out.printf(" APROXIMADO (Gonzalez): Raio = %d | Tempo = %.3f ms\n", raioAproximado, tempoAproximadoKCenterMs);
            }
            System.out.println("=======================================================");
            
        } catch (IOException e) {
            System.err.println("\nERRO: Processamento falhou. Verifique o caminho/nome do arquivo.");
            e.printStackTrace();
        }
    }
}
