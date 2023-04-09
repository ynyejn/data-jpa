package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class Member {
    @Id @GeneratedValue
    private Long id;

    private String username;

    protected Member() {
    }

    public Member(String username) {
        this.username = username;
    }

    public void changeUsername(String username){
        this.username=username;
    }
}

