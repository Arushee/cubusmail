/* ActionRegistry.java

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
package com.cubusmail.client.actions;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry for all actions used by Cubusmail.
 * 
 * @author Juergen Schlierf
 */
public enum ActionRegistry {
	LOGIN;

	private static Map<ActionRegistry, IGWTAction> ACTION_MAP = new HashMap<ActionRegistry, IGWTAction>();

	/**
	 * @return
	 */
	public IGWTAction get() {

		IGWTAction result = ACTION_MAP.get( this );
		if ( result == null ) {
			result = create();
			ACTION_MAP.put( this, result );
		}

		return result;
	}

	/**
	 * @param <T>
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends IGWTAction> T get( Class<T> type ) {

		// type.cast() is not pssible with GWT
		return (T) get();
	}

	/**
	 * create actions for the main toolbar
	 */
	private IGWTAction create() {

		switch (this) {
		case LOGIN:
			return new LoginAction();
		}

		throw new IllegalArgumentException( "Action missing: " + name() );
	}

	/**
	 * 
	 */
	public void execute() {

		get().execute();
	}
}
