/* JPICTextFormatter.java - August 5, 2003 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

*/
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

package jpicedt.graphic.io.formatter;

import jpicedt.graphic.*;
import jpicedt.graphic.model.*;

import java.awt.*;

import static jpicedt.graphic.io.formatter.JPICConstants.*;
import static jpicedt.graphic.model.PicAttributeName.*;


/**
 * A formatter for PicText objects in the JPIC-XML format :<br>
 * &lt;text vert-align="bottom|baseline|center-v|top"
 * hor-align="left|center-h|right" anchor-point="(x,y)"
 * box="no_frame|rectangle|circle|oval"&gt;text here !&lt;/text&gt;
 * where : <br>
 * <ul>
 * <li>refpoint = B|b|t for vertical and l|r for horizontal (B = baseline)
 * <li>rotation = any angle in degree or U,L,D,R,N,W,S or E.
 * </ul>
 * @since jpicedt 1.4
 * @author Sylvain Reynal
 * @version $Id: JPICTextFormatter.java,v 1.16 2013/03/27 07:04:34 vincentb1 Exp $
*/
public class JPICTextFormatter  extends AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicText obj;
	/** the factory that produced this formatter */
	private JPICFormatter factory;

	public Element getElement(){ return obj; }

	/**
	 * @param obj the PicText to be formatted
	 * @param factory the factory that produced this formatter
	 */
	public JPICTextFormatter(PicText obj, JPICFormatter factory){
		this.obj = obj;
		this.factory=factory;
	}

	/**
	 * @return a String representing this Element in the PsTricks
	 */
	public String format(){

		StringBuffer buf = new StringBuffer(100);

		buf.append("<text");

		XmlAttributeSet map = new XmlAttributeSet();

		// alignment
		map.putNameValuePair(TEXT_VERT_ALIGN, obj.getAttributeSet());
		map.putNameValuePair(TEXT_HOR_ALIGN, obj.getAttributeSet());

		// anchor-point
		map.putNameValuePair("anchor-point",obj.getCtrlPt(PicText.P_ANCHOR,null));


		// text-icon
		map.putNameValuePair(TEXT_ICON, obj.getAttributeSet());

		// text-mode
		map.putNameValuePair(TEXT_MODE, obj.getAttributeSet());

		// box
		map.putNameValuePair(TEXT_FRAME, obj.getAttributeSet());

		// rotation (degrees)
		map.putNameValuePair(TEXT_ROTATION, obj.getAttributeSet());

		map.putCommonAttributes(obj);

		// text
		buf.append(map.toXML());
		buf.append(" >"); // close tag
		buf.append(CR_LF);
		XMLizer.appendXMLized(buf,obj.getText());
		buf.append(CR_LF);
		buf.append("</text>");
		buf.append(CR_LF);
		return buf.toString();

	}


}
