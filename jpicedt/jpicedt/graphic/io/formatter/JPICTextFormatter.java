/* JPICTextFormatter.java - August 5, 2003 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2006 Sylvain Reynal

 Departement de Physique
 �cole Nationale Sup�rieure de l'�lectronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org/

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
