package com.appspot.TwitterBot;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.TwitterException;

@SuppressWarnings("serial")
public class CambridgeServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// データストアのアクセスの際に用いるpmを作成
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			// クエリの発行
			Query query = pm.newQuery(TweetData.class);

			// クエリの実行
			List<TweetData> results = (List<TweetData>) query.execute();

			// resultsが空で無いならば
			if (!results.isEmpty()) {
				// 乱数の生成
				Random rnd = new Random();
				int rnd_results = rnd.nextInt(results.size());

				// TweetDataから乱数のものを取り出しデータを格納
				TweetData data = results.get(rnd_results);
				// つぶやくテキスト
				String message = "[@" + data.getUser() + "]の読めちゃう文章:"
						+ data.getText();

				// messageが140文字以下なら
				int post_limit = 140;
				if (message.length() <= post_limit) {
					// つぶやく
					TwitterHelper.tweet(message);
				}
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}

	}

}