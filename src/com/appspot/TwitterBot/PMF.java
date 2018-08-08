package com.appspot.TwitterBot;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

//このクラスはCRUD操作に必要なPersistanceManager型と互換性のあるクラスを扱いやすくするもの
public class PMF {

	private static final PersistenceManagerFactory pmfInstance =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private PMF(){

	}
	public	static PersistenceManagerFactory get(){
		return pmfInstance;
	}
}
