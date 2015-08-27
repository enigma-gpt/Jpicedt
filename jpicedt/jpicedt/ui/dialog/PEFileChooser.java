// PEFileChooser.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006  Sylvain Reynal
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
// Version: $Id: PEFileChooser.java,v 1.14 2013/06/15 09:57:10 vincentb1 Exp $
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

import jpicedt.ui.*;
import jpicedt.Localizer;
import jpicedt.graphic.ContentType;

import javax.swing.*;
import java.io.*;
import java.util.*;
import static jpicedt.Localizer.*;

/**
 * A factory that produces and caches frequently accessed JFileChooser's.<p>
 * JFileChooser's get instanciated on first call,
 * and are then cached for better efficiency. As a side-effect, the last accessed directory is
 * kept in memory, unless a call to <code>configure(Properties)</code> is made, which reinit the
 * file chooser's.
 */
public class PEFileChooser extends JFileChooser { // [pending] change name to FileChooserFactory and move to jpicedt.ui.util

	/** properties's key for open-file-directory */
	public static final String KEY_FILE_DIRECTORY = "directory.files";
	/** file chooser type for saving files */
	public static final int SAVEFILE = 0; // [pending] change to SAVE_FILE
	/** file chooser type for saving fragments */
	public static final int SAVEFRAGMENT = 1;
	/** file chooser type for opening files */
	public static final int OPENFILE = 2;
	/** file chooser type for opening fragments */
	public static final int OPENFRAGMENT = 3;
	/** file chooser type for browsing the save/open file directory */
	public static final int BROWSEFILEDIR = 4;
	/** the user's home directory */
    public static final String DEFAULT_DIRECTORY = System.getProperty("user.home");

	/////////////////////////////////////////////////////////////////////////////////

	private static String fileDirectory = DEFAULT_DIRECTORY;
    private static String fragmentDirectory = jpicedt.JPicEdt.getUserSettingsDirectory()+File.separator+"fragments";
	private static PEFileChooser saveFile,saveFragment,openFile,openFragment; // cache for frequently accessed choosers
	private PEFileFilter[] filters; // one for each available content-type (see MiscUtilities.getAvailableContentTypes)

	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * Opens (and possibly inits on first call) a JFileChooser, the type of which is specified by one of
	 * the hereabove constants. If this is the first time a particular file chooser is accessed,
	 * it's created using, either the default settings, or the settings set by the last call to
	 * <code>configure(Properties)</code>.
	 * @param chooserType allows to initialize the file chooser to be opened.
	 * @return the selected file ; null if a cancel action occured at any time.
	 */
	public static String getFileName(int chooserType, String contentTypeClassName){

		switch (chooserType) {

		case SAVEFILE:
			if (saveFile == null)
				saveFile = new PEFileChooser(fileDirectory, localize("action.ui.SaveAs"));
			else saveFile.rescanCurrentDirectory();
			saveFile.setFileFilter(contentTypeClassName);
			return saveFile.saveToFileChooser(saveFile);

		case SAVEFRAGMENT :
			if (saveFragment == null)
				saveFragment = new PEFileChooser(fragmentDirectory, localize("action.ui.SaveSelectionAs"));
			else saveFragment.rescanCurrentDirectory();
			saveFragment.setFileFilter(contentTypeClassName);
			return saveFragment.saveToFileChooser(saveFragment);

		case OPENFILE :
			if (openFile == null)
				openFile = new PEFileChooser(fileDirectory, localize("action.ui.Open"));
			else openFile.rescanCurrentDirectory();
			openFile.setFileFilter(contentTypeClassName);
			if(openFile.showOpenDialog(null) == CANCEL_OPTION) return null;
			return openFile.getSelectedFile().toString();

		case OPENFRAGMENT :
			if (openFragment == null)
				openFragment = new PEFileChooser(fragmentDirectory, localize("action.ui.InsertFragment"));
			else openFragment.rescanCurrentDirectory();
			openFragment.setFileFilter(contentTypeClassName);
			if(openFragment.showOpenDialog(null) == CANCEL_OPTION) return null;
			return openFragment.getSelectedFile().toString();

		case BROWSEFILEDIR :
			PEFileChooser browseFileDir = new PEFileChooser(fileDirectory, localize("preferences.DefaultSaveDirectory"));
			browseFileDir.setFileSelectionMode(DIRECTORIES_ONLY);
			if (browseFileDir.showDialog(null, localize("misc.SelectDirectory")) == CANCEL_OPTION) return null;
			return browseFileDir.getSelectedFile().toString(); // [pending] save to properties file...

		default :
			throw new IndexOutOfBoundsException(Integer.toString(chooserType));
		}
	}

	/**
	 * Private constructor (prevents instanciation from the outside)
	 * Instanciates a new FileChooser with the given title and the given directory,
	 * and attaches it the adequate file filter (e.g. ".pic" & ".tex")
	 */
	private PEFileChooser(String directory, String title){

		super(directory);
		setDialogTitle(title);
		// 	setApproveButtonText("text"); //[pending] (syd) Don't seem to work ! Besides, there's no way to localize the Cancel button properties :-(((
		// 	setApproveButtonToolTipText("tool tip");
		// 	setApproveButtonMnemonic('a');

		String[] contentTypeNames = jpicedt.MiscUtilities.getAvailableContentTypesNames();
		String[][] contentTypeFileExtensions = jpicedt.MiscUtilities.getAvailableContentTypesFileExtensions();
		filters = new PEFileFilter[contentTypeNames.length];

		for (int i=0; i<contentTypeNames.length; i++){
			filters[i] = new PEFileFilter();
			String[] extensions = contentTypeFileExtensions[i];
			for (int j=0; j<extensions.length; j++){
				filters[i].addExtension(extensions[j]);
			}
			filters[i].setDescription(contentTypeNames[i]);
			addChoosableFileFilter(filters[i]);
		}

		addChoosableFileFilter(getAcceptAllFileFilter());
		//setFileFilter(filter); // now done from inside getFileName() according to given ContentType
	}

	private void setFileFilter(String contentTypeClassName){
		if (contentTypeClassName==null){ // do nothing
		}
		else {
			int idx = jpicedt.MiscUtilities.getContentTypeIndex(contentTypeClassName);
			setFileFilter(filters[idx]);
		}
	}

	/**
	 * Open a saveDialog box using the given FileChooser
	 * @return the selected file ; if it already exists,
	 * ask user for confirmation ; return null if a CANCEL action did occur at any time.
	 */
	private String saveToFileChooser(PEFileChooser fc){
		File file;
		do {
			if (fc.showSaveDialog(null) == CANCEL_OPTION) return null;
			if (!(file = fc.getSelectedFile()).exists()) return file.toString();
			if (jpicedt.JPicEdt.getMDIManager().showConfirmDialog(
			                                  localize("exception.FileAlreadyExists"),
			                                  getDialogTitle(),
			                                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
				return file.toString();
		} while (true);
	}

	/**
	 * Configure the default directory for the static JFileChooser's
	 * from the given Properties object, using the following key/value pairs :<br>
	 * <ul>
	 * <li> key = KEY_FILE_DIRECTORY, value = path-to-a-directory-for-opening-new-files
	 * </ul>
	 * Then reinits the static JFileChooser so that they'll reflect the new preferences
	 * next time they're called.<p>
	 * Should be called on start-up, then each time preferences change.
	 */
	public static void configure(Properties preferences){
		fileDirectory = preferences.getProperty(KEY_FILE_DIRECTORY,DEFAULT_DIRECTORY);
		saveFile=saveFragment=openFile=openFragment=null;
	}

	/**
	 * Store the current file and fragment directories to the given Properties object
	 */
	public static void store(Properties preferences){
		preferences.setProperty(KEY_FILE_DIRECTORY, fileDirectory);
	}



}
