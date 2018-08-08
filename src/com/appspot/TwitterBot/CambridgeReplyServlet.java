package com.appspot.TwitterBot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.reduls.igo.Morpheme;
import net.reduls.igo.Tagger;

import twitter4j.Status;
import twitter4j.TwitterException;

@SuppressWarnings("serial")
public class CambridgeReplyServlet extends HttpServlet {

	// カタカナをひらがなに変換するメソッド
	private static String KatakanaToHiragana(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c >= 'ァ' && c <= 'ン') {
				sb.setCharAt(i, (char) (c - 'ァ' + 'ぁ'));
			} else if (c == 'ヵ') {
				sb.setCharAt(i, 'か');
			} else if (c == 'ヶ') {
				sb.setCharAt(i, 'け');
			} else if (c == 'ヴ') {
				sb.setCharAt(i, 'う');
				sb.insert(i + 1, '゛');
				i++;
			}
		}
		return sb.toString();
	}

	// 文字列を入れ替えるメソッド
	private static String ShuffleWord(String s) {

		// 文字の上限
		int threshold = 3;

		// 受け取った文字をひらがなに変換
		s = KatakanaToHiragana(s);

		// 受け取った文字列が3文字以下なら返す
		if (s.length() <= threshold) {
			return s;
		}

		// 変数の初期化
		// ArrayListインスタンスの作成
		ArrayList<Character> midstr = new ArrayList<Character>();
		// 接頭辞
		String ss = "";
		// 接尾辞
		String ls = "";
		// 真ん中の文字
		String ms = "";
		// シャッフル後の真ん中の文字
		String sms = "";

		// 最初と最後の文字を変数に格納
		for (int i = 0; i < s.length(); i++) {
			if (i == 0) {
				ss = String.valueOf(s.charAt(i));
			} else if (i == (s.length() - 1)) {
				ls = String.valueOf(s.charAt(i));
			} else {
				midstr.add(s.charAt(i));
			}
		}

		// msに文字列を格納
		for (Character character : midstr) {
			ms += character;
		}

		// シャッフルして元の言葉以外になったら返す
		int i = 0;
		while (ms.equals(sms) || i == 0) {
			// smsを初期化
			sms = "";
			// 真ん中の要素をシャッフル
			Collections.shuffle(midstr);
			// smsに文字列を格納
			for (Character character : midstr) {
				sms += character;
			}
			i++;
		}

		return (ss + sms + ls);

	}

	// MentionDataからデータを検索するメソッド
	@SuppressWarnings("unchecked")
	private static List<MentionData> SearchMention(Status mention,
			PersistenceManager pm) {

		// クエリの発行
		Query query = pm.newQuery(MentionData.class);
		// 条件にstatusID=mentionID(mention.getId())を指定
		query.setFilter("statusid == id");
		query.declareParameters("Long id");

		// クエリの実行
		List<MentionData> results = (List<MentionData>) query.execute(mention
				.getId());

		return results;

	}

	// データストアを作成するメソッド
	private static void MakeDataStore(String user, String text, long statusid,
			String cambridgetext, PersistenceManager pm) {

		MentionData md = new MentionData(user, text, statusid);
		TweetData td = new TweetData(user, cambridgetext);
		pm.makePersistentAll(md, td);

	}

	// 呼び出されたときに実行されるメソッド
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {

			// mentionを取得
			List<Status> mentions = TwitterHelper.getMentions();

			// mentionの数ループ
			for (Status mention : mentions) {

				// データストアのアクセスの際に用いるpmを作成
				PersistenceManager pm = PMF.get().getPersistenceManager();

				try {
					// 結果が空ならば
					if (SearchMention(mention, pm).isEmpty()) {

						String user = mention.getUser().getScreenName();
						String text = mention.getText();
						long statusid = mention.getId();
						String cambridgetext = "";

						// ここから形態素解析の処理
						// (1)辞書を指定して形態素解析Taggerのインスタンスを作成する.
						Tagger tagger = new Tagger("ipadic/");

						// (2)フォーム中のテキストをparseで解析する.戻り値はMorpheme型のリストである.
						List<Morpheme> parsed = tagger.parse(text);

						// (3) ListひとつひとつMorpheme型を画面に出力する.
						// surfaceには元の単語,featureには解析結果が入っている.
						for (Morpheme morpheme : parsed) {
							// 文字を解析し、分割したときに入れる配列
							String[] split;
							// 読み方を配列に格納
							split = morpheme.feature.split(",");
							// 配列が7個以上あるならば、読み方があるので文字列を変換して変換して追加
							// 無いならば元の単語をそのまま代入
							int threshold = 7;
							if (split.length > threshold) {
								cambridgetext += (ShuffleWord(split[7]) + " ");
							} else {
								cambridgetext += morpheme.surface;
							}
						}

						// @自分 を削除
						if (cambridgetext.contains("@ap3_10132")) {
							cambridgetext = cambridgetext.replaceAll(
									"@ap3_10132", "");
						}

						// 140文字を超えなければポストする
						String message = "@" + user + " [変換後]:" + cambridgetext;

						int post_limit = 140;
						if (!(message.length() <= post_limit)) {
							TwitterHelper.tweet("@" + user + "ちょっとなすがきます。");
						} else {

							TwitterHelper.tweet(message);

							// mentionをデータストアに登録
							MakeDataStore(user, text, statusid, cambridgetext,	pm);
						}
					}
				} finally {
					pm.close();
				}

			}

		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}
}