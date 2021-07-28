package com.mococo.common.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mococo.common.model.User;
import com.mococo.common.model.UserSetting;
import com.mococo.common.service.UserSettingService;


//http://localhost:8080/swagger-ui.html/

@CrossOrigin(origins = { "*" }, maxAge = 6000)
@RestController
@RequestMapping("/user")

public class UserSettingController {
	
	private static final Logger logger = LoggerFactory.getLogger(UserSettingController.class);
	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	
	@Autowired
	UserSettingService userSettingService;
	
	@RequestMapping(value = "/setting/notice", method = RequestMethod.PUT)
	private ResponseEntity<String> updateNotice (@RequestBody UserSetting userSetting) throws IOException {
		logger.info("알림 상태 수정");
		
		try {
			userSettingService.updateByUserNumber(userSetting.getUserNumber(), userSetting);
			System.out.println("알림 상태 수정 성공");
			return new ResponseEntity<String>(SUCCESS, HttpStatus.OK);
		} catch (Exception e) {
			System.out.println("알림 상태 수정 실패");
			return new ResponseEntity<String>(FAIL, HttpStatus.NO_CONTENT);
		} 
		

	}
	
	@RequestMapping(value = "/setting/mode", method = RequestMethod.PUT)
	private ResponseEntity<String> updateMode (@RequestBody UserSetting userSetting) throws IOException {
		logger.info("다크모드  on/off");
		
		return null;


	}
	

}