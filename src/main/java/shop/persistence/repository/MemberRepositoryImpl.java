package shop.persistence.repository;

import org.springframework.stereotype.Repository;
import shop.domain.member.Member;
import shop.domain.member.MemberName;
import shop.domain.member.Password;
import shop.domain.repository.MemberRepository;
import shop.persistence.dao.MemberDao;
import shop.persistence.entity.MemberEntity;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberDao memberDao;

    public MemberRepositoryImpl(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public Long save(Member member) {
        MemberEntity memberEntity = new MemberEntity(member.getName(), member.getPassword());

        return memberDao.insertMember(memberEntity);
    }

    @Override
    public List<Member> findAll() {
        List<MemberEntity> allMemberEntities = memberDao.findAll();

        return allMemberEntities.stream()
                .map(this::toMember)
                .collect(Collectors.toList());
    }

    @Override
    public Member findById(Long id) {
        MemberEntity findMember = memberDao.findById(id);

        return toMember(findMember);
    }

    @Override
    public Member findByName(String name) {
        MemberEntity findMember = memberDao.findByName(name);

        return toMember(findMember);
    }

    private Member toMember(MemberEntity memberEntity) {
        return new Member(
                memberEntity.getId(),
                new MemberName(memberEntity.getName()),
                Password.createFromEncryptedPassword(memberEntity.getPassword())
        );
    }

    @Override
    public boolean isExistMemberByName(String name) {
        return memberDao.isExistByName(name);
    }
}
