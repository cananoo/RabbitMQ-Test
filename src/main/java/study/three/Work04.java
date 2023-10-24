package study.three;

import Utils.RabbitMqUtils;
import Utils.SleepUtils;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;

public class Work04 {
    // 队列名称
    public  static  final  String TASK_QUEUE_NAME = "ack_queue";

    public static void main(String[] args) throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        System.out.println("C2等待接收消息，处理时间较长");

        DeliverCallback deliverCallback = (consumerTag,message)->{
            //沉睡 1s
            SleepUtils.sleep(30);
            System.out.println("接收到的消息" + new String(message.getBody(),"UTF-8"));
            //手动应答
            /**
             * 1.消息的标记 tag
             * 2.是否批量应答
             */
            channel.basicAck(message.getEnvelope().getDeliveryTag(),false);
        };

        CancelCallback cancelCallback = Tag -> {
            System.out.println(Tag + "消费者取消消费");
        };

        //不公平分发
        channel.basicQos(1);

        Boolean autoAck = false;
        channel.basicConsume(TASK_QUEUE_NAME,autoAck,deliverCallback,cancelCallback);

    }

}
