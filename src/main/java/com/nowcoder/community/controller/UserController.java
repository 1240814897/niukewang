package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.servers.AlphaServers;
import com.nowcoder.community.servers.UserService;
import com.nowcoder.community.util.Communityutil;
import com.nowcoder.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //配合拦截器使用拦截只有用户登录了才能使用的特定方法
    @LoginRequired
    @RequestMapping(path = "setting",method = RequestMethod.GET)
    public String getSettingPage(){
        return "/site/setting";
    }

    @LoginRequired
    @RequestMapping(path = "upload",method = RequestMethod.POST)
    public String uploadHeader(MultipartFile headerImage, Model model){
        if(headerImage==null){
            model.addAttribute("error","您还没有选择图片!");
            return "/site/setting";
        }

        //判断文件名是否正确
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        if(StringUtils.isBlank(suffix)){
            model.addAttribute("error","文件格式不正确！");
            return "/site/setting";
        }

        //生成随机文件名
        fileName = Communityutil.generateUUID() + suffix;
        //确定文件存放路径
        File dest = new File(uploadPath + "/" + fileName);
        try {
            //存储文件
            headerImage.transferTo(dest);
        }catch (IOException e){
            logger.error("上传文件失败："+e.getMessage());
            throw new RuntimeException("上传文件失败，服务器发送异常！",e);
        }

        //更新当前用户的头像路径
        //http://localhost:8080/community/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain+contextPath+"/user/header/"+fileName;
        userService.updateHeader(user.getId(),headerUrl);

        return "redirect:/index";
    }

    //前端访问数据库的头像地址，头像地址定位本地，故而访问以下请求方法
    @RequestMapping(path = "/header/{fileName}",method = RequestMethod.GET)
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        //服务器存放路径
        fileName = uploadPath+"/"+fileName;
        //文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        //响应图片
        response.setContentType("image/"+suffix);
        try (
                FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream();
                ){
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1){
                os.write(buffer,0,b);
            }
        }catch (IOException e){
            logger.error("读取头像失败！：" + e.getMessage());
        }
    }

    //更改密码接口
    @RequestMapping(path = "/changePassword",method = RequestMethod.POST)
    public String changepassword(String oldPassword,String newPassword,String confirmPassword,Model model){
        //对比两次密码是否一致
        if(!newPassword.equals(confirmPassword)){
            model.addAttribute("confirmPasswordMsg","两次密码不一致！");
            return "/site/setting";
        }
        HashMap<String,Object> map = new HashMap<>();
        User user = hostHolder.getUser();
        if(user.getPassword().equals(Communityutil.md5(oldPassword+user.getSalt()))){
            int result = userService.updatePassword(user.getId(),Communityutil.md5(newPassword+user.getSalt()));
            if(result==0){
                model.addAttribute("newPasswordMsg","密码格式错误！");
                return "/site/setting";
            }else{
                model.addAttribute("confirmPasswordMsg","密码修改成功！");
                return "redirect:/logout";
            }
        }else {
            model.addAttribute("oldPasswordMsg","初始密码不正确！");
            return "/site/setting";
        }

    }


}
