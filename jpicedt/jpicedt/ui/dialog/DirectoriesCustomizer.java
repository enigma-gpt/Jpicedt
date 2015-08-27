// DirectoriesCustomizer.java --- -*- coding: iso-8859-1 -*-
// December 31, 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999-2006 Sylvain Reynal
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
// Version: $Id: DirectoriesCustomizer.java,v 1.13 2013/03/27 06:52:41 vincentb1 Exp $
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
package jpicedt.ui.dialog;

import jpicedt.JPicEdt;
import jpicedt.Localizer;
import jpicedt.graphic.toolkit.AbstractCustomizer;
import jpicedt.ui.dialog.PEFileChooser; // needed by directories browsers
import jpicedt.ui.util.BeanShell;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import static jpicedt.Localizer.*;

/**
 * A panel for editing default directories, e.g. for saving/loading pictures, fragments or running scripts.
 * @author Sylvain Reynal
 * @since jPicEdt 1.3
 * @version $Id: DirectoriesCustomizer.java,v 1.13 2013/03/27 06:52:41 vincentb1 Exp $
 */
public class DirectoriesCustomizer extends AbstractCustomizer implements ActionListener {

	private JTextField fileDirectoryTF;
	private JButton buttonDirectoryBrowse;

	private JTextField tmpDirectoryTF;
	private JButton buttonTmpdirBrowse;

	private Properties preferences;

	/**
	 * Construct a new panel for editing directories.
	 */
	public DirectoriesCustomizer(Properties preferences){

		this.preferences  = preferences;

		setBorder(BorderFactory.createTitledBorder(
			BorderFactory.createEtchedBorder(), localize("preferences.Directories")));

		Box box = new Box(BoxLayout.Y_AXIS);
		JPanel p;
		JLabel l;

		/* Sub-Panel 1 : open-file default dir */
		p = new JPanel(); // horizontal flowlayout
		p.add(l=new JLabel(" "+localize("preferences.OpenFileDirectory.label")+" :"));
			l.setToolTipText(localize("preferences.OpenFileDirectory.tooltip"));
		p.add(buttonDirectoryBrowse= new JButton(localize("button.Browse")));
			buttonDirectoryBrowse.addActionListener(this);
		box.add(p);

		box.add(fileDirectoryTF=new JTextField(20));

		/* Sub-Panel 2 : user tmp dir */
		p = new JPanel();
		p.add(l=new JLabel(" "+localize("preferences.TmpDirectory.label")+" :"));
			l.setToolTipText(localize("preferences.TmpDirectory.tooltip"));
		p.add(buttonTmpdirBrowse= new JButton(localize("button.Browse")));
			buttonTmpdirBrowse.addActionListener(this);
		box.add(p);

		box.add(tmpDirectoryTF=new JTextField(20));

		//////////////////
		add(box, BorderLayout.NORTH);

	}

	/**
	 * Load widgets display content with default values fetched from the PEFileChooser class.
	 */
	public void loadDefault() {
		fileDirectoryTF.setText(PEFileChooser.DEFAULT_DIRECTORY);
		tmpDirectoryTF.setText(JPicEdt.DEFAULT_TMPDIR);
	}

	/**
	 * Load widgets value from the Options class.
	 */
	public void load() {
		fileDirectoryTF.setText(preferences.getProperty(PEFileChooser.KEY_FILE_DIRECTORY,PEFileChooser.DEFAULT_DIRECTORY));
		tmpDirectoryTF.setText(JPicEdt.getTmpDir().getPath());
	}

	/**
	 * Store current widgets value, presumably to the Properties object given in the constructor
	 */
	 public void store(){
		preferences.setProperty(PEFileChooser.KEY_FILE_DIRECTORY, fileDirectoryTF.getText());
		JPicEdt.setTmpDir(tmpDirectoryTF.getText());
	}

	/**
	 * @return the panel title, used e.g. for Border or Tabpane title.
	 */
	public String getTitle(){
		return localize("preferences.Directories");
	}


	/**
	 * @return the Icon associated with this panel, used e.g. for TabbedPane decoration
	 */
	public Icon getIcon(){
		return null;
	}


	/**
	 * @return the tooltip string associated with this panel
	 */
	public String getTooltip(){
		return localize("preferences.DefaultDirectories");
	}

	/**
	 * Open a directory browser when an action occurs on the "Browse" button.
	 */
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == buttonDirectoryBrowse) {
			String s = PEFileChooser.getFileName(PEFileChooser.BROWSEFILEDIR,null);
			if (s!= null) fileDirectoryTF.setText(s);
		}
		else if (e.getSource() == buttonTmpdirBrowse)  {
			JFileChooser fileChooser = new JFileChooser(tmpDirectoryTF.getText());
			fileChooser.setDialogTitle(localize("preferences.TmpDirectory.label"));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showDialog(null, localize("misc.SelectDirectory")) == JFileChooser.CANCEL_OPTION) return;
			String s =  fileChooser.getSelectedFile().toString();
			if (s!= null) tmpDirectoryTF.setText(s);
		}
	}

} // class
