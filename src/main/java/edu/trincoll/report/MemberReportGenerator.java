package edu.trincoll.report;

import edu.trincoll.repository.MemberRepository;
/**
 * Report generator for library membership statistics.
 * Generates a report showing the total count of library members.
 */
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