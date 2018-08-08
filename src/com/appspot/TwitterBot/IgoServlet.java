package com.appspot.TwitterBot;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.reduls.igo.Morpheme;
import net.reduls.igo.Tagger;

@SuppressWarnings("serial")
public class IgoServlet extends HttpServlet {

	protected String[] split;
	ArrayList<String> katakana = new ArrayList<String>();

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

	private static String ShuffleWord(String s) {

		// 変数の初期化
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

		// 真ん中の文字が1文字以下の言葉ならシャッフルしないでそのまま返す
		if (ms.length() <= 1) {

			return (ss + ms + ls);

		} else {
		// それ以外はシャッフルして元の言葉以外になったら返す

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

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// ブラウザからアクセスが来たら,テキスト入力用のフォーム画面へ移動する
		RequestDispatcher dispatcher = getServletContext()
				.getRequestDispatcher("/WEB-INF/index.jsp");

		dispatcher.forward(req, resp);

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8"); // 文字コードをUTF-8に設定
		String str = req.getParameter("itext"); // POSTのリクエストからitextに関連付けられたデータを取り出す.

		// ここから形態素解析の処理
		// (1)辞書を指定して形態素解析Taggerのインスタンスを作成する.
		Tagger tagger = new Tagger("ipadic/");

		// (2)フォーム中のテキストをparseで解析する.戻り値はMorpheme型のリストである.
		List<Morpheme> parsed = tagger.parse(str);

		// ブラウザにテキスト形式で出力するための準備
		resp.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		// (3) ListひとつひとつMorpheme型を画面に出力する.
		// surfaceには元の単語,featureには解析結果が入っている.
		for (Morpheme morpheme : parsed) {

			// 読み方を配列に格納
			split = morpheme.feature.split(",");
			// 読み方をカタカナからひらがなに変換し文字列型の変数に格納
			if (split.length > 7) {
				out.print(ShuffleWord(KatakanaToHiragana(split[7])) + " ");
			}else{
				out.print(morpheme.surface+" ");
			}

			// out.print(ShuffleWord(s)+" ");
			/*
			 * for (int i = 0; i < split.length; i++) { out.println(split[i]+
			 * "  " + i +"番目"); out.println(""); }
			 */

		}

	}

}
