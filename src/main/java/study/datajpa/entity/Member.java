package study.datajpa.entity;

import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id","username","age"}) //team은적으면안돼 무한루프뜸
public class Member {
    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;
    private String username;
    private  int age;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "team_id")
    private Team team;



    public Member(String username) {
        this.username = username;
    }

    public Member(String name, int age, Team team) {
        this.username = name;
        this.age = age;
        if(team!=null){
            changeTeam(team);
        }
    }

    public void changeUsername(String username){
        this.username=username;
    }

    //연관관계 세팅
    public void changeTeam(Team team){
        this.team = team;
        team.getMembers().add(this);
    }

    public Member(String username, int age) {
        this.username = username;
        this.age = age;
    }
}

