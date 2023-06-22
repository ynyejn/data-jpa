package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import java.util.List;
import java.util.Optional;

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

    public Optional<Member> findById(Long id){
        Member member = em.find(Member.class,id);
        return Optional.ofNullable(member); //null일수도있다는것을 옵셔널로 감싸서 제공.
    }

    public long count(){
        return em.createQuery("select count(m) from Member m",Long.class).getSingleResult();//단 건인 경우
    }

    public void delete(Member member){
        em.remove(member);
    }

    public List<Member> findAll(){
        return em.createQuery("select m from Member m",Member.class)
                .getResultList();
    }

    public List<Member> findByUsernameAndAgeGreaterThen(String username, int age){
        return em.createQuery("select m from Member m where m.username = :username and m.age>:age " )
                .setParameter("username",username)
                .setParameter("age",age)
                .getResultList();
    }

}
