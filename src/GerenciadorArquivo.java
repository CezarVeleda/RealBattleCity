import java.io.*;

public class GerenciadorArquivo {

    private static final String NOME_ARQUIVO = "ranking.txt";

    // NOTA PARA APRESENTAÇÃO (Exceções e Persistência):
    // Usamos FileWriter e try/catch para gravar os dados sem quebrar o jogo se der erro.
    public static void salvarPontuacao(String nome, int pontos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOME_ARQUIVO, true))) {
            writer.write(nome + " - " + pontos);
            writer.newLine(); // Pula para a próxima linha
        } catch (IOException e) {
            System.out.println("Erro ao salvar no arquivo: " + e.getMessage());
        }
    }

    // Lê o arquivo e retorna o texto formatado para o Menu exibir
    public static String lerRanking() {
        StringBuilder ranking = new StringBuilder();

        // Verifica se o arquivo já existe antes de tentar ler
        File arquivo = new File(NOME_ARQUIVO);
        if (!arquivo.exists()) {
            return "Nenhuma pontuação registrada ainda!";
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(NOME_ARQUIVO))) {
            String linha;
            int contador = 1;
            while ((linha = reader.readLine()) != null && contador <= 10) { // Limita aos 10 melhores
                ranking.append(contador).append(". ").append(linha).append("\n");
                contador++;
            }
        } catch (IOException e) {
            return "Erro ao ler o ranking.";
        }

        return ranking.toString();
    }
}