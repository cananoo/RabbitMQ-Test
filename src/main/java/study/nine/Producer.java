package study.nine;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Producer {
    //队列名称
    public  static  final String QUEUE_NAME = "hello";

    //发消息
    public static void main(String[] args) throws Exception {
        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //工厂IP 连接RabbitMQ的队列
        factory.setHost("192.168.95.133");
        //用户名和密码
        factory.setUsername("admin");
        factory.setPassword("123456");

        //创建连接
        Connection connection = factory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        /**
         * 生成一个队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化，默认存储在内存中
         * 3.该队列是否只提供一个消费者进行消费，是否进行消息共享
         * 4.是否自动删除 最后一个消费者断开连接以后，该队列是否自动删除
         * 5.其他参数 null
         *
         */
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-priority",10); //此处设置允许优先级的范围为0-10 不要设置过大浪费内存
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);

        for (int i = 1; i < 11; i++) {
            String message = "info"+i;
            if (i == 5){
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(5).build();
                channel.basicPublish("",QUEUE_NAME,properties,message.getBytes());
            }else {
                channel.basicPublish("",QUEUE_NAME,null ,message.getBytes());
            }
        }
        System.out.println("消息发送完毕!");
    }
}
