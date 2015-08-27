// AbstractMouseTransformFactory.java --- -*- coding: iso-8859-1 -*-
// August 23, 2003 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ÉNSÉA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
//         Tel : +33 130 736 245
//         Fax : +33 130 736 667
//         e-mail : reynal@ensea.fr
//
// Version: $Id: AbstractMouseTransformFactory.java,v 1.11 2013/03/27 07:00:18 vincentb1 Exp $
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

import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.ui.dialog.UserConfirmationCache;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * Provide a skeletal implementation of the <code>MouseTransformFactory</code> interface.
 * This include some generic <code>MouseTransform</code>'s which may be easily extended by
 * concrete subclassers, like <code>SelectAreaMouseTransform</code>.
 * @author Sylvain Reynal
 * @since jPicEdt 1.4
 * @version $Id: AbstractMouseTransformFactory.java,v 1.11 2013/03/27 07:00:18 vincentb1 Exp $
 */
abstract public class AbstractMouseTransformFactory implements MouseTransformFactory {

	private EditorKit kit;
	private CursorFactory cursorFactory=new CursorFactory();
	protected UserConfirmationCache ucc;

	/**
	 * @param kit The hosting <code>EditorKit</code>.
	 */
	public AbstractMouseTransformFactory(EditorKit kit){
		this.kit = kit;
	}

	/**
	 * Return the hosting EditorKit for mouse-tools which work with this factory.
	 * This may be used to work with the selection-handler and use <code>hitTest()</code> methods.
	 */
	public final EditorKit getEditorKit(){
		return kit;
	}

	/**
	 * Allows the <code>MouseTransformFactory</code> to do specific graphic rendering when it's installed in a
	 * hosting <code>SelectionTool</code>. This default implementation does nothing.
	 * @since jpicedt 1.4
	 */
	public void paint(Graphics2D g, Rectangle2D allocation, double scale){
	}

	/**
	 * Called when the associated <code>SelectionTool</code> is being activated in the hosting
	 * <code>EditorKit</code>.  Initialization work required before any mouse-event occurs should be done
	 * here.  Other initialization work may be carried out in the <code>MouseTransform</code>'s themselves.
	 * <p> This implementation does nothing.
	 */
	public void init(UserConfirmationCache ucc){
		this.ucc = ucc;
	}

	/** Called when the associated <code>SelectionTool</code> is being deactivated in the hosting
	 * <code>EditorKit</code>.  This provides a way for the factory to do some final clean-up, e.g. local
	 * buffers, graphic context,&hellip;
	 */
	public void flush(){
		this.ucc = null;
	}


	/////////////////////////////////////////////////////////////////////////////////
	//// INVALID MT
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * This mouse transform does nothing, it simply returns an invalid cursor, and can be used
	 * by <code>MouseTransform</code> factories to notify the user of an invalid UI action.
	 * @since jpicedt 1.3.2
	 */
	protected class InvalidMouseTransform implements MouseTransform {

		/**
		 * Called when the mouse is pressed. The transform should do the initialization work here.
		 * @since jpicedt 1.3.2
		 */
		public void start(PEMouseEvent e){}

		/**
		 * Called when the mouse is dragged. <code>Element</code> geometry update should occur here.
		* @since jpicedt 1.3.2
		 */
		public void process(PEMouseEvent e){}

		/**
		 * Called when the mouse is released
		 * @since jpicedt 1.3.2
		 */
		public boolean next(PEMouseEvent e){return false;}

		/**
		 * @return The cursor for this <code>MouseTransform</code>.
		 * @since jpicedt 1.3.2
		 */
		public Cursor getCursor(){
			return new CursorFactory().getPECursor(CursorFactory.INVALID);
		}

		/**
		 * Allows the MouseTransform to do specific graphic rendering when it's operating.
		 * @since jpicedt 1.3.2
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		}

		/**
		 * @return A help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.InvalidAction";
		}

	} // InvalidMouseTransform


	/**
	 * This mouse transform does nothing, it is just a convenience to post a HelpMessage (regarding
	 * what the user COULD do) when no other mouse-transform makes sense with the current mouse-event.
	 * @since jpicedt 1.3.2
	 */
	protected class HelpMessageMouseTransform implements MouseTransform {

		String helpMsg;

		public HelpMessageMouseTransform(String helpMsg){
			this.helpMsg=helpMsg;
		}

		/**
		 * Called when the mouse is pressed. The transform should do the initialization work here.
		 * @since jpicedt 1.3.2
		 */
		public void start(PEMouseEvent e){}

		/**
		 * Called when the mouse is dragged. <code>Element</code> geometry update should occur here.
		* @since jpicedt 1.3.2
		 */
		public void process(PEMouseEvent e){}

		/**
		 * Called when the mouse is released
		 * @since jpicedt 1.3.2
		 */
		public boolean next(PEMouseEvent e){return false;}

		/**
		 * @return The cursor for this <code>MouseTransform</code>
		 * @since jpicedt 1.3.2
		 */
		public Cursor getCursor(){
			return new CursorFactory().getPECursor(CursorFactory.SELECT);
		}

		/**
		 * Allows the <code>MouseTransform</code> to do specific graphic rendering when it's operating.
		 * @since jpicedt 1.3.2
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
		}

		/**
		 * @return A help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return helpMsg;
		}

	} // HelpMessageMouseTransform


	/////////////////////////////////////////////////////////////////////////////////
	//// Select from rectangular area
	/////////////////////////////////////////////////////////////////////////////////

	/**
	 * A mouse-transform dedicated to select objects (<code>Element</code>'s, control-point's,&hellip;)
	 * inside a rectangle dragged by the user. Only a skeletal implementation is provided here.
	 * This implementation grows the selection rectangle,
	 * yet where <b>really</b> selecting things is concerned, subclassers may override methods by
	 * calling super.method_name before hand.
	 */
	protected class SelectAreaTransform implements MouseTransform {

		private Line2D.Double diag = new Line2D.Double();
		private double scale=0.0; 		// cache for scale factor
		private BasicStroke lineStroke = new BasicStroke(1.0f); 		// cache for BasicStroke

		/**
		 * Return the selection rectangle as built from the first mouse-pressed event and the
		 * current drag position.
		 */
		protected final Rectangle2D getSelectionRectangle(){
			return diag.getBounds2D();
		}

		/**
		 * Invoked by <code>mousePressed()</code>. This implementation simply initializes the geometry
		 * of the selection rectangle. Override if you must carry out specific selection operations
		 * on <code>mousePressed()</code>.
		 */
		public void start(PEMouseEvent e){
			diag.x1 = diag.x2 = e.getPicPoint().x;
			diag.y1 = diag.y2 = e.getPicPoint().y;
		}

		/** Called when the mouse is dragged. This simply grows the selection rectangle.
		 * Override if you must carry out specific selection operations while the mouse is being dragged.
		 */
		public void process(PEMouseEvent e){
			Rectangle2D oldclip = getClipRectangle();
			diag.x2 = e.getPicPoint().x;
			diag.y2 = e.getPicPoint().y;
			Rectangle2D clip = getClipRectangle();
			clip.add(oldclip);
			e.getCanvas().repaintFromModelRect(clip);
		}

		/**
		 * Called when the mouse is released. This implementation simply repaint the selection
		 * rectangle.
		 * Override if you must carry out specific selection operations while the mouse is being released.
		 */
		public boolean next(PEMouseEvent e){
			Rectangle2D clip = getClipRectangle();
			e.getCanvas().repaintFromModelRect(clip);
			return false;
		}

		/**
		 * Rendu de la plage rectangulaire sélectionnée.
		 * @param scale le facteur d'échelle <code>Graphics2D</code> entre modèle et vue.
		 */
		public void paint(Graphics2D g, Rectangle2D allocation, double scale){
			if (this.scale != scale){ // update stroke after zoom changed
				this.scale = scale;
				// scale thickness down so that it's displayed with the same thickness whatever value the current zoom factor may have
				lineStroke = new BasicStroke((float)(2.0/scale));
			}
			g.setPaint(Color.blue); // [pending] fetch color from Properties ?
			g.setStroke(lineStroke);
			g.draw(diag.getBounds2D());
		}

		/**
		 * Returns a rectangle defining the clip boundary for this mouse-transform, in model-coordinates
		 */
		public Rectangle2D getClipRectangle() {

			Rectangle2D rect2D = diag.getBounds2D();
			rect2D.setFrame(
				rect2D.getX()-2*lineStroke.getLineWidth(),
			    rect2D.getY()-2*lineStroke.getLineWidth(),
			    rect2D.getWidth()+4*lineStroke.getLineWidth(),
			    rect2D.getHeight()+4*lineStroke.getLineWidth());
			return rect2D;
		}

		/**
		 * @return A help-message for the UI, that makes sense with this transform.
		 */
		public String getHelpMessage(){
			return "help-message.SelectArea";
		}

		/** @return a textual representation of this transform for debugging purpose */
		public String toString(){
			return "[SelectAreaTransform]";

		}

		/**
		 * @return a cursor adequate with this mouse-transform, delegating to <code>CursorFactory</code>.
		 */
		public Cursor getCursor(){
			return cursorFactory.getPECursor(CursorFactory.SELECT);
		}
	} // SelectAreaTransform

} // MouseTransformFactory
