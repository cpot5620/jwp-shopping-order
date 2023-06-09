package cart.member.dao;

import cart.config.DaoTest;
import cart.member.domain.Member;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static cart.fixtures.MemberFixtures.Member_Ber;
import static cart.fixtures.MemberFixtures.Member_Dooly;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
class MemberDaoTest extends DaoTest {

    @Test
    void 유저_id를_통해_유저를_찾는다() {
        final Member member = memberDao.getMemberById(2L);

        assertThat(member).usingRecursiveComparison().isEqualTo(Member_Ber.ENTITY);
    }

    @Test
    void 이메일_주소를_통해_유저를_찾는다() {
        final Member member = memberDao.getMemberByEmail("ber@ber.com");

        assertThat(member).usingRecursiveComparison().isEqualTo(Member_Ber.ENTITY);
    }

    @Test
    void 유저를_추가하다() {
        final Member member = Member.of(null, "test@test.com", "1234", 0L);

        assertDoesNotThrow(() -> memberDao.addMember(member));
    }

    @Test
    void 유저_정보를_변경하다() {
        final Member member = Member.of(1L, "test@test.com", "1234", 0L);

        assertDoesNotThrow(() -> memberDao.updateMember(member));
    }

    @Test
    void 유저를_삭제하다() {
        assertDoesNotThrow(() -> memberDao.deleteMember(1L));
    }

    @Test
    void 모든_유저들을_구한다() {
        final List<Member> members = memberDao.getAllMembers();

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(members).hasSize(3);
            softAssertions.assertThat(members.get(0)).usingRecursiveComparison().isEqualTo(Member_Dooly.ENTITY);
        });
    }
}
