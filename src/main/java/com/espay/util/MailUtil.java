package com.espay.util;

import com.espay.pojo.TokenCache;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱工具类
 */
@Component
public class MailUtil {
    public void sendEmail(String to, JavaMailSenderImpl mailSender, HttpServletRequest request, MongoTemplate mongoTemplate) throws MessagingException,IOException {
        InputStream inputStream = MailUtil.class.getClassLoader().getResourceAsStream("email.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        Set<String> collectionNames= mongoTemplate.getCollectionNames();
        if(!collectionNames.contains("tokenCache")){
            Index index=new Index();
            index.on("createAt",Sort.Direction.ASC);
            index.expire(30*60,TimeUnit.SECONDS);
            IndexOperations staffIndexOperations= mongoTemplate.indexOps("tokenCache");
            staffIndexOperations.ensureIndex(index);
        }
        String token = UUID.randomUUID().toString().replace("-", "").toLowerCase();
        //缓存
        mongoTemplate.insert(new TokenCache(token, to, new Date()));
        String url = request.getRequestURL().toString();
        String host = url.split(request.getRequestURI())[0];
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(mailSender.getUsername());
        helper.setTo(to);
        helper.setSubject("智能客服密码找回");
        String html = "<a href='"+ properties.getProperty("website") + "/#/resetPass?token=" + token +"' target='_blank'>"+ properties.getProperty("website") + "/#/resetPass</a>";
        helper.setText(html,true);
        mailSender.send(msg);
        inputStream.close();
    }
}
