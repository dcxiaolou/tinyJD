package com.dcxiaolou.tinyJD.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.dcxiaolou.tinyJD.bean.OmsOrder;
import com.dcxiaolou.tinyJD.bean.OmsOrderItem;
import com.dcxiaolou.tinyJD.mq.ActiveMQUtil;
import com.dcxiaolou.tinyJD.order.mapper.OrderItemMapper;
import com.dcxiaolou.tinyJD.order.mapper.OrderMapper;
import com.dcxiaolou.tinyJD.service.CartService;
import com.dcxiaolou.tinyJD.service.OrderService;
import com.dcxiaolou.tinyJD.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ActiveMQUtil activeMQUtil;

    //远程调用CartService
    @Reference
    private CartService cartService;

    @Override
    public String createTradeCode(String memberId) {
        Jedis jedis = redisUtil.getJedis();
        String tradeKey = "user:" + memberId + ":tradeCode";
        String tradeCode = UUID.randomUUID().toString();
        jedis.setex(tradeKey, 60 * 30, tradeCode);
        jedis.close();
        return tradeCode;
    }

    @Override
    public String checkTradeCode(String memberId, String tradeCode) {
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String tradeKey = "user:" + memberId + ":tradeCode";
            String tradeCodeFromCache = jedis.get(tradeKey);

            //将交易码删除，防止重复提交
            //可以使用lua脚本在发现可以的同时将key立即删除，防止订单并发攻击
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList(tradeKey), Collections.singletonList(tradeCode));

            if (eval != null && eval != 0) {
                // jedis.del(tradeKey);
                return "success";
            } else {
                return "fail";
            }
        } finally {
            jedis.close();
        }
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        System.out.println("omsOrder = " + omsOrder);
        // 保存订单
        orderMapper.insert(omsOrder);
        String orderId = omsOrder.getId();
        // 保存订单详情
        List<OmsOrderItem> omsOrderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem omsOrderItem : omsOrderItems) {
            omsOrderItem.setOrderId(orderId);
            orderItemMapper.insert(omsOrderItem);
            //删除购物车中对应的商品
            System.out.println(omsOrderItem.getProductSkuId());
            cartService.delCartItemBySkuId(omsOrderItem.getProductSkuId());
        }
    }

    @Override
    public OmsOrder getByOutTradeNo(String outTradeNo) {
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(outTradeNo);
        OmsOrder order = orderMapper.selectOne(omsOrder);
        return order;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        //将订单更新为已支付
        omsOrder.setStatus("1");
        Example e = new Example(OmsOrder.class);
        e.createCriteria().andEqualTo("orderSn", omsOrder.getOrderSn());

        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        Connection connection = null;
        Session session = null;
        try {
            orderMapper.updateByExample(omsOrder, e);
            //发送一个订单已支付的对列，提供给库存消费
            connection = connectionFactory.createConnection();
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue payment_success_queue = session.createQueue("ORDER_PAY_QUEUE");
            MessageProducer producer = session.createProducer(payment_success_queue);
            MapMessage message = new ActiveMQMapMessage();

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
