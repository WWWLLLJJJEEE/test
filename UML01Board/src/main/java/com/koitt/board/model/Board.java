package com.koitt.board.model;

import java.util.Date;

// Java Bean (VO, DTO)
public class Board {

	private Integer no;
	private String title;
	private String content;
	private String id;
	private Date regdate;
	private String attachment;
	
	public Board() {}

	public Board(Integer no, String title, String content, 
			String id, Date regdate, String attachment) {
		this.no = no;
		this.title = title;
		this.content = content;
		this.id = id;
		this.regdate = regdate;
		this.attachment = attachment;
	}

	public Integer getNo() {
		return no;
	}

	public void setNo(Integer no) {
		this.no = no;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getRegdate() {
		return regdate;
	}

	public void setRegdate(Date regdate) {
		this.regdate = regdate;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	@Override
	public int hashCode() {
		int result = this.no.hashCode() + this.content.hashCode()
				+ this.id.hashCode() + this.title.hashCode() + this.regdate.hashCode()
				+ this.attachment.hashCode();
		
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (!(obj instanceof Board)) {
			return false;
		}
		
		Board item = (Board) obj;
		if (this.no.equals(item.no)) {
			return true;
		}
		
		return false;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Board [no=");
		builder.append(no);
		builder.append(", title=");
		builder.append(title);
		builder.append(", content=");
		builder.append(content);
		builder.append(", id=");
		builder.append(id);
		builder.append(", regdate=");
		builder.append(regdate);
		builder.append(", attachment=");
		builder.append(attachment);
		builder.append("]");
		return builder.toString();
	}
}
