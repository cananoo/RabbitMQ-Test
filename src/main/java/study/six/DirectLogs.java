package study.six;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;

import java.util.Scanner;

public class DirectLogs {
    // 交换机名称
    public static final String EXCHANGE_NAME = "direct_logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        Scanner scan = new Scanner(System.in);
        while (scan.hasNext()){
            String message = scan.next();
            channel.basicPublish(EXCHANGE_NAME,"info",null,message.getBytes("UTF-8"));
            System.out.println("生产者发出消息：" + message);
        }


    }
}
