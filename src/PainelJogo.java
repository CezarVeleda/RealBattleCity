import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class PainelJogo extends JPanel implements Runnable{ // Runnable aqui para atualizar a TELA
    // Definições de tamanho da tela (Grid 15x13 como sugere o PDF)
    final int TAMANHO_BLOCO = 40;
    final int LARGURA_TELA = 15 * TAMANHO_BLOCO;
    final int ALTURA_TELA = 13 * TAMANHO_BLOCO;
    /* o pc entende em pixels, logo 40x40 pixels, ai imagine que temos UM bloco de 40x40 pixels, precisamos que a nossa
    tela tenha 15 blocos de largura por 13 de altura, isso vem la do pdf */

    // Lista que vai guardar todas as paredes do jogo
    List<Bloco> blocos = new ArrayList<>();
    // NOTA PARA APRESENTAÇÃO: Lista para guardar os tiros que estão voando na tela no momento
    List<Projetil> tiros = new ArrayList<>();

    Thread threadTela; // Thread só para redesenhar a imagem (FPS)

    // criando NA TELA os tanques alinhados com o grid (Múltiplos de 40)
    Player jogador = new Player(160, 480); // posição de nascimento lembra?

    // NOTA PARA APRESENTAÇÃO (Uso de Coleções / Gerenciador de Inimigos):
    List<Tanque> inimigos = new ArrayList<>();

    private boolean gameOver = false;

    // NOVAS VARIÁVEIS: Controlo de Fases, Persistência e Pause
    private int faseAtual = 1;
    private int quantidadeInimigosBase = 3;
    private String nomeJogador;
    private String mapaEscolhido;
    private boolean pontuacaoSalva = false;

    public static boolean isPaused = false;

    // Construtor atualizado
    public PainelJogo(String nomeJogador, String dificuldade, String mapaEscolhido) {
        this.nomeJogador = nomeJogador;
        this.mapaEscolhido = mapaEscolhido;

        this.setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        this.setBackground(Color.BLACK); // Fundo preto clássico
        this.setDoubleBuffered(true); // Melhora a renderização

        this.setFocusable(true); // Permite que o painel "tenha foco" para receber teclas
        this.addKeyListener(new TecladoAdapter()); // Pluga o "fio" do teclado

        if (dificuldade.equals("Fácil")) { quantidadeInimigosBase = 2; }
        else if (dificuldade.equals("Média")) { quantidadeInimigosBase = 3; }
        else if (dificuldade.equals("Difícil")) { quantidadeInimigosBase = 5; }

        carregarFase();
    }

    public void carregarFase() {
        blocos.clear();
        tiros.clear();
        inimigos.clear();

        carregarMapa();

        jogador.setPosicao(160, 480);
        jogador.setMapa(blocos);
        jogador.set_direcao(null);

        for (int i = 0; i < quantidadeInimigosBase; i++) {
            int spawnX = (i % 3) * 280;
            Inimigo_Rapido inimigo = new Inimigo_Rapido(spawnX, 0);
            inimigo.setMapa(blocos);
            inimigo.set_direcao(Direcao.BAIXO);
            inimigos.add(inimigo);
        }
    }

    public void iniciarTela() { //auto explicativo né ?
        threadTela = new Thread(this);
        threadTela.start();
        isPaused = false;
    }

    public void run() {
        /* Loop simples para redesenhar a tela a cada 17ms (~60 FPS), ou seja, temos o tanque em si sendo movime
        ntando dentro
        memória com a posição atualizada, mas AQUI a janela atualiza (se redesenha) e pega a nova posição
         */
        while (threadTela != null) {
            if (!isPaused) {
                verificarEstadoDoJogo();
            }
            repaint(); // Chama o paintComponent

            try { Thread.sleep(17); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
    }

    private void verificarEstadoDoJogo() {
        if (gameOver) return;

        boolean baseViva = false;
        for (Bloco bloco : blocos) {
            if (bloco.tipo == Bloco.BASE) {
                baseViva = true;
                break;
            }
        }

        if (!baseViva || jogador.isMorto()) {
            this.gameOver = true;
            if (!pontuacaoSalva) {
                GerenciadorArquivo.salvarPontuacao(nomeJogador, jogador.getPontuacao());
                pontuacaoSalva = true;
            }
        }

        if (inimigos.isEmpty() && !gameOver) {
            faseAtual++;
            quantidadeInimigosBase = (int) Math.ceil(quantidadeInimigosBase * 1.3);
            carregarFase();
        }

        for (Tanque ini : inimigos) {
            if (ini.prontoParaAtirar) {
                Projetil tiroInimigo = new Projetil(ini.get_x(), ini.get_y(), ini.getUltimaDirecao(), blocos, false, inimigos, jogador);
                tiros.add(tiroInimigo);
                ini.prontoParaAtirar = false;
            }
        }
    }

    private void encerrarPartidaEVoltarAoMenu() {
        this.gameOver = true;
        this.threadTela = null;
        PainelJogo.isPaused = false;

        jogador.forcarParada();
        for (Tanque ini : inimigos) { ini.forcarParada(); }
        for (Projetil tiro : tiros) { tiro.forcarParada(); }

        JFrame janelaAtual = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (janelaAtual != null) { janelaAtual.dispose(); }
        new MenuPrincipal().setVisible(true);
    }

    // É aqui que a mágica visual acontece
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(Bloco bloco : blocos){ bloco.desenhar(g); }

        if (!jogador.isMorto()) {
            g.setColor(Color.GREEN);
            g.fillRect(jogador.get_x(), jogador.get_y(), 40, 40);
        }

        g.setColor(Color.RED);
        for (Tanque ini : inimigos) { g.fillRect(ini.get_x(), ini.get_y(), 40, 40); }

        for (int i = 0; i < tiros.size(); i++) {
            Projetil tiro = tiros.get(i);
            if (tiro.isAtivo()) { tiro.desenhar(g); }
            else { tiros.remove(i); i--; }
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Jogador: " + nomeJogador, 10, 20);
        g.drawString("Fase: " + faseAtual, 150, 20);
        g.drawString("Inimigos: " + inimigos.size(), 230, 20);
        g.drawString("Vidas: " + jogador.getVida(), 350, 20);
        g.drawString("Pontos: " + jogador.getPontuacao(), 450, 20);

        if (isPaused && !gameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, LARGURA_TELA, ALTURA_TELA);
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            g.drawString("PAUSADO", LARGURA_TELA / 2 - 130, ALTURA_TELA / 2 - 20);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Pressione ESC para Continuar", LARGURA_TELA / 2 - 145, ALTURA_TELA / 2 + 30);
            g.drawString("Pressione ENTER para Voltar ao Menu", LARGURA_TELA / 2 - 180, ALTURA_TELA / 2 + 60);
        }

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60));
            g.drawString("GAME OVER", LARGURA_TELA / 2 - 190, ALTURA_TELA / 2);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Pontuação guardada no Ranking!", LARGURA_TELA / 2 - 160, ALTURA_TELA / 2 + 50);
            g.drawString("Pressione ENTER para Voltar ao Menu", LARGURA_TELA / 2 - 180, ALTURA_TELA / 2 + 90);
        }
    }

    // Metodo que transforma os números da matriz em objetos visuais
    public void carregarMapa() {
        int[][] mapa;

        // NOTA PARA APRESENTAÇÃO (Seleção Dinâmica de Cenário):
        // O jogo avalia a string vinda do Menu e invoca o método correspondente da classe Mapas
        if (mapaEscolhido.equals("Mapa 2")) {
            mapa = Mapas.getMapaFase2(); // Carrega a matriz do Mapa 2
        } else if (mapaEscolhido.equals("Mapa 3")) {
            mapa = Mapas.getMapaFase3(); // Carrega a matriz do Mapa 3
        } else {
            mapa = Mapas.getMapaFase1(); // Pega a matriz da fase padrão
        }

        for (int linha = 0; linha < 13; linha++) {
            for (int coluna = 0; coluna < 15; coluna++) {
                int tipoBloco = mapa[linha][coluna];

                if (tipoBloco != Bloco.VAZIO) {
                    // Cria o bloco na posição correta (Coluna * 40, Linha * 40)
                    blocos.add(new Bloco(coluna * TAMANHO_BLOCO, linha * TAMANHO_BLOCO, TAMANHO_BLOCO, TAMANHO_BLOCO, tipoBloco));
                }
            }
        }
    }

    // Classe interna para ouvir o teclado
    public class TecladoAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int codigoTecla = e.getKeyCode();

            if (codigoTecla == KeyEvent.VK_ENTER) {
                if (gameOver || isPaused) {
                    encerrarPartidaEVoltarAoMenu();
                    return;
                }
            }

            if (codigoTecla == KeyEvent.VK_ESCAPE || codigoTecla == KeyEvent.VK_P) {
                if (!gameOver) { isPaused = !isPaused; }
                return;
            }

            // Se o jogo acabou ou tá pausado, ignora as teclas de movimento e tiro
            if (gameOver || isPaused) return;

            // Passamos a ordem para o jogador
            if (codigoTecla == KeyEvent.VK_W || codigoTecla == KeyEvent.VK_UP) { jogador.set_direcao(Direcao.CIMA); }
            if (codigoTecla == KeyEvent.VK_S || codigoTecla == KeyEvent.VK_DOWN) { jogador.set_direcao(Direcao.BAIXO); }
            if (codigoTecla == KeyEvent.VK_A || codigoTecla == KeyEvent.VK_LEFT) { jogador.set_direcao(Direcao.ESQUERDA); }
            if (codigoTecla == KeyEvent.VK_D || codigoTecla == KeyEvent.VK_RIGHT) { jogador.set_direcao(Direcao.DIREITA); }

            // NOTA PARA APRESENTAÇÃO (Ação de Atirar com Memória de Estado):
            if (codigoTecla == KeyEvent.VK_SPACE) {
                Direcao direcaoTiro = jogador.getUltimaDirecao();
                Projetil novoTiro = new Projetil(jogador.get_x(), jogador.get_y(), direcaoTiro, blocos, true, inimigos, jogador);
                tiros.add(novoTiro);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (gameOver || isPaused) return;
            // Quando soltar qualquer tecla, definimos a direção como NULL
            // Isso fará o 'if(direcao == null) return' lá no Tanque funcionar e ele para.
            jogador.set_direcao(null);
        }
    }
}