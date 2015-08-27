// TransferableGraphic.java --- -*- coding: iso-8859-1 -*-
// March 3, 2002 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: TransferableGraphic.java,v 1.8 2013/03/27 06:56:16 vincentb1 Exp $
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

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.model.Element;

import java.awt.datatransfer.*;

/**
 * Wrapper for <code>Element</code>'s that can be transfered to/from a <code>Clipboard</code>. Contrary to a
 * <code>BranchElement</code> implementation:
 * <ul><li>this object stores a reference to a CLONE of the <code>Element</code>(s) of interest, not to the
 * <code>Element</code>(s) itself (this allow the source <code>Element</code> to be further modified w/o
 * affecting the content of this <code>TransferableGraphic</code>, which is necessary for proper
 * <code>ClipBoard</code> behaviour)</li>
 * <li>As a result, <code>Element</code>'s added to this <code>TransferableGraphic</code> have no parent, no
 * view,&hellip;</li></ul>
 */
public class TransferableGraphic implements Transferable, ClipboardOwner {

	/** the <code>DataFlavor</code> for <code>Element</code>'s */
	public static final DataFlavor JPICEDT_DATA_FLAVOR =
		new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType,"jPicEdt graphic content");

	private Element[] content;
	private String formatted;
	private PECanvas sourceBoard;

	/**
	 * Construct a new TransferableGraphic from a clone of the given elements and the given String containing
	 * the formatted representation of the elements (this depends on the <code>ContentType</code> of of the
	 * caller).
	 * @param elements an array of elements to be set as the content of this <code>Transferable</code>
	 * @param formatted the text representation of the given elements (can be <code>null</code>)
	 * @param sourceBoard the board from which the elements are copied (can be <code>null</code>)
	 * @since jPicEdt 1.3.2
	 */
	public TransferableGraphic(Element[] elements, String formatted, PECanvas sourceBoard){
		this.content = new Element[elements.length];
		for (int i=0; i<elements.length; i++){
			content[i] = (Element)elements[i].clone();
		}
		if (formatted == null) this.formatted = "";
		else
			this.formatted = formatted;
		this.sourceBoard = sourceBoard;
	}

	public PECanvas getSourceBoard(){ return sourceBoard; }
	public void setSourceBoard(PECanvas board){ sourceBoard = board; }


	/**
	 * @return An <code>Object</code> which represents the data to be transferred.  Current implementation
	 *         returns an array of elements if flavor is of type <code>JPICEDT_DATA_FLAVOR</code>, or a string
	 *         representation of the elements (as given in the constructor) is flavor is of type
	 *         <code>DataFlavor.stringFlavor</code>.
	 * @param flavor the requested flavor for the data
	 * @since jPicEdt 1.3.2
	 */
	public Object getTransferData(DataFlavor flavor){
		if (flavor.equals(JPICEDT_DATA_FLAVOR)){
			return content;
		}
		else if (flavor.equals(DataFlavor.stringFlavor)){
			return formatted;
		}
		else return null;


	}

	/**
	 * @return An array of <code>DataFlavor</code> objects indicating the flavors the data can be provided in.
	 * @since jPicEdt 1.3.2
	 */
	public DataFlavor[] getTransferDataFlavors(){
		DataFlavor[] flavors = new DataFlavor[2];
		flavors[0] = JPICEDT_DATA_FLAVOR;
		flavors[1] = DataFlavor.stringFlavor;
		return flavors;
	}

	/**
	 * @return whether or not the specified data flavor is supported for this <code>TransferableGraphic</code>
	 *         Currently only <code>stringFlavor</code> and <code>javaJVMLocalObjectMimeType</code> are
	 *         supported.
	 * @since jPicEdt 1.3.2
	 */
	public boolean isDataFlavorSupported(DataFlavor flavor){

		if (flavor.equals(DataFlavor.stringFlavor) || flavor.equals(JPICEDT_DATA_FLAVOR))
			return true;
		return false;
	}


	/**
	 * Notifies this object that it is no longer the owner of the content of the clipboard
	 * This implementation does nothing.
	 * @since jPicEdt 1.3.2
	 */
	 public void lostOwnership(Clipboard c, Transferable contents){
	 }

}
