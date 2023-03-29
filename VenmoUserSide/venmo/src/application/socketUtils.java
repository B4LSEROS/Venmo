package application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

public class socketUtils 
{
    Socket 	clientSocket=null;
    DataOutputStream outToServer=null;
    BufferedReader inFromServer=null;

	public boolean socketConnect()
	{
		String ipAddress, portString;
		int portNumber;
		boolean rc=false;
		
		try 
		{
	    	File file = new File("config.txt");
	        if (file.exists())
	        {
	           BufferedReader br = new BufferedReader(new FileReader("config.txt"));
	           
             
               ipAddress  = br.readLine();
               portString = br.readLine();
               portNumber = Integer.parseInt(portString);
               br.close();
	        }
	        else
	        {        
	           ipAddress  = "localhost"; //127.0.0.1 and "localhost" aren't working
	           portNumber = 3333;
	        }
	        
	        
  
           clientSocket  = new Socket(ipAddress, portNumber);
           
           outToServer   = new DataOutputStream(clientSocket.getOutputStream());
           inFromServer  = new BufferedReader(
   	                       new InputStreamReader(clientSocket.getInputStream()));
           
           rc= true;
		}
		catch (ConnectException ex)
		{
			ex.printStackTrace();
		}					
		catch (UnknownHostException ex)
	    {
			ex.printStackTrace();
	    }
		catch (IOException ex) 
	    {
			ex.printStackTrace();
	    }
		
		return rc;
	}
	
	public boolean sendMessage(String msg)
	{
		boolean rc=false;
		
		try 
		{
			outToServer.writeBytes(msg + "\r\n");
			rc = true;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rc;
	}
	
	public String recvMessage()
	{
		String msg=null;
		
		try
		{
			msg = inFromServer.readLine();
		} 
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return msg;
	}
	
	public boolean closeSocket()
	{
		boolean rc=false;
		
		try
		{
			clientSocket.close();
                        rc=true;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rc;
	}
}
