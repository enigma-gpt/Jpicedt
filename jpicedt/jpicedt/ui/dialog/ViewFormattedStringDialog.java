/*
 ViewFormattedStringDialog.java - December 31, 2001 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 D�partement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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
package jpicedt.ui.dialog;

import jpicedt.graphic.PECanvas;
import jpicedt.widgets.MDIComponent;
import jpicedt.widgets.PEFrame;
import jpicedt.ui.MDIManager;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import static jpicedt.Localizer.*;

/**
 * This class implements a dialog box that displays the LaTeX/PsTricks/... formatted string
 * and allows the user to edit, then (re)parse the text.
 * corresponding to a given object
 *
 * @since  jPicEdt 1.2.a
 * @author Sylvain Reynal
 * @version $Id: ViewFormattedStringDialog.java,v 1.12 2013/03/27 06:51:26 vincentb1 Exp $
 *
 */
public class ViewFormattedStringDialog implements ActionListener{

	private PECanvas canvas;
	private JTextArea ta;
	private JButton buttonOk, buttonReload, buttonParse;
	private MDIComponent frame;

	/**
	 * @param canvas canvas holding the drawing the encoding of which is the String to be displayed
	 * @since jPicEdt 1.2.a
	 */
	public ViewFormattedStringDialog(PECanvas canvas, MDIManager mdimgr) {
		this.canvas = canvas;

		JPanel panelString = new JPanel();
		panelString.setBorder(BorderFactory.createEtchedBorder());

		ta = new JTextArea( 40, 60 );
		ta.setEditable(true);
		load();

		JScrollPane scrollString = new JScrollPane(ta);
		//panelString.add(scrollString);

		JPanel panelButtons = new JPanel();
		panelButtons.setBorder(BorderFactory.createEtchedBorder());

		buttonOk = new JButton(localize("button.OK"));
		buttonOk.setDefaultCapable(true);
		buttonOk.addActionListener(this);
		panelButtons.add(buttonOk);

		buttonReload = new JButton(localize("button.Reload"));
		buttonReload.addActionListener(this);
		panelButtons.add(buttonReload);

		buttonParse = new JButton(localize("preferences.UpdateDrawing"));
		buttonParse.addActionListener(this);
		panelButtons.add(buttonParse);

		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollString,BorderLayout.CENTER);
		contentPane.add(panelButtons,BorderLayout.SOUTH);

		// frame:
		if (mdimgr==null)
			frame = new PEFrame(contentPane);
		else {
			frame = mdimgr.createMDIComponent(contentPane);
			mdimgr.addMDIComponent(frame);
		}
		frame.setTitle(localize("preferences.LatexString"));
		frame.setResizable(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.getRootPane().setDefaultButton(buttonOk);
		frame.pack();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
			frameSize.height = screenSize.height;
		if (frameSize.width > screenSize.width)
			frameSize.width = screenSize.width;
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

		frame.setVisible(true);

	}

	/**
	 * Reload the content of the TextArea with the formatted representation of the Drawing this Dialog
	 * is attached to.
	 */
	private void load(){
		StringWriter sw = new StringWriter();
		try {
			canvas.write(sw,false); // write whole drawing
			ta.setText(sw.toString());
		}
		catch (IOException ioex){ioex.printStackTrace();}
	}

	/**
	 * Reload the Drawing this dialog is attached to, from the content of the TextArea.
	 */
	private void parse(){
		try {
			StringReader sr = new StringReader(ta.getText());
			canvas.read(sr, jpicedt.MiscUtilities.createParser());
		}
		catch (jpicedt.graphic.io.parser.ParserException pEx){
			jpicedt.JPicEdt.getMDIManager().showMessageDialog(
				localize("exception.ParserWarning") + " :\n" + pEx,
				localize("action.editorkit.Paste"),
				JOptionPane.ERROR_MESSAGE);
		}
	}


	/**
	 * Dispose this dialog box when "ok" is pressed.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==buttonOk) frame.dispose();
		else if (e.getSource()==buttonReload) load();
		else if (e.getSource()==buttonParse) parse();
	}

} // class
