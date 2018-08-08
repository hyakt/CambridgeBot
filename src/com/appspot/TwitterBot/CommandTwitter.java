package com.appspot.TwitterBot;

import java.util.List;

import twitter4j.Status;
import twitter4j.TwitterException;

public class CommandTwitter {

	public static void main(String[] args){
		try {
			// 新規追加(1):ユーザの情報を取り出してみよう
			// TwitterHelper.tweet("テストツイート2");
			
			System.out.println("ID:"+TwitterHelper.getId());
			System.out.println("screen name:"+TwitterHelper.getScreenName());
			System.out.println("name:"+TwitterHelper.getUser());
			
	
			// 追加(2):ホームタイムラインのツイート一覧の取得
			List<Status> statuses = TwitterHelper.getStatus();
			
			for(Status status : statuses){
				System.out.println(status.getUser().getName() + ":" + status.getText());
			}
			
			// 追加(3):フォローしている人のユーザのIDリスト
			long[] friendsIDs = TwitterHelper.getFriendsIDs();
			for(long id : friendsIDs){
				System.out.println(id);
			}
						
		} catch (TwitterException e) {
			
			e.printStackTrace();
			
		}
	}
}
