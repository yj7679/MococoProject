package com.mococo.common.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mococo.common.model.Post;

@Repository
public interface PostDAO extends JpaRepository<Post, Integer>{
	
	public Optional<Post> findPostByPostNumber(int no);
	public List<Post> findAll();
	public List<Post> findAllByPostType(int type);

	// limit은 어디부터 3개씩 가져올지 선택 no는 유저 번호
	@Query(value = "SELECT p "
					+ "FROM post p "
					+ "WHERE user_number = :no  AND isDelete = 0 ")	
	public List<Post> findAllByUserNumber(int no,Pageable pageable);
	
	// 무한스크롤을 위한 dao
	@Query(value = "SELECT p "
					+ "FROM post p "
					+ "WHERE isDelete = 0 ")
	public List<Post> findInfinitePost(Pageable pageable);
	
	// 유저별 추천 한 게시글 리스트 리턴
	@Query(value = "SELECT DISTINCT p "
					+"FROM post As p "
					+"LEFT JOIN post_recommend pr "
					+"ON p.postNumber = pr.postNumber "  
					+"WHERE p.user_number = :no AND isDelete = 0 ", nativeQuery =true)
	public List<Post> findAllByUserRecommend(int no,Pageable pageable);
	public Post save(Post post);
	
	@Query(value = "SELECT new Map(p.postNumber as postNumber, p.userNickname as userNickname, p.userNumber as userNumber, p.postType as postType, p.title as title, p.recommend as recommend,p.commentCount as commentCount, p.view as view, p.date as date, p.content as content, p.keyword as keyword, p.isDelete as isDelete, p.isFinish as isFinish) "
			+ "FROM post AS p "
			+ "WHERE isDelete = 0 "
			+ "ORDER BY p.recommend DESC ")
	public List<Object> findTopPost(Pageable pageable);
	

	public List<Post> findByKeywordContains(String keyword);
	
	public List<Post> findByPostNumberIn(List<Integer> postnums, Pageable of);
	
	

}
