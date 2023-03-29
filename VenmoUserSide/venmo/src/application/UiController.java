package application;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.StageStyle;

public class UiController extends Main{
	@FXML
	private TextArea clockTextArea;
	@FXML
	private Button helpButton;
	@FXML
	private Button exitButton;
	@FXML
	private TextArea serverClockTextArea;
	@FXML
	private TextArea transactionLog;
	@FXML
	private TextField recipentField;
	@FXML
	private Button payButton;
	@FXML
	private Button requestButton;
	@FXML
	private TextField commentField;
	@FXML
	private TextField transactionMoneyField;
	@FXML
	private Pane socialFeed;
	@FXML 
	private ComboBox<String> salvationDonationBox;
	@FXML 
	private ComboBox<String> goodwillDonationBox;
	@FXML 
	private ComboBox<String> awfDonationBox;
	@FXML
	private Button donateSalvationButton;
	@FXML
	private Button donateGoodWillButton;
	@FXML
	private Button donateAWFButton;
	@FXML
	private HBox SocialFeedBox;
	
	public Queue<String> socialFeedList = new PriorityQueue();
	public Queue<HBox> boxList = new LinkedList<HBox>();
	public String[] fields     = new String[3];
	public int  counter        = 0; 

	public void initialize() {
		
		// Combobox list for Donations 
				ObservableList<String> donationAmountList = FXCollections.observableArrayList("$5", "$10", "$15", "$20");
				
				salvationDonationBox.setItems(donationAmountList);
				goodwillDonationBox.setItems(donationAmountList);
				awfDonationBox.setItems(donationAmountList);

		
	
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// Update the control code of JavaFX and put it here

				// Real Time Clock Code
				Thread refreshClock = new Thread() {
					public void run() {
						while (true) {
							Date dte = new Date();

							String topMenuStr = "       " + dte.toString();
							clockTextArea.setText(topMenuStr);

							try {
								sleep(300L);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						} // end while ( true )

					} // end run thread

				};

				refreshClock.start();
			}
		});
		
		

	}
	
	public void getString(String name, String amount, String comment)
	{
		fields[0] = name;
		fields[1] = amount;
		fields[2] = comment;
	}	
	
	public void addNode(String information, String comment) throws FileNotFoundException
	{
		HBox  socialFeedBox  = new HBox();
		Label amount         = new Label();
		Label comment1       = new Label();
		
		
		Image     icon      = new Image(new FileInputStream("@../../venmoprojectassests/profile.png"));
		ImageView imageView = new ImageView(icon);
		
		imageView.setFitHeight(80.0); 
	    imageView.setFitWidth(80.0); 
	    imageView.setTranslateX(20.0);
	    imageView.setTranslateY(10.0);
	    imageView.setPreserveRatio(true); 
	    imageView.setPickOnBounds(true);
	    
	    amount.setText(information);
	    amount.setPrefSize(200.0, 40.0);
	    amount.setTranslateX(50.0);
	    amount.setWrapText(true);
	    
	    comment1.setText("for " + comment);
	    comment1.setPrefSize(200.0, 60.0);
	    comment1.setStyle("-fx-font-size: 14; -fx-font-weight: bold;");
	    comment1.setWrapText(true);
	    comment1.setTranslateX(-70.0);
	    comment1.setTranslateY(41.0);
	    
		socialFeedBox.setPrefSize(315.0, 110.0);
		socialFeedBox.getChildren().addAll(imageView, amount, comment1);
		
		
		if (counter <= 4)
		{
			HBox temp = new HBox(socialFeedBox);
			
			if (counter == 0)
			{
				temp.setLayoutY(75);
			}
			else
			{
				temp.setLayoutY((counter) * 110 + 75);
			}
			
			if (counter % 2 != 0)
			{
				temp.setStyle("-fx-background-color: #dce0e6;");
			}
			else
			{
				temp.setStyle("-fx-background-color: #bcc2cc;");
			}
				
			socialFeed.getChildren().addAll(temp);
			boxList.add(temp);
				
			socialFeedList.add(information);
			System.out.println(boxList.size());
			counter++;
			
			if (boxList.size() == 5)
			{
				socialFeed.getChildren().remove(boxList.poll());	
			}
			if (counter == 4)
			{
				counter = 0;
			}
		}

		System.out.println("This counter" + counter);
		System.out.println(socialFeedList);	
	}

	public void handlePay() {

		// Initialize values
		String recipentString = null;
		String commentString = null;
		String transactionMoneyString = null;
		String addtoSocialFeed = null;


		fileIO trans1 = new fileIO();

		// Convert fields to strings
		recipentString = recipentField.getText().toString();
		commentString = commentField.getText().toString();
		transactionMoneyString = transactionMoneyField.getText().toString();


		if (recipentString.isEmpty() == true || commentString.isEmpty() == true || transactionMoneyString.isEmpty() == true) 
		{ // if all fields are not filled display prompt to complete them.
			transactionLog.setText("Please fill out all of the fields.");
		} 
		else 
		{	
			transactionLog.setText("You paid " + recipentString + " $" + transactionMoneyString);
			
			addtoSocialFeed = "You paid " + recipentString + " $" + transactionMoneyString;
			
			try {
				addNode(addtoSocialFeed, commentString);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			trans1.wrTransactionData(transactionLog.getText()); // write transaction log data to .txt file

			// Send information to server side via sockets

			Platform.runLater(new Runnable() {
				public void run() {
					String rs = null;
					socketUtils su = new socketUtils();

					String recipentString = recipentField.getText().toString();
					String commentString = commentField.getText().toString();
					String transactionMoneyString = transactionMoneyField.getText().toString();

					if (su.socketConnect() == true) // this always seems to be false for whatever reason
					{

						String msg = "Transaction>Transaction#0500000" + "," + "1" + "," + transactionLog.getText();
						su.sendMessage(msg);
						String ackOrNack = su.recvMessage();

						msg = "quit";
						su.sendMessage(msg);
						rs = su.recvMessage();

						su.closeSocket();

						msg = "CLIENT: Transaction>transaction#001" + "," + recipentString + "," + commentString + ","
								+ transactionMoneyString;
						fileIO trans = new fileIO();
						trans.wrTransactionData(msg);

						recipentField.setText("");
						commentField.setText("");
						transactionMoneyField.setText("");


					} else {
						String msg = "CLIENT NETWORK ERROR : Transaction>kiosk#001";

						fileIO trans = new fileIO();
						trans.wrTransactionData(msg);

						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("--- Network Communications Error ---");
						alert.setHeaderText("Unable to talk to Socket Server!");

						alert.showAndWait();
					}
				}
			});
		}
	}
	

	public void handleRequest() {
		// Initialize values
		String recipentString = null;
		String commentString = null;
		String transactionMoneyString = null;
		String addtoSocialFeed = null;

		fileIO trans1 = new fileIO();

		// Convert fields to strings
		recipentString = recipentField.getText().toString();
		commentString = commentField.getText().toString();
		transactionMoneyString = transactionMoneyField.getText().toString();

		if (recipentString.isEmpty() == true || commentString.isEmpty() == true
				|| transactionMoneyString.isEmpty() == true) { // if all fields are not filled display prompt to
																// complete them

			transactionLog.setText("Please fill out all of the fields.");

		} else {

			transactionLog.setText("You requested " + recipentString + " $" + transactionMoneyString);	
			addtoSocialFeed = "You requested " + recipentString + " $" + transactionMoneyString;
			try {
				addNode(addtoSocialFeed, commentString);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			trans1.wrTransactionData(transactionLog.getText()); // write transaction log data to .txt file

			// Send information to server side via sockets

			Platform.runLater(new Runnable() {
				public void run() {
					String rs = null;
					socketUtils su = new socketUtils();

					String recipentString = recipentField.getText().toString();
					String commentString = commentField.getText().toString();
					String transactionMoneyString = transactionMoneyField.getText().toString();

					if (su.socketConnect() == true) // this always seems to be false for whatever reason
					{

						String msg = "Transaction>Transaction#0500000" + "," + "1" + "," + transactionLog.getText();
						su.sendMessage(msg);
						String ackOrNack = su.recvMessage();

						msg = "quit";
						su.sendMessage(msg);
						rs = su.recvMessage();

						su.closeSocket();

						msg = "CLIENT: Transaction>transaction#001" + "," + recipentString + "," + commentString + ","
								+ transactionMoneyString;
						fileIO trans = new fileIO();
						trans.wrTransactionData(msg);

						recipentField.setText("");
						commentField.setText("");
						transactionMoneyField.setText("");

					} else {
						String msg = "CLIENT NETWORK ERROR : Transaction>kiosk#001";

						fileIO trans = new fileIO();
						trans.wrTransactionData(msg);

						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("--- Network Communications Error ---");
						alert.setHeaderText("Unable to talk to Socket Server!");

						alert.showAndWait();
					}
				}
			});
		}
	}
	public void handleSalvation() {
		
		String addtoSocialFeed = null;
		fileIO file = new fileIO();
		Vector<Vector<String>> dataAnalytics = new Vector<Vector<String>>();
			
		if (salvationDonationBox.getValue() == null) {
			 transactionLog.setText("Please select an amount to donate");
			
		} else {

			
		    transactionLog.setText("You donated " + salvationDonationBox.getValue() + " to the Salvation Army ");
		    file.wrTransactionData(transactionLog.getText());
		    
		    addtoSocialFeed = "You donated " + salvationDonationBox.getValue() + " to the Salvation Army";
		    try {
				addNode(addtoSocialFeed, "donation");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    
	    	//wrTransactionData(transactionLog.getText());                                                              // write transaction log data to .txt file
	    	Platform.runLater(new Runnable() {
			public void run() {
				String rs = null;
				socketUtils su = new socketUtils();

				if (su.socketConnect() == true) // this always seems to be false for whatever reason
				{

					int amountCounter = 1;
					String msg = "Transaction>Transaction#0500000" + "," + amountCounter + "," + transactionLog.getText();
					amountCounter++; // increase transaction count by 1 after each transaction 
					su.sendMessage(msg);
					String ackOrNack = su.recvMessage();

					msg = "quit";
					su.sendMessage(msg);
					rs = su.recvMessage();

					su.closeSocket();

					msg = "CLIENT: Transaction>transaction#001" + "," + "Salvation Army recieved" + salvationDonationBox.getValue();
;					fileIO trans = new fileIO();
					trans.wrTransactionData(msg);

				} else {
					
					String msg = "CLIENT NETWORK ERROR : Transaction>kiosk#001";

					fileIO trans = new fileIO();
					trans.wrTransactionData(msg);

					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("--- Network Communications Error ---");
					alert.setHeaderText("Unable to talk to Socket Server!");

					alert.showAndWait();
				}
			}
		});
	
		}
	
	
	}
	
public void handleGoodWill() {
	
		
		String addtoSocialFeed = null;
		fileIO file = new fileIO();
		
		if (goodwillDonationBox.getValue() == null) {
			
       transactionLog.setText("Please select an amount to donate");
			
		} else {

			
		    transactionLog.setText("You donated " + goodwillDonationBox.getValue() + " to Goodwill");
	    	file.wrTransactionData(transactionLog.getText());        // write transaction log data to .txt fil
		    addtoSocialFeed = "You donated " + goodwillDonationBox.getValue() + " to Goodwill.";
		    try {
				addNode(addtoSocialFeed, "donation");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    	Platform.runLater(new Runnable() {
			public void run() {
				String rs = null;
				socketUtils su = new socketUtils();

				if (su.socketConnect() == true) // this always seems to be false for whatever reason
				{

					int amountCounter = 1;
					String msg = "Transaction>Transaction#0500000" + "," + amountCounter + "," + transactionLog.getText();
					amountCounter++;                                              // increase transaction count by 1 after each transaction 
					su.sendMessage(msg);
					String ackOrNack = su.recvMessage();

					msg = "quit";
					su.sendMessage(msg);
					rs = su.recvMessage();

					su.closeSocket();

					msg = "CLIENT: Transaction>transaction#001" + "," + "Goodwill recieved" + goodwillDonationBox.getValue();
;					fileIO trans = new fileIO();
					trans.wrTransactionData(msg);

				} else {
					
					String msg = "CLIENT NETWORK ERROR : Transaction>kiosk#001";

					fileIO trans = new fileIO();
					trans.wrTransactionData(msg);

					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("--- Network Communications Error ---");
					alert.setHeaderText("Unable to talk to Socket Server!");

					alert.showAndWait();
				}
			}
		});
		
		
		}
			
	}

public void handleAWF() {
	
	String addtoSocialFeed = null;
	fileIO file = new fileIO();
	
	
	
	if (awfDonationBox.getValue() == null) {
		
		
		
		 transactionLog.setText("Please select an amount to donate");
		
	} else {

		
	    transactionLog.setText("You donated " + awfDonationBox.getValue() + " to the African Wildlife Foundation ");
    	file.wrTransactionData(transactionLog.getText());  
	    addtoSocialFeed = "You donated " + awfDonationBox.getValue() + " to the African Wildlife Foundation.";
	    try {
			addNode(addtoSocialFeed, "donation");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// write transaction log data to .txt file

    	
    	Platform.runLater(new Runnable() {
		public void run() {
			String rs = null;
			socketUtils su = new socketUtils();

			if (su.socketConnect() == true) // this always seems to be false for whatever reason
			{

				int amountCounter = 1;
				String msg = "Transaction>Transaction#0500000" + "," + amountCounter + "," + transactionLog.getText();
				amountCounter++;                                              // increase transaction count by 1 after each transaction 
				su.sendMessage(msg);
				String ackOrNack = su.recvMessage();

				msg = "quit";
				su.sendMessage(msg);
				rs = su.recvMessage();

				su.closeSocket();

				msg = "CLIENT: Transaction>transaction#001" + "," + "African Wildlife Foundation recieved" + awfDonationBox.getValue();
;					fileIO trans = new fileIO();
				trans.wrTransactionData(msg);

			} else {
				
				String msg = "CLIENT NETWORK ERROR : Transaction>kiosk#001";

				fileIO trans = new fileIO();
				trans.wrTransactionData(msg);

				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("--- Network Communications Error ---");
				alert.setHeaderText("Unable to talk to Socket Server!");

				alert.showAndWait();
			}
		}
	});

	}
}

public void handleHelp() {
	  
	Platform.runLater(new Runnable() 
	 {
		 public void run() 
	        {
	          Alert alert = new Alert(Alert.AlertType.INFORMATION);
	          alert.setTitle("--- Ticket Kiosk Help Window ---");
	          alert.setHeaderText("Help Screen");
	          
	          String hStr="- Click on   EXIT   button to quit Venmo .\r\n" + 
	        		      "- Click on   PAY   to complete transaction and send money to recipient.\r\n" +
	        		      "- Click on   REQUEST   button to complete transaction and request money to recipient .\r\n";
	          
	          alert.setContentText(hStr);
	          alert.showAndWait();
	        }
	    });
}

public void handleExit()
{
	  Platform.runLater(new Runnable() 
		 {
		        public void run() 
		        {
		           Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		           
		           alert.setTitle("Confirmation Dialog");
		           alert.setHeaderText("EXIT confirmation dialog");
		           alert.setContentText("Are you sure you want to exit Venmo?");

		           Optional<ButtonType> result = alert.showAndWait();
		           
		           if (result.get() == ButtonType.OK)
		           {
			          // writeHashTableData();
			           System.exit(0);
		           }
		           else 
		           {
		               // ... user chose CANCEL or closed the dialog
		           }
		        }
		    });	
	}
}


