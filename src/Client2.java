import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import javax.swing.*;

public class Client2 extends JFrame implements ActionListener, KeyListener{

    private static final long serialVersionUID = 1L;
    private JTextArea texto;
    private JTextField txtMsg;
    private JButton btnSend;
    private JButton btnSair;
    private JLabel lblHistorico;
    private JLabel lblMsg;
    private JPanel pnlContent;
    private Socket socket;
    private OutputStream ou ;
    private Writer ouw;
    private BufferedWriter bfw;
    private JTextField txtIP;
    private JTextField txtPorta;
    private JTextField txtNome;
    private InputStream in;
    private InputStreamReader inr;
    private BufferedReader bfr;

    public Client2() throws IOException{
        JLabel lblMessage = new JLabel("Login - Chat");
        txtIP = new JTextField("127.0.0.1");
        txtPorta = new JTextField("12345");
        txtNome = new JTextField("guest");
        Object[] texts = {lblMessage, txtIP, txtPorta, txtNome };
        JOptionPane.showMessageDialog(null, texts);
        pnlContent = new JPanel();
        texto              = new JTextArea(10,20);
        texto.setEditable(false);
        texto.setBackground(new Color(240,240,240));
        txtMsg                       = new JTextField(20);
        lblHistorico     = new JLabel("Histórico");
        lblMsg        = new JLabel("Mensagem");
        btnSend                     = new JButton("Enviar");
        btnSend.setToolTipText("Enviar Mensagem");
        btnSair           = new JButton("Sair");
        btnSair.setToolTipText("Sair do Chat");
        btnSend.addActionListener(this);
        btnSair.addActionListener(this);
        btnSend.addKeyListener(this);
        txtMsg.addKeyListener(this);
        JScrollPane scroll = new JScrollPane(texto);
        texto.setLineWrap(true);
        pnlContent.add(lblHistorico);
        pnlContent.add(scroll);
        pnlContent.add(lblMsg);
        pnlContent.add(txtMsg);
        pnlContent.add(btnSair);
        pnlContent.add(btnSend);
        pnlContent.setBackground(Color.LIGHT_GRAY);
        texto.setBorder(BorderFactory.createEtchedBorder(Color.BLUE,Color.BLUE));
        txtMsg.setBorder(BorderFactory.createEtchedBorder(Color.BLUE, Color.BLUE));
        setTitle(txtNome.getText());
        setContentPane(pnlContent);
        setLocationRelativeTo(null);
        setResizable(false);
        setSize(250,300);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /***
     * Método usado para conectar no server socket, retorna IO Exception caso dê algum erro.
     * @throws IOException
     */
    public void conectar() {
        try{
            socket = new Socket(txtIP.getText(),Integer.parseInt(txtPorta.getText()));
            ou = socket.getOutputStream();
            ouw = new OutputStreamWriter(ou);
            bfw = new BufferedWriter(ouw);
            bfw.write(txtNome.getText()+"\r\n");
            bfw.flush();

        }catch (IOException | IllegalArgumentException err){
            System.out.println(err.getMessage());
            System.exit(0);
        }
    }

    /***
     * Método usado para enviar mensagem para o server socket
     * @param msg do tipo String
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void enviarMensagem(String msg) throws IOException{
        if(msg.equals("exit")){
            bfw.write("exit");
            texto.append("Desconectando ...\r\n");
            sair();
        }if (msg.startsWith("\\changename")) {
            bfw.write(msg+"\r\n");
            texto.append( txtNome.getText() + " -> " + txtMsg.getText()+"\r\n");

            msg     = msg.substring(11);
            txtNome.setText(msg);
        }
        else{
            bfw.write(msg+"\r\n");
            texto.append( txtNome.getText() + " -> " + txtMsg.getText()+"\r\n");
        }
        bfw.flush();
        txtMsg.setText("");
    }

    /**
     * Método usado para receber mensagem do servidor
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void escutar() throws IOException{
        InputStream in = socket.getInputStream();
        InputStreamReader inr = new InputStreamReader(in);
        BufferedReader bfr = new BufferedReader(inr);
        String msg = "";

        while(true) {
            if (bfr.ready()) {
                msg = bfr.readLine();
                if (msg.contains("erro-name:")) {
                    System.out.println("Esse NickName já existe!");
                    sair();
                    break;
                } else {
                    texto.append(msg + "\r\n");
                }
            }
        }
    }

    /***
     * Método usado quando o usuário clica em sair
     * @throws IOException retorna IO Exception caso dê algum erro.
     */
    public void sair() throws IOException{
        bfw.close();
        ouw.close();
        ou.close();
        socket.close();
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            if(e.getActionCommand().equals(btnSend.getActionCommand()))
                enviarMensagem(txtMsg.getText());
            else
            if(e.getActionCommand().equals(btnSair.getActionCommand()))
                enviarMensagem("exit");
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER){
            try {
                enviarMensagem(txtMsg.getText());
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    public static void main(String []args) throws IOException{
        Client client = new Client();
        client.conectar();
        client.escutar();
    }
}
