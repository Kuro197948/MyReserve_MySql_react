package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.Member;

@Mapper
public interface MemberMapper {

    List<Member> selectAll();

    Member selectById(Integer id);

    Member selectByName(String name);

    Member selectByEmail(String email);

    List<Member> selectLimited(@Param("offset") int offset, @Param("limit") int limit);

    long count();

    void insert(Member member);

    void update(Member member);

    void updatePasswordById(@Param("id") Integer id, @Param("loginPass") String loginPass);

    void updateCredentialsById(Member member);

    void delete(Integer id);
    
    int updateMemberType(@Param("memberId") Integer memberId,
            @Param("typeId") Integer typeId);
}