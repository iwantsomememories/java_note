package Client;


import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 10086);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        while (true) {
            System.out.println("服务器连接成功");
            System.out.println("================欢迎来到聊天室================");
            System.out.println("1登录");
            System.out.println("2注册");
            System.out.println("请输入你的选择：");
            Scanner sc = new Scanner(System.in);
            String choose = sc.nextLine();
            switch(choose){
                case "1" -> login(socket);
                case "2" -> register(socket);
                default -> System.out.println("没有这个选项");
            }
        }

    }


    public static void register(Socket socket) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名");
        String usernameRegis = sc.nextLine();
        System.out.println("请输入密码");
        String passwordRegis = sc.nextLine();

        //拼接
        StringBuilder sb = new StringBuilder();
        sb.append("username="+usernameRegis+"&password="+passwordRegis);

        bw.write("register");
        bw.newLine();
        bw.flush();

        //往服务器写出用户名和密码
        bw.write(sb.toString());
        bw.newLine();
        bw.flush();

        //接收数据
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var message = br.readLine();
        //1:用户名重复 2:用户名格式不正确 3:密码格式不正确 4:注册成功
        switch (message){
            case "1" -> System.out.println("用户名重复");
            case "2" -> System.out.println("用户名格式不正确，正确用户名格式为：长度6~18位，纯字母，不能有数字或其他符号");
            case "3" -> System.out.println("密码格式不正确，正确密码格式为：密码长度3~8位。第一位必须是小写或者大小的字母，后面必须是纯数字。");
            case "4" -> {
                System.out.println("注册成功，开始聊天");
                new Thread(new listener(socket)).start();
                talk2All(bw);
            }
        }
    }

    public static void login(Socket socket) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入用户名");
        String username = sc.nextLine();
        System.out.println("请输入密码");
        String password = sc.nextLine();

        //拼接
        StringBuilder sb = new StringBuilder();
        sb.append("username="+username+"&password="+password);

        bw.write("login");
        bw.newLine();
        bw.flush();

        //往服务器写出用户名和密码
        bw.write(sb.toString());
        bw.newLine();
        bw.flush();

        //接收数据
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var message = br.readLine();
        //1:登录成功 2:密码有误 3:用户名不存在
        if("1".equals(message)){
            System.out.println("登录成功，开始聊天");
            new Thread(new listener(socket)).start();
            talk2All(bw);
        } else if ("2".equals(message)) {
            System.out.println("密码输入错误");
        } else if ("3".equals(message)){
            System.out.println("用户名不存在");
        }
    }

    //往服务器写出消息
    private static void talk2All(BufferedWriter bw) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true){
            System.out.println("请输入您要说的话");
            String str = sc.nextLine();
            //把聊天内容写给服务器
            bw.write(str);
            bw.newLine();
            bw.flush();
        }
    }
}

class listener implements Runnable{

    Socket socket;

    public listener(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while (true) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                var message = br.readLine();
                System.out.println(message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
