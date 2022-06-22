package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    /**
     *  根据用户的id查到帖子的数据，包装在List中
     */
    List<DiscussPost> selectDiscussPose(int userid,int offset,int limit);

    /**
     *  根据用户id查询帖子的数量
     */
    //@Param注解用于给参数取别名
    //如果只有一个参数，并且在<if>里使用，则必须加别名
    int selectDiscussPostRows(@Param("userid") int userid);

    int insertDiscussPost(DiscussPost discussPost);

    //搜索某一人的帖子
    DiscussPost selectDiscussPostById(int id);

    //更新帖子的评论数目
    int updateCommentCount(int id,int commentCount);
}
