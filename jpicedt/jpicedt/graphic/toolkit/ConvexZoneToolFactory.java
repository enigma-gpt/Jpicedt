// ConvexZoneToolFactory.java --- -*- coding: iso-8859-1 -*-
// Copyright 2010/2013 Vincent Belaïche
//
// Author: Vincent Belaïche <vincentb1@users.sourceforge.net>
// Version: $Id: ConvexZoneToolFactory.java,v 1.14 2013/03/27 06:59:11 vincentb1 Exp $
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
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.event.ZoomEvent;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.util.ConvexPolygonalZone;

import static jpicedt.graphic.PECanvas.SelectionBehavior.REPLACE;
import static jpicedt.Localizer.localize;

/**
 * Fabrique d'outils de manipulation de zones convexes.
 *
 * @author <a href="mailto:vincentb1@users.sourceforge.net">Vincent Belaïche</a>
 * @version $Id: ConvexZoneToolFactory.java,v 1.14 2013/03/27 06:59:11 vincentb1 Exp $
 */
public class ConvexZoneToolFactory{

	// Convexe zone edition
	public static final String SELECT      = "action.convexzone.Select";
	public static final String EDIT        = "action.convexzone.Edit";

	public static final String RECTANGLE   = "action.convexzone.Rectangle";
	public static final String EXTENSIBLE  = "action.convexzone.Extensible";
	public static final String BAND_SHAPED = "action.convexzone.BandShape";
	public static final String U_SHAPED    = "action.convexzone.UShape";
	public static final String HALF_PLANE  = "action.convexzone.HalfPlane";

	// Drawing edition via Convexe zone
	public static final String DRAWING_TRANSLATE = "action.convexzone.CzDrawingTranslate";
	public static final String DRAWING_TRIM      = "action.convexzone.CzDrawingTrim";

	// Convexe zone use-selection mode
	public static final String USE_SELECTION = "action.convexzone.UseCzSelection";

	// add new tool name here :
	/**
	 * Tableau de tableaux de <code>String</code> contenant les noms de chaque
	 * outil de manipaltion de zone convexe que cette fabrique peut produire,
	 * rangés par groupes d'outils en relation proche.<br> Ce rangement par
	 * groupe peut peut être utils pour enrigstrer les outils dans
	 * l'<code>EditorKit</code> hôte, ou pour automatiser le processus de
	 * construction des <code>PEAction</code>s correspondantes.
	 */
	private static final String[][] ALL_TOOL_NAMES = new String[][]{
		{ RECTANGLE, EXTENSIBLE, BAND_SHAPED, U_SHAPED, HALF_PLANE}};

	private CursorFactory cursorFactory = new CursorFactory();
	private EditorKit kit;

	/**
	 * @param editorKit the EditorKit
	 */
	public ConvexZoneToolFactory(EditorKit editorKit){
		this.kit = editorKit;
	}

	/**
	 * @return une copie du tableau  <code>ALL_TOOL_NAMES</code>
	 * @see #ALL_TOOL_NAMES
	 */
	public static String[][] getAvailableToolNames(){
		String[][] names = new String[ALL_TOOL_NAMES.length][];
		for (int i=0; i<names.length; i++){
			names[i] = new String[ALL_TOOL_NAMES[i].length];
			for (int j=0; j<names[i].length; j++){
				names[i][j] = ALL_TOOL_NAMES[i][j].toString();
			}
		}
		return names;
	}

	/**
	 * Un outil de création de zone convexe
	 */
	protected class GenericConvexZoneTool extends ConvexZoneTool {

		GenericConvexZoneTool(String type){
		}

		public void mousePressed(PEMouseEvent e){
			super.mousePressed(e);
			JOptionPane.showMessageDialog(
				null,
				"Attention: chantier!!\nUnder construction!!",
				"Avertissement, Warning",
				JOptionPane.ERROR_MESSAGE);
		}

	}

	protected abstract class ConvexZoneToolBase extends ConvexZoneTool {
        // indexe la tâche courant de création de zone convexe, est incrémenté
		// à chaque évènement mouse-press
		private int currentTaskIndex;
		// passe à true dès que tous les points ont été définis
		private boolean isCompleted;
		private ArrayList<PicPoint> ptBuffer;
		protected int   pointCount;
		private int     pointCount0;
		private ConvexZone currentConvexZone;
		private Line2D.Double vec;
		private GeneralPath   path;
		private Stroke        stroke;

		abstract String getHelpMessageIds(int currentTaskIndex);


		public ConvexZoneToolBase(int pointCount,String type){
			isCompleted = true;
			this.pointCount0 = this.pointCount = pointCount;
			ptBuffer = new ArrayList<PicPoint>(pointCount > 0 ? pointCount : 10);
		}

		abstract ConvexPolygonalZone makeConvexZone(PicPoint[] definitionPoints);
		abstract String     getConvexZoneName();

		public void mousePressed(PEMouseEvent e){
			super.mousePressed(e);
			// clic droit
			int event = 0;
			if(e.isPopupTrigger()) event |= 1;
			if(e.isLeftButton()) event |= 2;
			if ((event & 1) != 0){
				// either : back to Convex zone SELECT_MODE
				if(isCompleted
				   && kit.getCurrentMouseToolType() == MouseTool.MouseToolType.CONVEXZONE_MOUSE_TOOL)
					kit.setCurrentMouseTool(SELECT); // calls back flush()
				else if(pointCount < 0){
					event &= ~3;
					event |= 2;
					pointCount = currentTaskIndex+1; // cause l'arrêt
				}
			}
			if((event & 3) == 2){
				PicPoint pt = new PicPoint();
				ptBuffer.add(currentTaskIndex,pt);

				e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),pt);
				if(currentTaskIndex == 0){
					vec = new Line2D.Double();
					vec.x2 = vec.x1 = pt.x;
					vec.y2 = vec.y1 = pt.y;
				}
				else if(currentTaskIndex > 1){
					if(path == null){
						path = new GeneralPath();
						path.moveTo(vec.x1, vec.y1);
					}
					path.lineTo(vec.x2,vec.y2);
					vec.x1 = vec.x2;
					vec.y1 = vec.y2;
					vec.x2 = pt.x;
					vec.y2 = pt.y;
				}
				++currentTaskIndex;
				isCompleted = false;
				if(currentTaskIndex == pointCount)
				{

					// e.getCanvas().unSelectAll();


					// instancie une nouvelle ConvexZone selon la méthode
					// makeConvexZone implantée dans classe dérivée
					PicPoint [] definitionPoints = new PicPoint[ptBuffer.size()];
					definitionPoints = ptBuffer.toArray(definitionPoints);
					ptBuffer.clear();
					ConvexPolygonalZone cpz = makeConvexZone(definitionPoints);
					if(cpz != null){
						currentConvexZone = new ConvexZone(makeConvexZone(definitionPoints),e.getCanvas());

						e.getCanvas().beginUndoableUpdate(localize("action.editorkit.Draw") +" ("
													  + getConvexZoneName()+")");
						e.getCanvas().addConvexZone(currentConvexZone);
						e.getCanvas().endUndoableUpdate();
						e.getCanvas().select(currentConvexZone,REPLACE); // replace selection
					}
					flush();
					e.getCanvas().repaint();
					init();
				}
				else
					e.getCanvas().repaint();
			}
			kit.postHelpMessage(getHelpMessageIds(currentTaskIndex));
		}

		protected void doMouseMoved(PEMouseEvent e){
			if (e.isPopupTrigger())  return; // right button -> do nothing :
			if(vec != null){
				PicPoint pt = e.getPicPoint();
				e.getCanvas().getGrid().nearestNeighbour(pt,pt);
				vec.x2 = pt.x;
				vec.y2 = pt.y;
				e.getCanvas().repaint();
			}
		}

		/** set current point */
		public void mouseDragged(PEMouseEvent e){
			super.mouseDragged(e);
			doMouseMoved(e);
		}

		/** set cursor for canvas, then call mouseDragged */
		public void mouseMoved(PEMouseEvent e){
			super.mouseMoved(e);

			// e.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			kit.postHelpMessage(getHelpMessageIds(currentTaskIndex));
			doMouseMoved(e);
		}

		/**
		 * Updates the value of the current Help-Message to be posted by the hosting EditorKit, according
		 * to current key modifiers. This aims at mimicking the behavior in EditElementMouseTransformFactory,
		 * where modifiers-dependent messages are posted in real-time.
		 */
		private void updateHelpMessage(PEMouseEvent e){
			// helpMsg = helpMsgDEFAULT;
			// if (currentElement instanceof PicEllipse){
			// 	PicEllipse ellipse = (PicEllipse)currentElement;
			// 	if (!ellipse.isPlain()){ // it's an arc -> cycle through arc types (this is valid only after the surrounding parallelogram has been set)
			// 		helpMsg = "help-message.DrawArc";
			// 		return;
			// 	}
			// }
			// // --- parallelograms and ellipses ---
			// if (currentElement instanceof PicParallelogram && !(currentElement instanceof PicCircleFrom3Points)){
			// 	if (e.isControlDown() && e.isAltDown()) {
			// 		if (e.isShiftDown()) {
			// 			if (currentElement instanceof PicEllipse)  helpMsg = "help-message.EllipseCircle";
			// 			else helpMsg = "help-message.ParalleloSquare";
			// 		}
			// 		else helpMsg = "help-message.MovePointCenterFixed";
			// 	}
			// }
		}

		/**
		 * move the points indexed in drawPoints[currentTaskIndex] to the current click-point
		 * (possibly after grid alignment).
		 */
		public void setCurrentPoint(PEMouseEvent e){
			// if (isCompleted) return;
			// e.getCanvas().getGrid().nearestNeighbour(e.getPicPoint(),ptBuffer);
			// if (ptBuffer.equals(currentElement.getCtrlPt(drawPoints[currentTaskIndex][0],ptBuffer1))) return;
			// if (currentElement instanceof PicParallelogram && !(currentElement instanceof PicCircleFrom3Points)){
			// 	if (e.isControlDown() && e.isAltDown()) {
			// 		if (e.isShiftDown()) this.constraint = SQUARE; // same as in EditElementMouseTransformFactory
			// 		else this.constraint = CENTER_FIXED;
			// 	}
			// 	else this.constraint = DEFAULT;
			// }
			// for (int i=0; i<(drawPoints[currentTaskIndex]).length;i++)
			// 	currentElement.setCtrlPt(drawPoints[currentTaskIndex][i],ptBuffer,this.constraint);
		}

		/**
		 * called when this tool is being activated in the hosting editor kit
		 */
		public void init(){
			// kit.getCanvas().setCursor(cursorFactory.getPECursor(CursorFactory.DRAW));
			kit.postHelpMessage(getHelpMessageIds(0));
 			vec = null;
			path = null;
		}

		/** called when this tool is being replaced by another mouse-tool in the hosting
		 * editor kit ; this is mainly for mousetools using more than one sequence, for it
		 * gives them a chance to clean themselves up for the next time */
		public void flush(){
			isCompleted = true;
			currentTaskIndex = 0;
 			vec = null;
			path = null;
			pointCount = pointCount0;
		}



		/**
		 * This method is called by the hosting EditorKit : this implementation paints
		 * the current element's highlighter.
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
			// mise en vedette de l'outil
			if(stroke == null){
				float[] dash = {1.0f,1.0f};
				stroke = new BasicStroke((float)(1.0/scale),BasicStroke.CAP_ROUND,
										 BasicStroke.JOIN_ROUND,10.0f,dash,0.5f);
			}
			g.setStroke(stroke);
			g.setPaint(Color.blue);
			if(path != null)
				g.draw(path);
			if(vec != null)
				g.draw(vec);

			// mise en vedette de la zone convexe qui vient d'être ajoutée
 			if (currentConvexZone==null) return;
			if(kit.isConvexZoneSetShown())
				currentConvexZone.paintHighlighter(g, allocation, scale);
		}

		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.CZ_SELECT);
		}

	}

	public class BandShapedConvexZoneTool extends ConvexZoneToolBase{
		final String[] HELP_MESSAGE_IDS = {
			"help-message.BandShapedConvexZone.Org1",
			"help-message.BandShapedConvexZone.Org2"
		};

		String getHelpMessageIds(int currentTaskIndex){ return HELP_MESSAGE_IDS[currentTaskIndex]; }
		String getConvexZoneName(){ return "BandShapedConvexZoneTool";}
		ConvexPolygonalZone makeConvexZone(PicPoint[] definitionPoints){
			ConvexPolygonalZone convexPolygonalZone = new ConvexPolygonalZone();
			PicVector dir;

			if(definitionPoints[0].equals(definitionPoints[1]))
				dir = new PicVector(PicVector.Y_AXIS);
			else
				dir = (new PicVector(definitionPoints[0],definitionPoints[1])).normalize();

			convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[0]),dir);
			convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[1]),
											 dir.cInverse());

			return convexPolygonalZone;
		}

		BandShapedConvexZoneTool(String type){
			super(2,type);
		}
	}

	public class RectangleConvexZoneTool extends ConvexZoneToolBase{
		final String[] HELP_MESSAGE_IDS = {
			"help-message.RectangleConvexZone.FirstCorner",
			"help-message.RectangleConvexZone.SecondCorner"
		};

		String getHelpMessageIds(int currentTaskIndex){ return HELP_MESSAGE_IDS[currentTaskIndex]; }
		String getConvexZoneName(){ return "RectangleConvexZoneTool";}
		ConvexPolygonalZone makeConvexZone(PicPoint[] definitionPoints){
			ConvexPolygonalZone convexPolygonalZone = new ConvexPolygonalZone();
			PicVector dir1,dir2;

			dir1 = new PicVector(PicVector.X_AXIS);
			if(definitionPoints[0].getX()  > definitionPoints[1].getX())
				dir1.inverse();

			dir2 = new PicVector(PicVector.Y_AXIS);
			if(definitionPoints[0].getY()  > definitionPoints[1].getY())
				dir2.inverse();


			// definitionPoint[0]
			// +----------------------------+	   	  ---> dir1
			// |	   	   	   	   	   	   	|				   	 !
			// |		 					|				   	 !
			// |		   	   	   	   	   	|   	   	   	   	 v dir2
			// +----------------------------+ definitionPoint[1]
			double xMid = 0.5*(definitionPoints[0].getX() +definitionPoints[1].getX());
			double yMid = 0.5*(definitionPoints[0].getY() +definitionPoints[1].getY());

			convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[0].getX(),yMid),dir1);
			convexPolygonalZone.addHalfPlane(new PicPoint(xMid, definitionPoints[0].getY()),dir2);
			convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[1].getX(),yMid),dir1.cInverse());
			convexPolygonalZone.addHalfPlane(new PicPoint(xMid, definitionPoints[1].getY()),dir2.cInverse());




		    // //    	 definitionPoints[0]
		    // //       +---------+--------+ definitionPoints[1]     ---> dir1
		    // //     	 !	   	   	org0   	!
		    // //     	 + org3	   	   	   	+ org1                      !
		    // //     	 !		   		   	!                           !
		    // //     	 +---------+--------+ definitionPoints[2]       v  dir2
		    // //     	 defPt3	    org2
		    // //
		    // //

			// // org0, dir2
		   	// convexPolygonalZone.addHalfPlane(
			// 	definitionPoints[0].clone().middle(definitionPoints[1]),// org0
			// 	dir2);
			// // org1, -dir1
			// convexPolygonalZone.addHalfPlane(
			// 	definitionPoints[1].clone().middle(definitionPoints[2]), // org1
			// 	(new PicVector(dir1)).inverse()); // -dir1
			// PicPoint defPt3 = definitionPoints[1].clone().symmetry(
			// 	definitionPoints[2].clone().middle(definitionPoints[0]));
			// // org2, -dir2
			// convexPolygonalZone.addHalfPlane(
			// 	defPt3.clone().middle(definitionPoints[2]), // org2
			// 	(new PicVector(dir2)).inverse()); // -dir2
			// // org3, dir1
			// convexPolygonalZone.addHalfPlane(
			// 	defPt3.middle(definitionPoints[0]), // org3
			// 	dir1);

			return convexPolygonalZone;
		}

		RectangleConvexZoneTool(String type){
			super(2,type);
		}
	}

	public class UShapedConvexZoneTool extends ConvexZoneToolBase{
		final String[] HELP_MESSAGE_IDS = {
			"help-message.UShapedConvexZone.FirstCorner",
			"help-message.UShapedConvexZone.SecondCorner",
			"help-message.UShapedConvexZone.Side"
		};

		String getHelpMessageIds(int currentTaskIndex){ return HELP_MESSAGE_IDS[currentTaskIndex]; }
		String getConvexZoneName(){ return "UShapedConvexZoneTool";}
		ConvexPolygonalZone makeConvexZone(PicPoint[] definitionPoints){

			ConvexPolygonalZone convexPolygonalZone = new ConvexPolygonalZone();
			PicVector dir1,dir2;

			if(definitionPoints[0].equals(definitionPoints[1]))
				dir1 = new PicVector(PicVector.Y_AXIS);
			else
				dir1 = (new PicVector(definitionPoints[0],definitionPoints[1])).normalize();

			dir2 = dir1.cIMul();

			if(dir2.dot(new PicVector(definitionPoints[1], definitionPoints[2])) < 0)
				dir2.inverse();



		   	convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[0]), dir1);
			convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[1]), dir1.cInverse());
			PicPoint defPt3 = definitionPoints[0].middle(definitionPoints[1]);
			convexPolygonalZone.addHalfPlane(defPt3, dir2);

			return convexPolygonalZone;
		}
		UShapedConvexZoneTool(String type){
			super(3,type);
		}
	}

	public class HalfPlaneConvexZoneTool extends ConvexZoneToolBase{
		final String[] HELP_MESSAGE_IDS = {
			"help-message.HalfPlaneConvexZone.Org",
			"help-message.HalfPlaneConvexZone.NormalVecTip"
		};

		String getHelpMessageIds(int currentTaskIndex){ return HELP_MESSAGE_IDS[currentTaskIndex]; }
		String getConvexZoneName(){ return "HalfPlaneConvexZoneTool";}
		ConvexPolygonalZone makeConvexZone(PicPoint[] definitionPoints){
			ConvexPolygonalZone convexPolygonalZone = new ConvexPolygonalZone();
			PicVector dir;

			if(definitionPoints[0].equals(definitionPoints[1]))
				dir = new PicVector(PicVector.Y_AXIS);
			else
				dir = (new PicVector(definitionPoints[0],definitionPoints[1])).normalize();

			convexPolygonalZone.addHalfPlane(new PicPoint(definitionPoints[0]),dir);

			return convexPolygonalZone;
		}

		HalfPlaneConvexZoneTool(String type){
			super(2,type);
		}
	}

	public class ExtensibleConvexZoneTool extends ConvexZoneToolBase{

		public String getConvexZoneName(){ return "ExtensibleConvexZoneTool";}

		public ExtensibleConvexZoneTool(String type){
			super(-1,type);
		}

		String getHelpMessageIds(int currentTaskIndex){ return "help-message.ExtensibleConvexZone"; }

		ConvexPolygonalZone makeConvexZone(PicPoint[] definitionPoints){
			// Calcul de l'enveloppe convexe

			ConvexPolygonalZone ret = new ConvexPolygonalZone();
			ret.extendByConvexHull(definitionPoints, pointCount);

			if(ret.size() > 0)
				return ret;
			else
				return null;
		}
	}

	/**
	 * Returns <code>MouseTool</code> of the given <code>type</code>
	 *
	 * @param type a <code>String</code> value identifying type of tool to be
	 * created.
	 * @return a <code>MouseTool</code> created for the corresponding to <code>type</code>
	 * @since jPicEdt 1.6
	 */
	public ConvexZoneTool createConvexZoneTool(String type){
		if(type.equals(BAND_SHAPED))
			return new BandShapedConvexZoneTool(type);
		else if(type.equals(HALF_PLANE))
			return new HalfPlaneConvexZoneTool(type);
		else if(type.equals(RECTANGLE))
			return new RectangleConvexZoneTool(type);
		else if(type.equals(U_SHAPED))
			return new UShapedConvexZoneTool(type);
		else if(type.equals(EXTENSIBLE))
			return new ExtensibleConvexZoneTool(type);
		else
			return new GenericConvexZoneTool(type);
	}

}

/// ConvexZoneToolFactory.java ends here
