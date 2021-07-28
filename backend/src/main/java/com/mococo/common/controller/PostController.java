package com.mococo.common.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mococo.common.model.Post;
import com.mococo.common.service.PostService;


//http://localhost:8080/swagger-ui.html/

@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/post")

public class PostController {
	
	private static final Logger logger = LoggerFactory.getLogger(PostController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	@Autowired
	PostService postService;
	

	@RequestMapping(value = "/{no}", method = RequestMethod.GET)
	private ResponseEntity<Optional<Post>> searchPost (@PathVariable String postno) throws IOException {
		logger.info("게시물 내용 조회");
		int post_number = Integer.parseInt(postno);
		
		return new ResponseEntity<Optional<Post>>(postService.findPostByPostNumber(post_number),HttpStatus.OK);
	}
	
	// 리스트로 리턴되는 것은 프론트에서 리스트 길이로 처리하기로함.
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	private ResponseEntity<List<Post>> searchAllPost () throws IOException {
		try {

			logger.info("게시물 전체 조회");
			
			return new ResponseEntity<List<Post>>(postService.findAllPost(), HttpStatus.OK);
		}
		catch (Exception e){
			e.printStackTrace();
			logger.info("게시물 전체 조회 에러");
			return new ResponseEntity<List<Post>>(postService.findAllPost(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	// 자유1,정보2, 질문3, 나눔4  
	@RequestMapping(value = "/type/{type}", method = RequestMethod.GET)
	private ResponseEntity<List<Post>> searchPostType (@PathVariable String type) throws IOException {
		logger.info("게시물 분류 조회");
		int postType = 0;
		if(type.equals("자유")) {
			postType=1;
		} else if(type.equals("정보")) {
			postType=2;
		} else if(type.equals("질문")) {
			postType=3;
		} else if(type.equals("나눔")) {
			postType=4;
		}
	
		try {
			return new ResponseEntity<List<Post>>(postService.findPostType(postType), HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("게시물 분류 조회 에러");
			return new ResponseEntity<List<Post>>(postService.findPostType(postType), HttpStatus.INTERNAL_SERVER_ERROR);
			
		}

	}
	
	
	
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	private ResponseEntity<String> insertPost (@RequestBody Post post) throws IOException {
		
		Date time = new Date();
		post.setDate(time);
		try {
			logger.info("게시글 등록");
			System.out.println(post);
			boolean ret = postService.insertPost(post);
			if(ret==false) {
				logger.info("게시글 등록 실패");
				return new ResponseEntity<String>("fail", HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("게시글 등록 오류");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("error", HttpStatus.NOT_ACCEPTABLE);
		}
		


	}
	@RequestMapping(value = "/{postno}", method = RequestMethod.DELETE)
	private ResponseEntity<String> deletePost (@PathVariable String postno) throws IOException {
		
		try {
			logger.info("게시글 삭제");
			int post_number = Integer.parseInt(postno);
			
			boolean ret = postService.deletePost(post_number);
			if(ret == false) {
				logger.info("게시글 수정 실패");
				return new ResponseEntity<String>("fail", HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			logger.info("게시글 수정 오류");
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ResponseEntity<String>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	@RequestMapping(value = "/{postno}", method = RequestMethod.PUT)
	private ResponseEntity<String> updatePost (@RequestBody Post post) throws IOException {
		
	

		try {
			logger.info("게시글 수정");
			boolean ret = postService.updatePost(post);
			if(ret == false) {

				logger.info("게시글 수정 실패");
				return new ResponseEntity<String>("fail", HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("게시글 수정 오류");
			e.printStackTrace();
			return new ResponseEntity<String>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	
	@RequestMapping(value = "/recommend/{postno}", method = RequestMethod.PUT)
	private ResponseEntity<String> recommendPost (@PathVariable String postno) throws IOException {
		
		try {
			logger.info("게시글 추천");
			int post_number =Integer.parseInt(postno);
			boolean ret = postService.recommendPost(post_number);
			if(ret == false) {

				logger.info("게시글 추천 실패");
				return new ResponseEntity<String>("fail", HttpStatus.NO_CONTENT);
			}
			
			return new ResponseEntity<String>("success", HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.info("게시글 추천 오류");
			e.printStackTrace();
			return new ResponseEntity<String>("error", HttpStatus.INTERNAL_SERVER_ERROR);
		}


	}
	
	
	
	

}