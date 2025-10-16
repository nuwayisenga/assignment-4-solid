package edu.trincoll.service;

import edu.trincoll.model.Member;
import edu.trincoll.repository.MemberRepository;
import org.springframework.stereotype.Service;
/**
 * Service for managing library member operations.
 * Handles member checkout counts and checkout eligibility.
 */
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public void incrementCheckoutCount(Member member) {
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        memberRepository.save(member);
    }

    public void decrementCheckoutCount(Member member) {
        member.setBooksCheckedOut(member.getBooksCheckedOut() - 1);
        memberRepository.save(member);
    }

    public boolean canCheckout(Member member, int maxBooks) {
        return member.getBooksCheckedOut() < maxBooks;
    }
}