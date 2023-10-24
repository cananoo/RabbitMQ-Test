package study.three;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.util.Scanner;

public class Task2 {
    // 队列名称
    public  static  final  String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 开启发布确认

        boolean durable = true;
        channel.queueDeclare(TASK_QUEUE_NAME,durable,false,false,null);
        Scanner scan = new Scanner(System.in);

        while (scan.hasNext()){
            String message = scan.nextLine();
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes());
            System.out.println("生产者发送消息" + message);
        }

    }
}
