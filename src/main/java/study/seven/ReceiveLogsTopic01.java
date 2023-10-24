package study.seven;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogsTopic01 {
    public static final  String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.queueDeclare("Q1",false,false,false,null);
        channel.queueBind("Q1",EXCHANGE_NAME,"*.orange.*");
        DeliverCallback deliverCallback = (tag, message) -> {
            System.out.println("Q1控制台打印消息：" + new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列Q1" +  "绑定键" + message.getEnvelope().getRoutingKey());
        };

        channel.basicConsume("Q1",true, deliverCallback,cancelCallback ->{});
    }
}
