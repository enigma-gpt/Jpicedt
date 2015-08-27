/*  jPicEdt, a picture editor for LaTeX.
    Copyright (C) 1999-2006  Sylvain Reynal
*/
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: SystemOutUtilities.java,v 1.9 2013/03/27 06:50:06 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est régi par la licence CeCILL soumise au droit français et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffusée par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilité au code source et des droits de copie, de modification et de
// redistribution accordés par cette licence, il n'est offert aux utilisateurs qu'une garantie limitée.  Pour
// les mêmes raisons, seule une responsabilité restreinte pèse sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les concédants successifs.
//
// À cet égard l'attention de l'utilisateur est attirée sur les risques associés au chargement, à
// l'utilisation, à la modification et/ou au développement et à la reproduction du logiciel par l'utilisateur
// étant donné sa spécificité de logiciel libre, qui peut le rendre complexe à manipuler et qui le réserve
// donc à des développeurs et des professionnels avertis possédant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invités à charger et tester l'adéquation du logiciel à leurs
// besoins dans des conditions permettant d'assurer la sécurité de leurs systèmes et ou de leurs données et,
// plus généralement, à l'utiliser et l'exploiter dans les mêmes conditions de sécurité.
//
// Le fait que vous puissiez accéder à cet en-tête signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accepté les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.ui.util;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * A class that allow redirection of stderr and/or stdout to a log file.
 * Invokation of this class must be done with "SystemOutUtilities.instance()" which return a reference to the singleton.
 *
 * @author Sylvain Reynal
 * @since PicEdt 1.3
 * @version $Id: SystemOutUtilities.java,v 1.9 2013/03/27 06:50:06 vincentb1 Exp $
 *
 */
public class SystemOutUtilities  {

	/** doesn't redirect stdout to a file, ie redirect to console ; instead open a frame and displays the error message in it */
	public static final int STANDARD = 1;

	/** redirects stdout to a file named "jPicedtError.log" */
	public static final int FILE = 2;


	/* DEBUG flag (this amongst other thing redirects only System.err, so that we can print debug messages to System.out) */
	private static final boolean DEBUG=false;

	////////////////////////////////////////
	//// PUBLIC STATIC METHODS
	////////////////////////////////////////
	private static SystemOutUtilities singleton = null;

	/**
	 * intanciates singleton if it's null, then returns it
	 */
	public static SystemOutUtilities instance(){

		if (singleton == null) singleton = new SystemOutUtilities();
		return singleton;
	}

	//////////////////////////////////////////////////////////////////////
	//// PUBLIC NON-STATIC METHODS (accessed through a call to instance())
	//////////////////////////////////////////////////////////////////////
	private int currentRedir = STANDARD; // when this class get accessed for the first time, System.out = standard out

	/**
	 * Redirect to the stream of the given type
	 * @param type one of the predefinite SystemOutUtilities's redirection types
	 */
	public void redirect(int type){

		if (DEBUG) System.out.println("SystemOutUtilities.Redirecting System.out : type = " + (type==FILE ? "FILE" : "STANDARD"));

		// first close open streams and release resource (currentRedir = old redir type)
		switch(currentRedir){
		case FILE :
			stopSystemOutToFileRedirection();
			break;
		default:
			// this was standard out => nothing to close
			break;
		}
		// now process redirection :
		currentRedir = type;
		switch(currentRedir){
		case FILE :
			redirectSystemOutToFile();
			break;
		default:
			// standard out
			break;
		}
	}


	/**
	 * Sets the "displayDialog" flag, ie what must happen when an error message comes up and redir=FILE : do we open a JDialog
	 * or simply write the error message to the log file ?
	 * (obviously, calling this method if redir=STANDARD simply makes no sense)
	 *
	 * @param state if currentRedir = FILE and :
	 * - state==TRUE, enables opening a dialog box whenever some String is written to System.out
	 *   A "watchdog" Thread is created for that purpose : System.out is redirected to a pipe, which the Thread watches periodically.
	 * - state==FALSE, redirect System.out directly to the "toFilePrintStream" FOS, and kill the watchdog Thread if it's still alive.
	 *
	 * Otherwise (ie currentRedir = STANDARD), we do nothing.
	 */
	public void displayDialog(boolean state){

		if (DEBUG) System.out.println("SystemOutUtilities.displayDialog(" + state + ")");

		if (currentRedir==STANDARD) return; // this may also happen if "redirectSystemOutToFile" previously failed

		// flag to signals Dialog enabling
		displayDialog = state;
		// redirect System.out to pipe
		if (state==true){
			try{
				// create output pipe for System.out
				PipedOutputStream pos = new PipedOutputStream(); // not yet connected
				newSystemOut = new PrintStream(new BufferedOutputStream(pos,1024), true); // autoflush = true
				// create input pipe (we'll read from it and send date to file/JTextPane/etc...)
				PipedInputStream pis = new PipedInputStream();
				bufReader = new BufferedReader(new InputStreamReader(pis),1024);
				// connect pipes
				pos.connect(pis);
				// start watchdog timer
				lt = new ListenerThread();
				lt.start();
			}
			catch(IOException e){
				System.err.println("SystemOutUtilities.Cannot redirect System.out and System.err to pipe !");
				e.printStackTrace();
				newSystemOut = null;
				return;
			}
			// set System.out and System.err to the previously created pipe, from which we'll read (through "pis") text
			// and then send it to a JTextArea :
			if (!DEBUG) System.setOut(newSystemOut);
			System.setErr(newSystemOut);
		}

		// we switch back to "redirection to toFilePrintStream"
		else{
			newSystemOut = toFilePrintStream;
			if (!DEBUG) System.setOut(newSystemOut);
			System.setErr(newSystemOut);
			lt = null; // stop thread
		}
	}








	////////////////////////////////////////////////////////////
	//// PRIVATE METHODS AND FIELDS
	///////////////////////////////////////////////////////////

	/**
	 * constructor is protected since the only way to access this class is by using SystemOutUtilities.instance()
	 */
	private SystemOutUtilities() {

		// remember default System.out and System.err
		oldSystemOut = System.out;
		oldSystemErr = System.err;
	}

	/* private variables */
	private PrintStream oldSystemOut; // save old System.out
	private PrintStream newSystemOut; // new System.out (with redirection)
	private PrintStream oldSystemErr; // save old System.err
	private BufferedReader bufReader; // a buffer reader that is connected, via a pipe, to System.out, and allow us to display error message in a JTextArea
	private PrintStream toFilePrintStream; // a print stream that writes to the log file
	private ListenerThread lt; // a watchdog thread that listen to incoming error messages from System.out, via a pipe
	private boolean displayDialog = false; // if TRUE, a dialog box opens whenever a message comes up (or within a short delay)
	private SystemOutToFrame systemOutToFrameInstance=null; // an instance of the JFrame used to display messages ; if it's null, we create a new one; otherwise we use the existing one, so as to avoid opening more than one frame.

	/**
	 * Redirect System.out to a file named "jPicEdtError.log" located in user's home directory
	 * - displayDialog = false by default (ie no dialog box gets opened when an error message occurs)
	 * - we init "toFilePrintStream" so that it points to "user's setting dir/error.log"
	 * - we set System.out to this stream (redirection)
	 */
	private void redirectSystemOutToFile(){

		if (DEBUG) System.out.println("SystemOutUtilities.redirectSystemOutToFile()...");
		try{
			// create output stream to log file
			String fileName = getErrorLogFile();
			toFilePrintStream = new PrintStream(new BufferedOutputStream(new FileOutputStream(fileName), 1024), true); // autoflush = true
			newSystemOut = toFilePrintStream;
		}
		// if an exception occurs, we switch back to "redirect to console"
		catch(IOException e){
			System.err.println("Can not redirect System.out and System.err to a file !");
			e.printStackTrace();
			newSystemOut = null;
			currentRedir = STANDARD;
			return;
		}
		// and redirect System.out if success
		if (!DEBUG) System.setOut(newSystemOut);
		System.setErr(newSystemOut);

	}

	/**
	 * Return the path to error.log
	 */
	public static String getErrorLogFile(){
		return jpicedt.JPicEdt.getUserSettingsDirectory() + File.separator + "error.log";
	}

	/**
	 * After a redirection to a pipe, sets back System.out and System.err to their default value
	 */
	private void stopSystemOutToFileRedirection(){

		if (DEBUG) System.out.println("SystemOutUtilities.stopSystemOutToFileRedirection() : closing previous streams...");

		if (newSystemOut == null) return;
		if (lt!= null) lt.interrupt(); // flush bufReader

		if (!DEBUG) System.setOut(oldSystemOut); // restore System.out default PrintStream
		System.setErr(oldSystemErr); // restore System.err default PrintStream
		newSystemOut.flush();

		// flush'n close previous stream (so that pending messages get written to jPicEdtError.log)
		toFilePrintStream.flush();
		toFilePrintStream.close();

		// kill thread
		lt = null;
	}

	/**
	 * the watchdog thread that periodically listens to incoming error messages (currently set to 3")
	 * then writes them to file and/or to a JTextArea (see inner class SystemOutToFrame)
	 * kill it by setting "lt = null"
	 */
	class ListenerThread extends Thread {

		public ListenerThread()  {
			super("SystemOutUtilities.ListenerThread");
			if (DEBUG) System.out.println("SystemOutUtilities : new ListenerThread()");
			setPriority(Thread.MIN_PRIORITY);
			//	    start(); // now done by caller (due to contention pbs)
		}

		public void run(){

			if (DEBUG) System.out.println("SystemOutUtilities.ListenerThread started...");

			Thread c = Thread.currentThread();
			while(c == lt){ // i.e. while this Thread is still a live
				if (DEBUG) System.out.println("SystemOutUtilities.ListenerThread is alive !");
				try{
					if (DEBUG) System.out.println("SystemOutUtilities : there's nothing in System.out pipe !");
					// if there's nothing in bufReader (i.e. no incoming message)
					while(bufReader != null && !bufReader.ready()){
						sleep(2999);
					}
				}
				catch(InterruptedException ie){ie.printStackTrace();}
				catch(IOException ioe){ioe.printStackTrace();}

				// if something has been written to System.out...
				if (bufReader != null){
					String str;
					StringBuffer buf = new StringBuffer(100);
					try{
						// read as much line as possible from bufReader (that is, from the pipe)
						// then, 1Â°) write them to file 2Â°) display them in a JDialog
						while(bufReader.ready() && (str = bufReader.readLine()) != null){
							if (DEBUG) System.out.println("SystemOutUtilities.Writing to jPicEdtError.log : " + str);
							toFilePrintStream.println(str);
							buf.append(str);
							buf.append("\n");
						}
					}
				catch(Exception e){e.printStackTrace();}
					if (displayDialog) {
						if (systemOutToFrameInstance == null) systemOutToFrameInstance = new SystemOutToFrame();
						systemOutToFrameInstance.println(buf.toString());
					}
				}
			}
			if (DEBUG) System.out.println("SystemOutUtilities.ListenerThread killed...");
		}
	}

	/**
	 * A JDialog box which is used to display error message in a JTextPane or to a file.
	 */
	class SystemOutToFrame extends JDialog {

		JTextArea streamTA = new JTextArea(50,20);
		JCheckBox dontShowAgainCB;

		/**
		 * build then asyncrhonously show a JDialog
		 * this method is thread-safe regarding Swing paint mechanism
		 */
		SystemOutToFrame(){
			super();
			setTitle("jPicEdt Error Log");
			setModal(true);
			if (DEBUG) System.out.println("SystemOutUtilities.Opening a new SystemOutToFrame...");
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);

			JPanel panelStream = new JPanel(new BorderLayout(5,5));
			panelStream.setBorder(BorderFactory.createEtchedBorder());

			// init text area :
			streamTA.setEditable(false);
			JScrollPane scrollStream = new JScrollPane(streamTA);
			panelStream.add(scrollStream, BorderLayout.CENTER);

			// init widgets
			JPanel p = new JPanel(new GridLayout(2,1,5,5));
			p.add(new JTextArea("Beta release BUG REPORT : please send "
			                    + System.getProperty("user.home") + System.getProperty("file.separator")
			                    + "jPicEdtError.log to reynal@ensea.fr, it'll be very helpful !\n"
			                    + "Getting annoyed ? Well, launching jpicedt with '-redir=standard' as the first arg simply writes these messages to the console."));
			dontShowAgainCB = new JCheckBox("Pretty boring indeed, so please don't show me the same message again and again :-((");
			dontShowAgainCB.addActionListener(new ActionListener(){
				                                  public void actionPerformed(ActionEvent e){SystemOutUtilities.instance().displayDialog(false);}});
			p.add(dontShowAgainCB);
			panelStream.add(p,BorderLayout.SOUTH);

			// add panelStream to the frame :
			getContentPane().add(panelStream,BorderLayout.NORTH);
			// ensure calling SystemOutToFrame is thread-safe
			SwingUtilities.invokeLater(new Runnable(){
				                           public void run(){
					                           pack();
					                           setVisible(true);}});
		}

		/**
		 * print out the given text in this JDialog
		 */
		public void println(String text){

			streamTA.append(text);
			streamTA.append("\n");
		}

		/**
		 * dispose this frame
		 */
		protected void processWindowEvent(WindowEvent e) {

			super.processWindowEvent(e);
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				dispose();
				systemOutToFrameInstance = null;
			}
		}
	} //SystemOutToFrame

} // class SystemOutUtilities
