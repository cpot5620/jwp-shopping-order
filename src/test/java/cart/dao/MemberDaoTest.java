package cart.dao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import cart.dao.entity.MemberEntity;
import cart.test.RepositoryTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

@RepositoryTest
class MemberDaoTest {

    @Autowired
    private MemberDao memberDao;

    @Test
    @DisplayName("findAll 메서드는 모든 멤버 데이터를 조회한다.")
    void findAll() {
        Long firstMemberId = memberDao.save(new MemberEntity("a@a.com", "password1", 0));
        Long secondMemberId = memberDao.save(new MemberEntity("b@b.com", "password2", 0));

        List<MemberEntity> result = memberDao.findAll();

        MemberEntity firstMember = memberDao.findById(firstMemberId).get();
        MemberEntity secondMember = memberDao.findById(secondMemberId).get();
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0)).usingRecursiveComparison().isEqualTo(firstMember),
                () -> assertThat(result.get(1)).usingRecursiveComparison().isEqualTo(secondMember)
        );
    }

    @Test
    @DisplayName("update 메서드는 멤버 정보를 수정한다.")
    void update() {
        MemberEntity member = new MemberEntity("a@a.com", "password1", 0);
        Long memberId = memberDao.save(member);
        MemberEntity newMemberEntity = new MemberEntity(memberId, "b@b.com", "password2", 10);

        memberDao.update(newMemberEntity);

        Optional<MemberEntity> result = memberDao.findById(memberId);
        assertAll(
                () -> assertThat(result).isNotEmpty(),
                () -> assertThat(result.get().getId()).isEqualTo(memberId),
                () -> assertThat(result.get().getEmail()).isEqualTo(newMemberEntity.getEmail()),
                () -> assertThat(result.get().getPassword()).isEqualTo(newMemberEntity.getPassword()),
                () -> assertThat(result.get().getPoint()).isEqualTo(newMemberEntity.getPoint())
        );
    }

    @Nested
    @DisplayName("findById 메서드는 ")
    class FindById {

        @Test
        @DisplayName("조회 시 ID와 일치하는 멤버가 존재하면 해당 멤버 데이터를 반환한다.")
        void getMember() {
            String email = "a@a.com";
            String password = "password1";
            Long savedMemberId = memberDao.save(new MemberEntity(email, password, 0));

            Optional<MemberEntity> result = memberDao.findById(savedMemberId);

            MemberEntity findMember = memberDao.findByEmailAndPassword(email, password).get();
            assertAll(
                    () -> assertThat(result).isNotEmpty(),
                    () -> assertThat(result.get()).usingRecursiveComparison().isEqualTo(findMember)
            );
        }

        @Test
        @DisplayName("조회 시 ID와 일치하는 멤버가 존재하지 않으면 빈 값을 반환한다.")
        void getEmpty() {
            Optional<MemberEntity> result = memberDao.findById(-1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByEmail 메서드는 ")
    class FindByEmail {

        @Test
        @DisplayName("조회 시 이메일과 일치하는 멤버가 존재하지 않으면 빈 값을 반환한다.")
        void getEmpty() {
            String email = "a@a.com";
            String password = "password1";
            memberDao.save(new MemberEntity(email, password, 0));

            Optional<MemberEntity> result = memberDao.findByEmail("b@b.com");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("조회 시 이메일과 일치하는 멤버가 존재하면 해당 멤버 데이터를 반환한다.")
        void getMember() {
            String email = "a@a.com";
            String password = "password1";
            Long savedMemberId = memberDao.save(new MemberEntity(email, password, 0));

            Optional<MemberEntity> result = memberDao.findByEmail(email);

            MemberEntity findMember = memberDao.findById(savedMemberId).get();
            assertAll(
                    () -> assertThat(result).isNotEmpty(),
                    () -> assertThat(result.get()).usingRecursiveComparison().isEqualTo(findMember)
            );
        }
    }

    @Nested
    @DisplayName("getMemberByEmailAndPassword 메서드는 ")
    class GetMemberByEmailAndPassword {

        @Test
        @DisplayName("조회 시 이메일, 비밀번호와 일치하는 멤버가 존재하면 해당 멤버 데이터를 반환한다.")
        void getMember() {
            String email = "a@a.com";
            String password = "password1";
            Long savedMemberId = memberDao.save(new MemberEntity(email, password, 0));

            Optional<MemberEntity> result = memberDao.findByEmailAndPassword(email, password);

            MemberEntity findMember = memberDao.findById(savedMemberId).get();
            assertAll(
                    () -> assertThat(result).isNotEmpty(),
                    () -> assertThat(result.get()).usingRecursiveComparison().isEqualTo(findMember)
            );
        }

        @ParameterizedTest
        @CsvSource(value = {"b@b.com:password1", "a@a.com:password2", "b@b.com:password2"}, delimiter = ':')
        @DisplayName("조회 시 이메일, 비밀번호와 일치하는 멤버가 존재하지 않으면 빈 값을 반환한다.")
        void getEmpty(String email, String password) {
            memberDao.save(new MemberEntity("a@a.com", "password1", 0));

            Optional<MemberEntity> result = memberDao.findByEmailAndPassword(email, password);

            assertThat(result).isEmpty();
        }
    }
}
