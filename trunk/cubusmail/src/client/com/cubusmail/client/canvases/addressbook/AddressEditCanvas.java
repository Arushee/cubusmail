/* AddressEditCanvas.java

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

import com.cubusmail.common.model.Address;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * TODO: documentation
 * 
 * @author Juergen Schlierf
 */
public class AddressEditCanvas extends VLayout {

	private AddressEditNameForm nameForm;
	private AddressEditPhoneCanvas phoneEditCanvas;

	public AddressEditCanvas() {

		super();
		setOverflow( Overflow.SCROLL );
		setWidth100();
		setAutoHeight();

		this.nameForm = new AddressEditNameForm();
		this.phoneEditCanvas = new AddressEditPhoneCanvas();

		setMembers( this.nameForm, this.phoneEditCanvas );

		init();
	}

	private void init() {

		this.phoneEditCanvas.init();
	}

	public void setAddress( Address address ) {

		this.nameForm.setAddress( address );
		this.phoneEditCanvas.setAddress( address );
	}
}