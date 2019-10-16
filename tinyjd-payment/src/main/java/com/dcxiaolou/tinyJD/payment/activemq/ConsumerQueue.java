package com.dcxiaolou.tinyJD.payment.activemq;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class ConsumerQueue {

    public static void main(String[] args) {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER, ActiveMQConnection.DEFAULT_PASSWORD, "tcp://192.168.159.3:61616");
        try {
            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //若没有session.rollback();则自动session.commit();
            Queue queue = session.createQueue("drink");
            MessageConsumer consumer = session.createConsumer(queue);
            consumer.setMessageListener(new MessageListener() {
                //消息监听器
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) {
                        String text = null;
                        try {
                            text = ((TextMessage) message).getText();
                            System.out.println(text + " ok, i have!");
                        } catch (JMSException e) {
                            try {
                                //执行失败回滚
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
