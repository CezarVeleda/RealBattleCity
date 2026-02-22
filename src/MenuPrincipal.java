import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame{

    private JTextField campoNome;
    // NOTA PARA APRESENTAÇÃO: JComboBox são listas suspensas que limitam as opções do utilizador
    private JComboBox<String> comboDificuldade;
    private JComboBox<String> comboMapa;

    public MenuPrincipal(){
        //configurações básicas da janela do menu;

        setTitle("Battle City -  Menu Inicial");
        setSize(400,400); // Aumentado para caber as opções
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centralizar na tela

        // Usamos um GridLayout para empilhar os botões bonitinhos (agora 7 linhas, 1 coluna)
        setLayout(new GridLayout(7,1,10,10));

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

        // Caixa de Escolha da Dificuldade
        JPanel painelDificuldade = new JPanel();
        painelDificuldade.add(new JLabel("Dificuldade:"));
        String[] opcoesDificuldade = {"Fácil", "Média", "Difícil"};
        comboDificuldade = new JComboBox<>(opcoesDificuldade);
        comboDificuldade.setSelectedIndex(1); // Deixa "Média" marcada por padrão
        painelDificuldade.add(comboDificuldade);
        add(painelDificuldade);

        // Caixa de Escolha do Mapa
        JPanel painelMapa = new JPanel();
        painelMapa.add(new JLabel("Mapa:"));
        String[] opcoesMapa = {"Mapa 1", "Mapa 2", "Mapa 3"};
        comboMapa = new JComboBox<>(opcoesMapa);
        painelMapa.add(comboMapa);
        add(painelMapa);

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

            String dificuldadeEscolhida = (String) comboDificuldade.getSelectedItem();
            String mapaEscolhido = (String) comboMapa.getSelectedItem();

            // Esconde o menu e chama o jogo
            this.dispose();
            iniciarJogo(nomeJogador, dificuldadeEscolhida, mapaEscolhido);
        });

    }

    // Metodo que faz exatamente o que o Main fazia antes, que é chamar os mapas etc, o Main vai chamar Menu Principal
    private void iniciarJogo(String nome, String dificuldade, String mapa) {
        JFrame janelaJogo = new JFrame();
        janelaJogo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janelaJogo.setResizable(false);
        janelaJogo.setTitle("Battle City - Jogador: " + nome + " | Dificuldade: " + dificuldade); // Mostra o nome na barra

        PainelJogo painel = new PainelJogo(nome, dificuldade, mapa);
        janelaJogo.add(painel);

        janelaJogo.pack();
        janelaJogo.setLocationRelativeTo(null);
        janelaJogo.setVisible(true);

        painel.iniciarTela();
        painel.requestFocusInWindow(); // OBRIGA O TECLADO A OLHAR PRO JOGO
    }
}