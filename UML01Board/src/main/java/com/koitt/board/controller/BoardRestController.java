package com.koitt.board.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.koitt.board.model.Board;
import com.koitt.board.model.CommonException;
import com.koitt.board.service.BoardService;

@RestController
@RequestMapping("/rest")
public class BoardRestController {
	
	private static final String UPLOAD_FOLDER = "/upload";

	private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	private BoardService boardService;

	// 글 목록
	@RequestMapping(value = "/board", method = RequestMethod.GET, 
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, 
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<List<Board>> list() throws CommonException {
		List<Board> list = null;

		list = boardService.list();
		if (list != null && !list.isEmpty()) {
			return new ResponseEntity<List<Board>>(list, HttpStatus.OK);
		}

		return new ResponseEntity<List<Board>>(HttpStatus.NO_CONTENT);
	}

	// 글 상세보기
	@RequestMapping(value = "/board/{no}", method = RequestMethod.GET, 
			produces = { MediaType.APPLICATION_JSON_UTF8_VALUE, 
					MediaType.APPLICATION_XML_VALUE })
	public ResponseEntity<Board> detail(@PathVariable("no") String no,
			UriComponentsBuilder ucBuilder)
					throws CommonException, Exception {

		Board board = null;
		String filename = null;

		board = boardService.detail(no);

		if (board != null && board.getNo() > 0) {
			filename = board.getAttachment();
			if (filename != null && !filename.trim().isEmpty()) {
				filename = URLDecoder.decode(filename, "UTF-8");
				board.setAttachment(filename);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path(
					"/rest/board/attachment/{attachment}")
					.buildAndExpand(board.getAttachment())
					.toUri());

			return new ResponseEntity<Board>(board, headers, HttpStatus.OK);
		}

		return new ResponseEntity<Board>(HttpStatus.NOT_FOUND);
	}

	// 파일 내려받기
	@RequestMapping(value = "/board/attachment/{attachment:.+}", method = RequestMethod.GET)
	public void download(HttpServletRequest request, 
			HttpServletResponse response, @PathVariable("attachment") String filename)
					throws CommonException {

		int length = 0;
		byte[] buff = new byte[1024];

		// 서버에 저장된 파일 경로 불러오기
		String directory = request.getServletContext().getRealPath(UPLOAD_FOLDER);

		// 요청한 파일명으로 실제 파일을 객체화 하기
		File file = new File(directory, filename);

		FileInputStream fis = null;
		BufferedOutputStream bos = null;
		try {
			fis = new FileInputStream(file);

			// 다운받을 때, 한글 깨짐현상 수정
			filename = new String(filename.getBytes("UTF-8"), "ISO-8859-1");

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", 
					"attachment; filename=" + filename + ";");
			response.setHeader("Content-Transfer-Encoding", "binary");
			response.setHeader("Content-Length", Integer.toString(fis.available()));
			response.setHeader("Pragma", "no-cache");
			response.setHeader("Expires", "-1");

			/*
			 * Connection Stream: ServletOutputStream
			 * Chain Stream: BufferedOutputStream
			 */
			bos = new BufferedOutputStream(response.getOutputStream());

			// 서버에 있는 파일을 읽어서 (fis), 클라이언트에게 파일을 전송(bos)
			while ( (length = fis.read(buff)) > 0) {
				bos.write(buff, 0, length);
			}

			// 변기 물내린다는 뜻, 버퍼에 남아있는 정보를 보내준다.
			bos.flush();

		} catch (Exception e) {
			throw new CommonException("E12: 파일 내려받기 실패");

		} finally {
			try {
				bos.close();
				fis.close();

			} catch (IOException e) {
				logger.debug(e.getMessage());
			}
		}
	}
}
