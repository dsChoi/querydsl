package study.querydsl.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;

import javax.persistence.EntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberJpaRepositoryTest {


	@Autowired
	EntityManager em;

	@Autowired MemberJpaRepository memberJpaRepository;

	@Test
	public void basicTest(){

		Member member = new Member("member1", 10);

		memberJpaRepository.save(member);


		Member findMember = memberJpaRepository.findById(member.getId()).get();
		assertThat(findMember).isEqualTo(member);

		List<Member> result1 = memberJpaRepository.findAll();

		assertThat(result1).containsExactly(member);



		List<Member> result2 = memberJpaRepository.findByUserName("member1");

		assertThat(result1).containsExactly(member);


	}

	@Test
	public void basicTest_queryDsl(){

		Member member = new Member("member1", 10);

		memberJpaRepository.save(member);


		Member findMember = memberJpaRepository.findById(member.getId()).get();
		assertThat(findMember).isEqualTo(member);

		List<Member> result1 = memberJpaRepository.findAll_Querydsl();

		assertThat(result1).containsExactly(member);



		List<Member> result2 = memberJpaRepository.findByUserName_QueryDsl("member1");

		assertThat(result1).containsExactly(member);


	}

}