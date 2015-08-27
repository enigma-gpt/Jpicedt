// TextEditable.java --- 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999-2006 Sylvain Reynal
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
// Version: $Id: TextEditable.java,v 1.20 2013/03/27 07:00:58 vincentb1 Exp $
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
package jpicedt.graphic.model;

import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.*;
import jpicedt.graphic.event.*;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * An abstract class for <code>Element</code>'s that have a textual
 * representation, and [pending:removed as of 1.3.2] whose text can be
 * graphically edited
 *
 * @author Sylvain Reynal
 * @since jPicEdt 1.3.1
 * @version $Id: TextEditable.java,v 1.20 2013/03/27 07:00:58 vincentb1 Exp $
 *
 */
public abstract class TextEditable extends AbstractElement  {

	////////////////////////////
	//// PUBLIC CONSTANT FIELDS
	////////////////////////////

	// end points
	public static final int FIRST_PT = 0;
	public static final int P_ANCHOR = 0;
	public static final int LAST_PT = 0;

	/** anchor point */
	protected PicPoint ptAnchor;// = new PicPoint();

	/** string argument */
	protected String textString;


	/**
	 * Construct a new <code>TextEditable</code> with the given content and attribute set
	 */
	protected TextEditable(String textString, PicPoint anchorPoint, PicAttributeSet set){
		super(set);
		this.textString = textString;
		ptAnchor = new PicPoint(anchorPoint);
	}

	/**
	 * Construct a new <code>TextEditable</code> with the given content and a default attribute set
	 */
	protected TextEditable(String textString, PicPoint anchorPoint){
		super();
		this.textString = textString;
		ptAnchor = new PicPoint(anchorPoint);
	}

	/**
	 * Construct a clone of the given <code>TextEditable</code>
	 */
	protected TextEditable(TextEditable e){
		super(e);
		this.textString = e.textString;
		ptAnchor = new PicPoint(e.ptAnchor);
	}


	/**
	 * Set the given attribute name to the given value for this AbstractElement
	 */
	public <T> void setAttribute(PicAttributeName<T> name, T value){
		attributeSet.setAttribute(name,value);
		// if (name == TEXT_ROTATION)
		//	fireChangedUpdate(DrawingEvent.EventType.TEXT_CHANGE); // because this forces updating the bitmap if applicable
		//  VG: it shouldn't.
		// else
		// peut-etre un changement de couleur devrait relancer text_change
		fireChangedUpdate(DrawingEvent.EventType.ATTRIBUTE_CHANGE);
	}

	//////////////////////////////////
	/// OPERATIONS ON CONTROL POINTS
	//////////////////////////////////

	/**
	 * Returns the index of the first user-controlled point that can be retrieved by <code>getCtrlPt()</code>.
	 * This default implementation returns 0.
	 */
	public int getFirstPointIndex() {
		return 0;
	}

	/**
	 * Returns the index of the last user-controlled point that can be retrieved by <code>getCtrlPt()</code>.
	 * This default implementation returns the number of specification points minus one.
	 */
	public int getLastPointIndex() {
		return 0;
	}

	public PicPoint getCtrlPt(int numPoint, PicPoint dest) {
		if (numPoint != P_ANCHOR)
			throw new IndexOutOfBoundsException(new Integer(numPoint).toString());
		 if (dest==null)
			 dest = new PicPoint();
		return dest.setCoordinates(ptAnchor);
	}

	//////////////////////////////////////
	// TEXT SPECIFIC
	//////////////////////////////////////

	// new *************************** begin (by ss & bp)
	/**
	 * Return the string argument (e.g of the corresponding \\makebox{} command)
	 * @param mode the textmode
	 */

	public String getText(boolean mode){
		if(mode)
			return new String(textString);
		return new String("T");
	}

	/**
	 * Return the string argument (e.g of the corresponding \\makebox{} command)
	 */
	public String getText(){
		return new String(textString);
	}

	/**
	 * Set the string argument (e.g. of the \\makebox{} command)
	 */
	public void setText(String str){
		this.textString = str;
		fireChangedUpdate(jpicedt.graphic.event.DrawingEvent.EventType.TEXT_CHANGE); // force updating everything in the view
	}

	/**
	 * @param csg l'ensemble de zones convexes dont on teste qu'il contient le point d'ancrage du texte.
	 * @param czExtension ignor�
	 * @see jpicedt.graphic.model#Element.getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension)
	 * @return <code>null</code> si le point d'ancrage du texte n'est pas dans l'ensemble de zones convexes
	 * <code>csg</code>, un <code>CtrlPtSubsetPlain</code> de <code>this</code> sinon.
	 * @since jPicEdt 1.6
	 */
	public CtrlPtSubset getCtrlPtSubset(ConvexZoneGroup csg,BitSet czExtension){
		if(csg.containsPoint(ptAnchor))
			return new CtrlPtSubsetPlain(this);
		else
			return null;
	}



}
