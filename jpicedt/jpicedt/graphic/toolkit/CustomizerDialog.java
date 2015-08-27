// CustomizerDialog.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
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
// Version: $Id: CustomizerDialog.java,v 1.15 2013/03/27 06:58:56 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import jpicedt.widgets.*;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JInternalFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import static jpicedt.Log.*;
import static jpicedt.Localizer.localize;

/**
 * A modal or non-modal dialog box that lays out an array of <code>AbstractCustomizer</code>'s inside a
 * <code>JPanel</code> or a <code>JTabbedPane</code>, depending on the number of customizers fed to the
 * constructor.  It further provides three default actions, namely : ok (store and dispose), cancel (marks as
 * cancelled), load default, reload.  Note that cancel() makes sense only if the hosting dialog is modal.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.2
 * @version $Id: CustomizerDialog.java,v 1.15 2013/03/27 06:58:56 vincentb1 Exp $
 */
 public class CustomizerDialog {

	public static enum ButtonMask { NONE, OK, CANCEL, DEFAULT, RELOAD, OK_CANCEL, ALL};

	private MDIComponent dialog; // hosting component

	// array of inner-panels
	private ArrayList<AbstractCustomizer> customizers;

	// UI buttons
	private JButton buttonOk, buttonCancel, buttonDefault, buttonReload;

	// user pressed cancel ?
	private boolean cancelled = false;

	// keyListener for ESC and ENTER keys handling
	private KeyHandler keyHandler;

	private boolean okButtonClosesDialog = false;

	/**
	 * Creates a new dialog box from the given array of <code>AbstractCustomizer</code>'s, laying them out in
	 * a <code>JTabbedPane</code>.<br>
	 * By default, clicking the OK button does not close this dialog box, this must be set
	 * separately by invoking {@link #setOkButtonClosesDialog setOkButtonClosesDialog}.
	 * @param title the JDialog title ; if null, we use the first customizer title.
	 * @param selected index of the selected customizer on start-up
	 * @param buttonsMask buttons to be displayed : a mask computed from predefinite masks OR'd together
	 * @param host the hosting component
	 */
	public CustomizerDialog(MDIComponent host, ArrayList<AbstractCustomizer> customizers, int selected,String title, EnumSet<ButtonMask> buttonsMask) {
		this.dialog = host;
		this.customizers = customizers;
		if (title == null){
			dialog.setTitle(customizers.get(0).getTitle());
		}
		else dialog.setTitle(title);
		preInit(buttonsMask); // add buttons

		/* root tabbed pane -> addTab(title,icon,component,tip)) */
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addChangeListener(new ChangeHandler());
		for (AbstractCustomizer customizer: customizers){
			if (customizers!=null)
				tabbedPane.addTab(customizer.getTitle(),customizer.getIcon(),customizer,customizer.getTooltip());
		}
		dialog.getContentPane().add(tabbedPane,BorderLayout.CENTER);
		for (AbstractCustomizer customizer: customizers){
			customizer.load();
			// [pending] should be done after setVisible(true)
			try {
				customizer.load();
			}
			catch(NumberFormatException ex){
				if (DEBUG) ex.printStackTrace();
				//JOptionPane.showMessageDialog(null, localize("NumberFormatError") + " : " + ex.getMessage() + ", " + customizer.getTitle(), localize("Preferences"),JOptionPane.ERROR_MESSAGE);
				System.err.println(ex.toString());
			}
		}
		initGeometry(); // size and location on screen
		if (selected < 0) selected = 0;
		if (selected > customizers.size()-1) selected = customizers.size()-1;
		tabbedPane.setSelectedIndex(selected);
	}

	/**
	 * Builds a new dialog box from a single customizer.<br>
	 * By default, clicking the OK button does not close this dialog box, this must be set
	 * separately by invoking {@link #setOkButtonClosesDialog setOkButtonClosesDialog}.
	 * @param buttonsMask buttons to be displayed : a mask computed from predefinite masks OR'd together
	 * @param host the hosting component; a JDialog if null
	 */
	public CustomizerDialog(MDIComponent host, AbstractCustomizer customizer, EnumSet<ButtonMask> buttonsMask) {
		this.dialog = host;
		customizers = new ArrayList<AbstractCustomizer>();
		customizers.add(customizer);
		dialog.setTitle(customizer.getTitle());
		preInit(buttonsMask);
		dialog.getContentPane().add(customizer,BorderLayout.CENTER);
		customizer.load();
		initGeometry();
	}

	/**
	 * @return true if the user pressed the Cancel action
	 */
	public boolean isCancelled(){
		return cancelled;
	}

	/**
	 * If this dialog was constructed with more than one inner-pane, this
	 * methods is invoked when a new pane gets activated.
	 */
	public void paneActivated(AbstractCustomizer c){
		c.activated();
	}

	/**
	 * Whether clicking the OK button, beside storing widgets content, also closes this dialog or not.
	 * It is valid to call this method at any time, e.g. to change behaviour once this dialog has
	 * been made visible.
	 */
	public void setOkButtonClosesDialog(boolean b){
		okButtonClosesDialog = b;
	}

	/** simply delegates to hosting component */
	public void setVisible(boolean b){
		dialog.setVisible(b);
	}

	/** simply delegates to hosting component */
	public void dispose(){
		dialog.dispose();
	}

	//////////////////////// PRIVATE MEMBERS /////////////////////////

	/**
	 * JDialog's attributes initialization.
	 */
	private void preInit(EnumSet<ButtonMask> buttonsMask){
		dialog.setResizable(true); // pb with pack() on some WindowMaker releases
		//dialog.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		dialog.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
		dialog.getContentPane().setLayout(new BorderLayout(5,5)); //hgap, vgap
		dialog.getContentPane().add(createButtonPanel(buttonsMask),BorderLayout.SOUTH);
		addListeners();
	}

	/**
	 * JDialog's geometry initialization
	 */
	private void initGeometry(){
		//dialog.setSize(dialog.getPreferredSize()); // pb with pack() not working on some Linux/WindowMakers releases
		dialog.pack();
		Dimension rootSize;
		if (dialog instanceof JInternalFrame)
			rootSize = ((JInternalFrame)dialog).getDesktopPane().getSize();
		else
			rootSize = Toolkit.getDefaultToolkit().getScreenSize();

		Dimension frameSize = dialog.getSize();
		if (frameSize.height > rootSize.height)
			frameSize.height = rootSize.height;
		if (frameSize.width > rootSize.width)
			frameSize.width = rootSize.width;
		dialog.setLocation((rootSize.width - frameSize.width) / 2, (rootSize.height - frameSize.height) / 2);
		//setVisible(true);
	}

	/**
	 * Register interested listeners
	 */
	private void addListeners(){
		dialog.addKeyListener(keyHandler=new KeyHandler());

		// cancel() on close
		if (dialog instanceof Window)
			((Window)dialog).addWindowListener(new WindowHandler());
		else if (dialog instanceof JInternalFrame)
			((JInternalFrame)dialog).addInternalFrameListener(new InternalFrameHandler());

		((Container)dialog.getLayeredPane()).addContainerListener(new ContainerHandler()); // needs keyHandler !
		dialog.getContentPane().addContainerListener(new ContainerHandler());
	}

	/**
	 * Creates default buttons according to the given buttonsMask
	 */
	private JPanel createButtonPanel(EnumSet<ButtonMask> buttonsMask){

		JPanel panelButtons = new JPanel(); //row, col, hgap, vgap
		panelButtons.setBorder(BorderFactory.createEtchedBorder());
		ActionListener actionHandler = new ActionHandler();
		if (buttonsMask.contains(ButtonMask.OK) || buttonsMask.contains(ButtonMask.ALL) || buttonsMask.contains(ButtonMask.OK_CANCEL)){
			buttonOk = new JButton();
			buttonOk.setText(localize("button.OK"));
			buttonOk.addActionListener(actionHandler);
			panelButtons.add(buttonOk);
		}
		if (buttonsMask.contains(ButtonMask.CANCEL) || buttonsMask.contains(ButtonMask.ALL) || buttonsMask.contains(ButtonMask.OK_CANCEL)){
			buttonCancel = new JButton();
			buttonCancel.setText(localize("button.Cancel"));
			buttonCancel.addActionListener(actionHandler);
			panelButtons.add(buttonCancel);
		}
		if (buttonsMask.contains(ButtonMask.DEFAULT) || buttonsMask.contains(ButtonMask.ALL)){
			buttonDefault = new JButton();
			buttonDefault.setText(localize("button.Default"));
			buttonDefault.addActionListener(actionHandler);
			panelButtons.add(buttonDefault);
		}
		if (buttonsMask.contains(ButtonMask.RELOAD) || buttonsMask.contains(ButtonMask.ALL)){
			buttonReload = new JButton();
			buttonReload.setText(localize("button.Reload"));
			buttonReload.addActionListener(actionHandler);
			panelButtons.add(buttonReload);
		}
		return panelButtons;
	}


	/**
	 * Helper for event handlers ; code to be executed after a click on the "ok" button occured,
	 * or whatever similar. Validate user entries and store them.
	 */
	private void ok(){
		for (AbstractCustomizer customizer: customizers){
			customizer.store();
		}
		dialog.dispose();
	}

	/**
	 * Helper for event handlers ; code to be executed after a click on the "cancel" button occured,
	 * or whatever similar. Close dialog w/o saving
	 */
	private void cancel(){
		cancelled = true;
		dialog.dispose();
	}

	/**
	 * Helper for event handlers ; code to be executed after a click on the "load" button occured,
	 * or whatever similar. Load default values
	 */
	private void loadDefault(){
		for (AbstractCustomizer customizer: customizers)
			customizer.loadDefault();
	}

	/**
	 * Helper for event handlers ; code to be executed after a click on the "reload" button occured,
	 * or whatever similar. Reload widgets with previously stored preferences
	 */
	private void reload(){
		for (AbstractCustomizer customizer: customizers){
			customizer.load();
			try {
				customizer.load();
			}
			catch(NumberFormatException ex){
				//JOptionPane.showMessageDialog(null, localize("NumberFormatError") + " : " + ex.getMessage() + ", " + customizer.getTitle(), localize("Preferences"), JOptionPane.ERROR_MESSAGE);
				System.err.println(ex.toString());
			}
		}
	}

	//////////////////////////////////////// EVENT HANDLERS ///////////////////

	/** windowListener */
	class WindowHandler extends WindowAdapter {
		public void windowClosing(WindowEvent evt){
			cancel();
		}
	}

	/** internalFrameListener */
	class InternalFrameHandler extends InternalFrameAdapter	{

		public void internalFrameClosing(InternalFrameEvent evt){
			cancel();
			//cancelled = true; // bug fix: cancel() calls dispose(), which mess up with the closing process
		}
	}

	/** actionListener for JButton's */
	class ActionHandler implements ActionListener {

		/**
		 * Handles button related actions : store pref. to file, load default values,...
		 * This method is public as an implementation side-effet.
		 */
		public void actionPerformed(ActionEvent e) {

			if (e.getSource()==buttonOk)
				ok();
			else if (e.getSource()==buttonCancel)
				cancel();
			else if (e.getSource()==buttonDefault)
				loadDefault();
			else if (e.getSource()==buttonReload)
				reload();
		}
	}

	/** keyListener for ESC and ENTER keys ; adapted from jEdit */
	class KeyHandler extends KeyAdapter {

		public void keyPressed(KeyEvent evt){
			//debug(evt.toString());
			if(evt.isConsumed()) return;
			// new *************************** begin (by ss & bp)
			// workaround for the new textarea-window

			//  if(evt.getKeyCode() == KeyEvent.VK_ENTER && okButtonClosesDialog){
				// crusty workaround so that ENTER doesn't close dialog when current focus owner
				// is a JComboBox or a child thereof
			//	    Component comp = dialog.getFocusOwner();
			//	    while(comp != null){
			//		    //debug("comp="+comp);
			//		    if(comp instanceof JComboBox){
			//			    JComboBox combo = (JComboBox)comp;
			//			    if(combo.isEditable()){
			//				    Object selected = combo.getEditor().getItem();
			//				    if(selected != null)
			//					    combo.setSelectedItem(selected);
			//			    }
			//			    break;
			//		    }
			//		    comp = comp.getParent();
			//	    }

            //	    ok();
            //	    evt.consume();
            //  }
			/*else*/
            if(evt.getKeyCode() == KeyEvent.VK_ESCAPE){
				cancel();
				evt.consume();
			}
			// new *************************** end (by ss & bp)
		}
	}

	/** stateChanged listener for JTabbedPane related event */
	class ChangeHandler implements ChangeListener {

		/** this method is public as an implementation side-effect */
		public void stateChanged(ChangeEvent e){
			JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
			AbstractCustomizer c = (AbstractCustomizer)tabbedPane.getSelectedComponent();
			paneActivated(c);
		}
	}

	// Recursively adds our key listener to sub-components
	// code snippet adapted from jEdit
	class ContainerHandler extends ContainerAdapter
	{
		public void componentAdded(ContainerEvent evt){
			componentAdded(evt.getChild());
		}

		public void componentRemoved(ContainerEvent evt){
			componentRemoved(evt.getChild());
		}

		private void componentAdded(Component comp){
			comp.addKeyListener(keyHandler);
			if(comp instanceof Container){
				Container cont = (Container)comp;
				cont.addContainerListener(this);
				Component[] comps = cont.getComponents();
				for(int i = 0; i < comps.length; i++){
					componentAdded(comps[i]);
				}
			}
		}

		private void componentRemoved(Component comp){
			comp.removeKeyListener(keyHandler);
			if(comp instanceof Container){
				Container cont = (Container)comp;
				cont.removeContainerListener(this);
				Component[] comps = cont.getComponents();
				for(int i = 0; i < comps.length; i++){
					componentRemoved(comps[i]);
				}
			}
		}
	}

}
