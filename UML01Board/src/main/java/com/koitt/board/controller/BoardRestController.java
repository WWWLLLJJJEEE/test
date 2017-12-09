package com.koitt.board.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.koitt.board.model.Board;
import com.koitt.board.model.CommonException;
import com.koitt.board.service.BoardService;

@RestController
@RequestMapping("/rest")
public class BoardRestController {
	
	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private BoardService boardService;

	// 글 목록 
		@RequestMapping(value = "/board", method = RequestMethod.GET)
		public ResponseEntity<List<Board>> list() throws CommonException {
			List<Board> list = null;

			list = boardService.list();
			if (list != null && list.isEmpty()) {
				return new ResponseEntity<List<Board>>(list, HttpStatus.OK);
			}
			
			return new ResponseEntity<List<Board>>(HttpStatus.NO_CONTENT);
		}
}
