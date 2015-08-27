// DXFInfoFormatter.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DXFInfoFormatter.java,v 1.5 2013/03/27 07:11:45 vincentb1 Exp $
// Keywords: AutoCAD, DXF (marque d�pos�e)
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

/// Installation:

/// Code:
package jpicedt.format.output.dxf;
import  jpicedt.format.output.dxf.DXFStringBuffer;

import java.util.ArrayList;

/**
 * Interface de tout formatteur de dessin donn�e, permettant le formattage des
 * informations DXF (marque d�pos�e) propre � ce dessin. Cette interface est app�l�e par les
 * objets de classe �tendant  l'interface DXFInformation
 * @see {@link jpicedt.format.output.dxf.DXFInformation}
 * @since jPicEdt 1.6
 */
interface DXFInfoFormatter
{
	/**
	 * R�cup�re l'information EXTMIN formattable
	 * @since jPicEdt 1.6
	 */
	ArrayList<DXFInformation>  getExtMin();

	/**
	 * R�cup�re l'information EXTMAX formattable
	 * @since jPicEdt 1.6
	 */
	ArrayList<DXFInformation>  getExtMax();

	/**
	 * R�cup�re la partie ENTITIES formatt�.
	 * @since jPicEdt 1.6
	 */
	DXFStringBuffer getEntities();
}

/// DXFInfoFormatter.java ends here
