import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class Projetil implements I_Movimento, Runnable {

    private int x;
    private int y;
    private int velocidade = 8; // O tiro é mais rápido que o tanque
    private Direcao direcao;
    private boolean ativo = true; // Diz se o tiro ainda existe ou se já bateu

    // NOTA PARA APRESENTAÇÃO: A Thread independente do projétil exigida no trabalho
    private Thread threadProjetil;

    // O tiro precisa conhecer o mapa para saber se bateu numa parede
    private List<Bloco> mapa;

    // NOVAS VARIÁVEIS PARA O COMBATE
    private boolean tiroDoPlayer;
    private List<Tanque> inimigos; // A lista de alvos que este tiro pode acertar
    private Player jogador; // Adicionado para o tiro inimigo saber onde o player está

    // Construtor
    public Projetil(int x, int y, Direcao direcao, List<Bloco> mapa, boolean tiroDoPlayer, List<Tanque> inimigos, Player jogador) {
        // Ajustamos o X e Y para o tiro sair do "meio" do tanque (que tem 40x40),
        // como o tiro vai ter 10x10, somamos 15 para centralizar.
        this.x = x + 15;
        this.y = y + 15;
        this.direcao = direcao;
        this.mapa = mapa;
        this.tiroDoPlayer = tiroDoPlayer;
        this.inimigos = inimigos;
        this.jogador = jogador;

        // Inicia a vida própria (Thread) do tiro assim que ele é criado
        threadProjetil = new Thread(this);
        threadProjetil.start();
    }

    @Override
    public void run() {
        while (ativo) {
            mover();
            checarColisao();

            try {
                Thread.sleep(20); // Velocidade de atualização do tiro (freio da animação)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean mover() {
        if (!ativo) return false;

        switch (direcao) {
            case CIMA: this.y -= velocidade; break;
            case BAIXO: this.y += velocidade; break;
            case ESQUERDA: this.x -= velocidade; break;
            case DIREITA: this.x += velocidade; break;
        }
        return true;
    }

    // NOTA PARA APRESENTAÇÃO (Colisão do Tiro):
    // Semelhante ao tanque, mas o tiro DESTRÓI o bloco de Tijolo e para no Aço.
    private void checarColisao() {
        // 1. Checa se saiu da tela (Some se sair da área 600x520)
        if (x < 0 || x > 600 || y < 0 || y > 520) {
            ativo = false;
            return;
        }

        // Cria o retângulo do tiro (10x10 pixels) para checar impacto
        Rectangle areaTiro = new Rectangle(x, y, 10, 10);

        // 2. Checa colisão com os blocos físicos
        if (mapa != null) {
            for (Bloco bloco : mapa) {
                // Se não for vazio e não for água (o tiro passa voando por cima da água)
                if (bloco.tipo != Bloco.VAZIO && bloco.tipo != Bloco.AGUA) {
                    if (areaTiro.intersects(bloco)) {
                        ativo = false; // O tiro morre ao bater (a Thread encerra)

                        // Lógica de destruição do cenário (CORRIGIDA PARA DESTRUIR A BASE TAMBÉM)
                        if (bloco.tipo == Bloco.TIJOLO || bloco.tipo == Bloco.BASE) {
                            bloco.tipo = Bloco.VAZIO; // O tijolo/base é destruído e vira chão vazio!
                        }
                        return; // Já bateu, sai da checagem
                    }
                }
            }
        }

        // 3. NOTA PARA APRESENTAÇÃO (Colisão Tiro -> Tanque Inimigo)
        if (tiroDoPlayer && inimigos != null) {
            for (int i = 0; i < inimigos.size(); i++) {
                Tanque inimigo = inimigos.get(i);
                Rectangle hitboxInimigo = new Rectangle(inimigo.get_x(), inimigo.get_y(), 40, 40);

                // Se a caixa do tiro bater na caixa do inimigo
                if (areaTiro.intersects(hitboxInimigo)) {
                    ativo = false; // O tiro some ao acertar
                    inimigo.levar_dano(); // O inimigo perde vida

                    // Se a vida zerou (Inimigo_Rapido tem só 1 de vida, então morre na hora)
                    if (inimigo.isMorto()) {
                        inimigos.remove(i); // Remove ele da lista da tela
                        jogador.adicionarPontos(100);
                    }
                    return; // Sai para o tiro não varar e acertar dois de uma vez
                }
            }
        }

        // 4. NOTA PARA APRESENTAÇÃO (Colisão Tiro Inimigo -> Player)
        if (!tiroDoPlayer && jogador != null && !jogador.isMorto()) {
            Rectangle hitboxPlayer = new Rectangle(jogador.get_x(), jogador.get_y(), 40, 40);
            if (areaTiro.intersects(hitboxPlayer)) {
                ativo = false; // Tiro some
                jogador.levar_dano(); // Tira uma vida do Player
                return;
            }
        }
    }

    // Metodo para a tela desenhar o tiro
    public void desenhar(Graphics g) {
        if (ativo) {
            // Se for do jogador, o tiro é Azul, se for do inimigo, o tiro é Vermelho
            if (tiroDoPlayer) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.RED);
            }
            g.fillOval(x, y, 10, 10); // Desenha uma bolinha de 10x10 pixels
        }
    }

    public boolean isAtivo() {
        return ativo;
    }

    // Métodos obrigatórios da interface I_Movimento
    @Override public void set_direcao(Direcao novaDirecao) { this.direcao = novaDirecao; }
    @Override public int get_x() { return this.x; }
    @Override public int get_y() { return this.y; }
}