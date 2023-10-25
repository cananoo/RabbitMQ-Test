package study.eight;


import Utils.RabbitMqUtils;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列 DEMO
 */
public class Consumer01 {
    // 普通交换机
    public static final String NORMAL_EXCHANGE = "normal_exchange";
    // 死信交换机
    public static final String DEAD_EXCHANGE = "dead_exchange";
    //普通队列
    public static final String NORMAL_QUEUE = "normal_queue";
    //死信队列
    public static final String DEAD_QUEUE = "dead_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();

        channel.exchangeDeclare(NORMAL_EXCHANGE, BuiltinExchangeType.DIRECT);
        channel.exchangeDeclare(DEAD_EXCHANGE, BuiltinExchangeType.DIRECT);
        // 死信队列接收普通队列中未被消费的消息
        Map<String, Object> arguments = new HashMap<>();
        //正常队列设置死信交换机
        arguments.put("x-dead-letter-exchange",DEAD_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","lisi");
        //过期时间 (建议设置在生产方)
        //arguments.put("x-message-ttl",10000);
        //设置正常队列长度限制
        //arguments.put("x-max-length",6);
        channel.queueDeclare(NORMAL_QUEUE,false,false,false,arguments);
        channel.queueDeclare(DEAD_QUEUE,false,false,false,null);

        //绑定交换机与队列
        channel.queueBind(NORMAL_QUEUE,NORMAL_EXCHANGE,"zhangsan");
        channel.queueBind(DEAD_QUEUE,DEAD_EXCHANGE,"lisi");

        System.out.println("等待接收消息....");

        DeliverCallback deliverCallback = (tag, message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            //模拟消息被拒
             if( msg.equals("info5")) {
                 // 参数2为是否放回队列
                 channel.basicReject(message.getEnvelope().getDeliveryTag(),false);
                 System.out.println("此消息被拒绝" + msg);
             }else {
                 // 参数2为是否批量应答
                 channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
                 System.out.println("C1控制台打印消息：" + msg);
             }
        };
        channel.basicConsume(NORMAL_QUEUE,false,deliverCallback,consumerTag ->{});
    }
}
