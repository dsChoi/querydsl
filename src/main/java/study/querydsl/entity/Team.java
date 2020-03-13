package study.querydsl.entity;

import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(of = {"id", "name"})
public class Team {

	@OneToMany(mappedBy = "team")
	List<Member> members = new ArrayList<>();
	@Id
	@GeneratedValue
	@Column(name = "team_id")
	private Long id;
	private String name;

	public Team(String name) {
		this.name = name;
	}


}
