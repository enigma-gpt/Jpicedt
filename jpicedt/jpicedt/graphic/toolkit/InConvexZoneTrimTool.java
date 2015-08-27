// InConvexZoneTrimTool.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: InConvexZoneTrimTool.java,v 1.6 2013/03/27 06:57:36 vincentb1 Exp $
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

import jpicedt.graphic.PECanvas;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.model.Element;
import jpicedt.graphic.util.AbstractEraser.ErasureStatus;
import jpicedt.graphic.util.AbstractEraser;
import jpicedt.graphic.util.ConvexPolygonalZone;
import jpicedt.graphic.util.Eraser;


import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Collection;
import javax.swing.JOptionPane;

import static jpicedt.Localizer.localize;


public class InConvexZoneTrimTool extends ConvexZoneTool{
	ConvexZoneGroup target;
	EditorKit       kit;
	private CursorFactory cursorFactory = new CursorFactory();

	public InConvexZoneTrimTool(EditorKit kit,ConvexZoneGroup target){
		this.target = target;
		this.kit    = kit;
	}

	public Cursor getCursor(){
		return cursorFactory.getPECursor(CursorFactory.CZ_SELECT);
	}

	public void init(){
		PECanvas canvas = kit.getCanvas();
		canvas.setCursor(getCursor());

		Collection<ConvexZone> czCollection  = kit.getConvexZoneSelectionHandler();
		if(czCollection.size() == 0)
			return;

		ArrayList<ConvexPolygonalZone> zones = new ArrayList<ConvexPolygonalZone>(czCollection.size());
		for(ConvexZone cz : czCollection)
			zones.add(cz.getConvexPolygonalZone());

		Collection<Element[]> toUpdate = new ArrayList<Element[]>(kit.getSelectionHandler().size());

		for(Element elt : kit.getSelectionHandler()){
			Element oldElt = elt;
			ErasureStatus erasureStatus = ErasureStatus.NO_ERASURE;
			for(ConvexPolygonalZone cz : zones){
				AbstractEraser eraser = new Eraser(elt,cz);
				if(eraser.getStatus() == ErasureStatus.NO_ERASURE)
					continue;
				else if(eraser.getStatus() == ErasureStatus.TOTALLY_ERASED){
					// on retire elt du dessin
					Element[] old2new = {elt};
					toUpdate.add(old2new);
					break;
				}
				else if(eraser.getStatus() == ErasureStatus.PARTIALLY_ERASED){
					erasureStatus = ErasureStatus.PARTIALLY_ERASED;
					elt = eraser.getErasedElt();

				}
			}
			if(erasureStatus == ErasureStatus.PARTIALLY_ERASED){
				Element[] old2new = { oldElt, elt };
				toUpdate.add(old2new);
			}
		}
		canvas.beginUndoableUpdate(localize(ConvexZoneToolFactory.DRAWING_TRIM));
		for(Element[] old2new : toUpdate){
			switch(old2new.length){
			case 1:
				canvas.getDrawing().remove(old2new[0]);
				kit.getSelectionHandler().remove(old2new[0]);
				break;
			case 2:
				old2new[0].replaceBy(old2new[1],true);
				break;
			}
		}
		canvas.endUndoableUpdate();
		canvas.repaint();
		kit.setCurrentMouseTool(EditorKit.CZ_SELECT);
	}
}
/// InConvexZoneTrimTool.java ends here
