/* AddressEditFormTypeEnum.java

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
package com.cubusmail.client.canvases.addressbook;


/**
 * TODO: documentation
 * 
 * @author Juergen Schlierf
 */
public enum AddressEditFormTypeEnum {
	DETAIL_NAME(""), PRIVATE_PHONE("Private Phone"), WORK_PHONE("Work Phone"), PRIVATE_MOBILE("Private Mobile"), WORK_MOBILE(
			"Work Mobile"), PRIVATE_FAX("Private Fax"), WORK_FAX("Work Fax");

	public final static AddressEditFormTypeEnum[] PHONE_GROUP = { PRIVATE_PHONE, WORK_PHONE, PRIVATE_MOBILE,
			WORK_MOBILE, PRIVATE_FAX, WORK_FAX };

	private String title;

	private AddressEditFormTypeEnum( String title ) {

		this.title = title;
	}

	/**
	 * @return
	 */
	public String getTitle() {

		return title;
	}

	public static AddressEditFormTypeEnum getByTitle( String title ) {

		for (AddressEditFormTypeEnum type : PHONE_GROUP) {
			if ( type.getTitle().equals( title ) ) {
				return type;
			}
		}

		return null;
	}
}