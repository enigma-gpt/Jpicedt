// PicGroupFormatter.java --- -*- coding: iso-8859-1 -*-
// 2001 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PicGroupFormatter.java,v 1.7 2013/03/27 07:23:20 vincentb1 Exp $
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
package jpicedt.format.output.pstricks;

import jpicedt.Log;
import jpicedt.graphic.model.PicGroup;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.PicAttributeSet;

import java.io.IOException;
import java.util.*;

import static jpicedt.format.output.pstricks.PstricksConstants.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;
import static jpicedt.graphic.view.ArrowView.Direction;
import static jpicedt.graphic.model.BranchElement.CompoundMode.*;

/**
 * A formatter for PicGroup objects. This implementation simply invokes {@link #format} on each children in turn.
 * @author Sylvain Reynal
 * @since jpicedt 1.0
 * @version $Id: PicGroupFormatter.java,v 1.7 2013/03/27 07:23:20 vincentb1 Exp $
 * <p>
 *
 */
public class PicGroupFormatter extends jpicedt.format.output.util.PicGroupFormatter {

	public PicGroupFormatter(PicGroup group, PstricksFormatter factory){
		super(group, factory);
	}

	public PstricksFormatter getFactory(){
		return (PstricksFormatter)factory;
	}
	
	/**
	 * @return a String representing this Element in the PsTricks 
	 */
	public String format() throws IOException {
		switch(group.getCompoundMode())
		{
		case JOINT:
			return formatPsCustom();
		case SEPARATE:
			return formatPsSetGroup(); 
		default:
			Log.error("Unexpected CompoundMode");
			return null;
		}
	}
	
	/**
	 * Returns a \\pscustom comprising every path contained in this group
	 * (except texts and closed paths)
	 */
	protected String formatPsCustom() throws IOException {


		if(getFactory().getContainerPsCustom() != null)
			throw new NestedPsCustomException(getFactory(),group);


		StringBuffer buf = new StringBuffer(100);
		PicAttributeSet set = group.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = getFactory().createParameterString(group);
		if (paramStr.isDefinedColourString()) 
			buf.append(paramStr.getUserDefinedColourBuffer());

		buf.append("\\pscustom");
		// parameters
		buf.append("[");
		buf.append(paramStr.getParameterBuffer());
		buf.append("]{%");
		buf.append(factory.getLineSeparator());

		// [pending: todo] check that attributes for inner elements don't have arrows activated
		
		ArrowStyle leftArrow = set.getAttribute(LEFT_ARROW);
		ArrowStyle rightArrow = set.getAttribute(RIGHT_ARROW);
		boolean isAtLeastOneArrow = (leftArrow!=ArrowStyle.NONE || rightArrow!=ArrowStyle.NONE);

		getFactory().setContainerPsCustom(group);
		for(Element e:group){
			buf.append(factory.createFormatter(e).format());
		}
		getFactory().setContainerPsCustom(null);
		
		if (group.isPathClosed()) buf.append("\\closepath");

		// pscustom does not draw the shadow by itself...
		if (set.getAttribute(SHADOW)){ // if we have to draw a shadow
			if (group.isPathClosed()) 
				buf.append("\\closedshadow ");
			else 
				buf.append("\\openshadow ");
	
		}

		/* [pending:SR:fev07] je fais quoi avec ca ?
		if (set.getAttribute(OVER_STRIKE)){ // if we have to draw a border
			//first, get parameters
			PstricksFormatter.ParameterString paramStrForBorder = createParameterStringForBorderInPsCustom(curve);
			buf.append("\\stroke[");
			buf.append(paramStrForBorder.getParameterBuffer());
			buf.append("]");
		}*/
		// end of border

		buf.append("} % pscustom"); // end of pscustom
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}
	/** Renvoie un groupe {\psset.... } contenant l'ensemble des objets dans ce
	 * PicGroup et  reprenant dans les psset les paramètres du groupe
	 */
	String formatPsSetGroup() throws IOException{
		StringBuffer buf = new StringBuffer(100);
		PicAttributeSet set = group.getAttributeSet();

		// first handle possibly user-defined colours
		PstricksFormatter.ParameterString paramStr = getFactory().createParameterString(group);
		if (paramStr.isDefinedColourString()) 
			buf.append(paramStr.getUserDefinedColourBuffer());

		buf.append("{"); 
		StringBuffer paramBuf = paramStr.getParameterBuffer();
		if(paramBuf.length() > 0)
		{
			buf.append("\\psset{");
			buf.append(paramBuf);
			buf.append("}");
		}
		buf.append("%");
		buf.append(factory.getLineSeparator());
		getFactory().pushDefaultAttributes(set);
		buf.append(super.format());
		getFactory().popDefaultAttributes();
		buf.append("}%"); 
		buf.append(factory.getLineSeparator());
		return buf.toString();
	}
} // class PicGroupFormatter
 
