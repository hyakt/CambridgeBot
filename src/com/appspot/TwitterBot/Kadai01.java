package com.appspot.TwitterBot;

import java.util.List;

import twitter4j.TwitterException;
import twitter4j.User;

public class Kadai01 {

	public static void main(String[] args) {

		try {

			// 課題(1):"akifumi_pub"で検索
			String name = "akifumi_pub";
			long userId = 0;
			List<User> users = TwitterHelper.searchUsers(name, -1);

			System.out.println("検索での名前:" + name);

			for (User user : users) {
				System.out.println("名前:" + user.getScreenName() + ",ID:"
						+ user.getId() +"\n");
				userId += user.getId();
			}

			// 課題(1):"akifumi_pub"のフォロワーのScreenNameを取得
			List<User> StatusesList = TwitterHelper
					.getFollowersScreenName(userId);

			System.out.println(name + "のフォロワー:");

			for (User user : StatusesList) {
				System.out.println(user.getScreenName());
			}

			// 課題(1):"akifumi_pub"をプログラムからフォロー
			TwitterHelper.createFriendship(userId);

			// 課題(1)"akifumi_pub"にメンションツイート
			TwitterHelper.tweet("@" + name + " 132");

		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}
}
