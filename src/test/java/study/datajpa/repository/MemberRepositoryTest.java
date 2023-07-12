package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)    //실무에선 X
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;
    @Autowired TeamRepository teamRepository;
    @PersistenceContext
    EntityManager em;
    @Test
    public void testMember(){
        //스프링 데이터 jpa가 구현 클래스 대신생성, 따라서 인터페이스지만 사용가능
        System.out.println("memberRepository.getClass() = " + memberRepository.getClass());
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(savedMember.getId()).get();

        assertThat(findMember.getId()).isEqualTo(member.getId());
        assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        assertThat(findMember).isEqualTo(member);
    }
    @Test
    public void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //단건조회검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //count 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);
    }

    @Test
    public void findByUsernameAndAgeGreaterThan(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);

    }

    @Test
    public void findHelloBy(){
        List<Member> helloBy = memberRepository.findTop3HelloBy();
    }

    @Test
    public void findUser(){ //jpql권장!
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("AAA", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> result = memberRepository.findUser("AAA",10);
        assertThat(result.get(0)).isEqualTo(member1);
    }

    @Test
    public void findUsernameList(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String > usernameList = memberRepository.findUsernameList();
        for (String s : usernameList) {
            System.out.println("s="+s);
        }
    }
    @Test
    public void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("AAA", 10);
        member1.setTeam(team);
        memberRepository.save(member1);


        List<MemberDto> memberDtos = memberRepository.findMemberDto();
        for (MemberDto memberDto : memberDtos) {
            System.out.println(memberDto);
        }
    }
    @Test
    public void findByNames(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member > result = memberRepository.findByNames(Arrays.asList("AAA","BBB"));
        for (Member m : result) {
            System.out.println(m);
        }
    }

    @Test
    public void returnType(){
        Member member1 = new Member("AAA", 10);
        Member member2 = new Member("BBB", 20);
        memberRepository.save(member1);
        memberRepository.save(member2);

        Optional<Member> aaa = memberRepository.findOptionalByUsername("AAA");
        System.out.println("aaa = " + aaa );
    }
    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;
        //pageable interface구현체 , 보통 PageRequest 많이 씀
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.Direction.DESC, "username");//sort생략가능

        //when  //반환타입이 slice면 totalCount안날리고 page면 날림 , slice면 size+1개가져옴
        Page<Member> page = memberRepository.findByAge(age, pageRequest);
//        Slice<Member> page = memberRepository.findByAge(age, pageRequest);
//        List<Member> page = memberRepository.findByAge(age, pageRequest); 걍 list로받아도됨 밑에기능은 안되지만 걍 짤라서가져오고싶을때 사용하면댐
        Page<MemberDto> memberDtos = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        //then
        List<Member> content = page.getContent();//페이지에들어있는 컨텐츠꺼냄
        long totalElements = page.getTotalElements();

        assertThat(content.size()).isEqualTo(3);        //row
        assertThat(page.getTotalElements()).isEqualTo(5);//totalCount
        assertThat(page.getNumber()).isEqualTo(0);      //현재페이지
        assertThat(page.getTotalPages()).isEqualTo(2);  //총페이지
        assertThat(page.isFirst()).isTrue();  //첫페이지냐
        assertThat(page.hasNext()).isTrue();  //다음페이지가있냐
    }
    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));

        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //벌크연산은 영속성컨텍스트와 관련없이 실행되기때문에 영속성컨텍스트 한번날려줘야함(초기화), 벌크연산만하는 트랜잭션이면안날려도댐
//        em.clear(); //날림     이러면 age 41로조회됨 날림없으면 걍 40으로나옴
//      하지만 벌크연산쿼리에 @Modifying(clearAutomatically = true) 붙이면 자동으로 쿼리날리고서 clear됨
        List<Member> result = memberRepository.findListByUsername("member5");
        Member member5 = result.get(0);
        System.out.println("member5 = " + member5);


        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void findMemberLazy() throws Exception{
        //given
        //member1 -> teamA
        //member2 -> teamB

        Team teamA = new Team("testA");
        Team teamB = new Team("testB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);
        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",10,teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();//영속성컨텍스트 초기화

        //when N + 1 @EntityGraph로 해결가능 간단할땐 이걸로쓰면댐 복잡하면 jpql fetch join
        //select Member
        List<Member> memberList = memberRepository.findByUsername("member1");
        for (Member member : memberList) {
            System.out.println("member.getUsername() = " + member.getUsername());
            System.out.println("member.getTeam().getClass() = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

        //then
    }
    @Test
    public void queryHint(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        Member findMember = memberRepository.findReadOnlyByUsername("member1");   //cf.실무에서는 Optional똑바로사용해랑
        findMember.setUsername("member2");
        em.flush();
        //@QueryHints 사용하면 flush해도 readOnly이기때문에 변경감지안됨. 조회만한다면 성능최적화를 위해 쓸수있지만 그닥많이쓰진않는다
    }

    @Test
    public void lock(){
        //given
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //when
        List<Member> findMembers = memberRepository.findLockByUsername("member1");
        //select ~ for update 붙음 실시간트래픽이 많은 서비스에서는 최대한걸지말고 옵티미스틱락이 낫다
        //실시간트래픽보다는 정합성이 중요하다면 페시미스틱 락을 사용하삼
    }

}