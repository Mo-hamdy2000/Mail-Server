package Classes;

import Misc.Utils;
import Interfaces.IContact;

import java.io.IOException;
import java.util.Iterator;

public class Contact implements IContact {

    private String name;
    private final DoublyLinkedList addresses = new DoublyLinkedList();
    private User owner;
    private int index;
    private String mainAddress;

    /**
     * class constructor used in App class
     *
     * @param name  contact's name
     * @param owner the User who has this contact
     * @param index number of contact in user's contact list
     */
    public Contact(String name, User owner, int index) {
        this.name = name;
        this.owner = owner;
        this.index = index;
    }

    /**
     * class constructor used in GUI
     *
     * @param name contact's name
     */
    public Contact(String name) {
        this.name = name;
    }

    /**
     * the index given to each contact according to the date added
     *
     * @return contact's index in user's list of contacts
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param owner User who has this contact in their list
     */
    protected void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * the index given to each contact according to the date added
     *
     * @param index contact's index in user's list of contacts
     */
    protected void setIndex(int index) {
        this.index = index;
    }

    /**
     * deletes the contact from the user's list of contacts
     *
     * @return true if the contact was deleted successfully, false otherwise
     */
    public boolean delete() {
        return owner.delContact(this);
    }

    /**
     * @return contact's name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name contact's name
     */
    public void setName(String name) {
        this.name = name;
        try {
            owner.exportContacts();
        } catch (IOException e) {
            Utils.fileNotFound();
        }
    }

    /**
     * every contact in the user's list of contacts may have more than one email address
     *
     * @return a doubly linked list containing all the contact addresses
     */
    public DoublyLinkedList getAddresses() {
        return addresses;
    }

    /**
     * adds an array of addresses to the contact's list of addresses
     * it takes an array because String.split returns an array
     *
     * @param arr array of strings containing the addresses
     */
    public void addAddresses(String[] arr) {
        if (arr == null) return;
        for (String s : arr) {
            this.addresses.add(s);
        }
        mainAddress = (String) addresses.get(0);
    }

    /**
     * @return the addresses of the contact as a single string comma separated
     */
    public String getAddressesString() {
        StringBuilder sb = new StringBuilder();
        if (addresses.isEmpty()) return "";
        Iterator<Object> iter = addresses.iterator(true);
        sb.append(iter.next());
        while (iter.hasNext()) {
            sb.append(",").append(iter.next());
        }
        return sb.toString();
    }

    /**
     * @return the first address in the contact's list of addresses
     */
    public String getMainAddress() {
        return mainAddress;
    }
}