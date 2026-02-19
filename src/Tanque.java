public abstract class Tanque implements I_Movimento, Runnable{

    protected int x;
    protected int y;
    protected int vida;
    protected int velocidade;
    protected Direcao direcao_atual;

    //variáveis de thread
    protected boolean ligado = true;
    protected Thread threadTanque;



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

    @Override
    public void mover(){
        if(this.direcao_atual == null) return; /* Player vai nascer parado (direção null), e a Thread dele vai ficar a girar
                                                em sair do lugar até você apertar uma tecla. Já o inimigo,
                                                precisaremos "dar um empurrão" nele depois para ele não nascer parado
                                                também. */


            // velocidade é a quantidade de pixels que vamos mexer o tanque
            // o Y é invertido do que no plano cartesiano usando Swing/AWT
            // então para cima Y diminui e para baixo Y aumenta
            // a matemática é NovaPosicao = PosicaoAtual + (Direcao x Velocidade)
        switch(this.direcao_atual){
            case CIMA:
                this.y -= this.velocidade;
                break;
            case BAIXO:
                this.y += this.velocidade;
                break;
            case ESQUERDA:
                this.x -= this.velocidade;
                break;
            case DIREITA:
                this.x += this.velocidade;
                break;
        }
    }

    @Override
    public void set_direcao(Direcao novaDirecao){
        this.direcao_atual = novaDirecao;
    }

    @Override
    public int get_x() {
        return this.x;
    }

    @Override
    public int get_y() {
        return this.y;
    }

    public void levar_dano(){
        this.vida--;
    }

    public void atirar(){};

}
