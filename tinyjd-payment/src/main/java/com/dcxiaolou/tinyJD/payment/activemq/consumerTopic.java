package com.dcxiaolou.tinyJD.payment.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class consumerTopic {
    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.159.3:61616");
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = session.createTopic("money");
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        String text = null;
                        try {
                            text = ((TextMessage) message).getText();
                            System.out.println(text + " oh...oh...oh...");
                        } catch (JMSException e) {
                            try {
                                session.rollback();
                            } catch (JMSException ex) {
                                ex.printStackTrace();
                            }
                            e.printStackTrace();
                        }
                    }
                }
            });
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
