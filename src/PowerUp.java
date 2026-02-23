import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

// NOTA PARA APRESENTAÇÃO (Herança para Colisão):
// PowerUp herda de Rectangle para podermos usar o método intersects() facilmente com o Tanque
public class PowerUp extends Rectangle {

    // Constantes dos tipos de itens
    public static final int TIPO_VIDA = 1;
    public static final int TIPO_BOMBA = 2;

    public int tipo;
    public boolean ativo = true;

    public PowerUp(int x, int y, int tipo) {
        // O tamanho é 30x30 (um pouco menor que o tanque que é 40x40)
        super(x, y, 30, 30);
        this.tipo = tipo;
    }

    public void desenhar(Graphics g) {
        if (!ativo) return;

        if (tipo == TIPO_VIDA) {
            g.setColor(Color.PINK); // Fundo Rosa para Vida
            g.fillRect(x, y, width, height);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("♥", x + 5, y + 22); // Desenha um coraçãozinho
        }
        else if (tipo == TIPO_BOMBA) {
            g.setColor(Color.ORANGE); // Fundo Laranja para Bomba
            g.fillRect(x, y, width, height);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("B", x + 7, y + 22); // B de Bomba
        }

        // Bordinha branca para dar um charme de "item brilhante"
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }
}
