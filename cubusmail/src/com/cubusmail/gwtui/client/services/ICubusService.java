/* ICubusService.java

   Copyright (c) 2009 J�rgen Schlierf, All Rights Reserved
   
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
package com.cubusmail.gwtui.client.services;

import com.google.gwt.user.client.rpc.RemoteService;

import com.cubusmail.gwtui.client.model.GWTMailbox;

/**
 * Interface for CubusService.
 * 
 * @author J�rgen Schlierf
 */
public interface ICubusService extends RemoteService {

	/**
	 * Do login
	 * 
	 * @param username
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public GWTMailbox login( String username, String password ) throws Exception;

	/**
	 * Do logout.
	 * 
	 * @throws Exception
	 */
	public void logout() throws Exception;

	/**
	 * get mailbox
	 * 
	 * @return
	 */
	public GWTMailbox retrieveMailbox();
}