public class Player extends Tanque{
    private int pontuacao = 0;

    public Player(int x, int y) {
        super(x, y, 3, 3);
    }
    // Metodo para o tiro avisar que matou alguém
    public void adicionarPontos(int pontos) {
        this.pontuacao += pontos;
    }

    // Metodo para o Painel pegar os pontos na hora de Salvar ou mostrar na tela
    public int getPontuacao() {
        return this.pontuacao;
    }
}
