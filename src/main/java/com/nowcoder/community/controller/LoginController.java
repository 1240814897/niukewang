package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.config.KaptchaConfig;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.servers.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.Communityutil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("server.servlet.context-path")
    private String contextpath;


    @Autowired
    private Producer kaptchaProducer;

    @Autowired
    private UserService userService;

    //注册页面
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String getRegisterPage(){
        return "/site/register";
    }

    //登录页面
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String getLoginPage(){
        return "/site/login";
    }

    //忘记密码界面
    @RequestMapping(value = "/forget",method = RequestMethod.GET)
    public String forgetpasswordPage(){
        return "/site/forget";
    }

    //注册页面提交功能
    @RequestMapping(value = "/register",method = RequestMethod.POST)
    public String register(Model model, User user){
        //map封装特定的消息和消息体进行返回前端渲染
        Map<String,Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg","注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target","/index");
            return "/site/operate-result";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            return "/site/register";
        }

    }

    //http://localhost:8080/community/activation/101/code
    //激活用户状态接口
    @RequestMapping(path = "/activation/{userId}/{code}",method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId,@PathVariable("code") String code){
        //判断激活情况
        int result = userService.activation(userId,code);
        if(result == ACTIVATION_SUCCESS){
            model.addAttribute("msg","激活成功，您的账号已经可以正常使用了！");
            model.addAttribute("target","/login");
        }else if(result == ACTIVATION_FAILURE){
            model.addAttribute("msg","重复激活，该账号已经激活过了！");
            model.addAttribute("target","/index");
        }else{
            model.addAttribute("msg","激活失败，您输入的激活码错误！");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }

    //验证码生成接口
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse httpServletResponse, HttpSession httpSession){
        //生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码存入session
        httpSession.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        httpServletResponse.setContentType("image/png");
        try{
            OutputStream os = httpServletResponse.getOutputStream();
            ImageIO.write(image,"png",os);
        }catch (IOException e){
            logger.error("响应验证码失败！",e.getMessage());
        }


    }

    //用户登录接口
    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean remember,
                        Model model,HttpSession session,HttpServletResponse response){
        //验证码存入session中检查用户输入是否正确
        String kaptcha = (String) session.getAttribute("kaptcha");
        if(StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code)|| !kaptcha.equalsIgnoreCase(code)){
            model.addAttribute("codeMsg","验证码不正确！");
            return "/site/login";
        }

        //检查账号密码
        int expiredSeconds = remember ? REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String,Object> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket")){
            //设置cook的数据
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            //设置cookie路径
            cookie.setPath(contextpath);
            //设置cookie生命
            cookie.setMaxAge(expiredSeconds);
            //返回给用户
            response.addCookie(cookie);
            return "redirect:/index";
        }else{
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }

    //退出账户接口
    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket){
        //更改用户的登录状态
        userService.logout(ticket);
        return "redirect:/login";
    }
}
