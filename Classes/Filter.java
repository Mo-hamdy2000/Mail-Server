package Classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import Interfaces.IFilter;
import Interfaces.ILinkedList;

public class Filter implements IFilter {

	private FilterAttribute filterAttribute;
	private Object value;
	
	public Filter(FilterAttribute attribute, Object value) {
		this.filterAttribute = attribute;
		this.value = value;
	}
	
	public ILinkedList filter(ILinkedList mails, Folder currentFolder) {
		if (filterAttribute == FilterAttribute.Attachments) {
			return filterAttachment(mails, currentFolder);
		}
		else if (filterAttribute == FilterAttribute.Date) {
			DoublyLinkedList list = (DoublyLinkedList) mails;
			return list.searchStack(new Sort(SortAttribute.LenientDate).sortAttribute(), value);
		}
		else if (filterAttribute == FilterAttribute.Recievers) {
			return filterRecievers(mails, currentFolder);
		}
		else if (filterAttribute == FilterAttribute.SenderAddress) {
			DoublyLinkedList list = (DoublyLinkedList) mails;
			return list.searchStack(new Sort(SortAttribute.SenderAddress).sortAttribute(), value);
		}
		else if (filterAttribute == FilterAttribute.SenderName) {
			DoublyLinkedList list = (DoublyLinkedList) mails;
			return list.searchStack(new Sort(SortAttribute.SenderName).sortAttribute(), value);
		}
		else if (filterAttribute == FilterAttribute.Text) {
			return filterText(mails, currentFolder);
		}
		else if (filterAttribute == FilterAttribute.Title) {
			DoublyLinkedList list = (DoublyLinkedList) mails;
			return list.searchStack(new Sort(SortAttribute.Title).sortAttribute(), value);
		}
		
		return null;
	}
	
	private ILinkedList filterAttachment(ILinkedList list, Folder folder) {
		String s = System.getProperty("file.separator");
		DoublyLinkedList mails = (DoublyLinkedList)list;
		DoublyLinkedList mailsFiltered = new DoublyLinkedList();
		Iterator<Object> it = mails.iterator(true);
		while(it.hasNext()) {
			Mail mail = (Mail)it.next();
			File file = new File(folder.getPath() 
					+ s + mail.getID() + s + "attachment");
			if (file.exists()) {
				String[] files = file.list();
				for (String pathname : files) {
					if (value.equals(pathname)) { mailsFiltered.add(mail);}
		        }
			}
		}
		return mailsFiltered;
	}

	private ILinkedList filterRecievers(ILinkedList list, Folder folder) {
		String s = System.getProperty("file.separator");
		DoublyLinkedList mails = (DoublyLinkedList)list;
		DoublyLinkedList mailsFiltered = new DoublyLinkedList();
		Iterator<Object> it = mails.iterator(true);
		while(it.hasNext()) {
			Mail mail = (Mail)it.next();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(folder.getPath() 
						+ s + mail.getID() + s + mail.getID() + s + ".txt"));
				for (int i = 0; i < 6; i++) {
					reader.readLine();
				}
				// Seventh Line the mail recievers addresses
				String row = reader.readLine();
				reader.close();
				String[] recievers = row.split(",");
				for (String reciever : recievers) {
					if (value.equals(reciever)) { mailsFiltered.add(mail);}
		        }
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Sorry we error occured while loading the file");
			}
		}
		return mailsFiltered;
	}
	
	private ILinkedList filterText(ILinkedList list, Folder folder) {
		String s = System.getProperty("file.separator");
		DoublyLinkedList mails = (DoublyLinkedList)list;
		DoublyLinkedList mailsFiltered = new DoublyLinkedList();
		Iterator<Object> it = mails.iterator(true);
		while(it.hasNext()) {
			Mail mail = (Mail)it.next();
			try {
				BufferedReader reader = new BufferedReader(new FileReader(folder.getPath() 
						+ s + mail.getID() + s + mail.getID() + ".txt"));
				for (int i = 0; i < 7; i++) {
					reader.readLine();
				}
				// Eighth Line the mail text
				String message = "";
				String row;
				while ((row = reader.readLine()) != null) {
				    message += row + "\n";
				}
				reader.close();
				if (message.contains(value.toString())) { mailsFiltered.add(mail);}
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Sorry we error occured while loading the file");
			}
		}
		return mailsFiltered;
	}
	
}
