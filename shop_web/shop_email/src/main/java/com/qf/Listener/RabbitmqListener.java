package com.qf.Listener;

import com.qf.entity.Email;
import com.qf.util.MailUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitmqListener {
   @Autowired
   private MailUtil mailUtil;
    /**
     * 处理rabbitmq的邮件对象
     */
    @RabbitListener(queues = "email_queue")
    public void emailHandler(Email email){
        //发送邮件
        try {
            mailUtil.sendMail(email);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
