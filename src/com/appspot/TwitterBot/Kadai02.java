package com.appspot.TwitterBot;

import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.TwitterException;

public class Kadai02 {

	// Loggerの作成
	private static final Logger logger = Logger.getLogger(Kadai02.class
			.getName());

	public static void main(String[] args) throws TwitterException {

		try {
			// リフォローの実行
			System.out.println(Kadai02.class.getName());
			TwitterHelper.refollow();
		} catch (TwitterException e) {
			// コンソールにエラーを表示
			e.printStackTrace();
			// 危険なエラーが出た場合はloggerに記録
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe(e.toString());
			}
		}

	}
}
