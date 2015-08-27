// HtmlViewer.java --- -*- coding: iso-8859-1 -*-
// March 3, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999/2006 Sylvain Reynal
// Copyright 2007/2012 Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: HtmlViewer.java,v 1.14 2013/03/27 06:50:21 vincentb1 Exp $
// Keywords: HTML
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

import jpicedt.widgets.*;
import jpicedt.ui.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.net.*;

import static jpicedt.Localizer.*;

/**
 * A frame for displaying HTML content (e.g. on-line help, license,&hellip;)
 *
 * @author Sylvain Reynal.
 * @since PicEdt 1.1.2
 * @version $Id: HtmlViewer.java,v 1.14 2013/03/27 06:50:21 vincentb1 Exp $
 *
 */
public class HtmlViewer implements HyperlinkListener{

	private MDIComponent frame;
	private MDIManager mdimgr;
	private JEditorPane textPane;
	private JLabel statusLbl;

	/**
	 * Teste l'accessibilité d'une resource.
	 * @param fileName nom d'une resource accessible depuis le classLoader courant.
	 */
	public static boolean fileExits(String fileName){
		return HtmlViewer.class.getResource(fileName) != null;
	}

	/**
	 * Construct a new HTML Window with the given file and title
	 * @param fileName name of resource accessible from the current classloader
	 * @param mdimgr frame provider
	 */
	public HtmlViewer(String fileName, String title, MDIManager mdimgr) {
		this(HtmlViewer.class.getResource(fileName),title, mdimgr);
	}
	/**
	 * Construct a new HTML Window with the given file and title
	 * @param mdimgr frame provider
	 */
	public HtmlViewer(URL contentUrl, String title, MDIManager mdimgr) {

		this.mdimgr = mdimgr;

		//// content pane ////

		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(5,5));

		textPane = new JEditorPane();
		textPane.setEditable(false);
		textPane.addHyperlinkListener(this);

		// sizing
		JScrollPane scroller = new JScrollPane(textPane);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		scroller.setPreferredSize(new Dimension(screenSize.width/2, screenSize.height/2));
		contentPane.add(scroller,BorderLayout.CENTER);

		// status bar
		statusLbl = new JLabel("Status...");
		contentPane.add(statusLbl,BorderLayout.SOUTH);

		// hosting component:
		if (mdimgr==null)
			frame = new PEFrame(title,contentPane);
		else {
			frame = mdimgr.createMDIComponent(contentPane);
			frame.setTitle(title);
			mdimgr.addMDIComponent(frame);
		}
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		//enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		//pack();
		frame.setSize(frame.getPreferredSize());
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.setVisible(true);

		// install HTML page
		try {
			statusLbl.setText("Loading " + contentUrl + "...");
			textPane.setPage(contentUrl);
			statusLbl.setText(contentUrl.toString());
		}
		catch (Exception ex){
			ex.printStackTrace();
			mdimgr.showMessageDialog(ex.toString(),
						//localize("exception.NetworkError"),
						localize("misc.PicEdtHelp"),
						JOptionPane.ERROR_MESSAGE);
			//textPane.setText(ex.toString());
			frame.dispose();
			return;
		}

	}

	/**
	 * called when a click occurs on a HTML hyperlink (aka &lt;a href=&hellip;&gt;)
	 */
	public void hyperlinkUpdate(HyperlinkEvent e) {

		//System.out.println(e);

		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {

			JEditorPane pane = (JEditorPane) e.getSource();
			//System.out.println(e.getDescription());

			// relative link :
			if (e instanceof HTMLFrameHyperlinkEvent && !e.getDescription().startsWith("http://")  && !e.getDescription().startsWith("../api-doc")) {
				HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
				HTMLDocument doc = (HTMLDocument)pane.getDocument();
				statusLbl.setText("Loading " + e.getURL().toString() + "...");
				doc.processHTMLFrameHyperlinkEvent(evt);
				statusLbl.setText(e.getURL().toString());
			}
			else {
				HtmlViewer hv = new HtmlViewer(e.getURL(),e.getURL().toString(),mdimgr);
				hv.frame.setLocation(frame.getLocation().x+30,frame.getLocation().y+30);
			}
		}
	}

	/** test */
	public static void main(String args[]){
		try {
			HtmlViewer hv = new HtmlViewer(new URL(args[0]),args[0],null);
		}
		catch (Exception ex){
			ex.printStackTrace();
		}
	}
} // class
