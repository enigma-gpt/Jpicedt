// ModalInternalFrame.java -*- coding: iso-8859-1 -*-
// April 15, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PEInternalDialog.java,v 1.7 2013/03/27 06:48:56 vincentb1 Exp $
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

package jpicedt.widgets;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.beans.*;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import static jpicedt.Log.*;
import static java.lang.System.*;

/**
 * Re-implementation of non-modal <code>JDialog</code> using <code>JInternalFrame</code>'s.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: PEInternalDialog.java,v 1.7 2013/03/27 06:48:56 vincentb1 Exp $
 * <p>
 *
 */
public class PEInternalDialog extends JInternalFrame implements MDIComponent {

	private boolean modal;
	private boolean modalStarted;
	private JDesktopPane desktop;
	private Component oldFocusOwner;
	private JInternalFrame oldSelectedInternalFrame;
	private FrameClosingHandler frameClosingHandler;

	/**
	 * Constructor to be used to wrap any <code>JComponent</code> in a modal internal frame.
	 * @param title the frame's title
	 * @param desktop used to compute the initial location of the internal frame wrt the hosting desktop; also
	 * added in the <code>MODAL_LAYER</code> of it
	 * @param modal whether this dialog is modal or not
	 * @param innerPane inner component contained in the modal internal frame
	 */
	public PEInternalDialog(JDesktopPane desktop, String title, boolean modal, JComponent innerPane) {

		super(title,true,true,true);        // resizable, closable, maximizable
		this.modal = modal;
		this.desktop = desktop;
		modalStarted=false;

		super.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		putClientProperty("JInternalFrame.frameType","optionDialog");

		// Add inner pane to this internal frame
		if (innerPane != null) {
			getContentPane().add(innerPane, BorderLayout.CENTER);
			pack();
		}


		desktop.add(this, JLayeredPane.MODAL_LAYER);
		initGeometry();
		desktop.validate();
		try {
			setSelected(true);
		} catch (java.beans.PropertyVetoException e) { }
	}

	private void initGeometry(){
		// Size frame
		Dimension size = getPreferredSize();
		Dimension rootSize = desktop.getSize();

		setBounds((rootSize.width - size.width) / 2, (rootSize.height - size.height) / 2,size.width, size.height);
	}


	private class FrameClosingHandler extends InternalFrameAdapter {

		public void internalFrameClosing(InternalFrameEvent e){
			JInternalFrame jif = e.getInternalFrame();
			//jif.setVisible(false);
			jif.dispose();
			//jif.setClosed(true);
		}
	}

	public void setDefaultCloseOperation(int operation){
		if (modal==false){
			super.setDefaultCloseOperation(operation);
			return;
		}

		switch (operation){
		case DO_NOTHING_ON_CLOSE:
		case HIDE_ON_CLOSE:
			if (frameClosingHandler!=null)
				removeInternalFrameListener(frameClosingHandler);
			break;

		case DISPOSE_ON_CLOSE:
			if (frameClosingHandler==null)
				frameClosingHandler = new FrameClosingHandler();
			addInternalFrameListener(frameClosingHandler); // reduces to DISPOSE_ON_CLOSE
			break;
		default:
		}
	}

	public void setModal(boolean b){
		this.modal = b;
	}

	public void dispose(){
		_stopModal();
		super.dispose(); // calls setVisible(false), then setSelected(false), finally fires a frame-closed event
	}

	public void setVisible(boolean vis){
		//debug("vis="+vis);
		if (modal == false){
			super.setVisible(vis);
			return;
		}

		// else stop/start modal behavior
		if (vis==false){// && isVisible()) {
			_stopModal();
			/*
			try {
				setClosed(true); // don't think it's necessary (setClosed ultimately calls setVisible(false)
		} catch (java.beans.PropertyVetoException e) { }
			*/
			super.setVisible(false);

			// re-select old frame
			//debug("Re-selecting old frame: " + oldSelectedInternalFrame);
			if (this.oldSelectedInternalFrame != null) {
				try {
					this.oldSelectedInternalFrame.setSelected(true);
				} catch (java.beans.PropertyVetoException e) { }
			}

			// re-focus old focus owner
			//debug("Re-focusing old focus owner: " + oldFocusOwner);
			if (this.oldFocusOwner != null && this.oldFocusOwner.isShowing()) {
				this.oldFocusOwner.requestFocus();
			}

		}
		else if (vis==true){
			// remember old focus owner and old selected frame:
			this.oldFocusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
			this.oldSelectedInternalFrame = desktop.getSelectedFrame(); // may be null

			super.setVisible(true);

			_startModal();
			// blocks until setVisible(false)...
		}

	}

	private void _stopModal(){
		if (!modal || !modalStarted) return;
		modalStarted=false;
		// Use reflection to get Container.stopLWModal().
		//debug("_stopModal");
		try {
			Object obj;
			obj = AccessController.doPrivileged (new ModalPrivilegedAction( Container.class, "stopLWModal"));
			if (obj != null) {
				((Method) obj).invoke(this);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void _startModal(){
		if (!modal || modalStarted) return;
		modalStarted=true;
		//debug("_startModal");
		// Use reflection to get Container.startLWModal.
		try {
			Object obj;
			obj = AccessController.doPrivileged(new ModalPrivilegedAction( Container.class, "startLWModal"));
			if (obj != null) {
				((Method)obj).invoke(this);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	  * Retrieves a method from the provided class and makes it accessible.
	  */
	private static class ModalPrivilegedAction implements PrivilegedAction {
		private Class clazz;
		private String methodName;

		public ModalPrivilegedAction(Class clazz, String methodName) {
			this.clazz = clazz;
			this.methodName = methodName;
		}

		public Object run() {
			Method method = null;
			try {
				method = clazz.getDeclaredMethod(methodName);
			} catch (NoSuchMethodException ex) {
			}
			if (method != null) {
				method.setAccessible(true);
			}
			return method;
		}
	}

	///////////////////////////////////////////////////////////////////////////////
	private static class CloseModal implements ActionListener {
		PEInternalDialog modal;
		public CloseModal(PEInternalDialog modal){
			this.modal = modal;
		}

		public void actionPerformed(ActionEvent e) {
			//modal.setVisible(false); // ok
			modal.dispose();
			//try { modal.setClosed(true);} catch (Exception ex){ex.printStackTrace();} // need fix
		}
	}

	private static class ShowModal implements ActionListener {
		JDesktopPane desktop;
		boolean isModal;
		public ShowModal(JDesktopPane desktop, boolean isModal){
			this.desktop = desktop;
			this.isModal = isModal;
		}

		public void actionPerformed(ActionEvent e) {

			// Manually construct an input popup
			JButton innerPane = new JButton("close");
			// Construct a message internal frame popup
			PEInternalDialog modal = new PEInternalDialog(desktop, "Really Modal", isModal, innerPane);
			innerPane.addActionListener(new CloseModal(modal));
			modal.setVisible(true); // block until ok/cancel
		}
	}
	/**
	 * test - code snippet from Java Developper Connection.
	 */
	public static void main(String args[]) {
		final JFrame frame = new JFrame("Modal Internal Frame");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final JDesktopPane desktop = new JDesktopPane();



		JInternalFrame internal;
		JButton button;

		// 1
		internal = new JInternalFrame("Opener",true,true,true,true);
		desktop.add(internal);

		button = new JButton("Open modal");
		button.addActionListener(new ShowModal(desktop,true));
		internal.add(button, BorderLayout.CENTER);
		button = new JButton("Open non-modal");
		button.addActionListener(new ShowModal(desktop,false));
		internal.add(button, BorderLayout.SOUTH);
		internal.add(new JTextField("text field"),BorderLayout.NORTH);

		internal.setBounds(25, 25, 200, 100);
		internal.setVisible(true);

		// 2
		internal = new JInternalFrame("Text",true,true,true,true);
		internal.add(new JTextField("          "),BorderLayout.CENTER);
		internal.setBounds(200, 100, 200, 100);
		internal.setVisible(true);
		desktop.add(internal);


		Container content = frame.getContentPane();
		content.add(desktop, BorderLayout.CENTER);
		frame.setSize(800, 500);
		frame.setVisible(true);
	}

}
