package com.dcxiaolou.tinyJD.payment.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.dcxiaolou.tinyJD.bean.PaymentInfo;
import com.dcxiaolou.tinyJD.mq.ActiveMQUtil;
import com.dcxiaolou.tinyJD.payment.mapper.PaymentInfoMapper;
import com.dcxiaolou.tinyJD.service.PaymentService;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentInfoMapper paymentInfoMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    @Autowired
    private AlipayClient alipayClient;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insert(paymentInfo);
    }

    @Override
    public void updatePayment(PaymentInfo paymentInfo) {

        //进行支付成功的幂等性检查
        PaymentInfo paymentInfoParam = new PaymentInfo();
        paymentInfoParam.setOrderSn(paymentInfo.getOrderSn());
        PaymentInfo paymentInfoResult = paymentInfoMapper.selectOne(paymentInfoParam);
        if (StringUtils.isNotBlank(paymentInfoResult.getPaymentStatus()) && paymentInfoResult.getPaymentStatus().equals("3")) {
            return ;
        } else {
            Example e = new Example(PaymentInfo.class);
            e.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());

            ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

            Connection connection = null;
            Session session = null;
            try {
                paymentInfoMapper.updateByExample(paymentInfo, e);

                //支付成功后，引起的系统服务-》订单服务的更新-》库存服务的更新-》物流服务
                //调用mq发送支付成功的消息

                connection = connectionFactory.createConnection();
                session = connection.createSession(true, Session.SESSION_TRANSACTED);
                Queue payment_success_queue = session.createQueue("PAYMENT_SUCCESS_QUEUE");
                MessageProducer producer = session.createProducer(payment_success_queue);
                MapMessage message = new ActiveMQMapMessage();
                message.setString("out_trade_no", paymentInfo.getOrderSn());
                producer.send(message);
                session.commit();
            } catch (JMSException ex) {
                try {
                    session.rollback();
                } catch (JMSException exc) {
                    exc.printStackTrace();
                }
                ex.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (JMSException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void sendDelayPaymentResultCheckQueue(String outTradeNo, int count) {
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        Connection connection = null;
        Session session = null;
        try {

            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue payment_success_queue = session.createQueue("PAYMENT_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(payment_success_queue);
            MapMessage message = new ActiveMQMapMessage();
            message.setString("out_trade_no", outTradeNo);
            message.setInt("count", count);
            //设置消息延迟，消息在等待相应的时间后才会生成
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000*30);
            producer.send(message);
            session.commit();
        } catch (JMSException ex) {
            try {
                session.rollback();
            } catch (JMSException exc) {
                exc.printStackTrace();
            }
            ex.printStackTrace();
        } finally {
            try {
                connection.close();
            } catch (JMSException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> checkAlipayPayment(String out_trade_no) {

        Map<String, Object> resultMap = new HashMap<>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("out_trade_no", out_trade_no);
        request.setBizContent(JSON.toJSONString(requestMap));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            System.out.println("可能支付以创建，调用成功");
            resultMap.put("out_trade_no", response.getOutTradeNo());
            resultMap.put("trade_no", response.getTradeNo());
            resultMap.put("trade_status", response.getTradeStatus());
            resultMap.put("call_back_content",response.getMsg());
        } else {
            System.out.println("可能支付未创建，调用失败");
        }

        return resultMap;

    }
}
