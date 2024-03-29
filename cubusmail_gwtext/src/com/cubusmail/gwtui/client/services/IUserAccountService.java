/* IUserAccountService.java

   Copyright (c) 2009 Juergen Schlierf, All Rights Reserved
   
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

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

import com.cubusmail.gwtui.domain.Contact;
import com.cubusmail.gwtui.domain.ContactFolder;
import com.cubusmail.gwtui.domain.UserAccount;

/**
 * Interface for UserAccountService.
 * 
 * @author Juergen Schlierf
 */
public interface IUserAccountService extends RemoteService {

	/**
	 * 
	 * @param account
	 */
	public UserAccount saveUserAccount( UserAccount account );

	/**
	 * 
	 * @return
	 */
	public UserAccount retrieveUserAccount();

	/**
	 * @return
	 */
	public List<ContactFolder> retrieveContactFolders();

	/**
	 * @param folder
	 */
	public ContactFolder createContactFolder( String folderName );

	/**
	 * @param folder
	 */
	public void saveContactFolder( ContactFolder folder );

	/**
	 * @param folder
	 */
	public void deleteContactFolder( ContactFolder folder );

	/**
	 * Retrieve the contacts especially for the grid.
	 * 
	 * @return
	 */
	public String[][] retrieveContactArray( ContactFolder folder, String sortField, String sortDirection );

	/**
	 * @param contact
	 */
	public void saveContact( Contact contact );

	/**
	 * @param contacts
	 */
	public void deleteContacts( Long[] ids );

	/**
	 * @param contactIds
	 * @param targetFolder
	 */
	public void moveContacts( Long[] contactIds, ContactFolder targetFolder );
	
	/**
	 * @param id
	 * @return
	 */
	public Contact retrieveContact( Long id );

	/**
	 * @return
	 */
	public String[][] retrieveTimezones();

	/**
	 * @return
	 */
	public String[][] retrieveRecipientsArray(String filterLine);
}
