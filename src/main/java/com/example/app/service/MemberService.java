package com.example.app.service;

import java.util.List;

import com.example.app.domain.Member;
import com.example.app.domain.MemberType;

public interface MemberService {

    List<Member> getMemberList();

    Member getMemberById(Integer id);

    List<Member> getMemberListByPage(int page, int numPerPage);

    int getTotalPages(int numPerPage);

    void addMember(Member member);

    void editMember(Member member);

    void deleteMember(Integer id);

    List<MemberType> getTypeList();
    
    Member getMemberByEmail(String email);

    void updateCredentials(Member member);
    
    
}

