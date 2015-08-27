// PEFileFilter.java --- -*- coding: iso-8859-1 -*-
// jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006  Sylvain Reynal
// Copyright (C) 2007/2013  Sylvain Reynal, Vincent Belaïche
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
// Version: $Id: PEFileFilter.java,v 1.9 2013/06/16 18:44:47 vincentb1 Exp $
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

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.File;
import java.util.*;
import javax.swing.filechooser.*;

/**
 * a concrete implementation of FileFilter for LaTeX and rel. file extensions
 *
 * @author Sylvain Reynal
 * @since PicEdt 1.1
 */
public class PEFileFilter extends FileFilter {

	private static String TYPE_UNKNOWN = "Type Unknown";
	private static String HIDDEN_FILE = "Hidden File";

	private HashMap<String,PEFileFilter> filters = null;
	private String description = null;
	private String fullDescription = null;
	private boolean useExtensionsInDescription = true;

	/**
	 * Construct a new empty file filter
	 */
	public PEFileFilter() {
		this.filters = new HashMap<String,PEFileFilter>();
	}

	/**
	 * Construct a new file filter with the given extension, e.g. "tex"
	 */
	public PEFileFilter(String extension) {
		this(extension,null);
	}

	/**
	* Construct a new file filter with the given extension (e.g. "pst") and description ("Pstricks files")
	 */
	public PEFileFilter(String extension, String description) {

		this();
		if(extension!=null) addExtension(extension);
		if(description!=null) setDescription(description);
	}

	/**
	* Construct a new file filter with the given array of extensions, e.g.
	 * new PEFileFilter(String {"gif", "jpg"})
	 */
	public PEFileFilter(String[] filters) {
		this(filters, null);
	}

	/**
	* Construct a new file filter with the given array of extensions and the given description.
	 * e.g. new PEFileFilter(String {"pic", "tex"}, "Fichiers Picture et Latex")
	 */
	public PEFileFilter(String[] filters, String description) {

		this();
		for (int i = 0; i < filters.length; i++) {
			// ajoute les filtres un par un
			addExtension(filters[i]);
		}
		if(description!=null) setDescription(description);
	}

	/**
	 * @return "true" if the given file can be displayed in the file filter
	 */
	public boolean accept(File f) {
		if(f != null) {
			if(f.isDirectory()) {
				return true;
			}
			String extension = getExtension(f);
			//System.out.println("extension="+extension);
			if(extension != null && filters.get(getExtension(f)) != null) {
				return true;
			};
		}
		return false;
	}

	/**
	 * Renvoie l'extension d'un fichier, sans le point et éventuellement avec le prefixe
	 * <code>"jpe."</code>. Par exemple pour <file>toto.tex</file> l'extension est <code>"tex"</code>, alors
	 * que pour <file>toto.jpe.tex</file> l'extension est <code>"jpe.tex"</code>.
	 * @param f le fichier dont on cherche l'extension
	 * @return L'extension associée au fichier <code>f</code> passé en paramètre
	 */
	public String getExtension(File f) {
		if(f != null) {
			String filename = f.getName().toLowerCase();
			int i = filename.lastIndexOf('.');
			if(i>0 && i<filename.length()-1) {
				String extension = filename.substring(i+1);
				if(i > 4 && filename.substring(i-4,i).equals(".jpe"))
					return "jpe." + extension;
				else
					return extension;
			};
		}
		return null;
	}

	/**
	 * Add a new extension to the file filter
	 */
	public void addExtension(String extension) {

		if(filters == null) {
			filters = new HashMap<String,PEFileFilter>(5);
		}
		filters.put(extension.toLowerCase(), this);
		fullDescription = null;
	}

	/**
	 * @return the file filter description e.g. "Picture files for LaTeX : *.pic, *.tex"
	 */
	public String getDescription() {

		if(fullDescription == null) {
			if(description == null || isExtensionListInDescription()) {
				fullDescription = (description==null) ? "(" : description + " (";
				Iterator<String> extensions = filters.keySet().iterator();
				if(extensions != null) {
					fullDescription += "." + extensions.next();
					while (extensions.hasNext()) {
						fullDescription += ", " + extensions.next();
					}
				}
				fullDescription += ")";
			} else {
				fullDescription = description;
			}
		}
		return fullDescription;
	}

	/**
	 * Set the description string for this filter
	 */
	public void setDescription(String description) {
		this.description = description;
		fullDescription = null;
	}

	/**
	* Set whether the list of extensions is included in the description string.
	 */
	public void setExtensionListInDescription(boolean b) {
		useExtensionsInDescription = b;
		fullDescription = null;
	}

	/**
	 * @return whether the list of extensions is included in the description string.
	 */
	public boolean isExtensionListInDescription() {
		return useExtensionsInDescription;
	}

} // PEFileFilter
