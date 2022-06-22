package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //搜索父帖子的子帖子
    List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //搜索评论父帖子的子帖子数目
    int selectCountByEntity(int entityType, int entityId);


    //插入评论
    int insertComment(Comment comment);
}
