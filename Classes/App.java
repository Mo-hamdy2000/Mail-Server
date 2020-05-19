package Classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import Interfaces.IApp;
import Interfaces.IContact;
import Interfaces.IFilter;
import Interfaces.IFolder;
import Interfaces.ILinkedList;
import Interfaces.IMail;
import Interfaces.ISort;
import Misc.Utils;
import Misc.AES;
import Misc.Birthday;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

public class App implements IApp {

	private Folder currentFolder = new Folder(".");
	private DoublyLinkedList mails = new DoublyLinkedList();
	private DoublyLinkedList workingList;
	boolean reverseSorting = false;
	private User loggedinUser;
	private final String sep = System.getProperty("file.separator");
	private final String path = System.getProperty("user.dir") + sep + "system"+ sep +"users";
	private final File fList = new File(path + sep +"list.txt");
	private final DoublyLinkedList list = new DoublyLinkedList();
	
	/**
	 * get current user instance from App class
	 * @return
	 */
	public User getLoggedinUser() {
	    return loggedinUser;
	  }
	
	@Override
	/**
	 * this function is responsible for signing in process  
	 * @param email mail of user
	 * @param password password of user
	 * @return true if valid sign in & false for invalid sign in
	 */
	public boolean signin(String email, String password) {
	    User user = Utils.binarySearch(email, list);
	    if (user == null) return false; //invalid sign-in
	    try {
	            if (!Objects.equals(AES.decrypt(user.getEncryptedPassword(), password), password)) return false;
	        } catch (Exception e) {
	            return false;
	        }
	        loggedinUser = loadInfo(user.getAddress());
	        return true;
	    }

	@Override
	/**
	 * this function is responsible for signing up process (creating new contact)
	 * @param contact new user info 
	 * @return true if all data is compatible & false if not compatible 
	 */
	public boolean signup(IContact contact) {
        User user;
        user = (User) contact;
        Utils.addToSorted(user, list);
        try {
            exportList();
            //create new folder with info
            Folder usersFolder = new Folder(path);
            usersFolder.createUserFolder(user.getAddress(), user.getName(), user.getGender(), user.getBirthday());
            user.setFilePath(path + sep + user.getAddress());
        } catch (IOException e) {
            return false;
        }
        loggedinUser = loadInfo(user.getAddress());
        return true;
    }
	/**
	 * this functions is used to load users 
	 * @throws FileNotFoundException
	 */
	public void loadUsers() throws FileNotFoundException {
        Scanner sc = new Scanner(fList);
        while (sc.hasNext()) {
            list.add(new User(sc.nextLine(), sc.nextLine()));
        }
        sc.close();
    }
	/**
	 * this functions check if the address is present or not
	 * @param address user address
	 * @return true if located 
	 */
	public boolean addressExists(String address) {
        User user = Utils.binarySearch(address, list);
        return user != null;
    }

    public int addressesExist(String [] arr) {
        int i = 0;
        if (arr.length == 0) return i;
        for (String address : arr) {
            if (Utils.binarySearch(address, list) == null) return i;
            else ++i;
        }
        return i;
    }
    
    /**
     * overwrites the file "list"
     *
     * @throws IOException file not found
     */
    private void exportList() throws IOException {
        if (list.isEmpty()) return;
        BufferedWriter writer = new BufferedWriter(new FileWriter(fList, false));
        Iterator<Object> i = list.iterator(true);
        while (i.hasNext()) {
            User user = (User) i.next();
            writer.write(user.getAddress());
            writer.newLine();
            writer.write(user.getEncryptedPassword());
            writer.newLine();
        }
        writer.close();
    }
    /**
     * this function load user info 
     * @param address user address
     * @return user info 
     */
    private User loadInfo(String address) {
        /*load user info*/
        User user = new User(address);
        Scanner sc;
        try {
            sc = new Scanner(new File(path + sep + address + sep +"info.txt"));
        } catch (FileNotFoundException e) {
            Utils.fileNotFound();
            return null;
        }
        user.setFilePath(path + sep + address);
        user.setName(sc.nextLine());
        user.setGender(sc.nextLine());
        /*nextLine instead of nextInt to avoid problem of newLine being taken in next nextLine call*/
        user.setBirthday(new Birthday(Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine()), Integer.parseInt(sc.nextLine())));
        sc.close();
        try {
            sc = new Scanner(new File(path + sep + address + sep +"contacts.csv"));
        } catch (FileNotFoundException e) {
            Utils.fileNotFound();
        }
        while (sc.hasNext()) {
      String s = sc.nextLine();
      String[] arr = s.split(",", 3);
      Contact c = new Contact(arr[0], user, Integer.parseInt(arr[1]));
      if (!arr[2].isEmpty())
        c.addAddresses(arr[2].split(","));
      user.getContacts().add(c);

    }
        sc.close();
        return user;
    }
    

	@Override
	/**
	 * this function manipulate folder , sorting , filter
	 * @param folder set current folder to this folder from which mails will be loaded
	 * @param filter filtering mails loaded from current folder
	 * @param sort sort and filter mails loaded from current folder according to instance created from filter class or sort class
	 */
	public void setViewingOptions(IFolder folder, IFilter filter, ISort sort) {
		// No loaded mails and no specified folder to load from
		if (folder == null && mails == null) { throw new IllegalArgumentException();}
		// Setting the specified folder
		if (folder != null) {
			this.currentFolder = (Folder) folder;
			mails.clear();
			loadMails();
		}
		workingList = mails.copyView();
		// Setting the specified filter
		if (filter != null) {
			workingList = (DoublyLinkedList) ((Filter) filter).filter(workingList, currentFolder);
		}
		// Setting the specified sort
		if (sort != null) {
			Sort sortClass = (Sort)sort; 
			workingList.Qsort(sortClass.sortAttribute());
		}
	}
	
	@Override
	/**
	 * this function list emails in certain page
	 * @param page page number
	 * @return array with mails in this page
	 */
	public IMail[] listEmails(int page) {
		// If no loaded mails then Set viewing options has not been called yet
		if (this.mails == null || this.workingList == null) { throw new IllegalStateException();}
		
		Mail[] returnedMails = new Mail[10];
		int counter = 0;
		
		Iterator<Object> it = workingList.iterator(!reverseSorting);
		// Skip the unwanted pages
		while (it.hasNext() && counter < (page-1) * 10) {
			it.next();
			counter ++;
		}
		// Load the wanted page
		while (it.hasNext() && counter < (page) * 10) {
			Mail tmpMail = (Mail) it.next();
			returnedMails[counter%10] = tmpMail;
			counter ++;
		}
		return returnedMails;
	}

	@Override
	/**
	 * this function is supposed to move a chosen email to trash
	 * @param mails list of mails wanted to be deleted
	 */
	public void deleteEmails(ILinkedList mails) {
		Folder des = new Folder(loggedinUser.getFilePath() + sep + "trash");
		moveEmails(mails, des);
	}
	
	/**
	 * this function is supposed to delete a chosen email in drafts
	 * @param mails list of mails wanted to be deleted
	 */
	public void deleteDrafts(ILinkedList mails) {
		DoublyLinkedList mailList = (DoublyLinkedList) mails;
		String indexFile = this.currentFolder.getPath() + sep + "index.csv";
		deleteIndex(mailList, indexFile);
		Iterator<Object> it = mailList.iterator(true);
		while (it.hasNext()) {
			Mail mail = (Mail) it.next();
			Folder.deleteMailFolder(loggedinUser.getFilePath() +sep+"drafts"+sep + mail.getID());
		}
	}
	
	@Override
	/**
	 * this function move mails to certain folder 
	 * @param mails list of mails to be moved
	 * @param des destination folder to be moved to
	 */
	public void moveEmails(ILinkedList mails, IFolder des) {
		Folder destFolder = (Folder) des;
		if(destFolder.folderName().equals(this.currentFolder.folderName())) { return;}
		DoublyLinkedList mailList = (DoublyLinkedList) mails;
		Iterator<Object> it = mailList.iterator(true);
		String indexFile = this.currentFolder.getPath() + sep + "index.csv";
		boolean flagForModificationDate = (destFolder.folderName().equals("trash"));
		
		deleteIndex(mailList, indexFile);
		// looping through mails
		try {
			BufferedWriter edit = new BufferedWriter(new FileWriter(destFolder.getPath() + sep + "index.csv", true));
			while (it.hasNext()) {
				Mail currentMail = (Mail) it.next();
	
				String content = currentMail.getID() + "," + currentMail.getTitle() + "," + currentMail.getSenderAddress()
						+ "," + currentMail.getSenderName() + "," + new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a").format(currentMail.getDate()) 
						+ "," + (currentMail.getPriority().ordinal()+1);
				if (flagForModificationDate) {content += "," + new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a").format(new Date());}
				edit.append(content);
				edit.append("\n");
				edit.flush();
				// moving folders by creating a copy
				File originalFile = new File(this.currentFolder.getPath() + sep
						+ currentMail.getID() + sep + currentMail.getID() + ".txt");
	
				destFolder.addSubFolder(String.valueOf(currentMail.getID()));
				File destination = new File(
						destFolder.getPath() + sep + currentMail.getID() + sep + currentMail.getID() + ".txt");
				Files.copy(originalFile.toPath(), destination.toPath());
				File file = new File(this.currentFolder.getPath() 
						+ sep + currentMail.getID() + sep + "attachment");
				if (file.exists()) {
					Folder mailFolder = new Folder(destFolder.getPath() + sep + currentMail.getID());
					mailFolder.addSubFolder("attachment");
					String[] files = file.list();
					for (String pathname : files) {
						
						File srcFile = new File(this.currentFolder.getPath() 
								+ sep + currentMail.getID() + sep + "attachment" + sep + pathname);
						String dest = destFolder.getPath() + sep + currentMail.getID() + sep + "attachment" + sep + pathname;
						Folder.copyFiles(srcFile, dest);
						srcFile.delete();
			        }
					File originalAttachmentFolder = new File(this.currentFolder.getPath() 
							+ sep + currentMail.getID() + sep + "attachment");
					originalAttachmentFolder.delete();
				}
				// deleting original files
				originalFile.delete();
				File originalFolder = new File(this.currentFolder.getPath() + sep + currentMail.getID());
				originalFolder.delete();
			}
			edit.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * this function used for creating a new email
	 * @param email mail info 
	 * @return true if mail is created successfully  
	 */
	public boolean compose(IMail email) {
		Mail  mail = (Mail)email;
    	// Data to be entered in index file
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a");
		String date = formatter.format(mail.getDate());
    	String content = mail.getID() + "," + mail.getTitle() + "," + mail.getSenderAddress() + "," + mail.getSenderName()
				+ "," + date + "," + (mail.getPriority().ordinal()+1);
		try {
			//appending data in csv file
			// Appending in the sender sent folder index
			BufferedWriter edit = new BufferedWriter(
					new FileWriter(this.getLoggedinUser().getFilePath() + sep + "sent" + sep + "index.csv", true));
			edit.append(content);
			edit.append("\n");
			edit.flush();
			edit.close();
			// Creating mail folder in the sent folder
			Folder dir = new Folder(this.getLoggedinUser().getFilePath() + sep + "sent" + sep);
			dir.addSubFolder(mail.getID()+"");
			Folder mailDir = new Folder(this.getLoggedinUser().getFilePath() + sep + "sent" + sep + mail.getID() + sep);
			mailDir.addSubFolder("attachment");
			File directory = new File(this.getLoggedinUser().getFilePath() + sep + "sent" + sep + mail.getID() + sep +  mail.getID() + ".txt");
			// Creating body file
			String mailBody = mail.getID() + "\n" + mail.getTitle() + "\n" + mail.getSenderAddress() + "\n" + mail.getSenderName() + "\n"
								+ date + "\n" + mail.getPriority().toString() + "\n";
			Iterator<Object> it = mail.getRecieverAddress().iterator();
			while (it.hasNext()) {
				mailBody += it.next().toString() + ",";
			}
			mailBody += "\n" + mail.getText() + "\n";
			FileWriter writer = new FileWriter(directory);
			directory.createNewFile();
			writer.write(mailBody);
			writer.close();
			// Upload attachments
			it = mail.getAttachments().iterator();
			while (it.hasNext()) {
				File file = (File)it.next();
				String dest = this.getLoggedinUser().getFilePath() + sep + "sent" + sep + mail.getID() + sep + "attachment" + sep + file.getName();
				if(!Folder.copyFiles(file, dest)) { return false;}
			}
			
			
			// Sending Mail
			QueueLinkedList queue = new QueueLinkedList();
			it = mail.getRecieverAddress().iterator();
			while (it.hasNext()) {
				queue.enqueue(it.next());
			}
			while (!queue.isEmpty()) {
				
				String reciever = queue.dequeue().toString();
				edit = new BufferedWriter(
						new FileWriter("system" + sep + "users" + sep + reciever + sep + "inbox" + sep + "index.csv", true));
				edit.append(content);
				edit.append("\n");
				edit.flush();
				edit.close();
				
				// Creating mail folder in the reciever's inbox folder
				dir = new Folder("system" + sep + "users"+ sep + reciever + sep + "inbox" + sep);
				dir.addSubFolder(mail.getID()+"");
				mailDir = new Folder("system" + sep + "users"+ sep + reciever + sep + "inbox" + sep + mail.getID() + sep);
				mailDir.addSubFolder("attachment");
				directory = new File("system"+ sep +"users"+ sep + reciever + sep + "inbox" + sep + mail.getID() + sep +  mail.getID() + ".txt");
				// Creating body file
				mailBody = mail.getID() + "\n" + mail.getTitle() + "\n" + mail.getSenderAddress() + "\n" + mail.getSenderName() + "\n"
									+ date + "\n" + mail.getPriority().toString() + "\n" + reciever;
				mailBody += "\n" + mail.getText() + "\n";
				writer = new FileWriter(directory);
				directory.createNewFile();
				writer.write(mailBody);
				writer.close();
				// Upload attachments
				it = mail.getAttachments().iterator();
				while (it.hasNext()) {
					File file = (File)it.next();
					String dest = "system" + sep + "users" + sep + reciever + sep + "inbox" + sep + mail.getID() + sep + "attachment" + sep + file.getName();
					if(!Folder.copyFiles(file, dest)) { return false;}
				}
			}
		} catch (Exception e) {
			System.out.println("not found");
			return false;
		}

		return true;
	}
	
	/**
	 * this function is supposed to create a draft
	 * @param email mail info
	 * @return true if draft is saved successfully 
	 */
	public boolean draft(IMail email) {
		Mail  mail = (Mail)email;
    	// Data to be entered in index file
		SimpleDateFormat formatter = new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a");
		String date = formatter.format(mail.getDate());
    	String content = mail.getID() + "," + mail.getTitle() + "," + mail.getSenderAddress() + "," + mail.getSenderName() + ","
				+ date + "," + (mail.getPriority().ordinal()+1);
		try {
			//appending data in csv file
			// Appending in the sender draft folder index
			BufferedWriter edit = new BufferedWriter(
					new FileWriter("system" + sep + "users" + sep + mail.getSenderAddress() + sep + "drafts" + sep + "index.csv", true));
			edit.append(content);
			edit.append("\n");
			edit.flush();
			edit.close();
			// Creating mail folder in the draft folder
			Folder dir = new Folder(this.getLoggedinUser().getFilePath() + sep + "drafts" + sep);
			dir.addSubFolder(mail.getID()+"");
			Folder mailDir = new Folder(this.getLoggedinUser().getFilePath() + sep + "drafts" + sep + mail.getID() + sep);
			mailDir.addSubFolder("attachment");
			File directory = new File(this.getLoggedinUser().getFilePath() + sep + "drafts" + sep + mail.getID() + sep +  mail.getID() + ".txt");
			// Creating body file
			String mailBody = mail.getID() + "\n" + mail.getTitle() + "\n" + mail.getSenderAddress() + "\n" + mail.getSenderName() + "\n"
								+ date + "\n" + mail.getPriority().toString() + "\n";
			Iterator<Object> it = mail.getRecieverAddress().iterator();
			while (it.hasNext()) {
				mailBody += it.next().toString() + ",";
			}
			mailBody += "\n" + mail.getText() + "\n";
			FileWriter writer = new FileWriter(directory);
			directory.createNewFile();
			writer.write(mailBody);
			writer.close();
			// Upload attachments
			it = mail.getAttachments().iterator();
			while (it.hasNext()) {
				File file = (File)it.next();
				String dest = this.getLoggedinUser().getFilePath() + sep + "drafts" + sep + mail.getID() + sep + "attachment" + sep + file.getName();
				if(!Folder.copyFiles(file, dest)) { return false;}
			}
		} catch (Exception e) {
			System.out.println("not found");
			return false;
		}
		return true;
	}
	
	/**
	 * this function is used for loading mails
	 */
	public void loadMails() {
		BufferedReader reader;
		mails.clear();
		try {
			reader = new BufferedReader(new FileReader(this.currentFolder.getIndexPath()));
			String row;
			while ((row = reader.readLine()) != null) {
			    String[] data = row.split(",");
			    /*
			     * At data
			     * Index 0 for ID
			     * Index 1 for Title
			     * Index 2 for SenderID
			     * Index 3 for SenderName
			     * Index 4 for Date
			     * Index 5 for Priority
			     * */
			    mails.add(new Mail(Integer.parseInt(data[0]), data[1], data[2], data[3],
			    		new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a").parse(data[4]), 
			    		Priority.values()[Integer.parseInt(data[5])-1]));
			    
			}
			reader.close();
			workingList = mails.copyView();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * this function check mails in trash if time of an email exceed 30 days this mail will be deleted
	 */
	public void updateTrash() {
		File trashIndex = new File(loggedinUser.getFilePath()+ sep + "trash" + sep + "index.csv");
		DoublyLinkedList toBeDeletedList = new DoublyLinkedList();

		try (BufferedReader br = new BufferedReader(new FileReader(trashIndex))) {
			String line = "";
			String splitBy = ",";
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				Date d1 = new Date();
				Date d2 = new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a").parse(data[6]);
				int diff = Utils.getDaysDiff(d2, d1);
				if (diff > 30) {
					toBeDeletedList.add(new Mail(Integer.parseInt(data[0]), data[1], data[2], data[3],
							new SimpleDateFormat("EEEE - MMM dd - yyyy HH:mm:ss a").parse(data[4]),
							Priority.values()[Integer.parseInt(data[5]) - 1]));
				}
			}
			br.close();
			deleteIndex(toBeDeletedList, trashIndex.toString());
			Iterator<Object> it = toBeDeletedList.iterator(true);
			while (it.hasNext()) {
				Mail current = (Mail) it.next();
				Folder.deleteMailFolder(loggedinUser.getFilePath() +sep+"trash"+sep + current.getID());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			Utils.fileNotFound();
		}
	}


	/**
	 * this function delete certain record in a file
	 * @param list list of mails 
	 * @param path path of the file
	 */
	public void deleteIndex(DoublyLinkedList list, String path) {
		File index = new File(path);
		String line = "";
		String splitBy = ",";
		File tmp = new File(path + "copy.csv");
		try (BufferedReader br = new BufferedReader(new FileReader(index))) {
			tmp.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmp, true));
			while ((line = br.readLine()) != null) {
				String[] data = line.split(splitBy);
				if (findID(list,Integer.parseInt (data[0]))) {
					continue;
				} else {
					bw.append(line);
					bw.append("\n");
				}
			}
			bw.close();
			br.close();
			index.delete();
			tmp.renameTo(index);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}
	/**
	 * this function search for a certain ID in a list (check if exist)
	 * @param list list of mails
	 * @param ID ID i want to look for in list of mails
	 * @return true if located
	 */
	public boolean findID(DoublyLinkedList list, int ID) {
		Iterator<Object> it = list.iterator(true);
		while (it.hasNext()) {
			Mail currentNode = (Mail) it.next();
			if (currentNode.getID() == ID) {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * Gets the folder in which the user is browsing
	 * */
	public Folder getCurrentFolder() {
		return this.currentFolder;
	}
	
	/*
	 * Get the number of thwe available mails in the specified filter and folder
	 * */
	public int availableMailsCount() {
		return workingList.size();
	}
	/**
	 * this function sort mails by priority
	 */
	public void SortByPriority() {
		PriorityQueue PQueue = new PriorityQueue();
		Iterator<Object> it = workingList.iterator(true);
		while (it.hasNext()) {
			Mail tmpMail = (Mail)it.next();
			PQueue.insert(tmpMail, tmpMail.getPriority().ordinal()+1);
		}
		workingList.clear();
		while (!PQueue.isEmpty()) {
			workingList.add(PQueue.removeMin());
		}
	}
	/**
	 * get next mail unique ID after each time using compose function or draft function
	 * @return
	 */
	public int updateMailID() {
        int i = 0;
        try {
            String des = System.getProperty("user.dir") + sep + "system" + sep + "mailID.txt";
            Scanner sc = new Scanner(new File(des));
            i = sc.nextInt();
            sc.close();
            BufferedWriter f = new BufferedWriter(new FileWriter(des, false));
            i++;
            f.write(i + "");
            f.close();
        } catch (IOException e) {
            Utils.fileNotFound();
        }
        return i;
    }
	/**
	 * this function reverse sorting
	 * @param flag control type of sorting ascending or descending
	 */
	public void reverseSort(boolean flag) {
		this.reverseSorting = flag;
	}
	
}
