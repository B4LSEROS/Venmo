package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Font;

public class serverController 
{	
	@FXML
	private TextArea clockTextArea;
	@FXML
	private Button helpButton;
	@FXML
	private Button exitButton;
	@FXML
	private TextArea transactionLog;
	@FXML
	private TextArea connections;
	//@FXML
	//private TextArea text3;
	@FXML
	private TextField recipentField;
	@FXML
	private TextField commentField;
	@FXML 
	private TextField transactionMoneyField;
	@FXML
	private TextField identification;
	
    public void initialize() {
    	class VenmoServer implements Runnable 
        { 
    		   Socket csocket;
    		   String ipString;
    		   char threadType;

    		   Vector<String> vec = new Vector<String>(5);
    		   
    		   public Hashtable<String, Transaction> clients = 
    				     new Hashtable<String, Transaction>();
    		   
    		   static final String newline = "\n";
    		   int first_time = 1;
    		   
    		   int port_num = 3333;
    		   
    		   int numOfConnections = 0;
    		   int numOfMessages = 0;
    		   int max_connections = 5;
    		   int numOfTransactions = 0; 

    		   VenmoServer(Socket csocket, String ip)
    		   {
    		      this.csocket  = csocket;
    		      this.ipString = ip;
    		   }
    		   
    		   public void runSockServer()   // throws Exception
    		   {
    		     boolean sessionDone = false;
    		  
    		     ServerSocket ssock = null;
    		   
    		     try
    		     {
    			   ssock = new ServerSocket(port_num);
    		     }
    		     catch (BindException e)
    		     {
    			    e.printStackTrace();
    		     }
    		     catch (IOException e)
    		     {
    			    e.printStackTrace();
    		     }
    		 
    		     // update the status text area to show progress of program
    		     try 
    		     {
    			     InetAddress ipAddress = InetAddress.getLocalHost();
    			     transactionLog.appendText("IP Address : " + ipAddress.getHostAddress() + newline);
    			     connections.appendText("Number of transactions: " + newline);
    			     connections.appendText("Total money paid: " + newline);
    		     }
    		     catch (UnknownHostException e1)
    		     {
    			    // TODO Auto-generated catch block
    			    e1.printStackTrace();
    		     }
    		 
    		       transactionLog.appendText("Listening on port " + port_num + newline);
    		   
    		   try
    	         {
    	     	      File f = new File("hashTableData.txt");
    	     	      if (f.exists())
    	     	      {
    	                 FileReader reader = new FileReader("hashTableData.txt");
    	                 BufferedReader br = new BufferedReader(reader);
    	               
    	                 String line = br.readLine();
    	                 while (line != null)
    	                 {
    	                	 String args[]   = line.split("\\,");
    	                 	
    	                 	String key     = args[0];
    	                 	int transCount = Integer.parseInt(args[1]);
    	                 	String comment    = args[2];
    	                 	String amount = args[3];
    	                 	
    	  					clients.put(key, new Transaction(key,
    	  					    	            transCount,
    	  						                   comment,
    	  				                           amount));
    	  					line = br.readLine();
    	                 }
    	                 
    	                 br.close();
    	                
    	                 
    	     	     int    transCount=0;
    	     	    List<String> v = new ArrayList<String>(clients.keySet());
            	     for (String key : v)
           	     {
           	        transCount  = transCount  + clients.get(key).getTransactions();
           	     }
           	     int currentSize     = clients.size();
         	    // text3.setText("Total# : " + currentSize + newline);
         	    
         	     clients.put("totalKiosk", new Transaction("totalTransaction",
		    	               transCount,
			                   "",
	                           ""));
     	      }
     	      else
     	      {

     		     clients.put("Transaction#001", new Transaction("Transaction#001", 0, "", ""));
     		     clients.put("Transaction#002", new Transaction("Transaction#002", 0, "", ""));
     		     clients.put("Transaction#003", new Transaction("Transaction#003", 0, "", ""));
     		     
     		     
     		     //
                 // add homemade key, "totalKiosk", to HASHTABLE data structure
                 //
                 int    transCount=0, ticketCount=0;
                 double dollarCount=0.0;
                 
                 List<String> v = new ArrayList<String>(clients.keySet());
         	     for (String key : v)
        	     {
        	        transCount  = transCount  + clients.get(key).getTransactions();
        	     }        
         	     
         	     
         	     int currentSize     = clients.size();
        	    // text3.setText("Total# : " + currentSize + newline);
        	     
         	     clients.put("totalTransactions", new Transaction("totalTransaction",
		    	               transCount,
			                   "",
	                           ""));   
     	      }
     	 }
         catch(Exception e2)
         {   
     	    e2.printStackTrace(); 
         }		
	     
	     sessionDone = false;
	     while (sessionDone == false)
	     {
	        Socket sock = null;
		    try
		    {
		       //	
  	     	   // blocking system call
		       //	
			   sock = ssock.accept();
		    }
		    catch (IOException e)
		    {
			   e.printStackTrace();
		    }
		 
		    // update the status text area to show progress of program
	        transactionLog.appendText("Client Connected : " + sock.getInetAddress() + newline);
	        
	        //
	        // start a NEW THREAD process
	        //
	        new Thread(new VenmoServer(sock, sock.getInetAddress().toString())).start();
	     }
	 
	     try 
	     {
		    ssock.close();
	     }
	     catch (IOException e) 
	     {
		    e.printStackTrace();
	     }
	}	  

	   
	synchronized void hashOperation(char type, String key, String ticks, String d)
	{
		switch (type)
		{
			case 'T':
				long start = System.nanoTime();
				
				if (clients.containsKey(key) == true)
		        {
					clients.get(key).incrementTrans();
					
					long finish = System.nanoTime();
					
					long timeElapsed = finish - start;
					//text3.appendText(" Time Nano-Seconds : " + timeElapsed + newline);
					//text3.appendText("Time Milli-Seconds : " + timeElapsed / 1000000 + newline);
					
					if (clients.containsKey("totalTransactions") == true)
					{
						clients.get("totalTransactions").incrementTrans();
					}
		        }	
			break;
		}
	}

	//
	// add a new KIOSK entry and number to the hash table
	//
	public void createNewTransaction()
	{
		int nextTransactionNumber, currentSize = 0;
		String transactionString;
		
		currentSize     = clients.size();
		nextTransactionNumber = currentSize + 1;
		
		nextTransactionNumber = nextTransactionNumber - 1;
		transactionString     = "kiosk#" + String.format("%03d", nextTransactionNumber);
				
		clients.put(transactionString, new Transaction(transactionString, 0, "", ""));
	}

	//
	// method to write out hash table data
	//
	public void writeHashTableData()
	{
		FileWriter fwg = null;
        try 
        {
        	// open the file in append write mode
        	fwg = new FileWriter("hashTableData.txt");
        }
        catch (IOException e)
        {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
   	    
        List<String> v = new ArrayList<String>(clients.keySet());
	    Collections.sort(v);
	    
        BufferedWriter bwg = new BufferedWriter(fwg);
        PrintWriter outg   = new PrintWriter(bwg);
		
	    for (String key : v)
	    {
	    	if (key.equals("totalTransactions") == true)
	    		continue;
	    	
	        outg.println(clients.get(key).forFileOutput());
	    }
	        
        outg.close();
	}
	
	//
	// get all transaction data from the hash table keys
	//
	public String getAllTransactions()
	{
		String rs="";
		
		List<String> v = new ArrayList<String>(clients.keySet());
	    Collections.sort(v);
		
	    for (String str : v) 
	        rs = rs + clients.get(str.toString()) + "\r\n";
				
		return rs;
	}
	//
	// CLIENT THREAD CODE - This is the thread code that ALL clients will run()
	//
	public void run()
	{
	   try
	   {
		  boolean session_done = false; 
	      long threadId;
	      String clientString;
	      String keyString = "";
	      
	      Hashtable<String, Transaction> clients = 
				     new Hashtable<String, Transaction>();
	    
	      threadId = Thread.currentThread().getId();
	      
	      numOfConnections++;
	      
	      transactionLog.appendText("Num of Connections = " + numOfConnections + newline);
	      
	      keyString = ipString + ":" + threadId;
	      
	      if (vec.contains(keyString) == false)
	      {
	    	    int counter = 0;
	        	vec.addElement(keyString);
	        	
	        	connections.setText("");
	        	Enumeration<String> en = vec.elements();
	        	while (en.hasMoreElements())
	        	{
	        		connections.appendText(en.nextElement() + " || ");
	        		
	        		if (++counter >= 6)
	        		{
	        			connections.appendText("\r\n");
	        			counter = 0;
	        		}
	        	}
	      }
	       
	      PrintStream pstream = new PrintStream (csocket.getOutputStream());
	      BufferedReader rstream = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
	       
	      while (session_done == false)
	      {
	    	  
	       	if (rstream.ready())   // check for any data messages
	       	{
	              clientString = rstream.readLine();

	              
	              //
	              // write to transaction log
	              //
	              fileIO transLog = new fileIO();
	              transLog.wrTransactionData("SERVER : " + clientString);
	              
	              	              
	              // update the status text area to show progress of program
	              transactionLog.appendText("RECV : " + clientString + newline);
	     	       
	     	       // update the status text area to show progress of program
	              transactionLog.appendText("RLEN : " + clientString.length() + newline);
	              
	              if (clientString.length() > 128)
	              {
	           	   session_done = true;
	           	   continue;
	              }

	              if (clientString.contains("quit"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("QUIT"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("Quit"))
	              {
	                 session_done = true;
	              }
	              else if (clientString.contains("Query>"))
	              {
	            	  String tokens[] = clientString.split("\\>");
	            	  
	            	  if (clients.containsKey(tokens[1]) == true)
	            	  {
	            		  pstream.println(clients.get(tokens[1]).toString());  
	            	  }
	            	  else
	            	  {
	            		  pstream.println("NACK : ERROR : No such transaction number!");
	            	  }
	              }
	              else if (clientString.contains("Transaction>"))
	              {
	            	  String tokens[] = clientString.split("\\>");
	            	  String args[]   = tokens[1].split("\\,");
	            	  
	            	  if (clients.containsKey(args[0]) == true)
	            	  {
	            		  hashOperation('T', args[0], args[1], args[2]);
	            		  
	            		  pstream.println("ACK");
	            	  }
	            	  else
	            	  {
	            		  pstream.println("NACK : ERROR : No such transaction number!");
	            	  }
	              }
	              else if (clientString.contains("Configure>"))
	              {
	            	  String tokens[] = clientString.split("\\>");
	            	  
	            	  if (tokens.length == 2)
	            	  {
	            	     clients.put(tokens[1], new Transaction(tokens[1], 0, "", ""));
	            	     
	            	     pstream.println("ACK");
	            	  }
	            	  else
	            	  {
	            		  pstream.println("NACK : ERROR : Invalid number of parameters!");
	            	  }
	              }
	              else if (clientString.contains("Date>"))
	              {
	            	numOfMessages++;
	            	  
	            	// Create an instance of SimpleDateFormat used for formatting 
	            	// the string representation of date (month/day/year)
	            	DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	            	// Get the date today using Calendar object.
	            	Date today = Calendar.getInstance().getTime();
	            	
	            	// Using DateFormat format method we can create a string 
	            	// representation of a date with the defined format.
	            	String reportDate = df.format(today);

	            	//
	            	// Print what date is today! Send to the individual THREAD
	            	//
	            	pstream.println("Num Of Messages : " + numOfMessages + "   Simple Date: " + reportDate);
	              }
	              else
	              {
	            	  pstream.println("NACK : ERROR : No such command!");
	              }
	       	   }
	         			    		        	
	           Thread.sleep(500);
	           
	        }    // end while loop
	
            keyString = ipString + ":" + threadId;
	      
	        if (vec.contains(keyString) == true)
	        {
	        	int counter = 0;
	        	vec.removeElement(keyString);
	        	
	        	connections.setText("");
	        	Enumeration<String> en = vec.elements();
	        	while (en.hasMoreElements())
	        	{        		     		
	        		connections.appendText(en.nextElement() + " || ");
	        		
	        		if (++counter >= 6)
	        		{
	        			connections.appendText("\r\n");
	        			counter = 0;
	        		}
	        	}

  	            //sss5.textArea_2.repaint();
	        }
	      
	        numOfConnections--;

	        // close client socket
	        csocket.close();
	       
	        // update the status text area to show progress of program
		     transactionLog.appendText("Child Thread : " + threadId + " : is Exiting!!!" + newline);
		     transactionLog.appendText("Num of Connections = " + numOfConnections);
		     
	     } // end try  
	 
	     catch (SocketException e)
	     {
		  // update the status text area to show progress of program
	    	 transactionLog.appendText("ERROR : Socket Exception!" + newline);
	     }
	     catch (InterruptedException e)
	     {
		  // update the status text area to show progress of program
	    	 transactionLog.appendText("ERROR : Interrupted Exception!" + newline);
	     }
	     catch (UnknownHostException e)
	     {
		  // update the status text area to show progress of program
	    	 transactionLog.appendText("ERROR : Unkonw Host Exception" + newline);
	     }
	     catch (IOException e) 
	     {
	     // update the status text area to show progress of program
	    	 transactionLog.appendText("ERROR : IO Exception!" + newline);
	     }     
	     catch (Exception e)
	     { 
		  numOfConnections--;
		  
		  // update the status text area to show progress of program
		  transactionLog.appendText("ERROR : Generic Exception!" + newline);
	     }
	   
	  }  // end run() thread method
}		
		Platform.runLater(new Runnable() {
		     @Override
		     public void run() {
		         //Update the control code of JavaFX and put it here
	 
		    	 // Real Time Clock Code
		    	 Thread refreshClock = new Thread()
				   {  
					  public void run()
					  {	 
						while (true)
						{
							Date dte = new Date();
				
							String topMenuStr = "       " + dte.toString();					      
						    clockTextArea.setText(topMenuStr); 
						   
					               
						    try
						    {
							   sleep(300L);
						    }
						    catch (InterruptedException e) 
						    {
							   // TODO Auto-generated catch block
							   e.printStackTrace();
						    }
						  
			            }  // end while ( true )
						 
				    } // end run thread
					  
					  
				 };

			     refreshClock.start();
		     }
		});
		     Platform.runLater(new Runnable() {
		     //@Override
		     public void run() {
		    	 Socket csocket = null;
	    		   String ipString = null;
		     VenmoServer server = new VenmoServer(csocket, ipString);
		     
		     Thread refreshServerThread = new Thread()
		  	 {
		  	    public void run()
		  		  { 	
		  	    	server.runSockServer();
		  	      }    
		  	 };
		  	refreshServerThread.start();
		     }
		});	
	}

	public class Transaction {
			String comment;
			String amount;
			String name;
			int transactionCount;
			
			public Transaction (String n, int transCount, String c, String a)
			{
				name    = n;
				transactionCount  = transCount;
				comment = c;
				amount = a;
			}
			
			public String toString()
			{
				return name + " : " + "  #Trans   = " + String.format("%-4d", transactionCount) 
				                    + "  Amount  = " + amount
					                + "  Comment   = " + comment;
			}
			
			public String forFileOutput()
			{
				return name + "," + transactionCount + ", " + amount + ", " + comment;
			}
			
			public void incrementTrans()
			{
				transactionCount++;
			}
			
			public int getTransactions()
			{
				return transactionCount;
			}
		}
	  
	  public class fileIO 
	  {
	  		public void wrTransactionData(String dataStr)
	  		{
	  	        FileWriter fwg = null;
	  	        try 
	  	        {
	  	        	// open the file in append write mode
	  	        	fwg = new FileWriter("LogServer.txt", true);
	  	        }
	  	        catch (IOException e)
	  	        {
	  	        	// TODO Auto-generated catch block
	  	        	e.printStackTrace();
	  	        }
	  	   	    
	  	        BufferedWriter bwg = new BufferedWriter(fwg);
	  	        PrintWriter outg   = new PrintWriter(bwg);
	  			
	  	        String timeStamp = new SimpleDateFormat("MM-dd-yyyy HH.mm.ss").format(new Date());
	  	        
	  	        outg.println(timeStamp + " : " + dataStr);
	  	        
	  	        outg.close();
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
			          
			          String hStr="- Click on   EXIT   button to quit the socket server.\r\n" + 
			        		      "- Click on   Show Log   to display current transaction log file.\r\n";
			          
			          alert.setContentText(hStr);
			          alert.showAndWait();
			        }
			    });
		}

	  public void handleLog()
	  {
				Platform.runLater(new Runnable() 
				 {
					    String logString = "";
					    
				        public void run() 
				        {
				        	try
				            {
				        	      File f = new File("LogServer.txt");
				        	      if (f.exists())
				        	      {
				                    FileReader reader = new FileReader("LogServer.txt");
				                    BufferedReader br = new BufferedReader(reader);
				                  
				                    String line = br.readLine();
				                    while (line != null)
				                    {
				                    	logString = logString + line + "\r\n";
				                    	line = br.readLine();
				                    }
				                    
				                    br.close();
				        	      }
				        	      else
				        	      {
				        	    	  logString = "No log File Found!";
				        	      }
				        	 }
				             catch(Exception e2)
				             {   
				        	    e2.printStackTrace(); 
				             }		
				        	
				             Alert alert = new Alert(Alert.AlertType.INFORMATION);
				             alert.setTitle("--- Ticket Kiosk ---");
				             alert.setHeaderText("Transaction Log File");
				          
				             alert.setContentText(logString);
				             alert.setWidth(300);
				             alert.setHeight(600);
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
			           alert.setContentText("Are you sure you want to exit this Socket Server Program?");

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
