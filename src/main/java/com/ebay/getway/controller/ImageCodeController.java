package com.ebay.getway.controller;

import com.ebay.getway.handle.ImageCodeHandle;
import com.google.code.kaptcha.Producer;
import jodd.io.FastByteArrayOutputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class ImageCodeController {
    @Autowired
    private Producer producer;

    @Autowired
    private RedisTemplate redisTemplate;
    @RequestMapping("code")
    public void code(HttpServletResponse response, @RequestParam(name = "randomSn") String randomSn){

        String text  =producer.createText();
        log.debug("生成的验证码为　{}", text);
        BufferedImage image = producer.createImage(text);

        //保存验证码信息
        redisTemplate.opsForValue().set(ImageCodeHandle.DEFAULT_CODE_KEY + randomSn, text, 60, TimeUnit.SECONDS) ;

        log.debug("已保存");

//        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpeg", response.getOutputStream());
        }catch (Exception e) {
            log.error("ImageIO write err", e);
        }
    }
}
