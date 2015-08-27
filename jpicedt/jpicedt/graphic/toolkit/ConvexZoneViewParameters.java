// ConvexZoneViewParameters.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneViewParameters.java,v 1.3 2013/03/27 06:59:06 vincentb1 Exp $
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
package jpicedt.graphic.toolkit;

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * La classe <code>ConvexZoneViewParameters</code> est une collection de
 * param�tres statiques configurant la fa�on dont les zones convexes sont
 * visualis�es. Ces param�tres sont partag�s entre les sous-classes
 * <code>ConvexZone</code> et <code>ConvexZoneSet</code>.
 *
 * @since jPicEdt 1.6
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @version $Id: ConvexZoneViewParameters.java,v 1.3 2013/03/27 06:59:06 vincentb1 Exp $
 */
class ConvexZoneViewParameters{

	private static BasicStroke solidLineStroke   = new BasicStroke(1.0f);
	private static float[] dashPattern = {5.0f,5.0f};
	private static BasicStroke dashedLineStroke  = new BasicStroke(
		1.0f,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0);

	private static Color   lineColor = Color.blue;

	public static Color getLineColor(){ return lineColor; }
	public static BasicStroke getSolidLineStroke(){ return solidLineStroke; }
	public static BasicStroke getDashedLineStroke(){ return dashedLineStroke; }
	public static void setScale(double scale){
		float width = (float)(1.0/scale);
		solidLineStroke   = new BasicStroke(width);
		dashedLineStroke  = new BasicStroke(
			width,BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dashPattern, 0);
	}
}


/// ConvexZoneViewParameters.java ends here
