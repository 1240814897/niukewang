package com.nowcoder.community.servers;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.dao.LoginTickMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.Communityutil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {



    @Autowired
    private LoginTickMapper loginTickMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${community.path.domain}")
    private String domain;

    //根据id查询到用户数据
    public User findUserByid(int id){
        return userMapper.selectByid(id);
    }

    public Map<String,Object> register(User user){
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if(user == null){
            throw new IllegalArgumentException("参数不为空");
        }

        if(StringUtils.isBlank((user.getUsername()))){
            map.put("usernameMsp","账号不能为空！");
        }
        if(StringUtils.isBlank((user.getPassword()))){
            map.put("passwordMsp","密码不能为空！");
        }
        if(StringUtils.isBlank((user.getUsername()))){
            map.put("emailMsp","邮箱不能为空！");
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if(u!=null){
            map.put("usernameMsg","该账号已存在！");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null){
            map.put("emailMsg","该邮箱已被注册！");
        }

        //注册用户
        user.setSalt(Communityutil.generateUUID().substring(0,5));
        user.setPassword(Communityutil.md5(user.getPassword()+user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(Communityutil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreatTime(new Date());
        userMapper.insertUser(user);

        //激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());
        //http://localhost:8080/community/activation/101/code
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);
        String content = templateEngine.process("mail/activation",context);
        mailClient.sendMail(user.getEmail(),"激活账号",content);

        return  map;

    }

    //判断用户的激活状态
    public int activation(int userId,String code){
        User user = userMapper.selectByid(userId);
        if(user.getStatus()==1){
            return ACTIVETION_REPEAT;
            //判断激活码是否正确
        }else if(user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }else{
            return ACTIVATION_FAILURE;
        }
    }

    //登录
    public Map<String,Object> login(String username,String password,int expiredSeconds){
        Map<String,Object> map = new HashMap<>();

        //空值处理
        if(StringUtils.isBlank(username)){
            map.put("usernameMsg","账号不能为空！");
            return map;
        }

        if(StringUtils.isBlank(password)){
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        User user = userMapper.selectByName(username);
        //验证账号
        if(user == null){
            map.put("usernameMsg","该账号不存在!");
            return map;
        }
        //验证状态
        if(user.getStatus() == 0){
            map.put("usernameMsg","该账号未激活!");
            return map;
        }

        //验证密码
        password = Communityutil.md5(password + user.getSalt());
        if(!user.getPassword().equals(password)){
            map.put("passwordMsg","该密码不正确！");
            return map;
        }

        //生成登录凭证，当一个用户激活的时候向ticket表新建用户达到一对一
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(Communityutil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));
        loginTickMapper.insertLoginTicket(loginTicket);
        map.put("ticket",loginTicket.getTicket());

        return map;
    }

    public void logout(String ticket){
        loginTickMapper.updateStatus(ticket,1);
    }

    public LoginTicket findLoginTicket(String ticket){
        return loginTickMapper.selectByTicket(ticket);
    }

    public int updateHeader(int userId,String headerUrl){
        return userMapper.updateHeader(userId,headerUrl);
    }

    public int updatePassword(int userId,String password) {return userMapper.updatePassword(userId,password);}

}
