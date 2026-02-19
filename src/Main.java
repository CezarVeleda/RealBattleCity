import javax.swing.JFrame;

public class Main {
    public static void main(String[] args) {
        JFrame janela = new JFrame();
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setResizable(false); // isso aqui é o motivo do porquê não da para redimensionar a tela
        janela.setTitle("Battle City - POO");

        PainelJogo painel = new PainelJogo();
        janela.add(painel);

        janela.pack(); // Ajusta a janela ao tamanho do painel
        janela.setLocationRelativeTo(null); // Centraliza na tela
        janela.setVisible(true);

        painel.iniciarTela(); // Começa o loop de desenho
    }
}