package com.appspot.TwitterBot;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;

public final class TwitterHelper {

	// アプリからTwitterにアクセスするためのOAuth情報を設定する.
	// final宣言なので定数,定数は全部大文字を使う.
	private static final String CONSUMER_KEY = "rc1WK7yTcYrIESmzUnbJfw";
	private static final String CONSUMER_SECRET = "TBf2aWua396FZRm2WMfexkpiydsxAekioDQO5Ne7M";
	private static final String ACCESS_TOKEN = "614159139-fqoy9cKeM93VApioCUtgvcUF5gMRZAUWYKi17KQJ";
	private static final String ACCESS_TOKEN_SECRET = "glQ82egEp7y5Oul0rtPQRtuUoxD2x7OsoCqg4SA3LIo";

	private static final TwitterFactory tf = new TwitterFactory();

	private static final Logger logger = Logger.getLogger(TwitterHelper.class
			.getName());

	// OAuthの情報を設定したtwitterへのインターフェースを返すメソッド
	private static Twitter getTwitter() {

		// (1)Twitter型のオブジェクトを取得する
		// Twitterはinterfaceなのでコンストラクタはない
		// FactoryからgetInstanceで取得する
		Twitter twitter = tf.getInstance();

		// (2)(1)のオブジェクトにOAuthの認証情報を設定する
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(new AccessToken(ACCESS_TOKEN,
				ACCESS_TOKEN_SECRET));

		// (3)(2)のオブジェクトを戻り値として返す
		return twitter;

	}

	// 引数の文字列をtwitter.comにtweetするクラスメソッド
	// tweetに失敗するとTwitterExceptionを投げる
	public static void tweet(String message) throws TwitterException {
		Twitter twitter = getTwitter(); // 認証情報つきtwitterを取得する
		twitter.updateStatus(message); // messageをtweetする
	}

	// 自分の表示名を取得するメソッド
	public static String getScreenName() throws TwitterException {
		Twitter twitter = getTwitter();
		return twitter.getScreenName();
	}

	// 自分のUserIDを返すメソッド
	public static long getId() throws TwitterException {
		Twitter twitter = getTwitter();
		return twitter.getId();
	}

	// Userを返す
	public static String getUser() throws TwitterException {
		Twitter twitter = getTwitter();
		return twitter.showUser(twitter.getId()).getName();
	}

	// 自分のタイムラインを返すメソッド
	public static List<Status> getStatus() throws TwitterException {
		Twitter twitter = getTwitter();
		return twitter.getHomeTimeline();
	}

	// ユーザがフォローしている人のIDの配列を返すメソッド
	public static long[] getFriendsIDs() throws TwitterException {
		Twitter twitter = getTwitter();
		long[] friendsIDs = twitter.getFriendsIDs(-1L).getIDs();
		return friendsIDs;
	}

	// ユーザをフォローしている人のIDの配列を返すメソッド
	public static long[] getFollowersIDs() throws TwitterException {
		Twitter twitter = getTwitter();
		long[] followersIDs = twitter.getFollowersIDs(-1L).getIDs();
		return followersIDs;
	}

	// ユーザの検索をするメソッド
	public static List<User> searchUsers(String query, int page)
			throws TwitterException {
		Twitter twitter = getTwitter();
		return twitter.searchUsers(query, page);
	}

	// 指定したユーザのフォロワーのスクリーンネームを返すメソッド
	public static List<User> getFollowersScreenName(long userId)
			throws TwitterException {
		Twitter twitter = getTwitter();
		long[] ids = twitter.getFollowersIDs(userId, -1).getIDs();
		return twitter.lookupUsers(ids);
	}

	// 指定したユーザをフォローするメソッド
	public static void createFriendship(Long userId) throws TwitterException {
		Twitter twitter = getTwitter();
		twitter.createFriendship(userId);
	}

	// フォローの自動返信をするメソッド
	public static void refollow() throws TwitterException {
		Twitter twitter = getTwitter();
		boolean isFollowing = false;

		// フォローしているアカウント一覧を取得
		long[] friendId = twitter.getFriendsIDs(-1).getIDs();

		// フォロワー1つ1つに対して括弧内の処理を実行する.
		for (long followerId : twitter.getFollowersIDs(-1).getIDs()) {
			for (int i = 0; i < friendId.length; i++) {
				// フォロワーのIDと,フォローしているアカウントのIDが一致すればフラグをON
				if (followerId == friendId[i]) {
					isFollowing = true;
				}
			}
			// isFollowingがFalseのまま,つまりフォローされている
			// アカウントを自分がフォローしていない場合
			if (!isFollowing) {
				try {
					twitter.createFriendship(followerId);
				} catch (TwitterException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning(e.toString());
					}
				}
			}
		}
	}

	// フォローの自動外し
	public static void unfollow() throws TwitterException {
		Twitter twitter = getTwitter();
		boolean isFollowed = false;

		// フォロワーのIDを取得
		long[] followersId = twitter.getFollowersIDs(-1).getIDs();

		// フォローしているアカウントのID1つ1つに対してカッコ内の処理を実行する
		for (long friendId : twitter.getFriendsIDs(-1).getIDs()) {
			for (int i = 0; i < followersId.length; i++) {
				// フォローしているIDと、フォロワーのIDが一致するなら
				if (friendId == followersId[i]) {
					isFollowed = true;
				}
			}

			// 自分はフォローしているが相手はフォローしていない
			if (!isFollowed) {
				try {
					twitter.destroyFriendship(friendId);
				} catch (TwitterException e) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning(e.toString());
					}
				}
			}
		}
	}

	// Mentionを取得するメソッド
	public static List<Status> getMentions() throws TwitterException {
		Twitter twitter = TwitterHelper.getTwitter();
		return twitter.getMentions();
	}

}
