package com.dcxiaolou.tinyJD.payment.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class MQQueueCreate {

    public static void main(String[] args) {
        ConnectionFactory connect = new ActiveMQConnectionFactory("tcp://192.168.159.3:61616");
        try {
            Connection connection = connect.createConnection();
            connection.start();

            // 是否开启事务，若为true，第二个值为0
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue queue = session.createQueue("drink");//队列模式的消息，只能被消费一次
            MessageProducer producer = session.createProducer(queue);
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText("I want some water!");
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            producer.send(textMessage);
            session.commit(); //提交事务
            connection.close(); //关闭连接
        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

}
