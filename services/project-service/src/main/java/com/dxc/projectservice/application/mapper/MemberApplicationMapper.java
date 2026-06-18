package com.dxc.projectservice.application.mapper;

import com.dxc.projectservice.application.dto.member.res.MemberResponse;
import com.dxc.projectservice.domain.model.entity.Member;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MemberApplicationMapper {

    public MemberResponse toResponse(Member member) {
        if (member == null) return null;
        
        return new MemberResponse(
            member.getId(),
            member.getProjectId(),
            member.getUserId(),
            member.getRole(),
            member.getAllocationPercentage(),
            member.getStatus(),
            member.getJoinedAt(),
            member.getLeftAt(),
            member.getCreatedAt(),
            member.getUpdatedAt()
        );
    }
    
    public List<MemberResponse> toResponseList(List<Member> members) {
        if (members == null) return null;
        return members.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
