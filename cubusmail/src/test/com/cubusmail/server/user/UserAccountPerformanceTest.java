/* UserAccountPerformanceTest.java

   Copyright (c) 2010 Juergen Schlierf, All Rights Reserved
   
   This file is part of Cubusmail (http://code.google.com/p/cubusmail/).
	
   This library is free software; you can redistribute it and/or
   modify it under the terms of the GNU Lesser General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.
	
   This library is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Lesser General Public License for more details.
	
   You should have received a copy of the GNU Lesser General Public
   License along with Cubusmail. If not, see <http://www.gnu.org/licenses/>.
 */
package com.cubusmail.server.user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cubusmail.common.model.UserAccount;

/**
 * TODO: documentation
 * 
 * @author Juergen Schlierf
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml",
		"classpath:com/cubusmail/server/user/testPerformanceDBContext.xml",
		"classpath:com/cubusmail/server/user/testUserAcountContext.xml" })
public class UserAccountPerformanceTest implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.context.ApplicationContextAware#setApplicationContext
	 * (org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext( ApplicationContext applicationContext ) throws BeansException {

		this.applicationContext = applicationContext;
	}

	@Test
	public void testUserAccountPerformance() {

		IUserAccountDao userAccountDao = (IUserAccountDao) this.applicationContext.getBean( "userAccountDao" );

		for (int i = 0; i < 1000000; i++) {
			UserAccount testAccount = (UserAccount) this.applicationContext.getBean( "testUserAccount" );
			testAccount.setUsername( "testuser" + i );
			userAccountDao.saveUserAccount( testAccount );
		}
	}
}
