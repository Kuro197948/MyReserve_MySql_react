package com.example.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberUpgradeService {

    private final MemberMapper memberMapper;

    @Transactional
    public void upgradeToPremium(Integer memberId) {
        memberMapper.updateMemberType(memberId, 2);
    }

    @Transactional
    public void downgradeToRegular(Integer memberId) {
        memberMapper.updateMemberType(memberId, 1);
    }
}