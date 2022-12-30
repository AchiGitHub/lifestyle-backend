package com.lifestyleservice.lifestyle.service.impl;

import com.lifestyleservice.lifestyle.entity.Member;
import com.lifestyleservice.lifestyle.enums.Gender;
import com.lifestyleservice.lifestyle.repository.MemberRepository;
import com.lifestyleservice.lifestyle.service.MemberService;
import com.lifestyleservice.lifestyle.util.RequestHelper;
import com.lifestyleservice.lifestyle.util.TransportDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {

    private MemberRepository memberRepository;
    private RequestHelper requestHelper;

    @Autowired
    private MemberServiceImpl(MemberRepository memberRepository, RequestHelper requestHelper) {
        this.memberRepository = memberRepository;
        this.requestHelper = requestHelper;
    }

    @Override
    public TransportDto createMember(Member member) {
        memberRepository.save(member);
        return requestHelper.setResponse(member);
    }

    @Override
    public TransportDto updateMember(UUID id, Member member) {
        try {
            Member updateMember = memberRepository.findById(id).get();
            memberRepository.save(member);
            return requestHelper.setResponse(updateMember);
        } catch (Exception e) {
            return requestHelper.setError(HttpStatus.NOT_FOUND, "Member not found!");
        }
    }

    @Override
    public TransportDto deleteMember(UUID id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member != null) {
            memberRepository.deleteById(id);
            return requestHelper.setResponse(id + " delete successfully");
        } else {
            return requestHelper.setError(HttpStatus.NOT_FOUND,id + " not found!");
        }
    }

    @Override
    public TransportDto getAllMembers() {
        List<Member> allMembers = memberRepository.findAll();
        return requestHelper.setResponse(allMembers);
    }

    @Override
    public TransportDto getMember(UUID id) {
        Optional<Member> member = memberRepository.findById(id);
        if (member == null) {
            return requestHelper.setError(HttpStatus.NOT_FOUND, "Member not found!");
        }
        return requestHelper.setResponse(member);
    }

    @Override
    public TransportDto getMembersByIdList(List<UUID> ids) {
        List<Member> members = memberRepository.findByIdIn(ids);
        return requestHelper.setResponse(members);
    }

    @Override
    public void setAllMembers() {
        TestData data = new TestData();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for(int i = 0; i < data.data.length; i++) {
            Member member = new Member();
            member.setAddress("");
            member.setDob(LocalDateTime.parse((data.data[i][5] + " 00:00").toString(), formatter));
            member.setFirstName((String) data.data[i][3]);
            member.setLastName((String) data.data[i][4]);

            if ((String) data.data[i][4] == "M") {
                member.setGender(Gender.MALE);
            } else {
                member.setGender(Gender.FEMALE);
            }

            member.setHeight(Double.parseDouble(data.data[i][16].toString())*100);
            member.setWeight(Double.parseDouble(data.data[i][15].toString()));
            member.setMobileNumber((String) data.data[i][14]);
            member.setOccupation((String) data.data[i][7]);
            member.setCreatedBy("SYSTEM");
            member.setCreatedDate(LocalDateTime.now());
            member.setLastModifiedBy("SYSTEM");
            member.setLastModifiedDate(LocalDateTime.now());
            memberRepository.save(member);
        }
    }
}
