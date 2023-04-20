package study.datajpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.datajpa.entity.Member;

public interface MemberRepository extends JpaRepository<Member,Long> {
    //스프링 데이터 jpa가 구현 클래스 대신생성, 따라서 인터페이스지만 사용가능
    //@Repository 어노테이션 생략가넝 스프링 데이터 jpa가 이것도 알아서해줌.
}
