package study.five;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogs02 {
    // 交换机名称
    public static final String EXCHANGE_NAME = "logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        //声明一个队列 临时队列
        String queueName = channel.queueDeclare().getQueue();

        /**
         * 参数3为RoutingKey
         */
        channel.queueBind(queueName,EXCHANGE_NAME,"");

        System.out.println("等待接收消息，打印在屏幕上...");

        DeliverCallback deliverCallback = (tag,message) -> {
            System.out.println("02控制台打印消息：" + new String(message.getBody(),"UTF-8"));
        };

        channel.basicConsume(queueName,true, deliverCallback,cancelCallback ->{});
    }
}
