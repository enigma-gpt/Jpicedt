// AbstractCurveFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2013 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: AbstractCurveFormatter.java,v 1.9 2013/06/13 20:46:52 vincentb1 Exp $
// Keywords: Tikz, PGF
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
package jpicedt.format.output.tikz;
import jpicedt.graphic.model.Element;
import jpicedt.format.output.util.*;
import java.lang.StringBuffer;
import jpicedt.graphic.io.formatter.Formatter;
import jpicedt.graphic.io.formatter.AbstractFormatter;
import jpicedt.graphic.model.AbstractCurve;
import jpicedt.graphic.PicPoint;
import static jpicedt.graphic.model.AbstractCurve.PointType.*;

/**
 * Formatteur en Tikz pour les <code>AbstractCurve</code>'s. Dessine un chemin en TikZ.
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 */
public class AbstractCurveFormatter extends AbstractFormatter {

	/** Les �l�ments sur lesquels ce formatteur agit. */
	protected AbstractCurve curve;
	protected TikzFormatter factory;

   /**
	* @since jPicEdt 1.6
	*/
	public Element getElement(){ return curve;}

   /**
	* @since jPicEdt 1.6
	*/
	public AbstractCurveFormatter(AbstractCurve curve, TikzFormatter factory){
		this.curve = curve;
		this.factory=factory;
	}

	/**
	 * @return Une cha�ne de caract�res repr�sentant cette <code>AbstractCurve</code> et
	 * utilisant les macros de TikZ.
	 * @since jPicEdt 1.6
	 */
	public String format(){
		StringBuffer buf =  new StringBuffer(80);
		PicPoint pt = new PicPoint();
		if(curve.getSegmentCount() > 0)
		{
			pt = curve.getBezierPt(0,pt);
			factory.draw(buf,curve);
			buf.append(pt);
			int widthIndicator = 0; // ce n'est pas la vraie largeur, juste une approx.
			int pointWidth = 7;
			int maxWidth = 70;
			int segIdx = 0;
			for (int ptIdx=3; ptIdx <= curve.getBezierPtsCount(); ptIdx+=3,++segIdx){

				if(widthIndicator >= maxWidth)
				{
					widthIndicator = 0;
					buf.append(factory.getLineSeparator());
				}

				if(curve.isStraight(segIdx))
				{
					buf.append(" -- ");
					widthIndicator += 4;
				}
				else
				{
					buf.append(" .. controls ");
					widthIndicator += 13;
					pt = curve.getBezierPt(ptIdx-2,pt);
					buf.append(pt);
					widthIndicator += pointWidth;
					buf.append(" and ");
					widthIndicator += 5;
					pt = curve.getBezierPt(ptIdx-1,pt);
					buf.append(pt);
					widthIndicator += pointWidth;
					buf.append(" .. ");
					widthIndicator += 4;
				}
				// pour un cycle le nombre de point est 3*N, et
				// pour une courbe ouverte c'est 3*N+1
				if(ptIdx == curve.getBezierPtsCount())
				{
					widthIndicator += pointWidth;
					buf.append("cycle");
					widthIndicator += 9;
				}
				else
				{
					pt = curve.getBezierPt(ptIdx,pt);
					buf.append(pt);
					widthIndicator += pointWidth;
				}
			}

			buf.append(factory.getEOCmdMark());
		}
		return buf.toString();
	}
}

/// AbstractCurveFormatter.java ends here
