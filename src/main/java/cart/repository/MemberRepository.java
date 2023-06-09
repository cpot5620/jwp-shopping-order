package cart.repository;

import static cart.exception.ErrorMessage.NOT_FOUND_MEMBER;

import cart.dao.MemberDao;
import cart.dao.entity.MemberEntity;
import cart.domain.Member;
import cart.exception.MemberException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {
    private final MemberDao memberDao;

    public MemberRepository(final MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    public Long save(Member member) {
        MemberEntity memberEntity = toEntity(member);

        return memberDao.save(memberEntity);
    }

    private MemberEntity toEntity(Member member) {
        return new MemberEntity(null, member.getEmail(), member.getPassword(), member.getPoint());
    }

    public Member findById(Long id) {
        MemberEntity memberEntity = memberDao.findById(id)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        return toDomain(memberEntity);
    }

    public Member findByEmail(final String email) {
        MemberEntity memberEntity = memberDao.findByEmail(email)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        return toDomain(memberEntity);
    }

    public List<Member> findAll() {
        List<MemberEntity> memberEntities = memberDao.findAll();

        return memberEntities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public void updatePoint(Member member) {
        MemberEntity memberEntity = new MemberEntity(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getPoint(),
                null, null
        );

        int updatedRow = memberDao.update(memberEntity);

        if (updatedRow == 0) {
            throw new MemberException(NOT_FOUND_MEMBER);
        }
    }

    private Member toDomain(MemberEntity memberEntity) {
        return new Member(
                memberEntity.getId(),
                memberEntity.getEmail(),
                memberEntity.getPassword(),
                memberEntity.getPoint()
        );
    }

    public int findPointByMember(Member member) {
        return memberDao.findPointById(member.getId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));
    }
}
