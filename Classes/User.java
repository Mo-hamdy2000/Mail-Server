package Classes;

import Misc.Birthday;
import Misc.Utils;
import Interfaces.IContact;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;

public class User implements IContact {


    private final String address;
    private final DoublyLinkedList contacts = new DoublyLinkedList();
    private String encryptedPassword;
    private String name, filePath;
    private String gender;
    private Birthday birthday;
    //path separator according to the OS
    private final String sep = System.getProperty("file.separator");

    /**
     * class constructor
     *
     * @param address           user's email address
     * @param encryptedpassword password after encryption (using Misc.AES)
     */
    public User(String address, String encryptedpassword) {
        this.address = address;
        this.encryptedPassword = encryptedpassword;
    }

    /**
     * class constructor
     *
     * @param address user's email address
     */
    public User(String address) {
        this.address = address;
    }

    /**
     * adds contact to list and to csv file
     *
     * @param contact to be added
     * @throws IOException file not found
     */
    public void addContact(Contact contact) {
        contact.setIndex(contacts.size());
        contact.setOwner(this);
        contacts.add(contact);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFilePath() + sep + "contacts.csv", true));
            writer.write(contact.getName() + "," + contact.getIndex() + "," + contact.getAddressesString());
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            Utils.fileNotFound();
        }
    }

    /**
     * used to sort list of contacts by date added
     *
     * @return doubly linked list containing the list of contacts sorted
     */
    public DoublyLinkedList sortContactsByIndex() {
        Sort sort = new Sort(SortAttribute.contactIndex);
        Comparator<Object> c = sort.sortAttribute();
        contacts.Qsort(c);
        return contacts;
    }

    /**
     * used to sort list of contacts by their name
     *
     * @return doubly linked list containing the list of contacts sorted
     */
    public DoublyLinkedList sortContactsByName() {
        Sort sort = new Sort(SortAttribute.contactName);
        Comparator<Object> c = sort.sortAttribute();
        contacts.Qsort(c);
        return contacts;
    }

    /**
     * used to search for contacts by their names
     * it iterates through contacts
     *
     * @param cName search key
     *              it doesn't have to be complete name as a match occurs if contact's name begins with the key
     *              in case the above criteria resulted in an empty list; we search again but this time a match occurs
     *              if the contact's name contains the key
     * @return doubly linked list of contacts having names as key
     */
    public DoublyLinkedList getContactByName(String cName) {
        cName = cName.toLowerCase();
        DoublyLinkedList res = new DoublyLinkedList();
        Iterator<Object> iter = contacts.iterator(true);
        while (iter.hasNext()) {
            Contact c = (Contact) iter.next();
            if (c.getName().toLowerCase().startsWith(cName)) res.add(c);
        }
        if (res.isEmpty()) {
            iter = contacts.iterator(true);
            while (iter.hasNext()) {
                Contact c = (Contact) iter.next();
                if (c.getName().toLowerCase().contains(cName)) res.add(c);
            }
        }
        return res;
    }

    /**
     * used to search for contacts by their address
     * it iterates through each contact's list of addresses
     *
     * @param address search key
     *                it doesn't have to be complete address as a match occurs if contact's address begins with the key
     *                in case the above criteria resulted in an empty list; we search again but this time a match occurs
     *                if the contact's address contains the key
     * @return doubly linked list of contacts having email address as key
     */
    public DoublyLinkedList getContactByAddress(String address) {
        address = address.toLowerCase();
        DoublyLinkedList res = new DoublyLinkedList();
        Iterator<Object> iter1 = contacts.iterator(true);
        while (iter1.hasNext()) {
            Contact c = (Contact) iter1.next();
            Iterator<Object> iter2 = c.getAddresses().iterator(true);
            while (iter2.hasNext()) {
                String s = (String) iter2.next();
                if (s.startsWith(address)) res.add(c);
            }
        }
        if (res.isEmpty()) {
            iter1 = contacts.iterator(true);
            while (iter1.hasNext()) {
                Contact c = (Contact) iter1.next();
                Iterator<Object> iter2 = c.getAddresses().iterator(true);
                while (iter2.hasNext()) {
                    String s = (String) iter2.next();
                    if (s.contains(address)) res.add(c);
                }
            }
        }
        return res;
    }

    /**
     * deletes the contact from this user's list of contacts
     * used by Contact.delete()
     *
     * @param contact to be deleted
     * @return true if contact deleted successfully, false otherwise
     */
    protected boolean delContact(Contact contact) {
        Iterator<Object> iter = contacts.iterator(true);
        int index;
        for (int i = 0; iter.hasNext(); i++) {
            Contact con = (Contact) iter.next();
            if (contact.equals(con)) {
                contacts.remove(i);
                index = con.getIndex();

                Iterator<Object> iter2 = contacts.iterator(true);
                //minus 1 from indexes
                while (iter2.hasNext()) {
                    Contact c = (Contact) iter2.next();
                    if (c.getIndex() > index) {
                        c.setIndex(c.getIndex() - 1);
                    }
                }
                try {
                    exportContacts();
                } catch (IOException e) {
                    Utils.fileNotFound();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * overwrites contacts.csv file in the user's directory with the list of contacts
     * used after any editing or deleting
     *
     * @throws IOException file not found
     */
    protected void exportContacts() throws IOException {
        if (contacts.isEmpty()) return;
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFilePath() + sep + "contacts.csv", false));
        Iterator<Object> iter = contacts.iterator(true);
        while (iter.hasNext()) {
            Contact contact = (Contact) iter.next();
            writer.write(contact.getName() + "," + contact.getIndex() + "," + contact.getAddressesString());
            writer.newLine();
        }
        writer.close();
    }

    /**
     * overwrites the folders.txt file in user/inbox with the new folders for the user
     * used after adding,deleting or renaming any of the user's folders
     */
    public void editFolders() {
        DoublyLinkedList dll = new DoublyLinkedList();
        try {
            Scanner sc = new Scanner(new File(this.getFilePath() + sep + "inbox" + sep + "folders.txt"));
            while (sc.hasNext()) {
                String folderName = sc.nextLine();
                File folder = new File(this.getFilePath() + sep + "inbox" + sep + folderName);
                if (folder.exists()) {
                    dll.add(folderName);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            Utils.fileNotFound();
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + sep + "inbox" + sep + "folders.txt", false));
            Iterator<Object> iter = dll.iterator(true);
            while (iter.hasNext()) {
                writer.write(iter.next().toString());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            Utils.fileNotFound();
        }

    }

    /**
     * adds new folder
     *
     * @param name new folder's name
     */
    public void addFolder(String name) {
        try {
            File index = new File(filePath + sep + "inbox" + sep + name + sep + "index.csv");
            index.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath + sep + "inbox" + sep
                    + "folders.txt", true));
            writer.write(name);
            writer.newLine();
            writer.close();
        } catch (IOException e) {
            Utils.fileNotFound();
        }

    }

    public String getFilePath() {
        return filePath;
    }

    public String getAddress() {
        return address;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public void setBirthday(Birthday bd) {
        this.birthday = bd;
    }

    public Birthday getBirthday() {
        return birthday;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public DoublyLinkedList getContacts() {
        return contacts;
    }
}
