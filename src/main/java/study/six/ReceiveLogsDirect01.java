package study.six;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class ReceiveLogsDirect01 {
    public static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
    // 声明交换机
        channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

        channel.queueDeclare("console", false, false, false, null);
        channel.queueBind("console",EXCHANGE_NAME,"info");

        DeliverCallback deliverCallback = (tag, message) -> {
            System.out.println("01控制台打印消息：" + new String(message.getBody(),"UTF-8"));
        };

        channel.basicConsume("console",true, deliverCallback,cancelCallback ->{});
    }
}
