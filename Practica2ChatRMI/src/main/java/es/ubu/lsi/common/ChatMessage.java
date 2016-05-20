package es.ubu.lsi.common;


import java.io.Serializable;

/**
 * Message in chat system with RMI.
 * 
 * @author Raúl Marticorena
 * @author Joaquin P. Seco
 *
 */
public class ChatMessage implements Serializable {
	
	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 3568394256872435476L;
	
	/** Nickname. */
	private String nickname;	

	/** Text. */
	private String message;
	
	/** Client id. */
	private int id;
	
	/** Encrypted **/
	private boolean isEncrypted;
	
	/**
	 * Constructor.
	 * 
	 * @param id client id
	 * @param message message
	 */
	public ChatMessage(int id, String message) {
		this.setId(id);
		this.setMessage(message);
		this.setEncrypted(false); 
	}
	
	/**
	 * Constructor.
	 * 
	 * @param id client id
	 * @param nickname nickname
	 * @param message message
	 */
	public ChatMessage(int id, String nickname, String message) {
		this(id, message);
		this.setNickName(nickname);
	}
	
	/**
	 * Gets message.
	 * 
	 * @return message
	 * @see #setMessage
	 */
	public String getMessage() {
		return message;
	}
	
	/**
	 * Sets message.
	 * 
	 * @param message message
	 * @see #getMessage
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Gets id.
	 * 
	 * @return sender id
	 * @see #setId(int)
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets sender id.
	 * 
	 * @param id sender id
	 * @see #getId()
	 * 
	 */
	private void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets nickname.
	 * 
	 * @return the nickname
	 * @see #setNickName(String)
	 */
	public String getNickName() {
		return nickname;
	}

	/**
	 * Sets nickname.
	 * 
	 * @param nickname nickname
	 * @see #getNickName()
	 */
	public void setNickName(String nickname) {
		this.nickname = nickname;
	}
	
	/**
	 * Checks if the message is encrypted
	 * 
	 * @return message's status
	 * @see #setEncrypted(boolean)
	 */
	public boolean isEncrypted() {
		return isEncrypted;
	}
	
	/**
	 * Sets encrypted status
	 * 
	 * @param isEncrypted message's status
	 * @see #isEncrypted()
	 */
	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}
}

