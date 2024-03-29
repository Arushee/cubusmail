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
package com.cubusmail.common.services;

import java.util.List;

import com.cubusmail.common.model.Address;
import com.cubusmail.common.model.AddressFolder;
import com.cubusmail.common.model.UserAccount;
import com.google.gwt.user.client.rpc.RemoteService;

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
	public List<AddressFolder> retrieveAddressFolders();

	/**
	 * @param folder
	 */
	public AddressFolder createAddressFolder( String folderName );

	/**
	 * @param folder
	 */
	public void saveAddressFolder( AddressFolder folder );

	/**
	 * @param folder
	 */
	public void deleteAddressFolder( AddressFolder folder );

	/**
	 * @param addressFolderId
	 * @return
	 */
	public List<Address> retrieveAddressList( AddressFolder folder, String beginChars );

	/**
	 * @param contact
	 */
	public void saveAddress( Address address );

	/**
	 * @param contacts
	 */
	public void deleteContacts( List<Long> ids );

	/**
	 * @param contactIds
	 * @param targetFolder
	 */
	public void moveContacts( Long[] contactIds, AddressFolder targetFolder );

	/**
	 * @return
	 */
	public String[][] retrieveTimezones();

	/**
	 * @return
	 */
	public String[][] retrieveRecipientsArray( String filterLine );
}
