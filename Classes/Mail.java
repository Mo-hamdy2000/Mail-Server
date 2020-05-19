package Classes;

import java.util.Date;

import Interfaces.IMail;

public class Mail implements IMail {
	
	/*
	 * ID member is  a unique ID identifies each mail
	 * */
	private int							ID;
	/*
	 * Text member is  a string field carrying the body message of the mail
	 * */
	private String						text;
	/*
	 * Title member is  a string field the title of the mail
	 * */
	private String						title;
	/*
	 * SenderAddress member to identify mail's sender
	 * */
	private String						senderAddress;
	/*
	 * SenderName member to facilitate sender recognition
	 * */
	private String						senderName;
	/*
	 * RecieverID member to identify mail's reciever
	 * */
	private SinglyLinkedList			recieversAddress;
	/*
	 * Attachments member carries mail attachments
	 * */
	private SinglyLinkedList			attachments;
	/*
	 * Date member stores mail sending date
	 * */
	private Date 						date;
	/*
	 * Priority member stores mail priority
	 * */
	private Priority					priority;
	// Adding Replies
	
	public Mail(String title, String senderAddress, String senderName, Date date, Priority priority) {
		//this.ID = Generate Unique next ID;
		this.title = title;
		this.senderAddress = senderAddress;
		this.senderName = senderName;
		this.date = date;
		this.setPriority(priority);
	}
	
	public Mail(int ID, String title, String senderAddress, String senderName, Date date, Priority priority) {
		this.ID = ID;
		this.title = title;
		this.senderAddress = senderAddress;
		this.senderName = senderName;
		this.date = date;
		this.setPriority(priority);
	}
	
	/*
	 * Return a copy of the head of the mail
	 * */
	public Mail copyHead() {
		Mail copy = new Mail(this.ID, this.title, this.senderAddress, this.senderName, this.date, this.priority);
		return copy;
	}
	
	public int getID() {
		return ID;
	}
	
	public String getText() {
		return text;
	}
	public void setText(String mailText) {
		this.text = mailText;
	}
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	
	
	public SinglyLinkedList getRecieverAddress() {
		return recieversAddress;
	}
	public void setRecieverAddress(SinglyLinkedList recieversAddress) {
		this.recieversAddress = recieversAddress;
	}
	
	
	public SinglyLinkedList getAttachments() {
		return attachments;
	}
	public void setAttachments(SinglyLinkedList attachments) {
		this.attachments = attachments;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getSenderAddress() {
		return senderAddress;
	}

	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	
}
