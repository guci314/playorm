package com.alvazan.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alvazan.orm.api.base.NoSqlEntityManager;
import com.alvazan.orm.api.base.NoSqlEntityManagerFactory;
import com.alvazan.test.db.PartSecurity;
import com.alvazan.test.db.PartitionedTrade;

public class TestPartitionedJoins {

	private static final Logger log = LoggerFactory.getLogger(TestPartitionedJoins.class);
	
	private static NoSqlEntityManagerFactory factory;
	private NoSqlEntityManager mgr;

	@BeforeClass
	public static void setup() {
		factory = FactorySingleton.createFactoryOnce();
	}
	
	@Before
	public void createEntityManager() {
		mgr = factory.createEntityManager();
	}
	@After
	public void clearDatabase() {
		NoSqlEntityManager other = factory.createEntityManager();
		other.clearDatabase(true);
	}
	
	@Test
	public void testInnerJoin() {
		putEntities();
		
		long start = System.currentTimeMillis();
		List<PartitionedTrade> trades = PartitionedTrade.findInPartition(mgr, 5, "one", null);
		long total = System.currentTimeMillis()-start;
		log.info("TOTAL time="+total+" ms");
		Assert.assertEquals(2, trades.size());
	}
	
	
	@Test
	public void testLeftOuterJoin() {
		putEntities();
		
		List<PartitionedTrade> trades = PartitionedTrade.findLeftOuter(mgr, 5, "one", null);
		Assert.assertEquals(3, trades.size());
	}
	
	private void putEntities() {
		PartSecurity sec = new PartSecurity();
		sec.setId("sec1");
		sec.setSecurityType("one");
		mgr.put(sec);
		
		PartSecurity sec2 = new PartSecurity();
		sec2.setId("sec2");
		sec2.setSecurityType("two");
		mgr.put(sec2);
		
		PartSecurity sec3 = new PartSecurity();
		sec3.setId("sec3");
		sec3.setSecurityType("one");
		mgr.put(sec3);
		
		mgr.flush();

		//This trade has no account so is in the null partition of accounts
		PartitionedTrade trade1 = new PartitionedTrade();
		trade1.setId("t1");
		trade1.setSecurity(sec);
		trade1.setNumShares(5);
		mgr.put(trade1);
		
		mgr.flush();
		
		PartitionedTrade trade2 = new PartitionedTrade();
		trade2.setId("t2");
		trade2.setSecurity(sec);
		trade2.setNumShares(6);
		mgr.put(trade2);

		PartitionedTrade trade3 = new PartitionedTrade();
		trade3.setId("t3");
		trade3.setSecurity(sec3);
		trade3.setNumShares(5);
		mgr.put(trade3);

		//has null security
		PartitionedTrade trade4 = new PartitionedTrade();
		trade4.setId("t4");
		trade4.setNumShares(5);
		mgr.put(trade4);
		
		//has security 2 not one
		PartitionedTrade trade5 = new PartitionedTrade();
		trade5.setId("t5");
		trade5.setSecurity(sec2);
		trade5.setNumShares(5);
		mgr.put(trade5);
		
		mgr.flush();
		
	}
}
