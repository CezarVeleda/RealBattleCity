import javax.swing.JPanel;
import java.awt.Color;
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
    Inimigo_Rapido inimigo = new Inimigo_Rapido(0, 0);


    public PainelJogo() {
        this.setPreferredSize(new Dimension(LARGURA_TELA, ALTURA_TELA));
        this.setBackground(Color.BLACK); // Fundo preto clássico
        this.setDoubleBuffered(true); // Melhora a renderização

        this.setFocusable(true); // Permite que o painel "tenha foco" para receber teclas
        this.addKeyListener(new TecladoAdapter()); // Pluga o "fio" do teclado

        // Gambiarra temporária: Dar uma direção para o inimigo ver ele andando sozinho
        inimigo.set_direcao(Direcao.BAIXO);

        carregarMapa();

        // NOTA PARA APRESENTAÇÃO (Injeção de Dependência):
        // Após carregar o mapa, entregamos a planta do cenário (lista de blocos) para os tanques.
        jogador.setMapa(blocos);
        inimigo.setMapa(blocos);
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
            repaint(); // Chama o paintComponent
            try {
                Thread.sleep(17);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // É aqui que a mágica visual acontece
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. Desenhamos o Mapa PRIMEIRO (para ficar no fundo)
        for(Bloco bloco : blocos){
            bloco.desenhar(g);
        }

        // Desenhar o Jogador (Cor Verde)
        g.setColor(Color.GREEN);
        g.fillRect(jogador.get_x(), jogador.get_y(), 40, 40);

        // Desenhar o Inimigo (Cor Vermelha)
        g.setColor(Color.RED);
        g.fillRect(inimigo.get_x(), inimigo.get_y(), 40, 40);

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

            // NOTA PARA APRESENTAÇÃO (Ação de Atirar):
            if (codigoTecla == KeyEvent.VK_SPACE) {
                // O tiro precisa saber de onde está saindo e para onde o jogador estava olhando.
                // Se o jogador estiver parado (direcao_atual == null), atira para CIMA por padrão.
                Direcao direcaoTiro = jogador.direcao_atual != null ? jogador.direcao_atual : Direcao.CIMA;

                // Cria um novo tiro (em sua própria Thread) e adiciona na lista da tela
                Projetil novoTiro = new Projetil(jogador.get_x(), jogador.get_y(), direcaoTiro, blocos);
                tiros.add(novoTiro);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            // Quando soltar qualquer tecla, definimos a direção como NULL
            // Isso fará o 'if(direcao == null) return' lá no Tanque funcionar e ele para.
            jogador.set_direcao(null);
        }
    }
}