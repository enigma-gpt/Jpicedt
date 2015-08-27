// AbstractEraser.java --- -*- coding: iso-8859-1 -*-
// Copyright 2009/2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: AbstractEraser.java,v 1.6 2013/03/27 06:56:06 vincentb1 Exp $
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

/// Installation:


/// Code:
package jpicedt.graphic.util;
import jpicedt.graphic.model.*;

/**
 * <p> classe abstraite d'un effaceur. Les sous-classes permettent d'effacer
 * une zone convexe d'un Element.  </p>
 * <p> Par effacement on entend juste qu'on calcule le r�sultat de
 * l'effacement: l'�l�ment graphique pass� en entr�e n'est ni d�truit, ni
 * modifi�, ni retir� d'un dessin.</p>
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Bela�che</a>
 * @since jPicEdt 1.6
 * @version $Id: AbstractEraser.java,v 1.6 2013/03/27 06:56:06 vincentb1 Exp $
 */
public abstract class AbstractEraser{
	public enum ErasureStatus{
		NO_ERASURE, PARTIALLY_ERASED, TOTALLY_ERASED};

	ErasureStatus status = ErasureStatus.TOTALLY_ERASED;
	Element       erasedElt = null;

	/**
	 * Renvoie l'<code>Element</code> apr�s avoir effacement. Selon l'�tat
	 * d'affacement cela peut �tre le m�me �l�ment que celui donn� en entr�
	 * &mdash; c'est � dire qu'aucun effacement n'aurait eu lieu &mdash; ou un
	 * �l�ment distinct, ou pas d'�l�ment du tout si l'effacement est total,
	 * c'est � dire que <code>null</code> est renvoy�.
	 * @return la valeur <code>Element</code> r�sultant de l'effacement
	 */
	public Element       getErasedElt(){ return erasedElt; }


	/**
	 * Renvoie l'�tat de l'effacement. Cela peut �tre: <dl>
	 * <dt><code>NO_ERASURE</code></dt><dd>Aucun n'effacement n'a eu lieu
	 * l'�lement renvoy� par <code>getErasedElt</code> est identique &mdash;
	 * pas de clonage &mdash; � celui pass� en entr�e.</dd>
	 * <dt><code>PARTIALLY_ERASED</code></dt><dd>L'�l�ment en entr�e � �tat
	 * partiellement effac�, le r�sultat est un nouvel �l�ment renvoy� au
	 * moyen de <code>getErasedElt</code></dd>
	 * <dt><code>TOTALLY_ERASED</code></dt><dd></dd>L'�l�ment en entr�e a �t� totalement effac�</dl>
	 * @return an <code>ErasureStatus</code> value
	 */
	public ErasureStatus getStatus(){ return status;}

};



/// AbstractEraser.java ends here
