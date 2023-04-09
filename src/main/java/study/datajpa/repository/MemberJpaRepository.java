package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

@Repository
public class MemberJpaRepository {
    @PersistenceContext //스프링 컨테이너가 jpa에있는 영속성컨테이너를 가져다줌
    private EntityManager em;
    public Member save(Member member){
        em.persist(member);
        return member;
    }

    public Member find(Long id){
        return em.find(Member.class,id);
    }

}
