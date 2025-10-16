package edu.trincoll.report;

import edu.trincoll.repository.MemberRepository;

public class MemberReportGenerator implements ReportGenerator {

    private final MemberRepository memberRepository;

    public MemberReportGenerator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public String generateReport() {
        long totalMembers = memberRepository.count();
        return "Total members: " + totalMembers;
    }
}