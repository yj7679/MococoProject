package com.mococo.common.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mococo.common.model.Post;
import com.mococo.common.model.PostRecommend;
import com.mococo.common.model.PostRecommendPK;
import com.mococo.common.model.User;


@Repository
public interface PostRecommendDAO extends JpaRepository<PostRecommend, PostRecommendPK>{


	//public Optional<PostRecommend> findByPostAndUser(int postno, int userno);

	
	@Query(value = "SELECT pr.post_number as postNumber "
			+ "FROM post_recommend pr "
			+ "WHERE user_number = :userno ",nativeQuery=true)
	public List<Integer> findLikePostById(int userno);

//	@Query(value = "SELECT pr.* "
//			+ "FROM post_recommend pr "
//			+ "WHERE user_number = :userno and post_number =:postno",nativeQuery=true)
//	public Optional<PostRecommend> findByPostAndUser(int postno, int userno);

	public Optional<PostRecommend> findByPostAndUser(Post post, User user);
	
	@Transactional
	public void deleteByPostAndUser(Post post, User user);
}
