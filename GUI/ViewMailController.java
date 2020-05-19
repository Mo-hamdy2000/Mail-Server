package GUI;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ResourceBundle;

import Classes.App;
import Classes.Folder;
import Classes.Mail;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class ViewMailController implements Initializable {
	
	private App app;
	private Mail mail;
	
	@FXML
	private Button openBtn;
	@FXML
	private Label bodyLbl;
	@FXML
	private Label dateLbl;
	@FXML
	private Label priorityLbl;
	@FXML
	private Label senderAddressLbl;
	@FXML
	private Label subjectLbl;
	@FXML
	private ListView<String> attachList;
	@FXML
	private ListView<String> recieverList;
	
	public void setParameters(App app, Mail mail) {
		this.app = app;
		this.mail = mail;
		showMail();
	}
	
	private void showMail() {
		String s = System.getProperty("file.separator");
		// Get the message body
		Folder folder = app.getCurrentFolder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(folder.getPath() 
					+ s + mail.getID() + s + mail.getID() + ".txt"));
			// First Line the mail ID
			String row = reader.readLine();
			// Second Line the mail Subject
			row = reader.readLine();
			subjectLbl.setText(row);
			// Third Line the mail sender's address
			row = reader.readLine();
			String sender = row;
			// Fourth Line the mail sender's name
			row = reader.readLine();
			sender += "\n"+row;
			senderAddressLbl.setText(sender);
			// Fifth Line the mail date
			row = reader.readLine();
			dateLbl.setText(row);
			// Sixth Line the mail priority
			row = reader.readLine();
			priorityLbl.setText(row);
			// Seventh Line the mail recievers addresses
			row = reader.readLine();
			String[] recievers = row.split(",");
			row = "";
			for (String reciever : recievers) {
				recieverList.getItems().add(reciever);
			}
			System.out.println(row);
			// Reading the mail message text
			String message = "";
			while ((row = reader.readLine()) != null) {
			    message += row + "\n";
			}
			bodyLbl.setText(message);
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		File file = new File(folder.getPath() 
				+ s + mail.getID() + s + "attachment");
		if (file.exists()) {
			String[] files = file.list();
			for (String pathname : files) {
				attachList.getItems().add(pathname);
	        }
		}
	}
	
	public void openFile() {
		if(attachList.getSelectionModel().getSelectedIndex() < 0) { return;}
		String s = System.getProperty("file.separator");
    	String fileName = attachList.getItems().get(attachList.getSelectionModel().getSelectedIndex());
    	Folder folder = app.getCurrentFolder();
		try  
		{  
			 File file = new File(folder.getPath() 
						+ s + mail.getID() + s + "attachment" + s + fileName);
			if(!Desktop.isDesktopSupported())  
			{  
				System.out.println("not supported");  
				return;  
			}  
			Desktop desktop = Desktop.getDesktop();  
			if(file.exists()) {
				desktop.open(file);
			}
		}  
		catch(Exception e)  
		{  
			e.printStackTrace();  
		}  
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
}
