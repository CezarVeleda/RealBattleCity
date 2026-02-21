import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame{

    private JTextField campoNome;

    public MenuPrincipal(){
        //configurações básicas da janela do menu;

        setTitle("Battle City -  Menu Inicial");
        setSize(400,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centralizar na tela

        // Usamos um GridLayout para empilhar os botões bonitinhos (5 linhas, 1 coluna)
        setLayout(new GridLayout(5,1,10,10));

        // Titulo do jogo

        JLabel titulo = new JLabel("Battle City - POO ", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 24));
        add(titulo);

        // Campo para digitar o nome

        JPanel painelNome = new JPanel();
        painelNome.add(new JLabel("Seu Nome:"));
        campoNome = new JTextField(15);
        painelNome.add(campoNome);
        add(painelNome);

        // Botoes essenciais

        JButton btnJogar = new JButton("1 - Jogar");
        JButton btnRanking = new JButton("2 - Ranking");
        JButton btnSair = new JButton("3 - Sair");

        add(btnJogar);
        add(btnRanking);
        add(btnSair);

        // --- AÇÕES DOS BOTÕES ---

        // Botão Sair: Encerra o programa
        btnSair.addActionListener(e -> System.exit(0));

        // Botão Ranking:
        btnRanking.addActionListener(e -> {
            String textoRanking = GerenciadorArquivo.lerRanking();
            JOptionPane.showMessageDialog(this, textoRanking, "Top 10 Jogadores", JOptionPane.INFORMATION_MESSAGE);
        });
        // Botão Jogar: Valida o nome e abre o jogo
        btnJogar.addActionListener(e -> {
            String nomeJogador = campoNome.getText().trim();

            if (nomeJogador.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, digite seu nome para jogar!");
                return; // Impede o jogo de abrir sem nome
            }

            // Esconde o menu e chama o jogo
            this.dispose();
            iniciarJogo(nomeJogador);
        });

    }

    // Metodo que faz exatamente o que o Main fazia antes, que é chamar os mapas etc, o Main vai chamar Menu Principal
    private void iniciarJogo(String nome) {
        JFrame janelaJogo = new JFrame();
        janelaJogo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaJogo.setResizable(false);
        janelaJogo.setTitle("Battle City - Jogador: " + nome); // Mostra o nome na barra

        PainelJogo painel = new PainelJogo(nome);
        janelaJogo.add(painel);

        janelaJogo.pack();
        janelaJogo.setLocationRelativeTo(null);
        janelaJogo.setVisible(true);

        painel.iniciarTela();
    }
}
