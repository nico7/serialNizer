package RXTXdemo;


import java.awt.Checkbox;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ESPUtil extends JFrame implements ActionListener{
	private static String finalMessage;
	
	private static JButton cipMux = new JButton("Set Mux to ");
	private static JButton checkIP = new JButton("Check IP");
	private static JButton startConnection = new JButton("Start Connection");
	private static JButton sendTCP = new JButton("Send Data");
	
	private static JButton setSTA = new JButton("Set STA");
	private static JButton setAP = new JButton("Set AP");
	private static JButton setBoth = new JButton("Set Both");
	
	private static JButton checkAP = new JButton("Check AP");
	private static JButton listAP = new JButton("List APs");
	private static JButton version = new JButton("Check Version");
	//private static JButton 
	
	private static Checkbox mux = new Checkbox("MUX");
	private static JTextField ip = new JTextField("IP Address");
	private static JTextArea message = new JTextArea("Enter Message here");
	
    JTextArea txArea;
    JTextArea mixArea;
	
	GridBagLayout layout = new GridBagLayout();
	GridBagConstraints c = new GridBagConstraints();
	
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	Writer myWriter;
	
	ESPUtil(JTextArea txArea, JTextArea mixArea)
	{
		this.txArea = txArea;
		this.mixArea = mixArea;
		myWriter = new Writer(txArea, mixArea);
		this.setLayout(layout);
		this.setSize((int) screenSize.getWidth()/4,(int) screenSize.getHeight()/4);
		this.setVisible(true);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		
		this.add(setSTA, c);
		
		c.gridx++;
		this.add(setAP, c);
		c.gridx++;
		this.add(setBoth, c);
		
		c.gridx = 0;
		c.gridy++;
		this.add(listAP, c);
		
		c.gridy++;
		this.add(checkAP,c);
		
		c.gridy++;
		this.add(version, c);
		
		c.gridy++;
		this.add(cipMux, c);
		
		c.gridx++;
		this.add(mux, c);
	
		c.gridx --;
		c.gridy ++;
		this.add(checkIP, c);
	
		c.gridy ++;
		this.add(startConnection, c);
	
		c.gridx++;
		this.add(ip, c);
		
		c.gridx--;
		c.gridy++;
		this.add(sendTCP, c);
	
		c.gridx++;
		this.add(message, c);
		
		c.gridx--;
		c.gridy++;
		
		setSTA.addActionListener(this);
		setAP.addActionListener(this);
		setBoth.addActionListener(this);
		cipMux.addActionListener(this);
		checkIP.addActionListener(this);
		startConnection.addActionListener(this);	
		sendTCP.addActionListener(this);
		checkAP.addActionListener(this);
		listAP.addActionListener(this);
		version.addActionListener(this);
		
		
	}
	


	
	public void actionPerformed(ActionEvent e) 
	{

		int extra = 0;
		int state = 0;
		if(e.getSource() == setSTA)
		{
			finalMessage = "AT+CWMODE=1\r\n";
		}
		if(e.getSource() == setAP)
		{
			finalMessage = "AT+CWMODE=2\r\n";
		}
		if(e.getSource() == setBoth)
		{
			finalMessage = "AT+CWMODE=3\r\n";
		}
		if(e.getSource() == listAP)
		{
			finalMessage = "AT+CWLAP\r\n";
		}
		if(e.getSource() == checkAP)
		{
			finalMessage = "AT+CWJAP?\r\n";
		}
		if(e.getSource()== version)
		{
			finalMessage = "AT+GMR\r\n";
		}
		if(e.getSource() == cipMux)
		{
			if(mux.getState())
			{
				finalMessage = "AT+CIPMUX=1\r\n";
			}else
			{
				finalMessage = "AT+CIPMUX=0\r\n";
			}
		}
		if(e.getSource() == checkIP)
		{
			finalMessage = "AT+CIFSR\r\n";
		}
		if(e.getSource() == startConnection)
		{
		   if(state == 0)
		   {
			   finalMessage = ip.getText();
			   finalMessage = "AT+CIPSTART=0,\"TCP\",\"" + finalMessage +"\",80\r\n";
			   state = 1;
		   }
		   else
		   {
			   state = 0;
			   System.out.println("State changed!");
		   }
		   
		}
		if(e.getSource() != sendTCP)
		{
			try
			{
				myWriter.send(FirstSerial.openz, FirstSerial.out, finalMessage, 0);
			} catch (IOException e1) 
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		if(e.getSource() == sendTCP)
		{
			extra = 0;
			finalMessage = message.getText();
			for(int i=0; i<finalMessage.length(); i++)
			{
				if(finalMessage.charAt(i)== '\n')
				{
					extra++;
				}
			}
			extra+=finalMessage.length();
			finalMessage = "AT+CIPSEND=0," + extra + "\r\n";
			try {
				myWriter.send(FirstSerial.openz, FirstSerial.out, finalMessage, 0);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
			    Thread.sleep(500);                 //1000 milliseconds is one second.
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			}
			finalMessage = message.getText();
			try {
				myWriter.send(FirstSerial.openz, FirstSerial.out, finalMessage, 1);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		
	}
		
}
