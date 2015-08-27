// ConvexZoneViewParameters.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneViewParameters.java,v 1.3 2013/03/27 06:59:06 vincentb1 Exp $
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

import java.awt.BasicStroke;
import java.awt.Color;

/**
 * La classe <code>ConvexZoneViewParameters</code> est une collection de
 * paramètres statiques configurant la façon dont les zones convexes sont
 * visualisées. Ces paramètres sont partagés entre les sous-classes
 * <code>ConvexZone</code> et <code>ConvexZoneSet</code>.
 *
 * @since jPicEdt 1.6
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
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
