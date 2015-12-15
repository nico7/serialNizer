package RXTXdemo;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.*;

import gnu.io.*;


public class FirstSerial implements ActionListener{
	ImageIcon img = new ImageIcon("RXTXDemo/lib/img.png");
// All the containers (used mostly for layout purposes)
	public JPanel masterPane;
	public JPanel topPane;
	public static JPanel txtPane;

// Window that contains JPanels 
	public JFrame window; 

// -------------------------------------- Text Objects --------------------
	public static JTextArea txStack;
	public static JTextArea mixStack;
	public static JTextArea rxStack;
	public static JTextField baud;
	
// ----------------------------- Buttons ----------------------------------	
	public static JButton sendButton = new JButton("Send");
	public static JButton ok = new JButton("OK");
	public static JButton saveSend = new JButton("Save");
	public static JButton saveMix = new JButton("Save");
	public static JButton saveRx = new JButton("Save");
	public static JButton refresh = new JButton("Refresh");
	public static JButton clearTx = new JButton("Clear");
	public static JButton clearMix = new JButton("Clear");
	public static JButton clearRx = new JButton("Clear");
	public static JButton clearAll = new JButton("Clear ALL");

// --------------------------------- Scroll Panes -------------------------	
	public static JScrollPane scrollSend;
	public static JScrollPane scrollMix;
	public static JScrollPane scrollRx;
	
// ---------------------------------- Menu Bar and Items -----------------	
	public static JMenuBar menuBar;
	public static JMenu	menuFile;
	public static JMenu menuEdit;
	public static JMenu menuTools;
	public static JMenu menuHelp;
	
	public static JMenuItem menuLoad;
	public static JMenuItem menuSaveAll;
	public static JMenuItem menuClose;
	public static JMenuItem menuCut;
	public static JMenuItem menuCopy;
	public static JMenuItem menuPaste;
	public static JMenuItem menuESP;
	
	public static JMenuItem menuAbout;
	
	public static Checkbox r;
	public static Checkbox n;
	public static Checkbox allCaps;
 // -------------------------------- DropDown Menu ------------------------
	
	public static JComboBox<String> comPorts;
	
	public static CommPort openz;
	CommPortIdentifier myCOM = null;
// --------------------------------- MAIN ---------------------------------
	public static void main(String[] args) throws Exception {
		new FirstSerial();
	}
	
	ArrayList<String> portNames = new ArrayList<String>();
	ArrayList<CommPortIdentifier> portId = new ArrayList<CommPortIdentifier>();
	
	InputStream in;
	public static OutputStream out;
	SerialPort serialPort;
	
	public FirstSerial() throws PortInUseException, UnsupportedCommOperationException, IOException
	{

// --------------------- Set up Action Listeners ---------------------------------------------------------
		sendButton.addActionListener(this);
		ok.addActionListener(this);
		saveSend.addActionListener(this);
		saveMix.addActionListener(this);
		saveRx.addActionListener(this);
		refresh.addActionListener(this);
		clearTx.addActionListener(this);
		clearMix.addActionListener(this);
		clearRx.addActionListener(this);
		clearAll.addActionListener(this);

		
// --------------------- Layouts used ---------------------------------------------------------		
		BorderLayout masterLayout = new BorderLayout();
		GridBagLayout cenLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		GridLayout txtLayout = new GridLayout(0, 3, 50, 50);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

// ----------------------------------- Menu setups -----------------------------------------
		menuFile = new JMenu("File");
		menuEdit = new JMenu("Edit");
		menuTools = new JMenu("Tools");
		menuHelp = new JMenu("Help");
		
		menuSaveAll = new JMenuItem("Save All...");
		menuLoad = new JMenuItem("Load...");
		menuClose = new JMenuItem("Close");
		menuESP = new JMenuItem("ESP8266 Utility");
		menuESP.addActionListener(this);
		
		menuFile.add(menuSaveAll);
		menuFile.add(menuLoad);
		menuFile.add(menuClose);
		
		menuCut = new JMenuItem("Cut (Ctrl+X)");
		menuCopy = new JMenuItem("Copy (Ctrl+C)");
		menuPaste = new JMenuItem("Paste (Ctrl+V)");
		
		menuEdit.add(menuCut);
		menuEdit.add(menuCopy);
		menuEdit.add(menuPaste);
		menuTools.add(menuESP);
		menuESP.setEnabled(false);
		
		menuAbout = new JMenuItem("About...");
		menuHelp.add(menuAbout);
		
		menuBar = new JMenuBar();
		menuBar.add(menuFile);
		menuBar.add(menuEdit);
		menuBar.add(menuTools);
		menuBar.add(menuHelp);
	
		r = new Checkbox("Append \'\\r\' to \'\\n\'");
		n = new Checkbox("Append \'\\n\' to send");
		allCaps = new Checkbox("CAPS");
// --------------------------------- Panes and setups --------------------------		
		masterPane = new JPanel(masterLayout);
		//topPane = new JPanel(topLayout);
		topPane = new JPanel(cenLayout);
		topPane.setBackground(Color.lightGray);
		topPane.setVisible(true);
	
		txtPane = new JPanel(cenLayout);
		txtPane.setVisible(true);
		
		window = new JFrame("SerialNizer");
		window.setIconImage(img.getImage());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize((int)screenSize.getWidth()*2/3,  (int)screenSize.getHeight()*2/3);
		window.setVisible(true);
		
//--------------------------   rate setup ---------------------------------------		
		baud = new JTextField("9600");
// -------------------------------------- Text Area Setups -------------------------		
		txStack = new JTextArea();
		txStack.setLineWrap(true);
		
		mixStack = new JTextArea();
		mixStack.setEditable(false);
		mixStack.setLineWrap(true);
		
		rxStack = new JTextArea();
		rxStack.setEditable(false);
		rxStack.setLineWrap(true);
		
		scrollSend = new JScrollPane(txStack);	
		scrollSend.setVisible(true);
		scrollSend.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		scrollMix = new JScrollPane(mixStack);
		scrollMix.setVisible(true);
		scrollMix.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		scrollRx = new JScrollPane(rxStack);
		scrollRx.setVisible(true);
		scrollRx.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

// -------------------------------- COM PORT FINDING -----------------------------------------		
	
		Enumeration myPorts = CommPortIdentifier.getPortIdentifiers();
		
		while(myPorts.hasMoreElements())
		{
			myCOM = (CommPortIdentifier) myPorts.nextElement();
			if(myCOM.getPortType() == CommPortIdentifier.PORT_SERIAL)
			{
				if(!myCOM.isCurrentlyOwned())
				{
					System.out.println(myCOM.getName());
					portNames.add(myCOM.getName());
					portId.add(myCOM);
				}	
			}
			
		}
		comPorts = new JComboBox(portNames.toArray());
		
// ----------------------------------------------- MASTER LAYOUT SETUP ---------------------------------------		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		topPane.add(menuBar, c);
		topPane.setBackground(menuBar.getBackground());
		
		c.weightx = 1.74;
		c.gridy = 1;
		topPane.add(sendButton, c);
		c.gridx = 1;
		c.weightx = 0.5;
		topPane.add(refresh, c);
		c.gridx = 2;
		c.weightx = 0.5;
		topPane.add(comPorts, c);
		c.gridx = 4;
		c.weightx = 0;
		topPane.add(new Label("              Baud Rate:"), c);
		
		c.gridx = 5;
		c.weightx = 0.5;
		topPane.add(baud, c);
		c.gridx = 6;
		c.weightx = 0.5;
		topPane.add(ok, c);
		c.gridx = 7;
		c.weightx = 0.5;
		topPane.add(r, c);
		c.gridx = 8;
		c.weightx = 0.5;
		topPane.add(n, c);
		c.gridx = 9;
		c.weightx = 0.5;
		topPane.add(allCaps, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.5;	
		txtPane.add(saveSend, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.5;
		
		txtPane.add(saveMix, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 0;
		c.weightx = 0.5;
		txtPane.add(saveRx, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.1;
		scrollSend.setPreferredSize(new Dimension(scrollSend.getWidth(), (int) (window.getHeight()*0.75)));
		txtPane.add(scrollSend, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.5;
		scrollMix.setPreferredSize(new Dimension(scrollMix.getWidth(), (int) (window.getHeight()*0.75)));
		txtPane.add(scrollMix, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 0.5;
		scrollRx.setPreferredSize(new Dimension(scrollRx.getWidth(), (int) (window.getHeight()*0.75)));
		txtPane.add(scrollRx, c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 0.5;	
		txtPane.add(clearTx, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.5;
		txtPane.add(clearMix, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 2;
		c.gridy = 2;
		c.weightx = 0.5;
		txtPane.add(clearRx, c);
		
		masterPane.add(topPane, BorderLayout.NORTH);
		masterPane.add(txtPane, BorderLayout.CENTER);
		masterPane.add(clearAll, BorderLayout.SOUTH);
		masterPane.setVisible(true);
		masterPane.setBackground(Color.GRAY);
		
		try {
		    window.setIconImage(ImageIO.read(new File("res/img.png")));
		}
		catch (IOException exc) {
		    exc.printStackTrace();
		}
		window.add(masterPane);
		window.setVisible(true);
		window.setPreferredSize(window.getPreferredSize());
//--------------------------------------------------------------------------------------------------------------------------		

        
	}
	
   
	@Override
	public void actionPerformed(ActionEvent e) 
	{
		String message;
		int option = 0;
		Writer myWriter = new Writer(txStack, mixStack);
		byte[] myBytes;
		if(e.getSource() == sendButton)
		{
			
			message = txStack.getText();
			txStack.select(0,message.length());
			try{
				txStack.getSelectedText().replace(txStack.getSelectedText(),"");
				if(message.length() == 0)
				{
					message = "";
				}
				if(r.getState()||n.getState())
				{
					if(r.getState())
					{
						option = 1;
					}
					if(n.getState())
					{
						option += 2;
					}
					if(allCaps.getState())
					{
						option +=4;
					}
					try {
						myWriter.send(openz, out, message, option);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				message = txStack.getText();
				if(option>3)
				{
					message = message.toUpperCase();
				}
				mixStack.append(message);
				if(message.charAt(message.length()-1)!='\n')
				{
					mixStack.append("\n");
				}
			} catch(NullPointerException ex)
			{
				myBytes = new byte[2];
				myBytes[0] = (byte) ('\r');
				myBytes[1] = (byte) ('\n');
				try {
					myWriter.send(openz, out, myBytes, 0);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			
		}
		if(e.getSource() == clearTx)
		{
			txStack.setText("");
		}
		if(e.getSource() == clearMix)
		{
			mixStack.setText("");
		}
		if(e.getSource() == clearRx)
		{
			rxStack.setText("");
		}
		if(e.getSource() == clearAll)
		{
			txStack.setText("");
			mixStack.setText("");
			rxStack.setText("");
		}
		if(e.getSource() == ok)
		{
		   ok.setEnabled(false);;
		   if(serialPort != null) 
		   {
		      try
		      {
			       // close the i/o streams.
			       out.close();
			       in.close();
			  } catch (IOException ex) 
		      {
			       // don't care
			  }
			  // Close the port.
			  serialPort.removeEventListener();
			  serialPort.close();
			       
			  openz.close();
			}

			myCOM = (CommPortIdentifier) portId.get(comPorts.getSelectedIndex());
			System.out.println("my COM is " + myCOM.getName());
			
			try{
				openz = myCOM.open(comPorts.getSelectedItem().toString(), 2000);
				if ( openz instanceof SerialPort )
		        {
		        	serialPort = (SerialPort) openz;
			        serialPort.setSerialPortParams(Integer.parseInt(baud.getText()),SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			                
			               in = serialPort.getInputStream();
			               out = serialPort.getOutputStream();
			                (new Thread(new Reader(in, mixStack, rxStack))).start();
			     }
			     else
			     {
			        System.out.println("Error: Only serial ports are handled by this example.");
			     }
			} catch (PortInUseException | UnsupportedCommOperationException | IOException e1) {
				System.out.println("Error opening COM PORT JBOX");
			}
			menuESP.setEnabled(true);
		}
		if(e.getSource() == refresh)
		{
			if(!ok.isEnabled())
			{
				ok.setEnabled(true);
			}
			Enumeration myPorts = CommPortIdentifier.getPortIdentifiers();
			comPorts.removeAllItems();
			while(myPorts.hasMoreElements())
			{
				myCOM = (CommPortIdentifier) myPorts.nextElement();
				if(myCOM.getPortType() == CommPortIdentifier.PORT_SERIAL)
				{
					if(!myCOM.isCurrentlyOwned())
					{
						System.out.println(myCOM.getName());
						portNames.add(myCOM.getName());
						portId.add(myCOM);
						comPorts.addItem(myCOM.getName());
					}	
				}
				
			}
			
	
		}
		if(e.getSource() == menuESP)
		{
			new ESPUtil(txStack, mixStack);
			
		}
	}
}
