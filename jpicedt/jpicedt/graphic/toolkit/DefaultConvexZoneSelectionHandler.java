// DefaultConvexZoneSelectionHandler.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: DefaultConvexZoneSelectionHandler.java,v 1.4 2013/03/27 06:58:41 vincentb1 Exp $
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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import jpicedt.graphic.ConvexZoneSelectionHandler;
import jpicedt.graphic.event.ConvexZoneEvent;
import jpicedt.graphic.event.ConvexZoneListener;
import jpicedt.graphic.event.PEMouseEvent;

import static jpicedt.Log.*;

public class DefaultConvexZoneSelectionHandler extends ConvexZoneGroup
	implements ConvexZoneSelectionHandler, ConvexZoneListener{

	/** EditorKit hôte */
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
	 * La méthode <code>paint</code> peint la mise en vedette des zones
	 * convexe.
	 *
	 * @param g le <code>Graphics2D</code> sur lequel on peint
	 * @param allocation la partie <code>Rectangle2D</code> de <code>g</code> qui doit être peinte
	 * @param scale le <code>double</code> facteur d'échelle
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
