import javax.swing.JPanel;
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
    // NOTA PARA APRESENTAÇÃO: Nascer nos múltiplos de 40 evita que o tanque inicie preso na parede.
    Player jogador = new Player(160, 480); // posição de nascimento lembra?

    // NOTA PARA APRESENTAÇÃO (Uso de Coleções / Gerenciador de Inimigos):
    // Em vez de um inimigo fixo, usamos uma lista para controlar múltiplos inimigos.
    List<Tanque> inimigos = new ArrayList<>();

    private String nomeJogador;
    private boolean rankingSalvo = false; // Impede de salvar o mesmo Game Over 60x por segundo
    // NOTA PARA APRESENTAÇÃO (Estado de Jogo):
    // Variável que controla se o jogo ainda está a decorrer ou se a base foi destruída.
    private boolean gameOver = false;

    public PainelJogo(String nomeJogador) {
        this.nomeJogador = nomeJogador;

        this.setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        this.setBackground(Color.BLACK); // Fundo preto clássico
        this.setDoubleBuffered(true); // Melhora a renderização

        this.setFocusable(true); // Permite que o painel "tenha foco" para receber teclas
        this.addKeyListener(new TecladoAdapter()); // Pluga o "fio" do teclado

        carregarMapa();

        // GERANDO INIMIGOS (Spawn)
        // Adicionamos 3 inimigos rápidos no topo do mapa em colunas diferentes
        inimigos.add(new Inimigo_Rapido(0, 0));
        inimigos.add(new Inimigo_Rapido(280, 0));
        inimigos.add(new Inimigo_Rapido(560, 0));

        // NOTA PARA APRESENTAÇÃO (Injeção de Dependência em Massa):
        // Após carregar o mapa, entregamos a planta do cenário e a direção para todos os inimigos.
        // Gambiarra temporária: Dar uma direção para o inimigo ver ele andando sozinho
        for (Tanque ini : inimigos) {
            ini.setMapa(blocos);
            ini.set_direcao(Direcao.BAIXO);
        }

        jogador.setMapa(blocos);
    }

    public void iniciarTela() { //auto explicativo né ?
        threadTela = new Thread(this);
        threadTela.start();
    }

    public void run() {
        /* Loop simples para redesenhar a tela a cada 17ms (~60 FPS), ou seja, temos o tanque em si sendo movime
        ntando dentro
        memória com a posição atualizada, mas AQUI a janela atualiza (se redesenha) e pega a nova posição
         */
        while (threadTela != null) {
            if (!gameOver) {
                verificarEstadoDoJogo(); // Se o jogo não acabou, verifica se a base ainda existe
            }
            repaint(); // Chama o paintComponent
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // NOTA PARA APRESENTAÇÃO (Lógica de Verificação - Game Loop):
    // O painel percorre os blocos e verifica estados do jogo (Base e Inimigos atirando).
    private void verificarEstadoDoJogo() {
        // 1. Verifica se a base ainda está viva
        boolean baseViva = false;
        for (Bloco bloco : blocos) {
            if (bloco.tipo == Bloco.BASE) {
                baseViva = true;
                break;
            }
        }

        // 2. Verifica se o Player perdeu todas as vidas
        if (!baseViva || jogador.isMorto()) {
            this.gameOver = true;

            // SÓ SALVA UMA VEZ E É QUANDO MORRE
            if (!rankingSalvo) {
                GerenciadorArquivo.salvarPontuacao(nomeJogador, jogador.getPontuacao());
                rankingSalvo = true;
            }
        }

        // 3. IA Atiradora: Lê se os inimigos querem atirar
        for (Tanque ini : inimigos) {
            if (ini.prontoParaAtirar) {
                // Cria o tiro do inimigo (tiroDoPlayer = false), e passamos o jogador para o tiro saber quem acertar
                Projetil tiroInimigo = new Projetil(ini.get_x(), ini.get_y(), ini.getUltimaDirecao(), blocos, false, inimigos, jogador);
                tiros.add(tiroInimigo);
                ini.prontoParaAtirar = false; // Reseta a vontade do inimigo para ele não atirar infinitamente
            }
        }
    }

    // É aqui que a mágica visual acontece
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // NOTA PARA APRESENTAÇÃO (HUD - Head-Up Display):
        // Desenhando o placar na tela usando os dados encapsulados do Player e da lista de Inimigos.
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("Jogador: " + nomeJogador, 10, 20);
        g.drawString("Vidas: " + Math.max(0, jogador.getVida()), 10, 40); // Math.max evita mostrar vida "-1"
        g.drawString("Pontos: " + jogador.getPontuacao(), 10, 60);
        g.drawString("Inimigos Restantes: " + inimigos.size(), LARGURA_TELA - 170, 20);

        // 1. Desenhamos o Mapa PRIMEIRO (para ficar no fundo)
        for(Bloco bloco : blocos){
            bloco.desenhar(g);
        }

        // Desenhar o Jogador (Cor Verde) apenas se estiver vivo
        if (!jogador.isMorto()) {
            g.setColor(Color.GREEN);
            g.fillRect(jogador.get_x(), jogador.get_y(), 40, 40);
        }

        // NOTA PARA APRESENTAÇÃO (Renderização de Múltiplos Inimigos):
        // Percorremos a lista desenhando apenas os inimigos que ainda estão vivos.
        g.setColor(Color.RED);
        for (Tanque ini : inimigos) {
            g.fillRect(ini.get_x(), ini.get_y(), 40, 40);
        }

        // NOTA PARA APRESENTAÇÃO (Renderização de Tiros):
        // Desenhamos todos os tiros que estão ativos na tela.
        for (int i = 0; i < tiros.size(); i++) {
            Projetil tiro = tiros.get(i);
            if (tiro.isAtivo()) {
                tiro.desenhar(g);
            } else {
                tiros.remove(i); // Remove da memória se o tiro já bateu em algo
                i--; // Ajusta o índice da lista após a remoção
            }
        }

        // NOTA PARA APRESENTAÇÃO (Interface de Fim de Jogo):
        // Se a variável gameOver for verdadeira, sobrepomos um texto gigante no ecrã.
        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 60)); // Fonte gigante e em negrito
            // Matemática simples para centrar o texto no meio da janela
            g.drawString("GAME OVER", LARGURA_TELA / 2 - 190, ALTURA_TELA / 2);
        }
    }

    // Metodo que transforma os números da matriz em objetos visuais
    public void carregarMapa() {
        int[][] mapa = Mapas.getMapaFase1(); // Pega a matriz da outra classe

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
            // Se for Game Over, o jogador perde o controlo do tanque (ignora as teclas)
            if (gameOver) return;

            int codigoTecla = e.getKeyCode();

            // Passamos a ordem para o jogador
            if (codigoTecla == KeyEvent.VK_W || codigoTecla == KeyEvent.VK_UP) {
                jogador.set_direcao(Direcao.CIMA);
            }
            if (codigoTecla == KeyEvent.VK_S || codigoTecla == KeyEvent.VK_DOWN) {
                jogador.set_direcao(Direcao.BAIXO);
            }
            if (codigoTecla == KeyEvent.VK_A || codigoTecla == KeyEvent.VK_LEFT) {
                jogador.set_direcao(Direcao.ESQUERDA);
            }
            if (codigoTecla == KeyEvent.VK_D || codigoTecla == KeyEvent.VK_RIGHT) {
                jogador.set_direcao(Direcao.DIREITA);
            }

            // NOTA PARA APRESENTAÇÃO (Ação de Atirar com Memória de Estado):
            if (codigoTecla == KeyEvent.VK_SPACE) {
                // Em vez de olhar para a direcao_atual (que fica nula ao parar),
                // o tiro agora pede a última direção que ficou gravada na memória do tanque.
                Direcao direcaoTiro = jogador.getUltimaDirecao();

                // Agora passamos 'true' (para indicar que é tiro do jogador), passamos a lista de inimigos, E o jogador
                Projetil novoTiro = new Projetil(jogador.get_x(), jogador.get_y(), direcaoTiro, blocos, true, inimigos, jogador);
                tiros.add(novoTiro);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (gameOver) return;
            // Quando soltar qualquer tecla, definimos a direção como NULL
            // Isso fará o 'if(direcao == null) return' lá no Tanque funcionar e ele para.
            jogador.set_direcao(null);
        }
    }
}