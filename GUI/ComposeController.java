package GUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;

import Classes.App;
import Classes.DoublyLinkedList;
import Classes.Folder;
import Classes.Mail;
import Classes.Priority;
import Classes.SinglyLinkedList;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ComposeController implements Initializable {

	private App app;
	private Stage currentStage;
	private Mail loadedDraft = null;
	private boolean isSent = false;
	
	
	@FXML
	private Button addRecieverBtn;
	@FXML
	private Button removeRecieverBtn;
	@FXML
	private Button composeBtn;
	@FXML
	private Button closeBtn;
	@FXML
	private Button removeAttachmentBtn;
	@FXML
	private Button attachBtn;
	@FXML
	private ChoiceBox<String> priorityChoiceBox;
	@FXML
	private Label recieverErrorLbl;
	@FXML
	private ListView<String> recieversList;
	@FXML
	private ListView<String> attachList;
	@FXML
	private TextArea bodyTxtArea;
	@FXML
	private TextField recieverTxt;
	@FXML
	private TextField subjectTxt;
	
	FileChooser fil_chooser = new FileChooser(); 
	private final String sep = System.getProperty("file.separator");
	private HomeController homeController; 
	
	public void setParameters(App app, Stage stage, HomeController homeController) {
		this.app = app;
		this.currentStage = stage;
		this.homeController = homeController;
	}
	
	public void addRecieverBtnClicked() {
		String address = recieverTxt.getText();
		if (app.addressExists(address.toLowerCase())) {
			recieverTxt.setText("");
			recieversList.getItems().add(address);
			recieverErrorLbl.setText("");
		}
		else {
			recieverErrorLbl.setText("Invalid address!!");
		}
		
	}
    public void attachBtnClicked() {
    	Stage stageFileChooser = new Stage();
        // get the file selected 
        File file = fil_chooser.showSaveDialog(stageFileChooser); 

        if (file != null) { 
        	attachList.getItems().add(file.getAbsolutePath()); 
        }
    }
    
    private Mail getMail() {
    	
    	String title = subjectTxt.getText();
    	Date date = new Date();
    	Priority priority = Priority.valueOf(priorityChoiceBox.getSelectionModel().getSelectedItem()); 
    	ObservableList<String> list = recieversList.getItems();
    	SinglyLinkedList recievers = new SinglyLinkedList();
    	Iterator<String> it = list.iterator();
    	while (it.hasNext()) {
    		recievers.add(it.next());
    	}
    	list = attachList.getItems();
    	SinglyLinkedList attachments = new SinglyLinkedList();
    	it = list.iterator();
    	while (it.hasNext()) {
    		attachments.add(new File(it.next()));
    	}
    	String message = bodyTxtArea.getText();
    	Mail mail = new Mail(app.updateMailID(), title, app.getLoggedinUser().getAddress(), app.getLoggedinUser().getName(), date, priority);
    	mail.setText(message);
    	mail.setRecieverAddress(recievers);
    	mail.setAttachments(attachments);
    	
    	return mail;
    }
    
    @FXML
    public void composeBtnClicked() {
    	Mail mail = getMail();
    	if (mail.getRecieverAddress().size() == 0) {
    		recieverErrorLbl.setText("No Addresses specified !!");
    		return;
    	}
    	app.compose(mail);
    	isSent = true;
    	if (loadedDraft != null) {
    		DoublyLinkedList mailList = new DoublyLinkedList(); 
        	mailList.add(loadedDraft);
        	app.deleteIndex(mailList, this.app.getLoggedinUser().getFilePath() + sep + "drafts" + sep + "index.csv");
        	Folder.deleteMailFolder(this.app.getLoggedinUser().getFilePath() + sep + "drafts" + sep + loadedDraft.getID());
    	}
    	currentStage.close();
    }
    
    public void removeAttachmentBtnClicked() {
    	if(attachList.getSelectionModel().getSelectedIndex() < 0) { return;}
    	attachList.getItems().remove(attachList.getSelectionModel().getSelectedIndex());
    }

    public void removeRecieverBtnClicked() {
    	if(recieversList.getSelectionModel().getSelectedIndex() < 0) { return;}
    	recieversList.getItems().remove(recieversList.getSelectionModel().getSelectedIndex());
    }
    
    @FXML
    public void draft() {
    	this.homeController.refresh();
    	if (!isSent && loadedDraft == null) {
    		app.draft(getMail());
    	}
    	currentStage.close();
    }
    
    public void loadDraft(Mail mail) {
    	this.loadedDraft = mail;
    	BufferedReader reader;
    	try {
			reader = new BufferedReader(new FileReader(app.getLoggedinUser().getFilePath() 
					+ sep + "drafts" + sep + mail.getID() + sep + mail.getID() + ".txt"));
			// First Line the mail ID
			String row = reader.readLine();
			// Second Line the mail Subject
			row = reader.readLine();
			subjectTxt.setText(row);
			// Third Line the mail sender's address
			row = reader.readLine();
			// Fourth Line the mail sender's name
			row = reader.readLine();
			// Fifth Line the mail date
			row = reader.readLine();
			// Sixth Line the mail priority
			row = reader.readLine();
			// Seventh Line the mail recievers addresses
			row = reader.readLine();
			String[] recievers = row.split(",");
			row = "";
			for (String reciever : recievers) {
				recieversList.getItems().add(reciever);
			}
			System.out.println(row);
			// Reading the mail message text
			String message = "";
			while ((row = reader.readLine()) != null) {
			    message += row + "\n";
			}
			bodyTxtArea.setText(message);
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File file = new File(app.getLoggedinUser().getFilePath() + sep 
				+ "drafts" + sep + mail.getID() + sep + "attachment");
		if (file.exists()) {
			String[] files = file.list();
			for (String pathname : files) {
				File attachmentFile = new File(app.getLoggedinUser().getFilePath() + sep 
						+ "drafts" + sep + mail.getID() + sep + "attachment" + sep + pathname);
				attachList.getItems().add(attachmentFile.getAbsolutePath());
	        }
		}
    	priorityChoiceBox.getSelectionModel().select(mail.getPriority().toString());
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		priorityChoiceBox.getItems().addAll(Priority.Serious.toString(), Priority.Essential.toString(), 
				Priority.Ordinary.toString(), Priority.Secondary.toString());
		priorityChoiceBox.getSelectionModel().select(Priority.Ordinary.toString());
	}
}
