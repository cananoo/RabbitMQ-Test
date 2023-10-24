package study.two;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class Task01 {
    // 队列名称
    public static final String QUEUE_NAME = "hello";

    //发送大量消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.queueDeclare(QUEUE_NAME,false,false,false,null);
        Scanner scan = new Scanner(System.in);

        while (scan.hasNext()){
            String message = scan.nextLine();
            channel.basicPublish("",QUEUE_NAME,null,message.getBytes());
            System.out.println("发送消息完成：" + message);
        }

    }
}
