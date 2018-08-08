package com.appspot.TwitterBot;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

// データストア保存用クラスの目印を追加する
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TweetData {

	// 主キーとなるフィールドにはPrimaryKeyをつける
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;

	@Persistent private String user;
	@Persistent private String text;

	/**
	 * コンストラクタ
	 * @param id
	 * @param user
	 * @param text
	 */
	public TweetData(String user, String text) {
		this.user = user;
		this.text = text;
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



}


