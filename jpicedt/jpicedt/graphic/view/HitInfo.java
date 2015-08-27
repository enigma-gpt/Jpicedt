// HitInfo.java --- -*- coding: iso-8859-1 -*-
// February 9, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright 1999-2006 Sylvain Reynal
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
// Version: $Id: HitInfo.java,v 1.18 2013/03/27 06:53:56 vincentb1 Exp $
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

package jpicedt.graphic.view;

import jpicedt.graphic.model.Element;
import jpicedt.graphic.model.TextEditable;
import jpicedt.graphic.model.BranchElement;
import jpicedt.graphic.event.PEMouseEvent;

import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.awt.font.TextHitInfo;

/**
 * A <code>HitInfo</code> encapsulates information related to a mouse click
 * that occured on a particular area of an <code>HitInfo</code>.
 * @author Sylvain Reynal
 * @since jpicedt 1.3.2
 * @version $Id: HitInfo.java,v 1.18 2013/03/27 06:53:56 vincentb1 Exp $
 */
public interface HitInfo {

	/**
	 * @return the <code>Element</code> on which the hit occured
	 */
	Element getTarget();

	/**
	 * @return the mouse-event that triggered this <code>HitInfo</code>
	 */
	PEMouseEvent getMouseEvent();

	/**
	 * @return a <code>HitInfo.List</code> containing the given
	 * <code>HitInfo</code> appended to this <code>HitInfo</code>.
	 */
	List append(HitInfo hi);

	//////////////////////////////// template /////////////////////

	static abstract class AbstractHitInfo implements HitInfo {

		protected Element clickedObject;
		protected PEMouseEvent mouseEvent;

		/**
		 * Construct a new <code>HitInfo</code> when a click occured on an object's end-point
		 * <br><b>author:</b> Sylvain Reynal
		 * @param clickedObject the <code>Element</code> on which the hit occured
		 * @param mouseEvent the hit type
		 * @since jPicEdt
		 */
		public AbstractHitInfo(Element clickedObject, PEMouseEvent mouseEvent) {
			this.clickedObject = clickedObject;
			this.mouseEvent = mouseEvent;
		}

		/**
		 * Cloning constructor
		 */
		public AbstractHitInfo(HitInfo hi) {
			this(hi.getTarget(), hi.getMouseEvent());
		}

		/**
		 * <br><b>author:</b> Sylvain Reynal
		 * @return the <code>Element</code> on which the hit occured
		 * @since jPicEdt
		 */
		public Element getTarget(){
			return clickedObject;
		}

		/**
		 * <br><b>author:</b> Sylvain Reynal
		 * @return the mouse-event that triggered this <code>HitInfo</code>
		 * @since jPicEdt
		 */
		public PEMouseEvent getMouseEvent(){
			return mouseEvent;
		}

		/**
		 * @return a <code>List</code> containing the given
		 * <code>HitInfo</code> appended to this <code>HitInfo</code>.
		 */
		public List append(HitInfo hi){
			List l = new List(this);
			l.add(hi);
			return l;
		}
	}

	///////////////////////////////// Collection of HitInfo's /////////////////////////

	/**
	 * A collection of <code>HitInfo</code>'s that are appropriate when a
	 * mouse-event hits several <code>Element</code>'s at once.  Members are
	 * sorted in descending z-order of the associated <code>Element</code>.
	 */
	public static class List extends java.util.TreeSet<HitInfo> implements HitInfo {

		public List(HitInfo hi){
			super(new InverseZOrderingComparator());
			add(hi);
		}

		/**
		 * @return the first <code>Element</code> in the list on which the hit
		 * occured. Since element are appended in descending z-order,
		 * <code>getTarget()</code> thus returns the highest-z element.
		 */
		public Element getTarget(){
			return first().getTarget();
		}

		/**
		 * @return the mouse-event that triggered this
		 * <code>HitInfo</code>. Arbitrarily returns the mouse-event attached
		 * to the element having the highest z, yet all <code>HitInfo</code>'s
		 * in this list "should" have the same mouse-event.
		 */
		public PEMouseEvent getMouseEvent(){
			return first().getMouseEvent();
		}

		/**
		 * Append the given <code>HitInfo</code> to this List.
		 * @return this for convenience (and also to implement the <code>HitInfo</code> interface)
		 */
		public List append(HitInfo hi){
			if (hi instanceof List){
				addAll((List)hi);
			}
			else
				add(hi);
			return this;
		}

		public String toString(){
			String s;
			s = "HitInfo.List : #="+size()+" [\n";
			for (HitInfo hi: this){
				s += "# " + hi.toString()+"\n";
			}
			return s+"]";
		}
	}

	/**
	 * A comparator for z-axis ordering
	 */
	static class InverseZOrderingComparator implements Comparator<HitInfo> {

		/**
		 * @return a negative number whenever <code>hi1</code> "is less than"
		 *  <code>hi2</code>, i.e., it has a higher z-axis height than <code>hi2</code>,
		 *  etc.
		 */
		public int compare(HitInfo hi1, HitInfo hi2){
			BranchElement root = hi1.getTarget().getDrawing().getRootElement();
			int i1 = root.indexOf(hi1.getTarget());
			int i2 = root.indexOf(hi2.getTarget());
			// if we've to compare two Composite's having the same PicGroup target, they have the same z,
			// hence we must further compare clicked children (otherwise Set.add() rejects one of these Composite's, since
			// they'd be considered "equal()==true")
			/* don't know if it's really useful
			if (i1==i2 && hi1 instanceof Composite && hi2 instanceof Composite){
				i1 = ((Composite)hi1).getClickedChildIndex();
				i2 = ((Composite)hi2).getClickedChildIndex();
			}
			*/
			return i2 - i1;
		}
	}


	///////////////////////////////// INTERIOR ////////////////////////////////////////
	/**
	 * Represent a hit that occured on the interior of a <code>Element</code>
	 */
	public static class Interior extends AbstractHitInfo {

		/**
		 * construct a new <code>HitInfo.Interior</code> that occured on the
		 * given <code>Element</code>
		 */
		public Interior(Element clickedObject, PEMouseEvent mouseEvent){
			super(clickedObject, mouseEvent);
		}

		public String toString(){
			return "HitInfo.Interior : target="+getTarget();
		}
	}

	///////////////////////////////// POINT ////////////////////////////////////////
	/**
	 * Represent a hit that occured on one or several <code>Element</code>'s
	 * control-points, the latter case occuring necessarily iff some
	 * control-points are identical.
	 */
	public static class Point extends AbstractHitInfo {

		private ArrayList<Integer> pointIndexArray=new ArrayList<Integer>();

		/**
		 * construct a new <code>HitInfo.Point</code> that occured on the
		 * given <code>Element</code> and on the given point index
		 */
		public Point(Element clickedObject, int pointIndex, PEMouseEvent mouseEvent){
			super(clickedObject, mouseEvent);
			pointIndexArray.add(new Integer(pointIndex));
		}

		/**
		 * construct a new <code>HitInfo.Point</code> that occured on the
		 * given <code>Element</code> and on the given points simultaneously.<br/>
		 * Note that the ArrayList argument is not deeply copied.
		 */
		public Point(Element clickedObject, ArrayList<Integer> pointIndices, PEMouseEvent mouseEvent){
			super(clickedObject, mouseEvent);
			this.pointIndexArray = pointIndices;
		}

		/**
		 * @return the point index on which the mouse hit occured. If several
		 * control-points were hit simultaneously because they were located at
		 * the same place, return the first one in ascending order according
		 * to the natural ordering of control-point indices.
			 */
		public int getIndex(){
			return getIndex(0);
		}

		/**
		 * @return the i<sup>th</sup> point index on which the mouse hit occured. If only
		 * one control-point was hit, this is similar as
		 * <code>getIndex()</code>.
		 */
		public int getIndex(int i){
			return ((Integer)pointIndexArray.get(i)).intValue();
		}

		/**
		 * Return the number of points that were hit simultaneously.
		 */
		public int getNbHitPoints(){
			return pointIndexArray.size();
		}

		public String toString(){
			return "HitInfo.Point : target="+getTarget()+", idx="+getIndex();
		}
	}

	///////////////////////////////// STROKE ////////////////////////////////////////
	/**
	 * Represent a Hit that occured on an <code>Element</code>. It includes
	 * information as of which stroke segment was hit (this may be used by the UI to know
	 * where to add new points).
	 */
	public static class Stroke extends AbstractHitInfo {

		private int clickedSegment;


		/**
		 * construct a new <code>HitInfo.Stroke</code> that occured on the
		 * stroke of the given <code>Element</code>
		 * @param clickedSegment Point the index (starting from 0) of the segment of the flattened path
		 * on which the hit occured.
		 */
		public Stroke(Element clicked, int clickedSegment, PEMouseEvent mouseEvent){
			super(clicked, mouseEvent);
			this.clickedSegment = clickedSegment;
		}

		/**
		 * <br><b>author:</b> Sylvain Reynal
		 * @return return the child on which the mouse-click occured
		 * @since jPicEdt
		 */
		public int getClickedSegment(){
			return clickedSegment;
		}

		public String toString(){
			return "HitInfo.Stroke : target="+getTarget()+", clickedSegment="+clickedSegment;
		}

	}


	///////////////////////////////// HIGHLIGHTER_STROKE ////////////////////////////////////////
	/**
	 * Represent a hit that occured on the stroke of a highlighter (eg
	 * tangents of a <code>PicSmoothPolygon</code>). It includes information
	 * as of which stroke segment was hit (this may be used by the UI to know
	 * where to add new points).
	 */
	public static class HighlighterStroke extends Stroke {

		public HighlighterStroke(Element clicked, int clickedSegment, PEMouseEvent mouseEvent){
			super(clicked, clickedSegment, mouseEvent);
		}
		public String toString(){
			return "HitInfo.HighlighterStroke : target="+getTarget()+", clickedSegment="+getClickedSegment();
		}

	}

	///////////////////////////////// ENVELOPE_STROKE ////////////////////////////////////////
	/**
	 * Represent a Hit that occured on the envelope of an element (aka
	 * tangents of a PicSmoothPolygon actually). It includes information as of
	 * which stroke segment was hit (this may be used by the UI to know where
	 * to add new points).
	 * @deprecated use HighlighterStroke instead
	 */
	public static class EnvelopeStroke extends Stroke {

		public EnvelopeStroke(Element clicked, int clickedSegment, PEMouseEvent mouseEvent){
			super(clicked, clickedSegment, mouseEvent);
		}
		public String toString(){
			return "HitInfo.EnvelopeStroke : target="+getTarget()+", clickedSegment="+getClickedSegment();
		}

	}

	///////////////////////////////// COMPOSITE ////////////////////////////////////////
	/**
	 * Represent a particular type of Hit that occured on a child of a branch-element, when
	 * one whants to specify both the element and its child. This can be considered an extended
	 * version of <code>HitInfo.Point</code> where the the "clicked" child play the role of the
	 * "clicked" control-point.
	 */
	public static class Composite extends AbstractHitInfo {

		private int clickedChildIndex;

		/**
		 * construct a new <code>HitInfo.Point</code> that occured in the given BranchElement, on
		 * the child with the given index.
		 */
		public Composite(BranchElement clicked, int clickedChildIndex, PEMouseEvent mouseEvent){
			super(clicked, mouseEvent);
			this.clickedChildIndex = clickedChildIndex;
		}

		public BranchElement getTarget(){
			return (BranchElement)clickedObject;
		}

		/**
		 * @return the child on which the mouse-click occured
		 */
		public int getClickedChildIndex(){
			return clickedChildIndex;
		}

		/**
		 * @return the child on which the mouse-click occured
		 */
		public Element getClickedChild(){
			return ((BranchElement)getTarget()).get(clickedChildIndex);
		}
		public String toString(){
			return "HitInfo.Composite : target="+getTarget()+", clickedChild="+getClickedChild();
		}
	}

	///////////////////////////////// TEXT ////////////////////////////////////////
	/**
	 * Represent a Hit that occured on some interior area of a
	 * TextEditable. This is simply a convenient wrapper for {@link
	 * java.awt.font.TextHitInfo java.awt.font.TextHitInfo}.
	 */
	public static class Text extends HitInfo.Interior {

		private TextHitInfo thi;

		/**
		 * construct a new <code>HitInfo.Text</code> that occured on the given
		 * <code>TextEditable</code>, fetching useful information from the
		 * given <code>TextHitInfo</code>.
		 */
		public Text(TextEditable clickedObject, TextHitInfo thi, PEMouseEvent mouseEvent){
			super(clickedObject, mouseEvent);
			this.thi = thi;
		}

		/**
		 * @return the {@link java.awt.font.TextHitInfo
		 * java.awt.font.TextHitInfo} wrapped into this <code>HitInfo</code>.
		 */
		public TextHitInfo getTextHitInfo(){
			return thi;
		}

		public String toString(){
			return "HitInfo.Text : target="+getTarget()+", charIdx="+thi.getCharIndex()+", insertIdx="+thi.getInsertionIndex();
		}
	}
}
