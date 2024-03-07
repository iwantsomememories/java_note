package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Server {

    static ArrayList<Socket> userlist =  new ArrayList<>();

    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(10086);

        //读取本地文件中的用户信息
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream("net_final_homework\\userinfo.txt");
        prop.load(fis);
        fis.close();

        /*ThreadPoolExecutor pool = new ThreadPoolExecutor(
                5,
                10,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );*/

        //只要来了一个客户端，就开一个线程处理
        while (true){
            Socket socket = ss.accept();
            System.out.println("有客户端来连接");
            new Thread(new MyRunnable(socket, prop)).start();
        }


    }
}

class MyRunnable implements Runnable{
    Socket socket;
    Properties prop;

    public MyRunnable(Socket socket, Properties prop){
        this.socket = socket;
        this.prop = prop;
    }

    @Override
    public void run() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                var choose = br.readLine();
                switch (choose){
                    case "login" -> login(br);
                    case "register" -> register(br);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //获取用户登陆时传递过来的信息
    public void login(BufferedReader br) throws IOException {
        System.out.println("用户选择了登录操作");
        var s = br.readLine();
        System.out.println(s);
        var arr = s.split("&");
        String usernameInput = arr[0].split("=")[1];
        String passwordInput = arr[1].split("=")[1];
        System.out.println("用户输入的用户名为"+usernameInput);
        System.out.println("用户输入的密码为"+passwordInput);

        if(prop.containsKey(usernameInput)){
            var rightPassword = prop.get(usernameInput)+"";
            if(rightPassword.equals(passwordInput)){
                //提示用户登录成功，可以开始聊天
                writeMessageToClient("1");
                Server.userlist.add(socket);
                //System.out.println("标记");
                talk2All(br, usernameInput);
            } else {
                //密码输入有误
                writeMessageToClient("2");
            }
        } else {
            //如果用户名不存在，直接回写
            writeMessageToClient("3");
        }
    }

    public void register(BufferedReader br) throws IOException {
        System.out.println("用户选择了注册操作");
        var s = br.readLine();
        System.out.println(s);
        var arr = s.split("&");
        String usernameInput = arr[0].split("=")[1];
        String passwordInput = arr[1].split("=")[1];
        System.out.println("用户输入的用户名为"+usernameInput);
        System.out.println("用户输入的密码为"+passwordInput);

        if(prop.containsKey(usernameInput)){
            writeMessageToClient("1");
        } else if (!usernameInput.matches("[A-Za-z]{6,18}")) {
            writeMessageToClient("2");
        } else if(!passwordInput.matches("[A-Za-z]\\d{2,7}")){
            writeMessageToClient("3");
        } else{
            prop.setProperty(usernameInput, passwordInput);
            FileOutputStream fos = new FileOutputStream("net_final_homework\\userinfo.txt");
            prop.store(fos, null);
            fos.close();
            writeMessageToClient("4");
            Server.userlist.add(socket);
            talk2All(br, usernameInput);
        }
    }

    private void talk2All(BufferedReader br,String username) throws IOException {
        while (true){
            var message = br.readLine();
            System.out.println(username+"发送过来消息："+message);

            //群发
            for (Socket s : Server.userlist) {
                //s依次表示每一个客户端的连接对象
                writeMessageToClient(username + "发送过来消息：" + message, s);
            }
        }
    }

    public void writeMessageToClient(String message) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        bw.write(message);
        bw.newLine();
        bw.flush();
    }

    public void writeMessageToClient(String message, Socket s) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        bw.write(message);
        bw.newLine();
        bw.flush();
    }

}
