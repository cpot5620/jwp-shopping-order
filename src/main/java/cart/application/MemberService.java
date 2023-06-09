package cart.application;

import cart.domain.member.Email;
import cart.domain.member.Member;
import cart.dto.response.MemberResponse;
import cart.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public MemberResponse findMemberById(final Long id) {
        final Member member = memberRepository.getMemberById(id);
        return MemberResponse.of(member);
    }

    public MemberResponse findMemberByEmail(final String email) {
        final Member member = memberRepository.getMemberByEmail(new Email(email));
        return MemberResponse.of(member);
    }

    public List<MemberResponse> getAllMembers() {
        return memberRepository.getAllMembers()
                .stream()
                .map(MemberResponse::of)
                .collect(Collectors.toUnmodifiableList());
    }
}
