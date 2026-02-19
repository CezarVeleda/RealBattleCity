import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bloco extends Rectangle {

    // Tipos de blocos
    public static final int VAZIO = 0;
    public static final int TIJOLO = 1; // Destrutível
    public static final int ACO = 2;    // Indestrutível
    public static final int AGUA = 3;   // Não anda, mas tiro passa
    public static final int BASE = 4;   // A águia que temos que proteger
    // La nos nossos mapas.txt esses serão os números que vamos usar para definir onde será agua, aço, etc.


    public int tipo;
    Color cor;

    public Bloco(int x, int y, int largura, int altura, int tipo) {
        // Inicializa o retangulo (x, y, width, height) da classe pai Rectangle, isso facilita la na colisão
        super(x, y, largura, altura);
        this.tipo = tipo;
        defineCor();
    }

    private void defineCor() {
        switch (tipo) {
            case TIJOLO:
                cor = new Color(184, 115, 51); // Cor de tijolo (cobre/marrom)
                break;
            case ACO:
                cor = Color.GRAY;
                break;
            case AGUA:
                cor = Color.BLUE;
                break;
            case BASE:
                cor = Color.YELLOW; // Temporário, depois pode ser imagem
                break;
            default:
                cor = Color.BLACK; // Vazio
        }
    }

    public void desenhar(Graphics g) {
        if (tipo == VAZIO) return; // Se for vazio, não desenha nada

        g.setColor(cor);
        g.fillRect(x, y, width, height);

        // Um detalhe visual para parecer tijolo (borda preta)
        if (tipo == TIJOLO || tipo == ACO) {
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, height);
        }
    }
}