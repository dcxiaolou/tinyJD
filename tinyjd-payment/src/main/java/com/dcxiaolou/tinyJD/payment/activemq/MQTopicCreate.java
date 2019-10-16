package com.dcxiaolou.tinyJD.payment.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class MQTopicCreate {
    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.159.3:61616");
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Topic topic = session.createTopic("money");//话题模式消息，可以多次消费
            MessageProducer producer = session.createProducer(topic);
            ActiveMQTextMessage textMessage = new ActiveMQTextMessage();
            textMessage.setText("I need a lot of money!");
            producer.setDeliveryMode(DeliveryMode.PERSISTENT); //topic话题不能在生产者中持久化，此处失效
            producer.send(textMessage);
            session.commit();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
