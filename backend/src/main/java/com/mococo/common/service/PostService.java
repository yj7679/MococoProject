package com.mococo.common.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.mococo.common.dao.PostDAO;
import com.mococo.common.dao.PostPhotoDAO;
import com.mococo.common.dao.PostRecommendDAO;
import com.mococo.common.model.Post;
import com.mococo.common.model.PostPhoto;
import com.mococo.common.model.PostRecommend;
import com.mococo.common.model.User;

@Service
public class PostService {

	@Autowired
	PostDAO postDAO;

	@Autowired
	PostRecommendDAO postrecommendDAO;

	@Autowired
	PostPhotoDAO postphotoDAO;

	@Autowired
	AmazonS3 amazonS3;

	@Value("${aws.s3.bucket}")
	private String s3bucket;

	// 게시글 번호로 해당 Post 리턴
	public Optional<Post> findPostByPostNumber(int no) {
		Optional<Post> post = postDAO.findPostByPostNumber(no);

		return post;
	}

	public List<Post> findAllPost() {
		List<Post> posts = postDAO.findAll();
		return posts;
	}

	// 무한스크롤으로 포스트를 뽑아주는 service
	public List<Object> findInfinitePost(int limit) {
		List<Object> posts = postDAO.findInfinitePost(PageRequest.of(limit, 3, Sort.by("date").descending()));
		return posts;
	}

	public List<Post> findPostType(int type) {
		List<Post> posts = postDAO.findAllByPostType(type);
		return posts;
	}

	// 유저 별로 게시글 쓴거 불러오기
	public List<Object> findPostUser(int no, int limit) {
		List<Object> posts = postDAO.findAllByUserNumber(no, PageRequest.of(limit, 3, Sort.by("date").descending()));
		return posts;
	}

	// 유저별로 추천 누른거 불러오기
	public List<Object> findPostRecommend(int no, int limit) {

		List<Object> posts = postDAO.findAllByUserRecommend(no, PageRequest.of(limit, 3, Sort.by("date").descending()));
		return posts;
	}

	public Post insertPost(Post post) {
		Optional<Post> ret = postDAO.findPostByPostNumber(post.getPostNumber());
		if (ret.isPresent()) {
			return null;
		}

		Post p = postDAO.save(post);
		return p;
	}

	public boolean updatePost(Post post) {
		Optional<Post> ret = postDAO.findPostByPostNumber(post.getPostNumber());

		// update할 post가 없는 경우
		if (!ret.isPresent()) {
			return false;
		}

		postDAO.save(post);
		return true;
	}

	public boolean deletePost(int no) {
		Optional<Post> ret = postDAO.findPostByPostNumber(no);

		// delete할 post가 없는 경우
		if (!ret.isPresent()) {
			return false;
		}

		postDAO.deleteById(no);
		return true;
	}

	public int recommendPost(int postno, int userno) {
		Optional<Post> ret = postDAO.findPostByPostNumber(postno);

		// 추천할 post가 없는 경우 - 잘못된 접근
		if (!ret.isPresent()) {
			return -1;
		}

		boolean isRecommend = false;
		Post post = ret.get();
		List<User> users = post.getUsers();
		
		for (User user : users) {
			// 이미 추천되어있으면 추천 취소
			if (user.getUserNumber() == userno) {
				isRecommend = true;
			}

		}
		// 이번 요청으로 추천을 누르는 경우
		if (isRecommend == false) {
			// 게시글 테이블의 추천수 컬럼 +1
			ret.get().setRecommend(ret.get().getRecommend() + 1);
			postDAO.save(ret.get());

			// POST RECOMMNED 테이블에 이번에 누른 정보를 insert
			PostRecommend pr = new PostRecommend(postno, userno);
			postrecommendDAO.save(pr);
			return 1;
		}

		// 이번 요청으로 추천을 취소 하는 경우
		else {
			// 게시글 테이블의 추천수 컬럼 -1
			ret.get().setRecommend(ret.get().getRecommend() - 1);
			postDAO.save(ret.get());

			// POST RECOMMNED 테이블에 이번에 누른 정보를 delete
			postrecommendDAO.deleteByPostNumberAndUserNumber(postno, userno);
			return 0;
		}

	}

	public Post insertPost(Post post, MultipartFile[] files) throws IllegalStateException, IOException {
		Post p = postDAO.save(post);
		if (files == null) {
			// TODO : 파일이 없을 땐 어떻게 해야할까.. 고민을 해보아야 할 것
			System.out.println("텅비었어....");
		}
		// 파일에 대해 DB에 저장하고 가지고 있을 것
		else {
			for (MultipartFile mfile : files) {
				PostPhoto photo = new PostPhoto();
				String originalFileName = mfile.getOriginalFilename();
				if (!originalFileName.isEmpty()) {
					String sourceFileName = mfile.getOriginalFilename();
					String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();

					String destinationFileName;
					destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;

					// S3 Bucket에 저장
					File file = convertMultiPartFileToFile(mfile);

					amazonS3.putObject(new PutObjectRequest(s3bucket, "post/" + destinationFileName, file)
							.withCannedAcl(CannedAccessControlList.PublicRead));

					photo.setPost(post);
					photo.setOriginFile(originalFileName);
					photo.setSaveFile(destinationFileName);
					photo.setSaveFolder("post");
					postphotoDAO.save(photo);
					file.delete();
				}

			}

		}

		return p;
	}

	public Post updatePost(Post post, MultipartFile[] files, List<Integer> dlist)
			throws IllegalStateException, IOException {
		Optional<PostPhoto> postphoto = null;
		// 삭제할 이미지 리스트들 삭제.
		if (dlist == null) {

		} else {
			for (Integer photono : dlist) {
				// 먼저 번호로 Image의 고유 이름을 찾는다.
				postphoto = postphotoDAO.findById(photono);

				// Bucket에서 삭제한다.
				amazonS3.deleteObject(s3bucket, postphoto.get().getSaveFolder() + "/" + postphoto.get().getSaveFile());

				// db에서도 정보를 삭제한다.
				postphotoDAO.deleteById(photono);
			}

		}

		Post p = postDAO.save(post);

		if (files == null) {
			// TODO : 파일이 없을 땐 어떻게 해야할까.. 고민을 해보아야 할 것
			System.out.println("텅비었어....");
		}
		// 파일에 대해 DB에 저장하고 가지고 있을 것
		else {
			for (MultipartFile mfile : files) {
				PostPhoto photo = new PostPhoto();
				String originalFileName = mfile.getOriginalFilename();
				if (!originalFileName.isEmpty()) {
					String sourceFileName = mfile.getOriginalFilename();
					String sourceFileNameExtension = FilenameUtils.getExtension(sourceFileName).toLowerCase();

					String destinationFileName;
					destinationFileName = RandomStringUtils.randomAlphanumeric(32) + "." + sourceFileNameExtension;

					// S3 Bucket에 저장
					File file = convertMultiPartFileToFile(mfile);

					amazonS3.putObject(new PutObjectRequest(s3bucket, "post/" + destinationFileName, file)
							.withCannedAcl(CannedAccessControlList.PublicRead));

					photo.setPost(post);
					photo.setOriginFile(originalFileName);
					photo.setSaveFile(destinationFileName);
					photo.setSaveFolder("post");
					postphotoDAO.save(photo);
					file.delete();
				}

			}

		}

		return p;
	}

	public List<Object> findTopPost(int size) {
		return postDAO.findTopPost(PageRequest.of(0, size));
	}

	// multipart file -> file
	private File convertMultiPartFileToFile(MultipartFile multipartFile) {
		File file = new File(multipartFile.getOriginalFilename());
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(multipartFile.getBytes());
			outputStream.close();
		} catch (final IOException ex) {
			System.out.println("Error converting the multi-part file to file= " + ex.getMessage());
		}
		return file;
	}

	public List<Object> findLikePostById(int user_number) {
		List<Object> ret = postrecommendDAO.findLikePostById(user_number);

		return ret;
	}

	public List<Post> findByKeywordContains(String keyword) {

		return postDAO.findByKeywordContains(keyword);

	}

	public List<PostPhoto> findPostPhotoByPostNumber(int postno) {
		return postphotoDAO.findAllByPostNumber(postno);
	}

}
