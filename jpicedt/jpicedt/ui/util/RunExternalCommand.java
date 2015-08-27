// RunExternalCommand.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2011  Sylvain Reynal
// Copyright (C) 2012  Sylvain Reynal, Vincent Belaïche
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: RunExternalCommand.java,v 1.19 2013/03/27 06:50:11 vincentb1 Exp $
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


/// Code:
package jpicedt.ui.util;

import jpicedt.*;
import jpicedt.graphic.io.formatter.*;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.*;
import jpicedt.ui.MDIManager;
import jpicedt.ui.PEDrawingBoard;
import jpicedt.widgets.MDIComponent;
import jpicedt.widgets.PEFrame;
import jpicedt.format.output.util.FormatterException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.CharBuffer;
import java.util.EnumMap;
import java.util.Properties;
import java.util.EnumSet;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.*;

import static jpicedt.Log.*;
import static jpicedt.Localizer.*;

/**
 * A class implementing methods for calling external processes like latex, (x)dvi, ghostscript&hellip;
 * on the current picture file (e.g. the active internal frame)<p>
 * <p>
 * A "tmp" file is generated on-the-fly in the "tmp" directory of the underlying platform, This tmp file
 * contains an adequate preamble (e.g. <code>\\usepackage{epic}</code>) depending on current options, and LaTeX
 * commands (or PsTricks or eepic commands) corresponding to each object found in the given picture.
 *<p>
 * These command lines may include the following special symbols:<ul>
 * <li><code>{f}</code>: tmp file name w/o extension (the user need to specify explicitely any extension required by the external program)
 * <li><code>{p}</code>: tmp file path including trailing separator
 * <li><code>{i}</code>: jPicEdt add-ons dir, that is : "<code>jpicedt.home/add-ons</code>" (trailing separtor excluded)</li>
 * <li><code>{j}</code>: jPicEdt installation directory (without trailing separator)</li>
 * <li><code>{d}</code>: drawing directory, that is the directory in which the drawing is saved, if any
 * <li><code>{u}</code>: user setting directory, where the user preferences, user defined fragments, and macros are saved
 * <li><code>{[}</code>: expands to <code>{</code>
 * <li><code>{]}</code>: expands to <code>}</code>
 * </ul>
 * <p>
 * E.g.: <code>kghostview {p}{f}.ps</code> or <code>"{i}\windows\miktex\jlatex.bat" "{p}" {f} "{d}"</code>
 * <p>
 * Since we don't want to resort on platform dependent tricks (working dirs, PATH variables,
 * root directory names differing on UNIX and Win32 systems, latex command line which might also differ, aso&helllip;),
 * the better thing the user could do is to write one batch/shell script
 * per process he wants to launch, then provide the names of these scripts to PicEdt.
 * <p>
 * Scripts are easy to write on UNIX/Window platforms,
 * and very likely on MacOS platforms by using AppleScript.
 *
 * @author Sylvain Reynal
 * @since PicEdt 1.2
 * @version $Id: RunExternalCommand.java,v 1.19 2013/03/27 06:50:11 vincentb1 Exp $
 *
 */
public class RunExternalCommand extends Thread {

	/** Enum for predefined external commands */
	public static enum Command {
		LATEX("action.command.LaTeX","command.latex","",true),
		DVIVIEWER("action.command.DVI", "command.dvi", "",false),
		DVIPS("action.command.Dvips", "command.dvips", "",false),
		GHOSTVIEW("action.command.Ghostview", "command.ghostview", "",false),
		USER1("action.command.UserProgram1","command.user1","",false),
		USER2("action.command.UserProgram2","command.user2","",false),
		BITMAP_CREATION("action.command.BitmapCreation","command.bitmap","",true);

		String name;
		String key;
		String defaultParams;
		boolean hasToWriteTmpTeXFile;

		Command(String name, String keyForParams, String defaultParams,boolean hasToWriteTmpTeXFile){
			this.name = name;
			this.key = keyForParams;
			this.defaultParams = defaultParams;
			this.hasToWriteTmpTeXFile = hasToWriteTmpTeXFile;
		}

		/** Returns the name of the external command (to be i18n'd), also used by ActionLocalizer */
		public String getName(){
			return name;
		}

		/** Returns a key used to retrieve/store command parameters from/to a Properties object */
		public String getKeyForParams(){
			return key;
		}

		/** Returns default commands parameters */
		public String getDefaultParams(){
			return defaultParams;
		}

		/** Returns whether this command requires first writing a temp TeX file */
		public boolean hasToWriteTmpTeXFile(){
			return hasToWriteTmpTeXFile;
		}
	}

	/** members of Command enum that are TeX related external commands */
	public final static EnumSet<Command> TeX_COMMANDS = EnumSet.range(Command.LATEX,Command.USER2);


	////////////////////////////////// PRIVATE FIELDS /////////////////////////////////

	private MDIManager mdimgr;
	private Drawing drawing;
	private ContentType contentType;
	private Process proc; // the process being executed (null if none)
	private PrintStream printStream; // command's input stream (shared by inner classes)
	private String commandLine; // command line created on-the-fly from the Properties and commandToRun
	private String commandName; // identifier of command.
	private static File tmpFile; // temp file created by the JVM the first time a command is run
	private static String tmpPath; // path for tmp file (e.g. "/tmp"), this is {p}
	private static String tmpFilePrefix; // file prefix, e.g. "jpicedt4875" (w/o extension)
	private static String addonDir; // jPicEdt's add-ons directory, trailing "/" included

	private static DlgBoxDisplayProcessIOStream dlgBox;
	// --- for debugging purpose ---
	public static boolean isGUI=true; // if true, redirect process output stream to a dialog box, otherwise to System.out

	/////////////////////////////////// METHODS /////////////////////////////////////

	/**
	 * @param drawing The target document upon which the command will acts
	 * @param contentType the content-type used to format the document
	 * @param commandToRun one of the predefined constant fields (e.g. LATEX, DVIVIEWER, DVIPS or GHOSTVIEW)
	 * @since jPicEdt 1.2
	 */
	public RunExternalCommand(Drawing drawing, ContentType contentType, Command commandToRun, MDIManager mdimgr) {

		super("RunExtCom");
		this.contentType = contentType;
		this.drawing = drawing;
		this.mdimgr = mdimgr;

		try {

			createTmpFile();

			if (commandToRun.hasToWriteTmpTeXFile())
				writeTmpTeXFile();

			// create command line :
			this.commandName = localize(commandToRun.getName());
			this.commandLine=(new CommandLineBuilder()).buildCommandLine(JPicEdt.getProperty(commandToRun.getKeyForParams(),commandToRun.getDefaultParams()));
		}
		catch(IOException ioEx){
			if (mdimgr != null)
				mdimgr.showMessageDialog( localize("exception.IOError") + " : " + ioEx.getMessage(), commandToRun.getName(), JOptionPane.ERROR_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, localize("exception.IOError") + " : " + ioEx.getMessage(), commandToRun.getName(), JOptionPane.ERROR_MESSAGE);
			commandLine = null;
			// now command line is null, and run() takes care of that.
		}

	}

	/**
	 * Mostly for debugging purpose, or when used outside the jpicedt.ui package (since then JPicEdt.preferences
	 * are no longer available).
	 * @param drawing The target document upon which the command will acts
	 * @param contentType the content-type used to format the document
	 * @param cmdLine a string containing the command to be executed (e.g. "ext_prod.sh {p} latex {f}")
	 * @since jPicEdt 1.4pre5
	 */
	public RunExternalCommand(Drawing drawing, ContentType contentType,String cmdLine, boolean isWriteTmpTeXfile) {

		super("RunExtCom");
		this.contentType = contentType;
		this.drawing = drawing;

		try {
			createTmpFile();
			if (isWriteTmpTeXfile) writeTmpTeXFile();
			this.commandName=localize("mundefined.command");
			this.commandLine=(new CommandLineBuilder()).buildCommandLine(cmdLine);
		}
		catch(IOException ioEx){
			JOptionPane.showMessageDialog(null,
			                              localize("exception.IOError") + " : " + ioEx.getMessage(),
			                              cmdLine,
			                              JOptionPane.ERROR_MESSAGE);
			commandLine = null;
			// now command line is null, and run() takes care of that.
		}

	}

	public static File getTmpFile(){
		return tmpFile;
	}

	public static String getTmpPath(){
		return tmpPath;
	}

	public static String getTmpFilePrefix(){
		return tmpFilePrefix;
	}

	private void writeTmpTeXFile() throws IOException {
		// create "/tmp/jpicedt.tex" :
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
		try
		{
			bw.write(contentType.createFormatter().createFormatter(drawing,FormatterFactory.MAKE_STANDALONE_FILE).format());
		}
		catch(FormatterException fmtEx)
		{
			fmtEx.resolve();
		}
		bw.newLine();
		bw.close();
	}


	public void openUI(){
		if (!isGUI) return;
		if (dlgBox==null) {
			dlgBox = new DlgBoxDisplayProcessIOStream();
			dlgBox.frame.pack();
		}
		dlgBox.frame.setVisible(true);
	}

	/**
	 * run an external process as a thread (overloading <code>Thread.run()</code> method)
	 * we then pipe the process std output to a <code>JTextArea</code>
	 *
	 * @since PicEdt 1.2
	 */
	public void run() {

		if (commandLine == null) return;

		dlgBox.frame.setVisible(true);
		dlgBox.frame.toFront();

		dlgBox.append("\njPicEdt running " + commandName + " on `" + tmpFile
					  + "' with ``" + commandLine + "''");

		// run process
		try{
			proc = Runtime.getRuntime().exec(commandLine);
		}
		catch(IOException e){
			if (dlgBox!=null) dlgBox.append(e.toString());
			else e.printStackTrace();
			return;
		}

		// fetch process's output stream (an input stream from the JVM's point of view) and plug a buffered reader input stream on it
		BufferedReader  bufferedReader= new BufferedReader(new InputStreamReader(proc.getInputStream()));

		// fetch process's input stream (used by DlgBox)
		printStream = new PrintStream(proc.getOutputStream(),true);

		// read output chars from command (we can't use readLine() since it blocks until it encounters a CR, which
		// can be annoying in case the running process waits for an input, e.g. with a  "?" or "*" prompt as for LaTeX)
		try{
			final int BUF_SIZE = 2048;
			char[] buffer = new char[BUF_SIZE];
			StringBuffer stringBuffer = null;
			if(dlgBox == null)
				stringBuffer= new StringBuffer(BUF_SIZE);
			int readLength;
			while((readLength=bufferedReader.read(buffer,0,BUF_SIZE)) != -1){
				if(readLength != 0)
				{
					if (dlgBox != null)
					{
						dlgBox.append(buffer,0,readLength);
					}
					else
					{
						stringBuffer.append(buffer,0,readLength);
						System.out.print(stringBuffer);
						stringBuffer.setLength(0);
					}

				}
			}
			//System.out.println();
		}
		catch(IOException e){
			if (dlgBox != null) dlgBox.append(e.toString());
			else e.printStackTrace();
		}

		// wait for command to terminate
		try {
			proc.waitFor();
		}
		catch (InterruptedException e) {
			if (dlgBox != null) dlgBox.append('\n'+localize("exception.RunExtCmdProcessInterrupted")+'\n');
			else e.printStackTrace();
		}

		// check its exit value

		if (proc.exitValue() != 0){
			if (dlgBox != null) dlgBox.append('\n'+localize("exception.RunExtCmdExitValueNonNil")+'\n');
			else System.out.println("Process terminated with non-null value"+'\n');
		}


		// close stream
		try{
			bufferedReader.close();
		}
		catch(IOException e){
			if (dlgBox != null) dlgBox.append(e.toString());
			else e.printStackTrace();
		}

		if (dlgBox != null) {
			dlgBox.append("\njPicEdt external process: process terminated (OK).\n");
			//dlgBox.dispose();
		}
		else System.out.println("\njPicEdt external process: process terminated (OK).\n");

		return;
	}

	/**
	 * Delete files created by JPicEdt or programms called by JPicEdt (LaTeX, dvips,&hellip;).
	 */
	public static void cleanTmpDir(){
		if (tmpPath==null) return; // ok, nothing dirty yet !
		String[] list = new File(tmpPath).list(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return name.startsWith(tmpFilePrefix);
		}});
		// delete them :
		for (int i=0; i<list.length; i++){
			//System.out.println("deleting " + new File(tmpPath,list[i]));
			new File(tmpPath,list[i]).delete();
		}
		tmpFile = null; // force re-creation of tmp file on next call to constructor
	}

	/**
	 * @return the add-ons directory w/o trailing "/"
	 */
	public static String getAddonDir(){
		if (addonDir==null) {
			addonDir = MiscUtilities.getJPicEdtHome() + File.separator + "add-ons";
			//System.out.println("addOnDir = " + addonDir);
		}
		return addonDir;
	}

	/**
 	 * Create a new tmp file if it doesn't exit yet
	 * @exception IOException if an error occurs
	 */
	public static void createTmpFile() throws IOException {
		if (tmpFile != null) return;
		tmpFile = File.createTempFile("jpicedt",".tex", JPicEdt.getTmpDir());
		tmpFile.deleteOnExit();
		tmpPath = tmpFile.getParent(); // "/tmp"
		String name =  tmpFile.getName(); // "jpicedt1432.tex"
		tmpFilePrefix = name.substring(0,name.indexOf(".")); // "jpicedt1432"
	}

	/**
	 * @return an AbstractCustomizer suited for editing Properties related to external commands
	 */
	public static AbstractCustomizer createCustomizer(Properties preferences){
		return new Customizer(preferences);
	}

	/////////////////////////////////////////////////////////////////////////
	/**
	 * An inner class implementing a JDialog to display external process
	 * inputStream and send keyboard events to process outputStream
	 */
	public class DlgBoxDisplayProcessIOStream implements ActionListener,KeyListener, Runnable{

		MDIComponent frame;

		/** panelStream contains an editable JTextArea and a JScrollPane */
		JTextArea streamTA = new JTextArea(25,50);
		JScrollPane scrollStream = new JScrollPane(streamTA);
		StringBuffer areaBuffer = new StringBuffer(); // buffer for async. filling of streamTA

		/** panel containing the buttons */
		JPanel panelButtons = new JPanel();;
		JButton buttonKill = new JButton(localize("commands.Kill"));

		/** buffer for typed chars (see KeyTyped) */
		StringBuffer strBuf = new StringBuffer();

		//boolean visible = false; // force show on first call to append

		/**
		 * @since PicEdt 1.2
		 */
		DlgBoxDisplayProcessIOStream() {

			streamTA.setEditable(false);

			scrollStream.setBorder(BorderFactory.createEtchedBorder());

			buttonKill.addActionListener(this);

			panelButtons.add(buttonKill);
			panelButtons.setBorder(BorderFactory.createEtchedBorder());

			JPanel contentPane = new JPanel(new BorderLayout());
			contentPane.add(scrollStream,BorderLayout.CENTER);
			contentPane.add(panelButtons,BorderLayout.SOUTH);

			if (mdimgr != null){
				frame = mdimgr.createMDIComponent(contentPane);
				mdimgr.addMDIComponent(frame);
			}
			else
				frame = new PEFrame(contentPane);
			frame.setTitle(localize("commands.NotAMenuCommand"));
			frame.setResizable(true);
			frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

			//Dimension dlgSize = this.getPreferredSize();
			//frame.setSize(getPreferredSize());
			streamTA.requestFocus(); // so that entering command after a prompt doesn't need a mouse click on the JTextArea
			streamTA.addKeyListener(this);
		}

		/** append text ; thread safe */
		public void append(String text){
			areaBuffer.append(text);
			SwingUtilities.invokeLater(new Thread(this));

		}

		/** append text ; thread safe */
		public void append(char[] text,int offset,int len){
			areaBuffer.append(text,offset,len);
			SwingUtilities.invokeLater(new Thread(this));
		}

		/** handle asynchronous widgets updating */
		public void run(){
			if (!frame.isVisible()) {
				frame.pack();
				frame.setVisible(true);
				//visible=true;
			}
			if (streamTA.getLineCount() > 100) streamTA.setText("");
			streamTA.append(areaBuffer.toString());
			//System.out.println(areaBuffer.toString());
			areaBuffer = new StringBuffer();
		}


		public void keyPressed(KeyEvent e){

			if (e.getKeyCode() == KeyEvent.VK_ENTER && printStream != null) {

				printStream.println(strBuf.toString()); // flush buffer, sending all typed keys + CR to external process
				strBuf.setLength(0);
				streamTA.append("\n");
			}
		}

		public void keyReleased(KeyEvent e){}

		public void keyTyped(KeyEvent e){

			char c = e.getKeyChar();
			strBuf.append(c); // accumulate typed keys in buffer
			streamTA.append(Character.toString(c));

		}

		// called by KILL :
		public void actionPerformed(ActionEvent e) {
			proc.destroy();
			//frame.dispose();
		}

	} // inner class DlgBoxDisplayProcessIOStream

	///////////////////////////////////////////////////////////////////////////

	/**
	 * A Customizer for the configuration of external commands preferences.
	 * @author Sylvain Reynal
	 * @since PicEdt 1.3
	 */
	private static class Customizer extends AbstractCustomizer implements ActionListener {

		private Properties preferences;
		private JButton loadConfigBT;
		private EnumMap<Command, JTextField> commandToJTFMap;

		private class HelpPanelFactory{
			private JPanel helpPanel;
			private GridBagLayout gbl;
			private GridBagConstraints c;
			private CommandLineBuilder clb;
			private int y;

			public HelpPanelFactory(CommandLineBuilder clb){
				this.clb = clb;
				helpPanel = new JPanel(gbl = new GridBagLayout());
				helpPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),localize("menu.Help")));
				c = new GridBagConstraints();
				c.ipadx = 3;
				y = 0;
				//JLabel l = new JLabel(localize("Commands.helpstring"));
				//c.anchor = GridBagConstraints.WEST;
				//c.gridwidth = 4;
				//gbl.setConstraints(l,c);
				//helpPanel.add(l);
				y = 1;
				c.gridwidth = 1;
			}

			public JPanel getPanel(){ return helpPanel; }
			public void addHelpRow(String description,String key){
				JLabel l;
				l = new JLabel(description);
				c.gridy = y++;
				c.gridx = 0;
				c.anchor = GridBagConstraints.WEST;
				gbl.setConstraints(l,c);
				helpPanel.add(l);
				l = new JLabel("\"{" + key + "}\"");
				c.anchor = GridBagConstraints.CENTER;
				++ c.gridx;
				gbl.setConstraints(l,c);
				helpPanel.add(l);
				l = new JLabel("\u21d2");
				++ c.gridx;
				gbl.setConstraints(l,c);
				helpPanel.add(l);
				l = new JLabel("\"" + clb.buildCommandLine("{" + key + "}") + "\"");
				c.anchor = GridBagConstraints.WEST;
				++ c.gridx;
				gbl.setConstraints(l,c);
				helpPanel.add(l);
			}

		}


		/**
		 * Creates a new panel for the configuration of external commands
		 */
		public Customizer(Properties preferences){

			this.preferences  = preferences;
			this.commandToJTFMap = new EnumMap<Command, JTextField>(Command.class);

			JPanel p;
			JTextField tf;

			p = new JPanel(new GridLayout(7,2,5,5));
			p.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),localize("commands.title")));

			for (Command c: TeX_COMMANDS){
				p.add(new JLabel(" "+localize(c.getName())+" :"));
				tf = new JTextField(20);
				p.add(tf);
				commandToJTFMap.put(c,tf);
			}

			p.add(new JLabel(" "+localize("commands.LoadPredefinedConfig")));
			loadConfigBT = new JButton(localize("button.Load"));
			loadConfigBT.addActionListener(this);
			p.add(loadConfigBT);

			add(p,BorderLayout.NORTH);

			// reminder for {f}, {p}, ... shortcuts :
			HelpPanelFactory helpPanelFactory = new HelpPanelFactory(new CommandLineBuilder());

			helpPanelFactory.addHelpRow("Tmp. dir : ","p");
			helpPanelFactory.addHelpRow("Tmp. TeX file : ", "f");
			helpPanelFactory.addHelpRow("Add-ons dir. : ","i");
			helpPanelFactory.addHelpRow("Drawing dir. : ","d");
			helpPanelFactory.addHelpRow("User settings dir. : ","u");
			helpPanelFactory.addHelpRow("Open curly backet : ","[");
			helpPanelFactory.addHelpRow("Close curly bracket : ","]");
			add(helpPanelFactory.getPanel(), BorderLayout.CENTER);
		}

		/**
		* @return the panel title, used e.g. for Border or Tabpane title.
		* @since jPicEdt
		* @author Sylvain Reynal
		*/
		public String getTitle(){
			return localize("commands.title");
		}


		/**
		 * @return the Icon associated with this panel, used e.g. for TabbedPane decoration
		 * @since jPicEdt
		 * @author Sylvain Reynal
		 */
		public Icon getIcon(){
			return null;
		}


		/**
		 * @return the tooltip string associated with this panel
		 * @since jPicEdt
		 * @author Sylvain Reynal
		 */
		public String getTooltip(){
			return localize("commands.tooltip");
		}

		/**
		 * Load widgets display content with a default value retrieved from the Properties object
		 * @since jPicEdt
		 * @author Sylvain Reynal
		 */
		public void loadDefault() {
			for (Command c: TeX_COMMANDS){
				JTextField tf = commandToJTFMap.get(c);
				tf.setText(c.getDefaultParams());
			}
		}

		/**
		 * Load widgets value from the Properties object given in the constructor
		 * @since jPicEdt
		 * @author Sylvain Reynal
		 */
		private void load(Properties pref) {
			for (Command c: TeX_COMMANDS){
				JTextField tf = commandToJTFMap.get(c);
				tf.setText(pref.getProperty(c.getKeyForParams(),c.getDefaultParams()));
			}
		}

		public void load(){
			load(JPicEdt.getPreferences());
		}

		/**
		 * ActionHandler for "loadConfigBT"
		 */
		public void actionPerformed(ActionEvent e){
			JFileChooser fileChooser = new JFileChooser(getAddonDir());
			fileChooser.setDialogTitle(localize("commands.LoadPredefinedConfig"));
			fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter(){
				public boolean accept(File f){return f.isDirectory() || f.getPath().endsWith(".properties");}
				public String getDescription(){return "External scripts configuration files";}
			});
			if (fileChooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) return;
			File file =  fileChooser.getSelectedFile();
			if (file!= null) {
				Properties prop = new Properties();
				try {
					InputStream is = new BufferedInputStream(new FileInputStream(file));
					prop.load(is);
					//prop.list(System.out);
					load(prop);
					is.close();
				}
				catch (IOException ioEx){
					JOptionPane.showMessageDialog(
						JOptionPane.getRootFrame(),
						localize("exception.IOError") + '\n' + ioEx.getMessage(),
						localize("commands.LoadPredefinedConfig"),
						JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		/**
		 * Store current widgets value to the Properties object given in the constructor
		 * @since jPicEdt
		 * @author Sylvain Reynal
		 */
		public void store(){
			for (Command c: TeX_COMMANDS){
				JTextField tf = commandToJTFMap.get(c);
				preferences.setProperty(c.getKeyForParams(),tf.getText());
			}
		}


	} // PanelExternalCommand


} // RunExternalCommand
