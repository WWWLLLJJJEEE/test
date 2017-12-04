package com.koitt.board.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koitt.board.dao.BoardDao;
import com.koitt.board.dao.UserInfoDao;
import com.koitt.board.model.Board;
import com.koitt.board.model.CommonException;
import com.koitt.board.model.UserInfo;
import com.koitt.board.model.UserType;
import com.koitt.board.model.UserTypeId;

@Service
public class UserInfoServiceImpl implements UserInfoService {
	
	@Autowired
	private UserInfoDao userInfoDao;
	
	@Autowired
	private BoardDao boardDao;

	@Autowired
	private PasswordEncoder encoder;

	@Override
	public UserInfo detail(String email) throws CommonException {
		return userInfoDao.select(email);
	}

	@Transactional
	@Override
	public void newUser(UserInfo userInfo) throws CommonException {

		userInfo.setPassword(encoder.encode(userInfo.getPassword()));

		UserType userType = new UserType(UserTypeId.USER.getUserTypeId(), UserTypeId.USER.name());
		Set<UserType> userTypes = new HashSet<>();
		userTypes.add(userType);

		userInfo.setUserTypes(userTypes);

		userInfoDao.insert(userInfo);
		userInfoDao.insertUserTypes(userInfo);
	}

	@Transactional
	@Override
	public String delete(String email, String password) throws CommonException {
		
		UserInfo item = userInfoDao.select(email);
		
		boolean isMatched = encoder.matches(password, item.getPassword());
		if (isMatched) {
			userInfoDao.deleteUserTypes(email);
			userInfoDao.delete(email);
			
		} else {
			throw new CommonException("E60: 비밀번호가 동일하지 않아 삭제 실패");
		}

		return item.getAvatar();
	}

	@Override
	public String modify(UserInfo userInfo) throws CommonException {
		
		UserInfo item = userInfoDao.select(userInfo.getEmail());
		String oldFilename = item.getAvatar();
		userInfoDao.update(userInfo);

		return oldFilename;
	}

	@Override
	public boolean isPasswordMatched(String email, String rawPassword) throws CommonException {
		
		// 데이터베이스로부터 현재 사용자의 암호화된 비밀번호를 가져온다.
		UserInfo item = userInfoDao.select(email);
		return encoder.matches(rawPassword, item.getPassword());
	}

	@Override
	public boolean isPasswordMatched(Integer no, String rawPassword) throws CommonException {
		/*
		 *  게시물 번호를 이용하여 게시물을 가져온 뒤
		 *  게시물의 작성자 email값을 획득한 후,
		 *  획득한 email값으로 사용자 정보를 가져와
		 *  해당 사용자의 비밀번호를 가져온다.
		 */
		Board board = boardDao.select(no.toString());
		UserInfo userInfo = userInfoDao.select(board.getId());
		
		// 해당 사용자의 비밀번호와 입력한 비밀번호 비교한 결과 리턴
		return encoder.matches(rawPassword, userInfo.getPassword());
	}
}
