package com.mococo.common.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.mococo.common.model.UserCrop;

@Repository
public interface UserCropDAO extends JpaRepository<UserCrop, Integer>{
	
	public Optional<UserCrop> findByUserCropNumber(int userCropNumber);
	public List<UserCrop> findAllByUserNumber(int userNumber);
	@Transactional
	public void deleteByUserCropNumber(int userCropNumber);
}