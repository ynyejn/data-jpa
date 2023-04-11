package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PutMapping;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository {

    @PersistenceContext
    private EntityManager em;

    private Team save(Team team){
        em.persist(team);
        return team;
    }
    public void delete(Team team){
        em.remove(team);
    }
    public List<Team> findAll(){
        return em.createQuery("select t from Team t").getResultList();
    }
    public Optional<Team> findById(Long id){
        Team team = em.find(Team.class,id);
        return Optional.ofNullable(team); //null일수도있다는것을 옵셔널로 감싸서 제공.
    }

    public long count(){
        return em.createQuery("select count(t) from Team t",Long.class).getSingleResult();
    }
}
