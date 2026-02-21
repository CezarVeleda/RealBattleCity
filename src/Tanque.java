import java.awt.Rectangle;
import java.util.List;

public abstract class Tanque implements I_Movimento, Runnable{

    protected int x;
    protected int y;
    protected int vida;
    protected int velocidade;
    protected Direcao direcao_atual;

    //variáveis de thread
    protected boolean ligado = true;
    protected Thread threadTanque;

    // NOTA PARA APRESENTAÇÃO (POO - Associação):
    // O Tanque precisa conhecer o ambiente ao seu redor para não bater nas paredes.
    // Usamos 'protected' para que as classes filhas (Player e Inimigo) também enxerguem o mapa.
    protected List<Bloco> mapa;

    // (Memória de estado): O tanque nasce olhando para cima
    protected Direcao ultima_direcao = Direcao.CIMA;

    // (Gatilho da IA): O tanque avisa ao painel quando quer criar um tiro
    public boolean prontoParaAtirar = false;

    //construtor
    public Tanque(int x, int y, int vida, int velocidade) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.velocidade = velocidade;

        // assim que um tanque é construido inicia a thread dele
        threadTanque = new Thread(this);
        threadTanque.start();
    }

    // NOTA PARA APRESENTAÇÃO (POO - Encapsulamento):
    // Metodo 'setter' para injetar a lista de blocos no tanque de forma segura.
    public void setMapa(List<Bloco> mapa) {
        this.mapa = mapa;
    }

    // O metodo RUN é obrigatório pela interface Runnable, faz parte
    // É aqui que o tanque "vive"
    @Override
    public void run(){
        while(ligado){ // se o tanque for destruído o ligado fica em false e ele não se move mais

            mover(); // o tanque faz UM movimento em determinada direcao que a IA achou de bom tom fazer

            try{
                Thread.sleep(50); /* Dai ele para, estamos a simular um fps, basicamente ele anda ali em cima
                                            depois dele tentar andar ele vai fazer uma parada de 50 milissegundos
                                            para dormir, atento a palavra SIMULANDO! o fps quem controla é PainelJogo
                                            esse milissegundo é para o tanque inimigo não cruzar a tela voando porque
                                             ele faria mover() a cada nanossegundo */
            }
            catch(InterruptedException erro){ // burocracia do java, se alguém tentar matar a thread enquanto ela dorme apita um erro, basicamente implementamos PORQUE SIM!
                erro.printStackTrace();
            }
        }

    }

    // NOTA PARA APRESENTAÇÃO (Lógica de Colisão e Hitbox):
    // Este metodo é o "Sensor" do tanque. Ele prevê o futuro: "Se eu for para X e Y, eu bato?"
    private boolean podeMover(int proximoX, int proximoY) {
        // 1. Barreira invisível das bordas da tela (Evita sair da janela)
        if (proximoX < 0 || proximoX + 40 > 600 || proximoY < 0 || proximoY + 40 > 520) {
            return false; // Bateu na borda da janela
        }

        // 2. Barreira física (Tijolos, Aço e Água)
        if (mapa != null) {

            // NOTA PARA APRESENTAÇÃO (Ajuste de Hitbox):
            // Reduzimos a caixa de colisão em 6 pixels de cada lado ANTES de checar a colisão.
            // O tanque continua desenhado com 40x40, mas o "corpo físico" dele tem 28x28.
            // Isso permite que ele entre em corredores estreitos mesmo estando levemente desalinhado.
            int margem = 6;
            Rectangle areaTanqueFutura = new Rectangle(
                    proximoX + margem,
                    proximoY + margem,
                    40 - (margem * 2),
                    40 - (margem * 2)
            );

            for (Bloco bloco : mapa) {
                // NOTA PARA APRESENTAÇÃO (POO - Herança na Prática):
                // Como a classe Bloco "extends Rectangle", herdamos o metodo intersects() do próprio Java.
                if (bloco.tipo != Bloco.VAZIO && areaTanqueFutura.intersects(bloco)) {
                    return false; // Bateu na parede, aço ou água
                }
            }
        }
        return true; // Caminho livre!
    }

    @Override
    public boolean mover(){
        if(this.direcao_atual == null) return false; /* Player vai nascer parado (direção null), e a Thread dele vai ficar a girar
                                                em sair do lugar até você apertar uma tecla. Já o inimigo,
                                                precisaremos "dar um empurrão" nele depois para ele não nascer parado
                                                também. */

        // NOTA PARA APRESENTAÇÃO (Previsão de Movimento):
        // Em vez de alterar o 'this.x' direto, criamos variáveis temporárias.
        int proximoX = this.x;
        int proximoY = this.y;

        // velocidade é a quantidade de pixels que vamos mexer o tanque
        // o Y é invertido do que no plano cartesiano usando Swing/AWT
        // então para cima Y diminui e para baixo Y aumenta
        // a matemática é NovaPosicao = PosicaoAtual + (Direcao x Velocidade)
        switch(this.direcao_atual){
            case CIMA:
                proximoY -= this.velocidade;
                break;
            case BAIXO:
                proximoY += this.velocidade;
                break;
            case ESQUERDA:
                proximoX -= this.velocidade;
                break;
            case DIREITA:
                proximoX += this.velocidade;
                break;
        }

        // SÓ MOVE O TANQUE SE O CAMINHO ESTIVER LIVRE DE COLISÕES
        if (podeMover(proximoX, proximoY)) {
            this.x = proximoX;
            this.y = proximoY;
            return true;
        }
        return false;
    }

    @Override
    public void set_direcao(Direcao novaDirecao){
        this.direcao_atual = novaDirecao;

        // NOTA PARA APRESENTAÇÃO: Salva a direção na memória, a menos que seja nula (parado)
        if (novaDirecao != null) {
            this.ultima_direcao = novaDirecao;
        }
    }

    // Entrega a direção salva para o tiro usar
    public Direcao getUltimaDirecao() {
        return this.ultima_direcao;
    }

    @Override
    public int get_x() {
        return this.x;
    }

    @Override
    public int get_y() {
        return this.y;
    }

    // NOTA PARA APRESENTAÇÃO (Encapsulamento do Dano):
    // O tanque controla a sua própria morte. Se a vida zerar, ele desliga a própria Thread.
    public void levar_dano(){
        this.vida--;
        if (this.vida <= 0) {
            this.ligado = false; // Desliga a Thread na memória
        }
    }

    // Metodo auxiliar para o jogo saber se este tanque já explodiu
    public boolean isMorto() {
        return this.vida <= 0;
    }

    public void atirar(){}
}