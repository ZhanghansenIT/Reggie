package com.it.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.it.reggie.common.R;
import com.it.reggie.entity.User;
import com.it.reggie.service.UserService;
import com.it.reggie.utils.SMSUtils;
import com.it.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService ;

    // 发送短信
    @PostMapping("/sendMsg")
    public R<String> sendMessage(@RequestBody User user , HttpSession session){
        // 获取手机号

        String phone = user.getPhone() ;
        // 如果手机不为空
        if(StringUtils.isNotEmpty(phone)){

            // 生成随机的验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString() ;

            log.info("code={}",code);
            // 调用阿里云的短信服务
            SMSUtils.sendMessage("瑞吉外卖Java项目实战","SMS_200702466",phone,code);


            // 需要将生成的验证保存到session

            session.setAttribute(phone,code);
            return R.success("手机验证码短信成功") ;
        }

        return R.error("手机验证码短信失败") ;
    }

    /**
     * 移动端登录　
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map , HttpSession session){

        log.info(map.toString());

        // 获取手机号
        String phone = map.get("phone").toString();

        // 获取验证码
        String code = map.get("code").toString() ;

        // 从session 中获取保存的验证码
        Object codeInsession = session.getAttribute(phone) ;
        if (codeInsession !=null && codeInsession.equals(code)){
            // 如果能够比对成功，说明登录成功　

            // 判断当前手机对应的用户是否是新用户，如果是新用户就自动完成注册

            // 从用户表中查询
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>() ;
            queryWrapper.eq(User::getPhone ,phone) ;
            User user  = userService.getOne(queryWrapper) ;
            if(user == null) {
                // 用户是新用户d
                user = new User();
                user.setPhone(phone); ;
                user.setStatus(1);
                userService.save(user) ;
            }
            //
            session.setAttribute("user",user.getId());
            return R.success(user) ;
        }
        return R.error("登录失败") ;
    }

}
