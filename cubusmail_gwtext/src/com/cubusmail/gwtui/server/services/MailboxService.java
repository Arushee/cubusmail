/* MailboxService.java

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
package com.cubusmail.gwtui.server.services;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.cubusmail.core.BeanFactory;
import com.cubusmail.core.BeanIds;
import com.cubusmail.core.CubusConstants;
import com.cubusmail.gwtui.client.exceptions.GWTInvalidAddressException;
import com.cubusmail.gwtui.client.exceptions.GWTInvalidSessionException;
import com.cubusmail.gwtui.client.exceptions.GWTMessageException;
import com.cubusmail.gwtui.client.exceptions.folder.GWTMailFolderException;
import com.cubusmail.gwtui.client.exceptions.folder.GWTMailFolderExistException;
import com.cubusmail.gwtui.client.model.GWTAttachment;
import com.cubusmail.gwtui.client.model.GWTMailFolder;
import com.cubusmail.gwtui.client.model.GWTMessage;
import com.cubusmail.gwtui.client.model.GWTMessageList;
import com.cubusmail.gwtui.client.services.IMailboxService;
import com.cubusmail.gwtui.domain.GWTMailConstants;
import com.cubusmail.gwtui.domain.MessageListFields;
import com.cubusmail.gwtui.domain.Preferences;
import com.cubusmail.gwtui.domain.UserAccount;
import com.cubusmail.mail.IMailFolder;
import com.cubusmail.mail.IMailbox;
import com.cubusmail.mail.MessageHandler;
import com.cubusmail.mail.SessionManager;
import com.cubusmail.mail.exceptions.IErrorCodes;
import com.cubusmail.mail.exceptions.MailFolderException;
import com.cubusmail.mail.text.MessageTextMode;
import com.cubusmail.mail.text.MessageTextUtil;
import com.cubusmail.mail.util.MessageUtils;
import com.cubusmail.mail.util.MessageUtils.AddressStringType;
import com.cubusmail.user.UserAccountDao;

/**
 * Servlet for all mailbox services.
 * 
 * @author Juergen Schlierf
 */
public class MailboxService extends RemoteServiceServlet implements IMailboxService {

	private Logger log = Logger.getLogger( getClass().getName() );

	private static final long serialVersionUID = 6489103982844626238L;

	private WebApplicationContext applicationContext;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.user.server.rpc.RemoteServiceServlet#processCall(java.
	 * lang.String)
	 */
	@Override
	public String processCall( String payload ) throws SerializationException {

		if ( this.applicationContext == null ) {
			this.applicationContext = WebApplicationContextUtils.getWebApplicationContext( getServletContext() );
		}

		if ( SessionManager.isLoggedIn() ) {
			try {
				return super.processCall( payload );
			}
			catch (SerializationException e) {
				log.error( e.getMessage(), e );
				throw e;
			}
		}
		else {
			RPCRequest rpcRequest = RPC.decodeRequest( payload, this.getClass(), this );
			return RPC.encodeResponseForFailure( rpcRequest.getMethod(), new GWTInvalidSessionException() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#retrieveFolderTree()
	 */
	public GWTMailFolder[] retrieveFolderTree() throws Exception {

		long millis = System.currentTimeMillis();

		IMailbox mailbox = SessionManager.get().getMailbox();
		try {
			mailbox.reloadFolder();
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		GWTMailFolder[] result = ConvertUtil.convert( mailbox.getMailFolderList() );

		log.debug( "Time for retrieveFolderTree(): " + (System.currentTimeMillis() - millis) + "ms" );

		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#createFolder(java
	 * .lang.String, java.lang.String)
	 */
	public GWTMailFolder createFolder( String parentFolderId, String folderName ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "creating folder... " + folderName );

		IMailFolder newFolder;
		try {
			newFolder = mailbox.createFolder( parentFolderId, folderName );
			log.debug( "...successful" );

			return ConvertUtil.convert( newFolder );
		}
		catch (MailFolderException e) {
			log.error( e.getMessage(), e );
			if ( e.hasErrorCode( IErrorCodes.EXCEPTION_FOLDER_ALREADY_EXIST ) ) {
				throw new GWTMailFolderExistException( null, folderName );
			}
			else {
				throw new GWTMailFolderException( null, folderName );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#moveFolder(java.lang
	 * .String, java.lang.String)
	 */
	public void moveFolder( String sourceFolderId, String targetFolderId ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "moving folder... " + sourceFolderId );

		IMailFolder sourceFolder = mailbox.getMailFolderById( sourceFolderId );
		try {

			mailbox.moveFolder( sourceFolderId, targetFolderId );
			log.debug( "...successful" );
		}
		catch (MailFolderException e) {
			log.error( e.getMessage(), e );
			if ( e.hasErrorCode( IErrorCodes.EXCEPTION_FOLDER_ALREADY_EXIST ) ) {
				throw new GWTMailFolderExistException( null, sourceFolder.getName() );
			}
			else {
				throw new GWTMailFolderException( null, sourceFolder.getName() );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#renameFoler(java.
	 * lang.String, java.lang.String)
	 */
	public String renameFolder( String folderId, String newName ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "renaming folder... " + folderId );

		try {
			mailbox.renameFolder( folderId, newName );
			log.debug( "...successful" );

			return newName;
		}
		catch (MailFolderException e) {
			log.error( e.getMessage(), e );
			if ( e.hasErrorCode( IErrorCodes.EXCEPTION_FOLDER_ALREADY_EXIST ) ) {
				throw new GWTMailFolderExistException( null, e.getFolder().getName() );
			}
			else {
				throw new GWTMailFolderException( null, e.getFolder().getName() );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#deleteFolder(java
	 * .lang.String)
	 */
	public void deleteFolder( String folderId ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "deleting folder " + folderId );

		try {
			mailbox.deleteFolder( folderId );
			log.debug( "...successful" );
		}
		catch (MailFolderException e) {
			log.error( e.getMessage(), e );
			throw new GWTMailFolderException( null, e.getFolder().getName() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#emptyFolder(java.
	 * lang.String)
	 */
	public void emptyFolder( String folderId ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "emptying folder " + folderId );

		try {
			mailbox.emptyFolder( folderId );
			log.debug( "...successful" );
		}
		catch (MailFolderException e) {
			log.error( e.getMessage(), e );
			throw new GWTMailFolderException( null, e.getFolder().getName() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#retrieveMessages(
	 * java.lang.String)
	 */
	public GWTMessageList retrieveMessages( String folderId, int start, int pageSize, String sortField, String dir,
			String[][] params ) throws Exception {

		if ( folderId != null ) {
			IMailbox mailbox = SessionManager.get().getMailbox();
			UserAccount account = SessionManager.get().getUserAccount();
			log.debug( "retrieving messages from " + folderId + " ..." );

			try {
				IMailFolder currentFolder = mailbox.getMailFolderById( folderId );
				mailbox.setCurrentFolder( currentFolder );

				Message[] msgs = currentFolder.retrieveMessages( sortField );

				String quickSearchFields = MessageUtils.getParamValue( params, "fields" );
				String extendedSearchFields = MessageUtils.getParamValue( params,
						GWTMailConstants.EXTENDED_SEARCH_FIELDS );

				// all messages with only header data

				// quick search params
				if ( quickSearchFields != null ) {
					String quickSearchText = MessageUtils.getParamValue( params, "query" );
					msgs = MessageUtils.quickFilterMessages( msgs, quickSearchFields, quickSearchText );
				}
				else if ( extendedSearchFields != null ) {
					msgs = MessageUtils.filterMessages( currentFolder, msgs, extendedSearchFields, params );
				}

				boolean ascending = "ASC".equals( dir );
				MessageUtils.sortMessages( msgs, sortField, ascending );

				if ( msgs != null && msgs.length > 0 ) {
					log.debug( "Building Array objects..." );
					long time = System.currentTimeMillis();

					int total_count = msgs.length;
					start = Math.min( total_count - 1, start == -1 ? 0 : start );
					pageSize = pageSize == -1 ? account.getPreferences().getPageCount() : pageSize;
					pageSize = Math.min( pageSize, total_count - start );

					Message[] pagedMessages = new Message[pageSize];
					int pagedIndex = 0;
					for (int msgIndex = start; msgIndex < start + pageSize; msgIndex++) {
						pagedMessages[pagedIndex++] = msgs[msgIndex];
					}
					FetchProfile completeProfile = MessageUtils.createFetchProfile( true, null );
					currentFolder.fetch( pagedMessages, completeProfile );

					String[][] messageStringArray = new String[pageSize][MessageListFields.values().length];
					Preferences preferences = SessionManager.get().getPreferences();

					// get date formats for message list date
					Locale locale = SessionManager.get().getLocale();
					TimeZone timezone = SessionManager.get().getTimeZone();
					String datePattern = this.applicationContext.getMessage(
							CubusConstants.MESSAGELIST_DATE_FORMAT_PATTERN, null, locale );
					String timePattern = this.applicationContext.getMessage(
							CubusConstants.MESSAGELIST_TIME_FORMAT_PATTERN, null, locale );

					NumberFormat sizeFormat = MessageUtils.createSizeFormat( locale );

					DateFormat dateFormat = null;
					DateFormat timeFormat = null;
					if ( preferences.isShortTimeFormat() ) {
						dateFormat = new SimpleDateFormat( datePattern, locale );
						timeFormat = new SimpleDateFormat( timePattern, locale );
						timeFormat.setTimeZone( timezone );
					}
					else {
						dateFormat = new SimpleDateFormat( datePattern + " " + timePattern, locale );
					}
					dateFormat.setTimeZone( timezone );
					Date today = Calendar.getInstance( timezone ).getTime();

					for (int i = 0; i < pageSize; i++) {
						if ( preferences.isShortTimeFormat()
								&& DateUtils.isSameDay( today, pagedMessages[i].getSentDate() ) ) {
							// show only time
							ConvertUtil.convertToStringArray( currentFolder, pagedMessages[i], messageStringArray[i],
									timeFormat, sizeFormat );
						}
						else {
							ConvertUtil.convertToStringArray( currentFolder, pagedMessages[i], messageStringArray[i],
									dateFormat, sizeFormat );
						}
					}
					log.debug( "..finish. Time for building Array: " + (System.currentTimeMillis() - time) );

					return new GWTMessageList( messageStringArray, msgs.length );
				}

				return null;
			}
			catch (MessagingException e) {
				log.error( e.getMessage(), e );
				throw new GWTMessageException( e.getMessage() );
			}
		}
		else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#retrieveMessage(int)
	 */
	public GWTMessage retrieveMessage( String folderId, long messageId, boolean loadImages ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "retrieving message for " + messageId + " ..." );

		try {
			IMailFolder selectedFolder = mailbox.getCurrentFolder();
			Message msg = selectedFolder.getMessageById( messageId );

			MessageHandler handler = MessageHandler.getInstance( mailbox.getJavaMailSession(), (MimeMessage) msg );
			handler.readBodyContent( loadImages, MessageTextMode.DISPLAY );
			GWTMessage result = handler.getGWTMessage();

			return result;
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		catch (IOException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#getFormattedMessageCount
	 * ()
	 */
	public String getFormattedMessageCount( String folderId ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		IMailFolder folder = mailbox.getMailFolderById( folderId );
		if ( folder != null ) {
			int unread = folder.getUnreadMessageCount();

			if ( unread > 0 ) {
				return "<b>" + folder.getName() + " (" + unread + ")</b>";
			}
			else {
				return folder.getName();
			}
		}
		else {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#markMessage(long[],
	 * com.cubusmail.gwtui.domain.MessageListFields, boolean)
	 */
	public void markMessage( long[] messageIds, MessageListFields flagField, boolean mark ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		if ( messageIds != null && messageIds.length > 0 ) {
			log.debug( "marking " + messageIds.length + " messages..." );

			try {
				IMailFolder currentFolder = mailbox.getCurrentFolder();
				for (int i = 0; i < messageIds.length; i++) {
					Message msg = currentFolder.getMessageById( messageIds[i] );
					if ( flagField.equals( MessageListFields.READ_FLAG ) ) {
						MessageUtils.setMessageFlag( msg, Flags.Flag.SEEN, mark );
					}
					else if ( flagField.equals( MessageListFields.DELETED_FLAG ) ) {
						MessageUtils.setMessageFlag( msg, Flags.Flag.DELETED, mark );
					}
					else {
						throw new IllegalArgumentException( "Unknown flag: " + flagField.name() );
					}
				}
				log.debug( "...successful" );
			}
			catch (MessagingException e) {
				log.error( e.getMessage(), e );
				throw new GWTMessageException( e.getMessage() );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#copyMoveMessages(
	 * long[], java.lang.String, boolean)
	 */
	public void copyMoveMessages( long[] messageIds, String targetFolderId, boolean toMove ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();

		if ( messageIds != null && messageIds.length > 0 ) {
			log.debug( "copy/move " + messageIds.length + " messages..." );

			try {
				mailbox.copyMessages( messageIds, targetFolderId );
				log.debug( "...successful" );

				if ( toMove ) {
					mailbox.deleteMessages( messageIds );
				}
			}
			catch (MessagingException e) {
				log.error( e.getMessage(), e );
				throw new GWTMessageException( e.getMessage() );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#deleteMessages(long
	 * [])
	 */
	public void deleteMessages( long[] messageIds ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();

		if ( messageIds != null && messageIds.length > 0 ) {
			log.debug( "delete " + messageIds.length + " messages..." );

			try {
				if ( mailbox.getCurrentFolder().isTrash() || mailbox.getTrashFolder() == null ) {
					mailbox.deleteMessages( messageIds );
				}
				else {
					copyMoveMessages( messageIds, mailbox.getTrashFolder().getId(), true );
				}
			}
			catch (MessagingException e) {
				log.error( e.getMessage(), e );
				throw new GWTMessageException( e.getMessage() );
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#sendMessage(com.cubusmail
	 * .gwtui.client.model.GWTMessage)
	 */
	public void sendMessage( GWTMessage message ) throws Exception {

		try {
			log.debug( "sending message..." );
			MessageHandler messageHandler = SessionManager.get().getCurrentComposeMessage();
			messageHandler.setGWTMessage( message );
			messageHandler.send();
			IMailbox mailbox = SessionManager.get().getMailbox();
			IMailFolder sentFolder = mailbox.getSentFolder();
			messageHandler.saveToFolder( sentFolder, false );
			log.debug( "...successful" );

			try {
				getUserAccountDao().saveRecipients( SessionManager.get().getUserAccount(),
						messageHandler.getAllRecipients() );
			}
			catch (Throwable e) {
				// catch all exceptions
				log.error( e.getMessage(), e );
			}
		}
		catch (AddressException e) {
			log.error( e.getMessage(), e );
			throw new GWTInvalidAddressException( e.getMessage(), e.getRef() );
		}
		catch (SendFailedException e) {
			log.error( e.getMessage(), e );
			if ( "Invalid Addresses".equals( e.getMessage() ) ) {
				String address = "";
				try {
					address = MessageUtils.getMailAdressString( e.getInvalidAddresses(), AddressStringType.PERSONAL );
				}
				catch (MessagingException ex) {
					log.error( ex.getMessage(), ex );
				}
				throw new GWTInvalidAddressException( e.getMessage(), address );
			}
			else {
				throw new GWTMessageException( e.getMessage() );
			}
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		catch (IOException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#prepareNewMessage()
	 */
	public void prepareNewMessage() {

		log.debug( "preparing new compose message..." );
		MessageHandler newMessageHandler = MessageHandler.getInstance( SessionManager.get().getMailbox()
				.getJavaMailSession() );
		SessionManager.get().setCurrentComposeMessage( newMessageHandler );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#prepareReplyMessage
	 * (long)
	 */
	public GWTMessage prepareReplyMessage( long messageId, boolean replyAll ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "preparing reply message..." );

		try {
			IMailFolder currentFolder = mailbox.getCurrentFolder();
			Message msg = currentFolder.getMessageById( messageId );

			MessageHandler replyMessageHandler = MessageHandler.getInstance( mailbox.getJavaMailSession() );
			replyMessageHandler.createReplyMessage( msg, replyAll );

			SessionManager.get().setCurrentComposeMessage( replyMessageHandler );

			return replyMessageHandler.getGWTMessage();
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		catch (IOException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
	}

	/**
	 * 
	 */
	public void cancelComposeMessage() {

		log.debug( "remove compose message from session..." );
		SessionManager.get().setCurrentComposeMessage( null );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#openDraftMessage(
	 * long)
	 */
	public GWTMessage openDraftMessage( long messageId ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "open message for " + messageId + " ..." );

		try {
			IMailFolder selectedFolder = mailbox.getCurrentFolder();
			Message msg = selectedFolder.getMessageById( messageId );

			MessageHandler readHandler = MessageHandler.getInstance( mailbox.getJavaMailSession(), (MimeMessage) msg );
			readHandler.readBodyContent( true, MessageTextMode.DRAFT );
			prepareNewMessage();

			GWTMessage result = readHandler.getGWTMessage();

			return result;
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		catch (IOException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#prepareForwardMessage
	 * (long)
	 */
	public GWTMessage prepareForwardMessage( long messageId ) throws Exception {

		IMailbox mailbox = SessionManager.get().getMailbox();
		log.debug( "preparing forward message..." );

		try {
			IMailFolder currentFolder = mailbox.getCurrentFolder();
			Message msg = currentFolder.getMessageById( messageId );

			MessageHandler forardMessageHandler = MessageHandler.getInstance( mailbox.getJavaMailSession() );
			forardMessageHandler.createForwardMessage( msg );

			SessionManager.get().setCurrentComposeMessage( forardMessageHandler );

			GWTMessage result = forardMessageHandler.getGWTMessage();
			result.setAttachments( forardMessageHandler.getGWTComposeAttachments() );

			return result;
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		catch (IOException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.cubusmail.gwtui.client.services.IMailboxService#
	 * retrieveCurrentComposeMessage()
	 */
	public GWTAttachment[] retrieveCurrentComposeMessageAttachments() throws Exception {

		log.debug( "retrieving compose message..." );
		MessageHandler composeMessage = SessionManager.get().getCurrentComposeMessage();
		return composeMessage.getGWTComposeAttachments();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.cubusmail.gwtui.client.services.IMailboxService#
	 * removeAttachmentFromComposeMessage(int)
	 */
	public GWTAttachment[] removeAttachmentFromComposeMessage( int index ) throws Exception {

		log.debug( "removing attachment from compose message..." );
		MessageHandler composeMessage = SessionManager.get().getCurrentComposeMessage();
		if ( composeMessage.getComposeAttachments() != null && composeMessage.getComposeAttachments().size() > 0 ) {
			composeMessage.getComposeAttachments().remove( index );
		}

		return composeMessage.getGWTComposeAttachments();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#saveMessageAsDraft
	 * (com.cubusmail.gwtui.client.model.GWTMessage)
	 */
	public void saveMessageAsDraft( GWTMessage message ) throws Exception {

		try {
			log.debug( "saving message to draft..." );
			MessageHandler messageHandler = SessionManager.get().getCurrentComposeMessage();
			IMailbox mailbox = SessionManager.get().getMailbox();
			IMailFolder draftFolder = mailbox.getDraftFolder();
			messageHandler.setGWTMessage( message );
			messageHandler.saveToFolder( draftFolder, true );

			// if there is the original message to delete
			if ( message.getId() > 0 ) {
				long[] deleteId = new long[] { message.getId() };
				deleteMessages( deleteId );
			}
			log.debug( "...successful" );
		}
		catch (AddressException e) {
			log.error( e.getMessage(), e );
			throw new GWTInvalidAddressException( e.getMessage(), e.getRef() );
		}
		catch (MessagingException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
		catch (IOException e) {
			log.error( e.getMessage(), e );
			throw new GWTMessageException( e.getMessage() );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#convert2PlainText
	 * (java.lang.String)
	 */
	public String convert2PlainText( String htmlText ) {

		return MessageTextUtil.convertHtml2PlainText( htmlText );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cubusmail.gwtui.client.services.IMailboxService#convert2Html(java
	 * .lang.String)
	 */
	public String convert2Html( String plainText ) {

		return MessageTextUtil.convertPlainText2Html( plainText, MessageTextMode.REPLY );
	}

	/**
	 * @return
	 */
	private UserAccountDao getUserAccountDao() {

		return (UserAccountDao) BeanFactory.getBean( BeanIds.USER_ACCOUNT_DAO );
	}
}