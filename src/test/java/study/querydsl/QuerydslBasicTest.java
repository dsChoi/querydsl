package study.querydsl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.member;


@SpringBootTest
@Transactional
public class QuerydslBasicTest {


	@Autowired
	EntityManager em;

	JPAQueryFactory queryFactory;

	@BeforeEach
	public void before() {

		queryFactory = new JPAQueryFactory(em);
		Team teamA = new Team("teamA");
		Team teamB = new Team("teamB");
		Member member1 = new Member("member1", 10, teamA);

		em.persist(teamA);
		em.persist(teamB);
		Member member2 = new Member("member2", 20, teamA);
		Member member3 = new Member("member3", 30, teamB);
		Member member4 = new Member("member4", 40, teamB);
		//확인
		List<Member> members = em.createQuery("select m from Member  m", Member.class)
								 .getResultList();
		em.persist(member1);
		em.persist(member2);
		em.persist(member3);
		em.persist(member4);
		//초기화
		em.flush();
		em.clear();
	}


	@Test
	public void test() {
		Member findMember = em.createQuery("select m from Member m " +
										   " where m.username = :username ", Member.class)
							  .setParameter("username", "member1")
							  .getSingleResult();
		assertThat(findMember.getUsername()).isEqualTo(("member1"));
	}

	@Test
	public void startQuerydsl() {
		queryFactory = new JPAQueryFactory(em);
		QMember m = member;
		Member findMember = queryFactory
				.select(m)
				.from(m)
				.where(m.username.eq("member1"))
				.fetchOne();
		assertThat(findMember.getUsername()).isEqualTo(("member1"));
	}


	@Test
	public void search() {
		Member member1 = queryFactory.selectFrom(member)
									 .where(member.username.eq("member1")
														   .and(member.age.eq(10))
										   ).fetchOne();

		assertThat(member1.getUsername()).isEqualTo(("member1"));

	}

	@Test
	public void resultFetch() {

		//		List<Member> fetch = queryFactory.selectFrom(member)
		//										 .fetch();
		//		Member fetchOne = queryFactory.selectFrom(QMember.member)
		//									.fetchOne();
		//		Member fetchFir = queryFactory.selectFrom(QMember.member)
		//									.fetchFirst();
		QueryResults<Member> memberQueryResults = queryFactory.selectFrom(member)
															  .fetchResults();

		long total = memberQueryResults.getTotal();
		List<Member> results = memberQueryResults.getResults();

	}


	/**
	 * 회원 정렬 순서
	 * 1. 회원 나이 내림차순(desc)
	 * 2. 회원 이름 올림차순(asc)
	 * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
	 */
	@Test
	public void sort() {
		em.persist(new Member(null, 100));
		em.persist(new Member("member5", 100));
		em.persist(new Member("member6", 100));


		List<Member> results = queryFactory.selectFrom(member)
										   .where(member.age.eq(100))
										   .orderBy(member.age.desc(), member.username.asc().nullsLast())
										   .fetch();


		Member member5 = results.get(0);
		Member member6 = results.get(1);
		Member memberNull = results.get(2);


		assertThat(member5.getUsername()).isEqualTo("member5");
		assertThat(member6.getUsername()).isEqualTo("member6");
		assertThat(memberNull.getUsername()).isNull();
	}


	@Test
	public void paging1() {


		List<Member> result = queryFactory.selectFrom(member)
										  .orderBy(member.username.desc())
										  .offset(1)
										  .limit(2)
										  .fetch();

		assertThat(result.size()).isEqualTo(2);
		assertThat(result.size()).isEqualTo(2);
	}

	@Test
	public void paging2() {


		QueryResults<Member> results = queryFactory.selectFrom(member)
												   .orderBy(member.username.desc())
												   .offset(1)
												   .limit(2)
												   .fetchResults();

		assertThat(results.getTotal()).isEqualTo(4);
		assertThat(results.getLimit()).isEqualTo(2);
		assertThat(results.getOffset()).isEqualTo(1);
		assertThat(results.getResults().size()).isEqualTo(2);
	}



	@Test
	public void dynamicQuery_BooleanBuilder(){
		String userNameParam = "member1";
		Integer ageParam = 10;

		List<Member> results = searchMember1(userNameParam, ageParam);
		assertThat(results.size()).isEqualTo(1);

	}

	private List<Member> searchMember1(String userNameParam, Integer ageParam) {
		BooleanBuilder builder = new BooleanBuilder();
		if(userNameParam != null){
			builder.and(member.username.eq(userNameParam));
		}
		if(ageParam != null){
			builder.and(member.age.eq(ageParam));
		}

		return queryFactory
				.selectFrom(member)
				.where(builder)
				.fetch();
	}

	@Test
	public void dynamicQuery_whereParam(){

		String userNameParam = "member1";
		Integer ageParam = 10;

		List<Member> results = searchMember2(userNameParam, ageParam);
		assertThat(results.size()).isEqualTo(1);


	}

	private List<Member> searchMember2(String userNameParam, Integer ageParam) {


		return queryFactory
				.selectFrom(member)
				.where(usernameEq(userNameParam), ageEq(ageParam))
				.fetch();
	}

	private BooleanExpression usernameEq(String userNameParam) {
		if(userNameParam == null){
			return null;
		}
		return member.username.eq(userNameParam);
	}

	private BooleanExpression ageEq(Integer ageParam) {
		if(ageParam == null){
			return null;
		}
		return member.age.eq(ageParam);
	}
	private BooleanExpression allEq(String userNameParam, Integer ageParam) {

		return usernameEq(userNameParam).and(ageEq(ageParam));
	}

}
