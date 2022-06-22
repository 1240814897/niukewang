package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.servers.CommentService;
import com.nowcoder.community.servers.DiscussPostService;
import com.nowcoder.community.servers.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.Communityutil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    //添加帖子接口
    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    //传入标题和正文
    public String addDiscussPost(String title,String content){
        User user = hostHolder.getUser();
        if(user==null){
            return Communityutil.getJSONString(403,"你还没有登录");
        }
        //添加帖子的基本信息
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //报错的情况，将来统一处理
        return Communityutil.getJSONString(0,"发布成功！");
    }

    //获取帖子的详情
    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        //根据传入的id值去查询对应的帖子
        DiscussPost discussPost = discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",discussPost);
        //根据帖子的id去查询对应的作者
        User user = userService.findUserByid(discussPost.getUserId());
        model.addAttribute("user",user);

        //评论分页每页显示5条信息
        page.setLimit(5);
        //记录帖子的路口
        page.setPath("/discuss/detail" + discussPostId);
        //得到对应帖子的评论数目
        page.setRows(discussPost.getCommentCount());

        //评论：给帖子的评论
        //回复：给评论的评论
        //评论列表

        //获取在父帖子下的子帖子的列表
        List<Comment> commentList = commentService.findCommentsByEntity(
                ENTITY_TYPE_POST,discussPost.getId(),page.getOffset(),page.getLimit());
        //子帖子视图列表
        List<Map<String,Object>> commentVoList = new ArrayList<>();
        if(commentList != null){
            for(Comment comment : commentList){
                //评论VO
                Map<String,Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //作者
                commentVo.put("user",userService.findUserByid(comment.getUserId()));
                //回复子帖子列表
                List<Comment> replyList = commentService.findCommentsByEntity(
                        ENTITY_TYPE_COMMENT,comment.getId(),0,Integer.MAX_VALUE
                );
                //回复子帖子VO列表
                List<Map<String,Object>> replyVoList = new ArrayList<>();
                if(replyVoList != null){
                    for(Comment reply : replyList){
                        Map<String,Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply",reply);
                        //作者
                        replyVo.put("user",userService.findUserByid(reply.getUserId()));
                        //回复目标人
                        User target = reply.getTargetId() == 0 ? null : userService.findUserByid(reply.getTargetId());
                        replyVo.put("target",target);
                        replyVo.put("likeStatus",1);
                        replyVo.put("likeCount",1);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys",replyVoList);

                //统计回复子帖子数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT,comment.getId());
                commentVo.put("replyCount",replyCount);
                commentVo.put("likeStatus",1);
                commentVo.put("likeCount",1);
                commentVoList.add(commentVo);

            }
        }
        model.addAttribute("comments",commentVoList);
        return "/site/discuss-detail";
    }
}
