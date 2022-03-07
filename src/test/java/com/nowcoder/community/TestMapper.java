package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class TestMapper {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser(){


        User user = userMapper.selectByid(101);
        System.out.println(user);

        user = userMapper.selectByName("liubei");
        System.out.println(user);

        user = userMapper.selectByEmail("nowcoder101@sina.com");
        System.out.println(user);
    }

    @Test
    public void testinsert(){
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("test@qq.com");
        user.setHeaderUrl("http://nowcoder.com/101.png");
        user.setCreatTime(new Date());
        int row = userMapper.insertUser(user);

        System.out.println(row);
        System.out.println(user.getId());
    }

    @Test
    public void testupdate(){
        int row = userMapper.updateStatus(150,1);
        System.out.println(row);
        row = userMapper.updateHeader(150,"http://nowcoder.com/102.png");
        System.out.println(row);
        row = userMapper.updatePassword(150,"hello");
        System.out.println(row);
    }

    @Test
    public void testdiscusspost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPose(149,0,10);
        for(DiscussPost post : list){
            System.out.println(post);
        }

        int row = discussPostMapper.selectDiscussPostRows(149);
        System.out.println(row);
    }
}
