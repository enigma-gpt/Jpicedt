// BeanShell.java --- -*- coding: iso-8859-1 -*-
// July 30, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         D�partement de Physique
//         �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (�NS�A)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: BeanShell.java,v 1.11 2013/03/27 06:50:46 vincentb1 Exp $
// Keywords:
// X-URL: http://www.jpicedt.org/
//
// Ce logiciel est r�gi par la licence CeCILL soumise au droit fran�ais et respectant les principes de
// diffusion des logiciels libres. Vous pouvez utiliser, modifier et/ou redistribuer ce programme sous les
// conditions de la licence CeCILL telle que diffus�e par le CEA, le CNRS et l'INRIA sur le site
// "http://www.cecill.info".
//
// En contrepartie de l'accessibilit� au code source et des droits de copie, de modification et de
// redistribution accord�s par cette licence, il n'est offert aux utilisateurs qu'une garantie limit�e.  Pour
// les m�mes raisons, seule une responsabilit� restreinte p�se sur l'auteur du programme, le titulaire des
// droits patrimoniaux et les conc�dants successifs.
//
// � cet �gard l'attention de l'utilisateur est attir�e sur les risques associ�s au chargement, �
// l'utilisation, � la modification et/ou au d�veloppement et � la reproduction du logiciel par l'utilisateur
// �tant donn� sa sp�cificit� de logiciel libre, qui peut le rendre complexe � manipuler et qui le r�serve
// donc � des d�veloppeurs et des professionnels avertis poss�dant des connaissances informatiques
// approfondies.  Les utilisateurs sont donc invit�s � charger et tester l'ad�quation du logiciel � leurs
// besoins dans des conditions permettant d'assurer la s�curit� de leurs syst�mes et ou de leurs donn�es et,
// plus g�n�ralement, � l'utiliser et l'exploiter dans les m�mes conditions de s�curit�.
//
// Le fait que vous puissiez acc�der � cet en-t�te signifie que vous avez pris connaissance de la licence
// CeCILL, et que vous en avez accept� les termes.
//
/// Commentary:

//



/// Code:
package jpicedt.ui.util;

import jpicedt.JPicEdt;
import jpicedt.ui.MDIManager;
import jpicedt.widgets.MDIComponent;
import jpicedt.widgets.PEFrame;


import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import bsh.*;
import bsh.util.*;

/**
 * Wrapper for the BSH interpreter console + some useful static methods for running scripts from inside JPicEdt.
 */
public class BeanShell implements Runnable {

	private Interpreter interpreter; // for use by BeanShell() (i.e. BSH Console)
	private MDIManager mdimgr;
	private MDIComponent frame;

	private static final String BSH_ALIASES="import jpicedt.*;"+
				"import jpicedt.graphic.*;"+
				"import jpicedt.graphic.element.*;"+
				"import jpicedt.graphic.toolkit.*;"+
				"preferences=JPicEdt.getPreferences();" +
				"clipboard=JPicEdt.getClipboard();" +
				"mdimgr=JPicEdt.getMDIManager();" +
				"board(){return JPicEdt.getActiveDrawingBoard();}" +
				"canvas(){return JPicEdt.getActiveCanvas();}" +
				"drawing(){return JPicEdt.getActiveDrawing();}" +
				"editorkit(){return JPicEdt.getActiveEditorKit();}"+
				"help = \"Essential BSH functions and variables:\\n" +
					"- board(): return the active \'PEDrawingBoard\' object\\n" +
					"- canvas(): return the active \'PECanvas\' object\\n" +
					"- drawing(): return the active \'Drawing\' object\\n" +
					"- editorkit(): return the active \'EditorKit\' object\\n" +
					"- clipboard: return a reference to the clipboard\\n" +
					"- mdimgr: return a reference to the current \'MDIManager\' object\\n" +
					"- preferences: return a reference to the \'Properties\' object that holds user preferences across the entire application\\n" +
					"- help(): print this help\\n" +
					"\";" +
				"help(){print(help);}" +
				"help();"; // print help to begin with


	public static void _main(String[] args){
		JFrame f = new JFrame();
		JButton b = new JButton("console");
		f.getContentPane().add(b);
		b.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent ae){
				BeanShell bsh = new BeanShell(null);
				//Thread t = new Thread(bsh);t.start();
		}});
		f.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.out.println("Exiting...");System.exit(0);}});
		f.pack();
		f.setVisible(true);
	}

	/**
	 * Open a new BSH console, and start the BSH interpreter interactively in it.
	 */
	public BeanShell(MDIManager mdimgr){
		this.mdimgr = mdimgr;


		//System.out.println("Creating console...");
		JConsole console = new JConsole();

		if (mdimgr != null){
			frame = mdimgr.createMDIComponent(console);
			mdimgr.addMDIComponent(frame);
		}
		else
			frame = new PEFrame(console);
		frame.setTitle("BeanShell Console");
		frame.setSize(new Dimension(600,400));

		//debug("Creating interpreter...");
		interpreter = new Interpreter(console);
		//debug("Setting predefined variables and methods...");

		try {
			interpreter.eval(BSH_ALIASES);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}

		//debug("Show !");
		frame.setVisible(true);
		//debug("Done !");

		//SwingUtilities.invokeLater(this); // [pending] But why the hell does it block ????
		Thread t = new Thread(this);t.start();
	}

	/**
	 * Run the BSH interpreter in interactive mode.
	 */
	public void run(){
		//System.out.println("Starting interpreter...");
		interpreter.run(); // run interactively
	}

	/////////////////////// STATIC ///////////////////////////
	/**
	 * Run a BSH script with the given path.
	 * @param scriptPath absolute script path
	 * @param scriptName script name, for instance "Repeat copy" ; used mainly for undo/redo menus.
	 */
	public static void runScript(String scriptPath, String scriptName){
		if (scriptPath == null) return;
		//System.out.println("Running script: " + scriptPath);
		Interpreter interpreter = new Interpreter(); // no console
		try {
			interpreter.eval(BSH_ALIASES);
			// start new UndoableEdit:
			jpicedt.graphic.PECanvas canvas = jpicedt.JPicEdt.getActiveCanvas();
			if (canvas != null) canvas.beginUndoableUpdate("Script: " + scriptName);
			interpreter.source(scriptPath);
			if (canvas != null) canvas.endUndoableUpdate();
		}
		catch ( TargetError te ) { // The script threw an exception
			JOptionPane.showMessageDialog(null,
				"BeanShell script threw exception:"
				+ "\n\"" + te.getTarget() + "\""
				+ "\nat line " + te.getErrorLineNumber()
				+ " in file [" + te.getErrorSourceFile() + "]",
				"BeanShell script",
				JOptionPane.ERROR_MESSAGE);
		}
		catch ( EvalError ee ) { // General Error evaluating script
			JOptionPane.showMessageDialog(null,
				"BeanShell script evaluation error:"
				+ "\n\"" + ee.getErrorText() + "\""
				+ "\nat line " + ee.getErrorLineNumber()
				+ " in file [" + ee.getErrorSourceFile() + "]",
				"BeanShell script",
				JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex){ // Other exception
			JOptionPane.showMessageDialog(null,
				"BeanShell script:\n" + ex.getMessage(),
				"BeanShell script",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void main(String[] args){
		JMenu menu = createMenu();
		for (int i=0; i<menu.getMenuComponentCount(); i++){
			System.out.println("item " + i + "=" + menu.getMenuComponent(i));
			System.out.println();
		}
	}

	/**
	 * Return a JMenu containing a hierarchy of JMenu's and JMenuItem's built from
	 * the BSH scripts contained, first in the installation script directory, then
	 * in the user script directory.
	 * @return an empty JMenu if no BSH scripts were found in either directory.
	 */
	public static JMenu createMenu(){
		// 1°) scan install script directory:
		String jpicedtHome = jpicedt.MiscUtilities.getJPicEdtHome();
		JMenu installScriptsMenu = null;
		if (jpicedtHome!=null) installScriptsMenu=createMenu(new File(jpicedtHome,"macros"));
		// 2°) scan user script directory:
		JMenu userScriptsMenu = null;
		if (JPicEdt.getUserSettingsDirectory() != null)
			userScriptsMenu = createMenu(new File(JPicEdt.getUserSettingsDirectory(),"macros"));
		// build array:
		JMenu menu = new JMenu("Macros");
		if (installScriptsMenu != null) {
			Component[] cc = installScriptsMenu.getMenuComponents();
			for(int i=0; i<cc.length; i++){
				menu.add(cc[i]);
			}
		}
		menu.add(new JSeparator());
		if (userScriptsMenu != null) {
			Component[] cc = userScriptsMenu.getMenuComponents();
			for(int i=0; i<cc.length; i++){
				menu.add(cc[i]);
			}
		}
		return menu;
	}

	/**
	 * Return a JMenu containing a hierarchy of JMenu's and JMenuItem's built from
	 * the BSH scripts contained in the given directory and its children.
	 * @param directory path where to look for .bsh scripts, or subdirectories.
	 * @return null if directory (or one of its children) doesn't exist, or contains no BSH script.
	 */
	private static JMenu createMenu(File directory){
		if (!directory.exists()) return null;
		String[] dirContent = directory.list();
		if (dirContent==null) return null; // directory is empty
		Arrays.sort(dirContent);
		// [pending] sort dir content
		JMenu menu = null; // so that we return null if nothing gets added...
		for (int i=0; i<dirContent.length; i++){
			String fileName = dirContent[i];
			File file = new File(directory, fileName); // create full path name
			// 1°) file is a BSH script:
			if (fileName.toLowerCase().endsWith(".bsh")){
				String label = fileName.substring(0,fileName.indexOf(".bsh")).replace('_',' ');
				JMenuItem menuItem = new JMenuItem(label);
				menuItem.addActionListener(new MenuScriptsAA(file.getPath(), label));
				// [pending] add CTRL+? shortcut
				if (menu==null) menu = new JMenu(directory.getName());
				menu.add(menuItem);
			}
			// 2°) file is a subdirectory:
			else if (file.isDirectory()){
				JMenu subMenu = createMenu(file);
				if (subMenu != null) {
					if (menu==null) menu = new JMenu(directory.getName());
					menu.add(subMenu);
				}
			}
		}
		return menu;
	}

	/**
	 * ActionAdapter for Scripts menuitems.
	 */
	private static class MenuScriptsAA implements ActionListener{

		String scriptPath;
		String scriptName;
		MenuScriptsAA(String scriptPath, String scriptName) {
			this.scriptPath = scriptPath;
			this.scriptName = scriptName;
		}
		public void actionPerformed(ActionEvent e) {
			BeanShell.runScript(scriptPath, scriptName);
		}
	}

}
