package com.koitt.board.service;

import com.koitt.board.model.CommonException;
import com.koitt.board.model.UserInfo;

public interface UserInfoService {

	public UserInfo detail(String email) throws CommonException;

	public void newUser(UserInfo userInfo) throws CommonException;

	public String delete(String email, String password) throws CommonException;

	public String modify(UserInfo userInfo) throws CommonException;
	
	// 게시물 번호를 이용하여 게시물 작성자의 비밀번호와 입력한 비밀번호 비교
	public boolean isPasswordMatched(Integer no, String rawPassword) throws CommonException;

	// 현재 사용자의 email값을 이용하여 비밀번호를 불러온 다음, 입력한 비밀번호와 비교
	public boolean isPasswordMatched(String email, String oldPassword) throws CommonException;

}
