// MirrorTool.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2012 Vincent Bela�che
//
// Author: Vincent Bela�che <vincentb1@users.sourceforge.net>
// Version: $Id: MirrorTool.java,v 1.6 2013/03/27 06:57:31 vincentb1 Exp $
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
import jpicedt.graphic.*;
import jpicedt.graphic.model.Element;
import static jpicedt.Log.debug;

import java.awt.*;
import java.awt.geom.*;
import static java.lang.Math.sqrt;
import static java.lang.Math.floor;
import static java.awt.event.InputEvent.ALT_DOWN_MASK;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK;

/**
 * Un <code>MouseTool</code> utilis� pour effectuer une r�flection sur un
 *  <code>Element</code> cible en utilisant un point origine
 *  <code>ptOrg</code> par lequel passe l'axe de r�flection, et un point
 *  extr�mit� <code>ptEnd</code> tel que le vecteur allant de
 *  <code>ptOrg</code> � <code>ptEnd</code> soit normal � l'axe de r�flection
 * <p> S�quence des op�rations � la souris:
 * <ul>
 * <li>mousePressed &rarr; place l'origine <code>ptOrg</code></li>
 * <li>mouseDragged &rarr; place l'extr�mit� <code>ptEnd</code> en dessinant
 * le vecteur correspondant.</li>
 * <li>mouseReleased &rarr; fixe le point <code>ptEnd</code></li>
 * <li>mousePressed &rarr; effectue la reflection si le clic est suffisamment
 * proche de <code>ptEnd</code>, c'est � dire dans un rayon inf�rieur � la
 * moiti� du vecteur normal, puis remise � z�ro. Si le clic est loin effectue
 * seulement la remise � z�ro de l'outil.</li>
 * </ul>
 * Describe class <code>MirrorTool</code> here.
 *
 * @since jPicEdt 1.6
 * @author <a href="mailto:vincent.belaiche@gmail.com">Vincent Bela�che</a>
 * @version $Id: MirrorTool.java,v 1.6 2013/03/27 06:57:31 vincentb1 Exp $
 */
public class MirrorTool extends MouseTool {

	private static final boolean DEBUG = jpicedt.Log.DEBUG;

	private EditorKit kit;

	// paint related :
	private Line2D.Double lineNormalVector; // visualisation du vecteur normal
	private Line2D.Double[] lineOrgBoundingBox = { null, null, null, null, null, null};
	private Line2D.Double[] lineDestBoundingBox = { null, null, null, null, null, null};
	private PicPoint[] boundingBoxVertices = { new PicPoint(), new PicPoint(),
											   new PicPoint(), new PicPoint()};
	private PicPoint[] boundingBoxDestVertices = { new PicPoint(), new PicPoint(),
											   new PicPoint(), new PicPoint()};
	private Stroke stroke;
	private CursorFactory cursorFactory = new CursorFactory();

	// geometry related :
	private Element target;
	private PicPoint ptOrg = new PicPoint(); // rotation center
	private PicPoint ptEnd = new PicPoint(); // last mobile point position
	private PicVector normalVector = new PicVector();
	private PicVector v2 = new PicVector();
	private double n2;
	private Rectangle2D.Double bb;
	private int repaintOnMoveAttempt = 0;

	// task related :
	private enum CurrentTask{
		SET_SRC_VECT, SET_NORMAL_VECT, COMPLETED, RESET};
	private CurrentTask currentTask;

	/** Construit une nouvel examplaire de <code>MirrorTool</code>.
	 *
	 * @param kit  l'<code>EditorKit</code> o� est enregistr� cet outil � la souris.
	 * @param target l'<code>Element</code> sur lequel porte la r�flection.
	 */
	public MirrorTool(EditorKit kit, Element target){
		this.kit = kit;
		this.target = target;
		currentTask = CurrentTask.SET_SRC_VECT; // le premier appui souris place le point
									// o� passe l'axe de r�flection (ptOrg)

	}

	private void setBBOutline(PicPoint[] vertices,Line2D.Double[] outline){
		int pi = 3;
		for(int i = 0; i<4; ++i){

			outline[i] = new Line2D.Double(
				vertices[i].getX(),
				vertices[i].getY(),
				vertices[pi].getX(),
				vertices[pi].getY());
			pi = i;
		}
		for(int i=0; i<=1; ++i)
		{
			outline[4+i]= new Line2D.Double(
				vertices[i].getX(),
				vertices[i].getY(),
				vertices[i+2].getX(),
				vertices[i+2].getY());
		}

	}

	/**
	 * La m�thode <code>mousePressed</code> place le point origine
	 * <code>ptOrg</code> , puis confirme la r�flection.
	 *
	 * @param e l'�v�nement <code>PEMouseEvent</code> donnant l'action � la souris.
	 */
	public void mousePressed(PEMouseEvent e){
		super.mousePressed(e);
		if (e.isPopupTrigger())  {
			kit.setCurrentMouseTool(EditorKit.SELECT); // callbacks flush()
			return;
		}
		else if(!e.isLeftButton())
			return;
		PicPoint pt = e.getPicPoint();
		switch (currentTask){
			case SET_SRC_VECT :
				int modifiers = e.getModifiersEx() & (ALT_DOWN_MASK | CTRL_DOWN_MASK);
				postHelpMessage(modifiers);
				switch(modifiers)
				{
				case 0:
					e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
					break;
				case ALT_DOWN_MASK | CTRL_DOWN_MASK:
				{
					double org, step,i,x,y;
					x = pt.getX();
					y = pt.getY();
					if(bb.getWidth() != 0.0){
						org = bb.getX();
						step = bb.getWidth()*.5;
						i  = floor((x - org)/step + 0.5);
						if(i < 0)
							i=0;
						else if(i>2)
							i = 2;
						x= i*step + org;
					}
					if(bb.getHeight() != 0.0){
						org = bb.getY();
						step = bb.getHeight()*.5;
						i  = floor((y - org)/step + 0.5);
						if(i < 0)
							i=0;
						else if(i>2)
							i = 2;
						y = i*step + org;
					}
					pt.setCoordinates(x,y);
				}
					break;
				default:
					return;
				}
				e.getCanvas().beginUndoableUpdate(jpicedt.Localizer.currentLocalizer().get("action.editorkit.Mirror.tooltip"));
				lineNormalVector = new Line2D.Double(pt.x,pt.y,pt.x,pt.y);

				ptOrg.x = pt.x;
				ptOrg.y = pt.y;
				break;
			case SET_NORMAL_VECT : // after a mouseReleased
				v2.setCoordinates(ptEnd,pt);
				{
					double nv2 = normalVector.dot(v2);
					nv2 *= nv2;
					if(nv2 <= 0.25 * n2)
						currentTask = CurrentTask.COMPLETED;
					else
						currentTask = CurrentTask.RESET;
				}
				break;
		default:
		}
		if(DEBUG) debug("rom="+Integer.toString(repaintOnMoveAttempt)+"target"+target.toString());
	}

	/**  */
	public void mouseDragged(PEMouseEvent e){
		super.mouseDragged(e);
		if (e.isPopupTrigger())
			return;
		else if(!e.isLeftButton())
			return;
		switch (currentTask){
			case SET_SRC_VECT : // set initial position of mobile point
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				if (lineNormalVector.x2 == pt.x && lineNormalVector.y2 == pt.y) return;
				lineNormalVector.x2 = pt.x;
				lineNormalVector.y2 = pt.y;
				ptEnd.setCoordinates(pt);
				e.getCanvas().repaint(); // [pending]
				break;
			case SET_NORMAL_VECT :
				break;
			default:
		}
	}

	/** */
	public void mouseReleased(PEMouseEvent e){
		super.mouseReleased(e);
		if (e.isPopupTrigger())
			return;
		switch (currentTask){
			case SET_SRC_VECT : // complete drawing of source vector, then switch to mobile vector.
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt); // pt = nn(pt)
				if(pt.equals(ptOrg)) return;
				ptEnd.setCoordinates(pt);
				normalVector.setCoordinates(ptOrg,ptEnd);
				n2 = normalVector.dot(normalVector);
				normalVector.scale(1/sqrt(n2));
				for(int i = 0; i < 4; ++i)
					boundingBoxVertices[i].mirror(ptOrg,normalVector,boundingBoxDestVertices[i]);
				setBBOutline(boundingBoxDestVertices,lineDestBoundingBox);
				currentTask = CurrentTask.SET_NORMAL_VECT;
				e.getCanvas().repaint(); // [pending]
				break;
			case COMPLETED :
				if(DEBUG) debug("mirror ptOrg=" + ptOrg.toString()
					  + ", normalVector=" + normalVector.toString()
					  + ", target=" + target.toString());
				target.mirror(ptOrg,normalVector);

				//PASSTHROUGH!!
		    case RESET:
				flush();
				e.getCanvas().repaint();
				e.getCanvas().endUndoableUpdate();
				break;
			default:
		}
		if(DEBUG) debug("rom="+Integer.toString(repaintOnMoveAttempt)+"target"+target.toString());
	}

	void postHelpMessage(int modifiers){
		switch(modifiers)
		{
		case 0:
			switch(currentTask)
			{
			case SET_SRC_VECT:
				kit.postHelpMessage("help-message.Mirror.OrgFreely");
				break;
			}
			break;
		case ALT_DOWN_MASK | CTRL_DOWN_MASK:
			switch(currentTask)
			{
			case SET_SRC_VECT:
				kit.postHelpMessage("help-message.Mirror.OrgContrained");
				break;
			}
			break;

		default:
			kit.postHelpMessage("help-message.InvalidModifiers");
			break;
		}
	}

	/**  */
	public void mouseMoved(PEMouseEvent e){
		super.mouseMoved(e);
		e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.SELECT));// [SR:pending] create specific MIRROR cursor

		if(bb == null){
			bb = new Rectangle2D.Double();
			target.getBoundingBox(bb);
			if(DEBUG) debug("rom="+Integer.toString(repaintOnMoveAttempt)+"\ntarget="+target.toString()
				  + "\nbb="+bb.toString());

			// 	   	 1=(0,1)   	   3=(1,1)
			// (i,j)   	+------------+
			//		   	|			 |
			//		   	+------------+
			//		 0=(0,0)       2=(1,0)
			for(int i = 0; i<2; ++i)
				for(int j = 0; j<2; ++j)
				{
					boundingBoxVertices[i*2+j].setCoordinates(
					  	bb.getX()+i*bb.getWidth(),
					  	bb.getY()+j*bb.getHeight());
				}
		}


		if(lineOrgBoundingBox[0] == null){
			setBBOutline(boundingBoxVertices,lineOrgBoundingBox);
			repaintOnMoveAttempt = 2;
		}
		if(repaintOnMoveAttempt > 0){
			--repaintOnMoveAttempt;
			e.getCanvas().repaint();//pending
		}

		postHelpMessage(e.getModifiersEx() & (CTRL_DOWN_MASK | ALT_DOWN_MASK ));


	}


	/**
	 * Called when this tool is being replaced by another tool in the hosting kit
	 */
	public void flush(){
		bb = null;
		repaintOnMoveAttempt = 0;
		lineNormalVector = null;
		for(int i = 0; i < 4; ++i){
			lineOrgBoundingBox[i] = lineDestBoundingBox[i] = null;
		}
		currentTask = CurrentTask.SET_SRC_VECT; // prepare next mousePressed
	}

	/**
	 * Allow the tool to paint shapes that are specific to this tool.
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		if (lineNormalVector == null ) return;
		if (stroke==null) {
			float[] dash = {1.0f,1.0f};
			stroke = new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,10.0f,dash,0.5f);
		}
		g.setStroke(stroke);
		g.setPaint(Color.blue);
		g.draw(lineNormalVector);
		if(lineOrgBoundingBox[0] != null){
			g.setPaint(Color.green);
			for(int i = 0; i < lineOrgBoundingBox.length; ++i)
				g.draw(lineOrgBoundingBox[i]);
		}
		if(lineDestBoundingBox[0] != null){
			g.setPaint(Color.red);
			for(int i = 0; i < lineDestBoundingBox.length; ++i)
				g.draw(lineDestBoundingBox[i]);
		}
	}

} // class



/// MirrorTool.java ends here
