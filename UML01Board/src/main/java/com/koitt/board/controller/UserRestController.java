package com.koitt.board.controller;

import java.io.File;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.koitt.board.model.CommonException;
import com.koitt.board.model.UserInfo;
import com.koitt.board.service.UserInfoService;

@Controller
@RequestMapping("/rest")
public class UserRestController {
	
	private static final String UPLOAD_FOLDER = "/avatar";
	
	private Logger logger = LogManager.getLogger(this.getClass());
	
	@Autowired
	private UserInfoService userInfoService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// 사용자 로그인
	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public ResponseEntity<String> login(UserInfo userInfo, 
			UriComponentsBuilder ucBuilder) {
		
		logger.debug(userInfo);
		
		// 아이디 존재 유무와 비밀번호 일치 여부 확인
		boolean isMatched = userInfoService.isPasswordMatched(
				userInfo.getId(),
				userInfo.getPassword());
		
		if (isMatched) {
			// Base64 인코딩 전 평문
			String plainCredentials = 
					userInfo.getEmail() + ":" + userInfo.getPassword();
			
			// 평문을 Base64로 인코딩
			String base64Credentials = 
					new String(
							Base64.encodeBase64(plainCredentials.getBytes()
					));
			
			logger.debug(base64Credentials);
			
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/rest/user/{id}")
					.buildAndExpand(userInfo.getId())
					.toUri());
			
			return new ResponseEntity<String>(base64Credentials, headers, HttpStatus.OK);
		}
		
		logger.debug("login failed");
		return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
	}
	
	// 사용자 생성
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public ResponseEntity<Void> newUser(HttpServletRequest request,
			String email,
			String password,
			String name,
			@RequestParam("avatar") MultipartFile avatar,
			UriComponentsBuilder ucBuilder)
					throws CommonException, Exception {

		UserInfo user = new UserInfo();
		user.setEmail(email);
		user.setPassword(password);
		user.setName(name);

		// 최상위 경로 밑에 upload 폴더의 경로를 가져온다.
		String path = request.getServletContext().getRealPath(UPLOAD_FOLDER);

		// MultipartFile 객체에서 파일명을 가져온다.
		String originalName = avatar.getOriginalFilename();

		// upload 폴더가 없다면, upload 폴더 생성
		File directory = new File(path);
		if (!directory.exists()) {
			directory.mkdir();
		}

		// avatar 객체를 이용하여, 파일을 서버에 전송
		if (avatar != null && !avatar.isEmpty()) {
			int idx = originalName.lastIndexOf(".");
			String fileName = originalName.substring(0, idx);
			String ext = originalName.substring(idx, originalName.length());
			String uploadFilename = fileName
					+ Long.toHexString(System.currentTimeMillis())
					+ ext;
			avatar.transferTo(new File(path, uploadFilename));
			uploadFilename = URLEncoder.encode(uploadFilename, "UTF-8");
			user.setAvatar(uploadFilename);
		}

		userInfoService.newUser(user);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/rest/user/{id}")
				.buildAndExpand(user.getId())
				.toUri());
		
		return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
	
	// 사용자 불러오기
	@RequestMapping(value = "/user/{id}", method = RequestMethod.GET,
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, 
						MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<UserInfo> homePage(@PathVariable("id") Integer id) {

		// 로그인 된 상태이면
		UserInfo item = null;
		if (id != null) {
			item = userInfoService.detail(id);
			
			if (item != null) {
				return new ResponseEntity<UserInfo>(item, HttpStatus.OK);
			}
		}

		return new ResponseEntity<UserInfo>(new UserInfo(), HttpStatus.NO_CONTENT);
	}
}
