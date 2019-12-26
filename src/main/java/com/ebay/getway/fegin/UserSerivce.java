package com.ebay.getway.fegin;

import com.superarmyknife.toolbox.handle.ResponseData;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "PC-SERVICE")
public interface UserSerivce {
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseData login(@RequestParam("username") String username,
                              @RequestParam("password") String password);
}
