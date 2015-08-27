// InConvexZoneTranslateTool.java --- -*- coding: iso-8859-1 -*-
// Copyright 2011 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: InConvexZoneTranslateTool.java,v 1.3 2013/03/27 06:57:41 vincentb1 Exp $
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

import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.model.CtrlPtSubset;
import jpicedt.graphic.PicPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.BitSet;

import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;


public class InConvexZoneTranslateTool extends ConvexZoneTool{

	private EditorKit		kit;
	private ConvexZoneGroup target;
	private CtrlPtSubset    cps;
	private Line2D.Double	vec;
	private Stroke          stroke;

	private enum CurrentTask{
		SET_CTRL_PT_SUBSET, TRANSLATE_CTRL_PT, COMPLETE
	};

	private CurrentTask currentTask;
	private CursorFactory cursorFactory = new CursorFactory();

	public Cursor getCursor(){
		return cursorFactory.getPECursor(CursorFactory.CZ_SELECT);
	}

	public void mousePressed(PEMouseEvent e){
		super.mousePressed(e);
		PicPoint pt;
		switch(currentTask){
		case SET_CTRL_PT_SUBSET:
			pt = e.getPicPoint();
			e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
			{
				BitSet czExtension = new BitSet();
				cps = kit.getSelectionHandler().getCtrlPtSubset(target,czExtension);
			}
			vec = new Line2D.Double();
			vec.x1 = vec.x2 = pt.x;
			vec.y1 = vec.y2 = pt.y;
			currentTask = CurrentTask.TRANSLATE_CTRL_PT;
			break;
		case TRANSLATE_CTRL_PT:
			break;
		}
	}

	public void mouseMoved(PEMouseEvent e){
		super.mouseMoved(e);
		switch(currentTask){
		case COMPLETE:
			currentTask = CurrentTask.SET_CTRL_PT_SUBSET;
			break;
		}
		e.getCanvas().setCursor(getCursor());
	}


	private void doTranslation(PEMouseEvent e){
		PicPoint pt = e.getPicPoint();
		e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
		if(vec.x2 == pt.x && vec.y2 == pt.y) return;
		double dx = pt.x - vec.x2;
		double dy = pt.y - vec.y2;
		vec.x2 = pt.x;
		vec.y2 = pt.y;
		if(cps != null)
			cps.translate(dx, dy);
		target.translate(dx, dy);
		e.getCanvas().repaint();
	}

	public void mouseReleased(PEMouseEvent e){
		super.mouseReleased(e);
		switch(currentTask){
		case TRANSLATE_CTRL_PT:
			doTranslation(e);
			currentTask = CurrentTask.COMPLETE;
			break;
		}
	}

	public void mouseDragged(PEMouseEvent e){
		super.mouseDragged(e);
		PicPoint pt;
		switch(currentTask){
		case TRANSLATE_CTRL_PT:
			doTranslation(e);
			break;
		}
	}

	/**
	 * Appel� quand cet outil est remplac� par un autre outil dans le kit h�te.
	 */
	public void flush(){
		currentTask = CurrentTask.SET_CTRL_PT_SUBSET;
		cps = null;
		vec = null;
		stroke = null;
	}

	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if(currentTask == CurrentTask.SET_CTRL_PT_SUBSET) return;
		if (vec == null) return;
		if (stroke==null) {
			float[] dash = {1.0f,1.0f};
			stroke = new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f,dash,0.5f);
		}
		g.setStroke(stroke);
		g.setPaint(Color.blue);
		g.draw(vec);
	}

	public InConvexZoneTranslateTool(EditorKit kit, ConvexZoneGroup target){
		this.kit = kit;
		this.target = target;
		this.currentTask = CurrentTask.SET_CTRL_PT_SUBSET;
		this.vec = new Line2D.Double();
	}

}


/// InConvexZoneTranslateTool.java ends here
