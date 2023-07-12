package study.datajpa.repository;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    //스프링 데이터 jpa가 구현 클래스 대신생성, 따라서 인터페이스지만 사용가능
    //@Repository 어노테이션 생략가넝 스프링 데이터 jpa가 이것도 알아서해줌.

    List<Member> findByUsernameAndAgeGreaterThan(String username, int age);
    List<Member> findTop3HelloBy(); //앞에뭐넣어도 By만잘쓰면댐

    @Query("select m from Member m where m.username= :username and m.age= :age") //jpql
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m ")
    List<String> findUsernameList();//유저이름만반환
    @Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();//dto로반환
    @Query("select m from Member  m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    List<Member> findListByUsername(String username);
    Member findMemberByUsername(String username);
    Optional<Member> findOptionalByUsername(String username);

    @Query(value = "select m from Member m left join m.team t",
            countQuery = "select count(m.username) from Member m")  //count query 분리가능 (실무에서쓰기좋음)
    Page<Member> findByAge(int age, Pageable pageable);

    @Modifying(clearAutomatically = true)  //얘를붙여줘야 update됨 빼면에러, 반환타입int
    @Query("update Member m set m.age=m.age+1 where m.age>=:age")
    int bulkAgePlus(@Param("age") int age);
    @Query("select m from Member m left join fetch m.team")
    List<Member> findMemberFetchJoin();

    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")    //쿼리짜고 entityGraph로 fetchjoin 할수있음
    List<Member> findMemberEntityGraph();

    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(@Param("username") String username);

    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly",value = "true"))
    Member findReadOnlyByUsername(String username);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String username);
}
