package study.seven;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogsTopic02 {
    public static final  String EXCHANGE_NAME = "topic_logs";
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        channel.queueDeclare("Q2",false,false,false,null);
        channel.queueBind("Q2",EXCHANGE_NAME,"*.*.rabbit");
        channel.queueBind("Q2",EXCHANGE_NAME,"lazy.#");
        DeliverCallback deliverCallback = (tag, message) -> {
            System.out.println("Q2控制台打印消息：" + new String(message.getBody(),"UTF-8"));
            System.out.println("接收队列Q2" +  "绑定键" + message.getEnvelope().getRoutingKey());
        };

        channel.basicConsume("Q2",true, deliverCallback,cancelCallback ->{});
    }
}
