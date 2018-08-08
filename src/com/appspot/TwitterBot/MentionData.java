package com.appspot.TwitterBot;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

// データストア保存用クラスの目印を追加する
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class MentionData {

	// 主キーとなるフィールドにはPrimaryKeyをつける
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent private String user;
	@Persistent private String text;
	@Persistent private Long statusid;

	/**
	 * コンストラクタ
	 * @param id
	 * @param user
	 * @param text
	 * @param statusid
	 */
	public MentionData(String user, String text, Long statusid) {
		this.user = user;
		this.text = text;
		this.statusid = statusid;
	}

	// setter/getter
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Long getStatusid() {
		return statusid;
	}

	public void setStatusid(Long statusid) {
		this.statusid = statusid;
	}

}