package com.yyandywt99.pandoraNext.controller;

import com.yyandywt99.pandoraNext.pojo.Result;
import com.yyandywt99.pandoraNext.pojo.systemSetting;
import com.yyandywt99.pandoraNext.service.impl.systemServiceImpl;
import com.yyandywt99.pandoraNext.service.loginService;
import com.yyandywt99.pandoraNext.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Yangyang
 * @create 2023-11-17 14:15
 */
@Slf4j
@RestController()
@RequestMapping("/api")
public class loginColltroller {
    @Autowired
    private loginService loginService;

    @Autowired
    private systemServiceImpl systemService;

    /**
     * 登录用户接口
     * @return "jwt令牌！"or"NOT_LOGIN"
     */
    @PostMapping("/login")
    public Result login(@RequestBody systemSetting setting) {
        String res = loginService.login(setting);
        log.info(res);
        if(res.contains("登录成功")){
            String password = setting.getLoginPassword();
            JwtUtils.setSignKey(password);
            log.info("登录成功");
            Map<String,Object> chaims = new HashMap<String,Object>();
            chaims.put("password",password);
            String s = JwtUtils.generateJwt(chaims);
            return Result.success(s);
        }
        return Result.error("登陆失败");
    }

    /**
     * 验证是否登录成功
     * @return 没登陆成功否则返回"NOT_LOGIN"
     */
    @PostMapping("/loginToken")
    public Result loginToken(@RequestParam("token") String token){

        String password = systemService.selectSetting().getLoginPassword();
        JwtUtils.setSignKey(password);
        log.info(token);
        if(!StringUtils.hasLength(token)){
            log.info("请求头token为空,返回未登录的信息");
            return Result.error("NOT_LOGIN");
        }
        try {

            Claims claims = JwtUtils.parseJWT(token);
            String res = claims.get("password").toString();
            if(res.equals(password)){
                log.info("令牌合法，可以正常登录");
                return Result.success("YES_LOGIN");
            }

            com.yyandywt99.pandoraNext.util.JwtUtils.parseJWT(token);
            log.info("令牌合法，可以正常登录");
            return Result.success("YES_LOGIN");
        } catch (Exception e) {//jwt解析失败
            e.printStackTrace();
            log.info("解析令牌失败, 返回未登录错误信息");
            Result error = Result.error("NOT_LOGIN");
            return error;
        }
    }
}
