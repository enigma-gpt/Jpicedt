// DefaultConvexZoneSelectionHandler.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: DefaultConvexZoneSelectionHandler.java,v 1.4 2013/03/27 06:58:41 vincentb1 Exp $
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import jpicedt.graphic.ConvexZoneSelectionHandler;
import jpicedt.graphic.event.ConvexZoneEvent;
import jpicedt.graphic.event.ConvexZoneListener;
import jpicedt.graphic.event.PEMouseEvent;

import static jpicedt.Log.*;

public class DefaultConvexZoneSelectionHandler extends ConvexZoneGroup
	implements ConvexZoneSelectionHandler, ConvexZoneListener{

	/** EditorKit h�te */
	private EditorKit kit;


	DefaultConvexZoneSelectionHandler(EditorKit kit){
		if (DEBUG) debug("<init>");
		this.kit=kit;
	}

	public void changedUpdate(ConvexZoneEvent e){
	}

	public void replace(ConvexZone cz){
		clear();
		add(cz);
	}

	/**
	 * La m�thode <code>paint</code> peint la mise en vedette des zones
	 * convexe.
	 *
	 * @param g le <code>Graphics2D</code> sur lequel on peint
	 * @param allocation la partie <code>Rectangle2D</code> de <code>g</code> qui doit �tre peinte
	 * @param scale le <code>double</code> facteur d'�chelle
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		for(ConvexZone cz : getCollection()){
			cz.paintHighlighter(g, allocation, scale);
		}
	}


	public ConvexZoneHitInfo hitTest(PEMouseEvent e,boolean isHighlightVisible){
		ConvexZoneHitInfo hi = null;
		for(ConvexZone cz : getCollection()){
			if(hi == null)
				hi = cz.hitTest(e, isHighlightVisible);
			else{
				ConvexZoneHitInfo _hi = cz.hitTest(e, isHighlightVisible);
				if(_hi != null)
					hi = hi.append(_hi);
			}
		}
		return hi;
	}

}



/// DefaultConvexZoneSelectionHandler.java ends here
