package study.four;

import Utils.RabbitMqUtils;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.MessageProperties;

import java.util.UUID;

import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConfirmMessage {
    //批量发消息的个数
    public static final int MESSAGE_COUNT = 1000;
    public static void main(String[] args) throws Exception {
        //1.单个确认 用时4034ms
       // publishMessageIndividually();
        //2.批量确认 用时106ms
        // publishMessageBatch();
        //3.异步批量确认 用时39ms
        publishMessageAsync();
    }

    //单个确认
    public static  void publishMessageIndividually() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
          channel.confirmSelect();
        //开始时间
        long start = System.currentTimeMillis();
        // 批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            channel.basicPublish("",queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,(i + "").getBytes());
            //确认
            boolean flag = channel.waitForConfirms();
            if (flag){
                System.out.println("消息发送成功");
            }
        }
        long end = System.currentTimeMillis();

        System.out.println("用时" + (end - start) + "ms");
    }
    //批量发布确认
    public static  void publishMessageBatch() throws Exception {
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        channel.confirmSelect();
        //开始时间
        long start = System.currentTimeMillis();
        //批量确认消息大小
        int batchSize = 100;
        // 批量发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            channel.basicPublish("",queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,(i + "").getBytes());
            //批量确认
            if ((i + 1) % batchSize  == 0){
                boolean flag = channel.waitForConfirms();
                if (flag){
                    System.out.println("消息发送成功");
                }
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("用时" + (end - start) + "ms");
    }
    //异步发布确认
    public  static  void  publishMessageAsync() throws Exception{
        Channel channel = RabbitMqUtils.getChannel();
        String queueName = UUID.randomUUID().toString();
        channel.queueDeclare(queueName,true,false,false,null);
        channel.confirmSelect();

        /**
         * 线程安全有序的一个哈希表 适用于高并发的情况
         * 1.轻松的将序号与消息进行关联
         * 2.轻松批量删除条目
         * 3.支持高并发（多线程）
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirms = new ConcurrentSkipListMap<>();

        //消息确认成功回调的函数
        ConfirmCallback ack =  (deliverTag,multiple) -> {
            // 如果是批量确认
            if (multiple){
                // 2.删除已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long,String> confirmed = outstandingConfirms.headMap(deliverTag);
                confirmed.clear();
            }else {
                outstandingConfirms.remove(deliverTag);
            }
            System.out.println("确认的消息" + deliverTag);

        };
        /**
         * 1.消息的标记
         * 2.是否为批量确认
         */
        //消息确认失败回调的函数
        ConfirmCallback back = (deliverTag,multiple) -> {
            //3.打印一下未确认的消息都有哪些
            String message = outstandingConfirms.get(deliverTag);
            System.out.println("未确认的消息标记" + deliverTag + ":::" + "未确认的消息" + message);
        };
        //准备消息的监听器 监听哪些消息成功了 哪些消息失败了
          channel.addConfirmListener(ack,back);

          //开始时间
        long start = System.currentTimeMillis();
        // 异步发消息
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            String message = ("消息" + i);
          channel.basicPublish("",queueName,null,message.getBytes());
          //1.此处记录下消息的总和
            outstandingConfirms.put(channel.getNextPublishSeqNo(),message);
        }
        long end = System.currentTimeMillis();
        System.out.println("用时" + (end - start) + "ms");
    }
}
