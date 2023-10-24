package study.two;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;




public class Worker01 {
    // 队列名称
    public static final String QUEUE_NAME = "hello";

    //接收消息
    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        DeliverCallback deliverCallback = (var1,var2) -> {
        System.out.println("接收到的消息：" + new String(var2.getBody()));
    };

        CancelCallback cancelCallback= var -> {
            System.out.println("接收异常" + var);
        };
        System.out.println("在等待接收消息...");

        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }

}
