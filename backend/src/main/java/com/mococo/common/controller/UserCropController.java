package com.mococo.common.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mococo.common.model.UserCrop;
import com.mococo.common.service.UserCropService;

import io.swagger.annotations.ApiOperation;


//http://localhost:8080/swagger-ui.html/

@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/user/crop/")

public class UserCropController {
	
	@Autowired
	UserCropService userCropService;
	
	private static final Logger logger = LoggerFactory.getLogger(UserCropController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	
	@RequestMapping(value = "/", method = RequestMethod.POST)
	@ApiOperation(value = "작물 등록")
	private ResponseEntity<String> insertCrop (@RequestBody UserCrop userCrop) throws IOException {
		logger.info("작물 등록");
		userCrop.setPlantedDate(new Date());
		
		boolean result = userCropService.insertCrop(userCrop);
		
		try {
	        if (result) {
	            return new ResponseEntity<>("success", HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	        }
		} catch(Exception e){
			System.out.println("test");
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	@ApiOperation(value = "작물 삭제")
	private ResponseEntity<String> deleteCrop (@RequestBody int userCropNumber) throws IOException {
		logger.info("작물 삭제");
		userCropService.deleteCrop(userCropNumber);
		
		ResponseEntity response = null;
		
        response = new ResponseEntity<>("success", HttpStatus.OK);
		
		return response;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.PUT)
	@ApiOperation(value = "작물 정보 수정")
	private ResponseEntity<String> updateCrop (@RequestBody UserCrop userCrop) throws IOException {
		logger.info("작물 정보 수정");
		boolean result = userCropService.updateCrop(userCrop);
		
		ResponseEntity response = null;
		
        if (result) {
            response = new ResponseEntity<>("success", HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }
		
		return response;
	}
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ApiOperation(value = "작물 정보 조회")
	private ResponseEntity<String> searchCrop (@RequestParam int userNumber) throws IOException {
		logger.info("작물 정보 조회");
		List<UserCrop> userCropOpt = userCropService.findAllByUserNumber(userNumber);
		
		ResponseEntity response = null;
		response = new ResponseEntity<>(userCropOpt, HttpStatus.OK);
		
		return response;
	}

}
