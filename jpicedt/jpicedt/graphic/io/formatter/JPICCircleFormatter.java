/*
 JPICCircleFormatter.java - September 29, 2003 - jPicEdt, a picture editor for LaTeX.
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
import java.util.HashMap;

import static jpicedt.graphic.io.formatter.JPICConstants.*;

/**
 * JPIC-XML formatter for PicCircleFrom3Points objects.
 * JPIC-XML syntax :
 * <circle p1="(x1,y1)" p2="(x2,y2)" p3="(x3,y3)" closure="plain|chord|open|pie" other-attribs />
 *
 * @since jpicedt 1.4
 * @author Sylvain Reynal
 * @version $Id: JPICCircleFormatter.java,v 1.10 2013/03/27 07:05:22 vincentb1 Exp $
 */
public class JPICCircleFormatter extends  AbstractFormatter {

	/** the Element this formatter acts upon */
	private PicCircleFrom3Points obj;
	/** the factory that produced this formatter */
	private JPICFormatter factory;

	public Element getElement(){ return obj; }

	/**
	 * @param obj the PicCircleFrom3Points to be formatted
	 * @param factory the factory that produced this formatter
	 */
	public JPICCircleFormatter(PicCircleFrom3Points obj, JPICFormatter factory){
		this.obj = obj;
		this.factory=factory;
	}

	public String format(){
		StringBuffer buf = new StringBuffer(100); // 100 as initial capacity seems to be a good guess

		buf.append("<circle");
		// simply add parallelogram's three specification points, which are BL, BR and TR, since it's enough
		// to create a new PicEllipseFromParallelo when re-parsing + add angles
		XmlAttributeSet map = new XmlAttributeSet();
		map.putNameValuePair("p1",obj.getCtrlPt(PicCircleFrom3Points.P_1,null));
		map.putNameValuePair("p2",obj.getCtrlPt(PicCircleFrom3Points.P_2,null));
		map.putNameValuePair("p3",obj.getCtrlPt(PicCircleFrom3Points.P_3,null));
		if (obj.isPlain()) map.putNameValuePair("closure","plain");
		else switch (obj.getArcType()){
			case PicEllipse.CHORD : map.putNameValuePair("closure","chord"); break;
			case PicEllipse.OPEN : map.putNameValuePair("closure","open"); break;
			case PicEllipse.PIE : map.putNameValuePair("closure","pie"); break;
		}
		map.putCommonAttributes(obj);

		buf.append(map.toXML());
		buf.append(" />");
		buf.append(CR_LF);
		return buf.toString();
	}

}
