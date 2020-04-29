import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;


public class Server extends Thread{
    private static ArrayList<BufferedWriter> clientes;
    private static ServerSocket server;
    private static ArrayList<String> nickNames;
    private String nickName;
    private Socket con;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;
    private Writer ouw;
    private BufferedWriter bfw;

    /**
     * Método construtor
     * @param con do tipo Socket
     */
    public Server(Socket con){
        this.con = con;
        try {
            ouw = new OutputStreamWriter(con.getOutputStream());
            bfw = new BufferedWriter(ouw);
            in  = con.getInputStream();
            inr = new InputStreamReader(in);
            bfr = new BufferedReader(inr);
            nickName = bfr.readLine();
            verifyNickName(bfw);
            System.out.println("*** Nicks: "+nickNames.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método run
     */
    @Override
    public void run() {
        try {
            if (!this.con.isClosed()) {
                String msg;
                OutputStream ouT =  this.con.getOutputStream();
                Writer ouwT = new OutputStreamWriter(ouT);
                BufferedWriter bfwT = new BufferedWriter(ouwT);
                clientes.add(bfwT);

                while (true) {
                    msg = bfr.readLine();
                    try{
                        if (msg.equals("exit")) {
                            break;
                        } else {
                            sendToAll(bfwT, msg);
                            System.out.println(msg);
                        }
                    }catch (NullPointerException erro){
                        break;
                    }
                }

                System.out.println(nickName + " ecerrou a conexão!");
                nickNames.remove(nickName);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * Método usado para enviar mensagem para todos os clients
     * @param bwSaida do tipo BufferedWriter
     * @param msg do tipo String
     * @throws IOException
     */
    public void sendToAll(BufferedWriter bwSaida, String msg) throws  IOException
    {
        for(BufferedWriter bw : clientes){
            if(!(bwSaida == bw)){
                bw.write(nickName + " -> " + msg+"\r\n");
                bw.flush();
            }
        }
    }

    public void verifyNickName(BufferedWriter bwSaida) throws IOException {
        for(String nk : nickNames){
            if(nk.equals(nickName)){
                System.out.printf("O NickName %s já existe!\n",nk);
                bwSaida.write("erro-name: Nikname já existe!");
                bwSaida.close();
                return;
            }
        }

        System.out.printf("Cliente %s conectado... \n", nickName);
        nickNames.add(nickName);
    }

    /***
     * Método main
     * @param args
     */
    public static void main(String []args) {
        try{
            //Cria os objetos necessário para instânciar o servidor
            JLabel lblMessage = new JLabel("Porta do Servidor:");
            JTextField txtPorta = new JTextField("12345");
            Object[] texts = {lblMessage, txtPorta };
            JOptionPane.showMessageDialog(null, texts);
            server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
            clientes = new ArrayList<BufferedWriter>();
            JOptionPane.showMessageDialog(null,"Servidor ativo na porta: "+ txtPorta.getText());
            System.out.println("Servidor ativo na porta: "+ txtPorta.getText());

            nickNames = new ArrayList<>();

            while(true){
                System.out.println("Aguardando conexão...");
                Socket con = server.accept();
                Thread t = new Server(con);
                t.start();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }// Fim do método main
} //Fim da classe