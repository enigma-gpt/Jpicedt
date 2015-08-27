// ArrowView.java --- -*- coding: iso-8859-1 -*-
// August 29, 2006 - jPicEdt, a picture editor for LaTeX.
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
// Version: $Id: ArrowView.java,v 1.8 2013/03/27 06:55:16 vincentb1 Exp $
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
package jpicedt.graphic.view;

import jpicedt.graphic.model.StyleConstants;
import jpicedt.graphic.model.PicAttributeSet;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;

import java.util.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import java.io.*; // for test

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.ArrowStyle.*;
import static jpicedt.graphic.view.ArrowView.Pencil.*;

/**
 * A class implementing a view for the arrow attribute. A <code>syncAttributes()</code> method allows for
 * synchronizing the current attribute set with the arrow parameters used by the <code>paint()</code>
 * method.<p> This class is also a container for predefined arrows (implemented as static inner classes).
 * @author Sylvain Reynal
 * @since jPicEdt 1.5
 * @version $Id: ArrowView.java,v 1.8 2013/03/27 06:55:16 vincentb1 Exp $
 */
public abstract class ArrowView {

	public static enum Direction {LEFT,RIGHT}; // [pending] move to StyleConstants???

	public static enum Pencil {FILL, DRAW};

	protected double globalScaleWidth, globalScaleLength;
	protected float lineWidth;

	/**
	 * Configure the parameters of this arrow (size, scale, inset, ...) from the
	 * given attribute set, aka view/model synchronization. Note that (according to PsTricks doc),
	 * attributes apply to both arrows at once for a given element; hence there's no
	 * need to separate left and right attributes.
	 * This implementation simply synchronizes global scale factors (ARROW_SCALE_WIDTH and
	 * ARROW_SCALE_LENGTH), and should be overriden in each concrete subclass.
	 * <p>
	 * Usage: this method should invoked by the view responsible for rendering the drawing, whenever
	 * synchronization is needed b/w the view and the model, i.e. whenever at least one attribute value changed.
	 */
	public ArrowView(PicAttributeSet set){
		this.globalScaleLength = set.getAttribute(ARROW_GLOBAL_SCALE_LENGTH);
		this.globalScaleWidth = set.getAttribute(ARROW_GLOBAL_SCALE_WIDTH);
		this.lineWidth = set.getAttribute(LINE_WIDTH).floatValue();
	}

	//////////////////////////////////////////////////
	//// PAINT and related
	//////////////////////////////////////////////////

	/**
	 * Updates then paints this arrow. Convenient call to #paint(Graphics2D, double, double, PicVector).
	 * @param g graphic context
	 * @param loc arrow reference point on the screen (that is, line end-point)
	 * @param dir a vector of length 1 indicating the direction of the arrow (see PEToolKit.getDirector...)
	 */
	public void paint(Graphics2D g, PicPoint loc, PicVector dir){
		paint(g,loc.x,loc.y,dir);
	}

	/**
	 * Updates then paints this arrow.
	 * @param g graphic context
	 * @param locX X-coord of arrow reference point on the screen (that is, line end-point)
	 * @param locY Y-coord of arrow reference point on the screen (that is, line end-point)
	 * @param dir a vector of length 1 indicating the direction of the arrow (see PEToolKit.getDirector...)
	 */
	 public void paint(Graphics2D g, double locX, double locY, PicVector dir){
		updateShape(locX, locY, dir);
		paint(g);
	 }

	/**
	 * Paints this arrow.
	 * @param g graphic context
	 */
	 public void paint(Graphics2D g){
		switch (getPencil()){
		case DRAW:
			g.draw(getShape());
			break;
		case FILL:
			g.fill(getShape());
			break;
		}
	 }

	 /**
	  * To be called when the geometry of the hosting Element changed. Note that whenever
	  * attributes changed, a whole new Arrow should be instanciated.
	 * @param locX X-coord of arrow reference point on the screen (that is, line end-point)
	 * @param locY Y-coord of arrow reference point on the screen (that is, line end-point)
	 * @param dir a vector of length 1 indicating the direction of the arrow (see PEToolKit.getDirector...)
	  */
	abstract public void updateShape(double locX, double locY, PicVector dir);

	 /**
	  * To be called when the geometry of the hosting Element changed. Note that whenever
	  * attributes changed, a whole new Arrow should be instanciated.
	  */
	public void updateShape(PicPoint loc, PicVector dir){
		updateShape(loc.x, loc.y, dir);
	}

	/** whether we call g.draw() or g.fill() */
	public abstract Pencil getPencil();

	public abstract Shape getShape();


	////////////////////////////////////////////////
	/// STATIC METHODS
	////////////////////////////////////////////////

	/**
	 * Return a View for the given arrow style.
	 */
	public static ArrowView createArrowView(StyleConstants.ArrowStyle arrow, PicAttributeSet set){
		switch (arrow){
	        case ARROW_HEAD:
			return new Head(set);
	        case REVERSE_ARROW_HEAD:
			return new ReverseHead(set);
	        case DOUBLE_ARROW_HEAD:
			return new DoubleHead(set);
	        case DOUBLE_REVERSE_ARROW_HEAD:
			return new DoubleReverseHead(set);
	        case T_BAR_CENTERED:
			return new TBarCentered(set);
	        case T_BAR_FLUSHED:
			return new TBarFlushed(set);
	        case SQUARE_BRACKET:
			return new SquareBracket(set);
	        case ROUNDED_BRACKET:
			return new RoundedBracket(set);
	        case CIRCLE_FLUSHED:
			return new CircleFlushed(set);
	        case CIRCLE_CENTERED:
			return new CircleCentered(set);
	        case DISK_FLUSHED:
			return new DiskFlushed(set);
	        case DISK_CENTERED:
			return new DiskCentered(set);
	        case NONE:
		default:
			return null;
		}
	}

	/**
	 * build icons for e.g. ComboBoxlists from predefined arrows
	 * @param direction specifies arrow direction, <code>LEFT</code> for left
	 * arrow, <code>RIGHT</code> for right arrow
	 */
	public static Map<StyleConstants.ArrowStyle,ImageIcon> createArrowIcons(Direction direction){

		PicAttributeSet set = new PicAttributeSet(); // used to feed arrows with default attribute values
		set.setAttribute(LINE_WIDTH, 4.0);
		BufferedImage im;
		EnumMap<StyleConstants.ArrowStyle,ImageIcon> arrowToIconMap = new EnumMap<StyleConstants.ArrowStyle,ImageIcon>(StyleConstants.ArrowStyle.class);
		double w = 50;
		double h = 32;
		double x = 0.9*w;
		double y = 0.5*h;
		for (StyleConstants.ArrowStyle arrow: StyleConstants.ArrowStyle.values()){
			im = new BufferedImage((int)w,(int)h,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = im.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			if (direction==Direction.LEFT) g.rotate(Math.PI,w/2,h/2); // mirror around central point
			// fill background with white
			g.setStroke(new BasicStroke(1.0f));
			g.setPaint(Color.white);
			g.fill(new Rectangle2D.Double(0,0,w,h));
			// paint a vertical red line to mark end-point (esp. useful where one's want to know the difference b/w FLUSHED and CENTERED arrows)
			g.setStroke(new BasicStroke(1.0f));
			g.setPaint(Color.red);
			g.draw(new Line2D.Double(x,0,x,h));
			// paint an horizontal black line to figure line's end
			g.setStroke(new BasicStroke((float)(0.8)));
			g.setPaint(Color.black);
			g.draw(new Line2D.Double(0,y,0.9*x,y));
			// paint arrow in black
			g.setPaint(Color.black);
			//	    PicPoint endPoint = new PicPoint(12,8);
			PicPoint endPoint = new PicPoint(x,y);
			PicVector director = new PicVector(1,0);
			// 	    g.translate(34.0,8.0);
			// 	    g.scale(2.0,2.0);
			//g.translate(-4.0,-4.0);
			ArrowView view = createArrowView(arrow,set);
			if (view != null){
				view.paint(g, endPoint, director); // lineWidth = 4.0
			}
			// fill image buffer
			ImageIcon ii = new ImageIcon(im);
			arrowToIconMap.put(arrow,ii);
		}
		return arrowToIconMap;
	}

	public static void main(String arg[]){

		JFrame f = new JFrame();
		f.getContentPane().setLayout(new BorderLayout(5,5));
		JComboBox cb = new jpicedt.widgets.PEComboBox<StyleConstants.ArrowStyle>(createArrowIcons(Direction.RIGHT));
		Box box = new Box(BoxLayout.X_AXIS);
		box.add(cb);
		f.getContentPane().add(box);
		f.pack();
		f.setVisible(true);
	}





	//////////////////////////////////////////////////////////////////////////
	public static class Head extends ArrowView {

		// arrow-width = width_MinimumMm + width_LineWidthScale * lineWidth
		private GeneralPath path = new GeneralPath();
		private double length, width, inset;
		private PicVector ortho = new PicVector();

		public Head(PicAttributeSet set){
			super(set);
			width = set.getAttribute(ARROW_WIDTH_MINIMUM_MM) + set.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE) * lineWidth;
			length = width * set.getAttribute(ARROW_LENGTH_SCALE);
			inset = length * set.getAttribute(ARROW_INSET_SCALE);
			width *= globalScaleWidth; // from superclass
			length *= globalScaleLength;
			inset *= globalScaleLength;
		}

		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			path.reset();
			path.moveTo((float)locX, (float)locY);
			path.lineTo((float)(locX - length*dir.x + width*0.5f*ortho.x), (float)(locY - length*dir.y + width*0.5f*ortho.y));
			path.lineTo((float)(locX - (length-inset)*dir.x), (float)(locY - (length-inset)*dir.y));
			path.lineTo((float)(locX - length*dir.x - width*0.5f*ortho.x), (float)(locY - length*dir.y - width*0.5f*ortho.y));
			path.closePath();
		}

		public Pencil getPencil(){
			return FILL;
		}

		public Shape getShape(){
			return path;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class ReverseHead extends ArrowView {
		// arrow-width = width_MinimumMm + width_LineWidthScale * lineWidth
		private GeneralPath path = new GeneralPath();
		private double length, width, inset;
		private PicVector ortho = new PicVector();

		public ReverseHead(PicAttributeSet set){
			super(set);
			width = set.getAttribute(ARROW_WIDTH_MINIMUM_MM) + set.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE) * lineWidth;
			length = width * set.getAttribute(ARROW_LENGTH_SCALE);
			inset = length * set.getAttribute(ARROW_INSET_SCALE);
			width *= globalScaleWidth; // from superclass
			length *= globalScaleLength;
			inset *= globalScaleLength;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			PicPoint loc1 = new PicPoint(locX - length*dir.x, locY - length*dir.y);
			double loc1X = locX - length*dir.x;
			double loc1Y = locY - length*dir.y;
			path.reset();
			path.moveTo((float)loc1X, (float)loc1Y);
			path.lineTo((float)(loc1X + length*dir.x + width*0.5f*ortho.x), (float)(loc1Y + length*dir.y + width*0.5f*ortho.y));
			path.lineTo((float)(loc1X + (length-inset)*dir.x), (float)(loc1Y + (length-inset)*dir.y));
			path.lineTo((float)(loc1X + length*dir.x - width*0.5f*ortho.x), (float)(loc1Y + length*dir.y - width*0.5f*ortho.y));
			path.closePath();
		}

		public Pencil getPencil(){
			return FILL;
		}
		public Shape getShape(){
			return path;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class DoubleHead extends ArrowView {
		// arrow-width = width_MinimumMm + width_LineWidthScale * lineWidth
		private GeneralPath path = new GeneralPath();
		private double length, width, inset;
		private PicVector ortho = new PicVector();

		public DoubleHead(PicAttributeSet set){
			super(set);
			width = set.getAttribute(ARROW_WIDTH_MINIMUM_MM) + set.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE) * lineWidth;
			length = width * set.getAttribute(ARROW_LENGTH_SCALE);
			inset = length * set.getAttribute(ARROW_INSET_SCALE);
			width *= globalScaleWidth; // from superclass
			length *= globalScaleLength;
			inset *= globalScaleLength;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			double loc1X = locX - length*dir.x;
			double loc1Y = locY - length*dir.y;
			path.reset();
			path.moveTo((float)locX, (float)locY);
			path.lineTo((float)(locX - length*dir.x + width*0.5f*ortho.x), (float)(locY - length*dir.y + width*0.5f*ortho.y));
			path.lineTo((float)(locX - (length-inset)*dir.x), (float)(locY - (length-inset)*dir.y));
			path.lineTo((float)(locX - length*dir.x - width*0.5f*ortho.x), (float)(locY - length*dir.y - width*0.5f*ortho.y));
			path.moveTo((float)loc1X, (float)loc1Y);
			path.lineTo((float)(loc1X - length*dir.x + width*0.5f*ortho.x), (float)(loc1Y - length*dir.y + width*0.5f*ortho.y));
			path.lineTo((float)(loc1X - (length-inset)*dir.x), (float)(loc1Y - (length-inset)*dir.y));
			path.lineTo((float)(loc1X - length*dir.x - width*0.5f*ortho.x), (float)(loc1Y - length*dir.y - width*0.5f*ortho.y));
			path.closePath();
		}

		public Pencil getPencil(){
			return FILL;
		}
		public Shape getShape(){
			return path;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class DoubleReverseHead extends ArrowView {
		// arrow-width = width_MinimumMm + width_LineWidthScale * lineWidth
		private GeneralPath path = new GeneralPath();
		private double length, width, inset;
		private PicVector ortho = new PicVector();

		public DoubleReverseHead(PicAttributeSet set){
			super(set);
			width = set.getAttribute(ARROW_WIDTH_MINIMUM_MM) + set.getAttribute(ARROW_WIDTH_LINEWIDTH_SCALE) * lineWidth;
			length = width * set.getAttribute(ARROW_LENGTH_SCALE);
			inset = length * set.getAttribute(ARROW_INSET_SCALE);
			width *= globalScaleWidth; // from superclass
			length *= globalScaleLength;
			inset *= globalScaleLength;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			path.reset();

			double loc1X = locX - length*dir.x;
			double loc1Y = locY - length*dir.y;
			path.moveTo((float)loc1X, (float)loc1Y);
			path.lineTo((float)(loc1X + length*dir.x + width*0.5f*ortho.x), (float)(loc1Y + length*dir.y + width*0.5f*ortho.y));
			path.lineTo((float)(loc1X + (length-inset)*dir.x), (float)(loc1Y + (length-inset)*dir.y));
			path.lineTo((float)(loc1X + length*dir.x - width*0.5f*ortho.x), (float)(loc1Y + length*dir.y - width*0.5f*ortho.y));

			double loc2X = locX - 2.0*length*dir.x;
			double loc2Y = locY - 2.0*length*dir.y;
			path.moveTo((float)loc2X, (float)loc2Y);
			path.lineTo((float)(loc2X + length*dir.x + width*0.5f*ortho.x), (float)(loc2Y + length*dir.y + width*0.5f*ortho.y));
			path.lineTo((float)(loc2X + (length-inset)*dir.x), (float)(loc2Y + (length-inset)*dir.y));
			path.lineTo((float)(loc2X + length*dir.x - width*0.5f*ortho.x), (float)(loc2Y + length*dir.y - width*0.5f*ortho.y));
			path.closePath();
		}

		public Pencil getPencil(){
			return FILL;
		}
		public Shape getShape(){
			return path;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class TBarCentered extends ArrowView {

		private double width; // width = width_MinimumMm + width_LineWidthScale * lineWidth
		private Line2D.Double line = new Line2D.Double();
		private PicVector ortho = new PicVector();

		public TBarCentered(PicAttributeSet set){
			super(set);
			double width_MinimumMm = set.getAttribute(TBAR_WIDTH_MINIMUM_MM);
			double width_LineWidthScale = set.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE);
			this.width = (width_MinimumMm + width_LineWidthScale * lineWidth) * globalScaleWidth;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			line.x1=locX - 0.5*width*ortho.x;
			line.y1=locY - 0.5*width*ortho.y;
			line.x2=locX + 0.5*width*ortho.x;
			line.y2=locY + 0.5*width*ortho.y;
		}

		public Pencil getPencil(){
			return DRAW;
		}
		public Shape getShape(){
			return line;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class TBarFlushed extends ArrowView {

		private double width; // width = width_MinimumMm + width_LineWidthScale * lineWidth
		private Line2D.Double line = new Line2D.Double();
		private PicVector ortho = new PicVector();

		public TBarFlushed(PicAttributeSet set){
			super(set);
			double width_MinimumMm = set.getAttribute(TBAR_WIDTH_MINIMUM_MM);
			double width_LineWidthScale = set.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE);
			this.width = (width_MinimumMm + width_LineWidthScale * lineWidth) * globalScaleWidth;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			line.x1=locX - 0.5*width*ortho.x - 0.5*lineWidth*dir.x;
			line.y1=locY - 0.5*width*ortho.y- 0.5*lineWidth*dir.y;
			line.x2=locX + 0.5*width*ortho.x- 0.5*lineWidth*dir.x;
			line.y2=locY + 0.5*width*ortho.y- 0.5*lineWidth*dir.y;
		}

		public Pencil getPencil(){
			return DRAW;
		}
		public Shape getShape(){
			return line;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class SquareBracket extends ArrowView {

		private double width, length; // width = width_MinimumMm + width_LineWidthScale * lineWidth
		private GeneralPath path = new GeneralPath();
		private PicVector ortho = new PicVector();

		public SquareBracket(PicAttributeSet set){
			super(set);
			double width_MinimumMm = set.getAttribute(TBAR_WIDTH_MINIMUM_MM);
			double width_LineWidthScale = set.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE);
			double lengthScale = set.getAttribute(BRACKET_LENGTH_SCALE);
			width = (width_MinimumMm + width_LineWidthScale * lineWidth) * globalScaleWidth;
			length = width * lengthScale * globalScaleLength;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			path.reset();
			path.moveTo((float)(locX - 0.5*width*ortho.x - (length+lineWidth)*dir.x), (float)(locY - 0.5*width*ortho.y - (length+lineWidth)*dir.y)); //3
			path.lineTo((float)(locX - 0.5*width*ortho.x - 0.5*lineWidth*dir.x), (float)(locY - 0.5*width*ortho.y- 0.5*lineWidth*dir.y)); // 2
			path.lineTo((float)(locX + 0.5*width*ortho.x - 0.5*lineWidth*dir.x), (float)(locY + 0.5*width*ortho.y- 0.5*lineWidth*dir.y));//9
			path.lineTo((float)(locX + 0.5*width*ortho.x - (length+lineWidth)*dir.x), (float)(locY + 0.5*width*ortho.y - (length+lineWidth)*dir.y));//8
		}

		public Pencil getPencil(){
			return DRAW;
		}
		public Shape getShape(){
			return path;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class RoundedBracket extends ArrowView {

		private double width, length; // width = width_MinimumMm + width_LineWidthScale * lineWidth
		private QuadCurve2D.Double curve = new QuadCurve2D.Double();
		private PicVector ortho = new PicVector();

		public RoundedBracket(PicAttributeSet set){
			super(set);
			double width_MinimumMm = set.getAttribute(TBAR_WIDTH_MINIMUM_MM);
			double width_LineWidthScale = set.getAttribute(TBAR_WIDTH_LINEWIDTH_SCALE);
			double lengthScale = set.getAttribute(RBRACKET_LENGTH_SCALE);
			width = (width_MinimumMm + width_LineWidthScale * lineWidth) * globalScaleWidth;
			length = width * lengthScale * globalScaleLength;
		}
		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			curve.x1=locX - (length+lineWidth)*dir.x - 0.5*width*ortho.x;
			curve.y1=locY - (length+lineWidth)*dir.y - 0.5*width*ortho.y;
			curve.ctrlx=locX;
			curve.ctrly=locY;
			curve.x2=locX - (length+lineWidth)*dir.x + 0.5*width*ortho.x;
			curve.y2=locY - (length+lineWidth)*dir.y + 0.5*width*ortho.y;
		}

		public Pencil getPencil(){
			return DRAW;
		}
		public Shape getShape(){
			return curve;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class CircleFlushed extends ArrowView {

		private double diameter; // diam = lineWidth * dotSize_LineWidthScale + dotSize_MinimumMm
		private Ellipse2D.Double ell = new Ellipse2D.Double();
		private PicVector ortho = new PicVector();

		public CircleFlushed(PicAttributeSet set){
			super(set);
			double dotSize_LineWidthScale = set.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
			double dotSize_MinimumMm = set.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
			//double diameter = DOT_SIZE[0] + DOT_SIZE[1] * lineWidth;
			diameter = dotSize_MinimumMm + dotSize_LineWidthScale * lineWidth;
		}

		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			ell.x=locX - 0.5*diameter*(dir.x+1.0);
			ell.y=locY - 0.5*diameter*(dir.y+1.0);
			ell.width=diameter;
			ell.height=diameter;
		}

		public Pencil getPencil(){
			return DRAW;
		}
		public Shape getShape(){
			return ell;
		}
	}

	//////////////////////////////////////////////////////////////////////////
	public static class CircleCentered extends ArrowView {

		private double diameter; // diam = lineWidth * dotSize_LineWidthScale + dotSize_MinimumMm
		private Ellipse2D.Double ell = new Ellipse2D.Double();
		private PicVector ortho = new PicVector();

		public CircleCentered(PicAttributeSet set){
			super(set);
			double dotSize_LineWidthScale = set.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
			double dotSize_MinimumMm = set.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
			//double diameter = DOT_SIZE[0] + DOT_SIZE[1] * lineWidth;
			diameter = dotSize_MinimumMm + dotSize_LineWidthScale * lineWidth;
		}

		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			ell.x=locX - diameter*0.5;
			ell.y=locY - diameter*0.5;
			ell.width=diameter;
			ell.height=diameter;
		}

		public Pencil getPencil(){
			return DRAW;
		}
		public Shape getShape(){
			return ell;
		}
	}
	//////////////////////////////////////////////////////////////////////////
	public static class DiskFlushed extends ArrowView {
		private double diameter; // diam = lineWidth * dotSize_LineWidthScale + dotSize_MinimumMm
		private Ellipse2D.Double ell = new Ellipse2D.Double();
		private PicVector ortho = new PicVector();

		public DiskFlushed(PicAttributeSet set){
			super(set);
			double dotSize_LineWidthScale = set.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
			double dotSize_MinimumMm = set.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
			//double diameter = DOT_SIZE[0] + DOT_SIZE[1] * lineWidth;
			diameter = dotSize_MinimumMm + dotSize_LineWidthScale * lineWidth;
		}

		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			ell.x=locX - 0.5*diameter*(dir.x+1.0);
			ell.y=locY - 0.5*diameter*(dir.y+1.0);
			ell.width=diameter;
			ell.height=diameter;
		}

		public Pencil getPencil(){
			return FILL;
		}
		public Shape getShape(){
			return ell;
		}
	}
	//////////////////////////////////////////////////////////////////////////
	public static class DiskCentered extends ArrowView {
		private double diameter; // diam = lineWidth * dotSize_LineWidthScale + dotSize_MinimumMm
		private Ellipse2D.Double ell = new Ellipse2D.Double();
		private PicVector ortho = new PicVector();

		public DiskCentered(PicAttributeSet set){
			super(set);
			double dotSize_LineWidthScale = set.getAttribute(POLYDOTS_SIZE_LINEWIDTH_SCALE);
			double dotSize_MinimumMm = set.getAttribute(POLYDOTS_SIZE_MINIMUM_MM);
			//double diameter = DOT_SIZE[0] + DOT_SIZE[1] * lineWidth;
			diameter = dotSize_MinimumMm + dotSize_LineWidthScale * lineWidth;
		}


		public void updateShape(double locX, double locY, PicVector dir){
			ortho.setCoordinates(-dir.y, dir.x); // orthogonal unitary vector
			ell.x=locX - diameter*0.5;
			ell.y=locY - diameter*0.5;
			ell.width=diameter;
			ell.height=diameter;
		}

		public Pencil getPencil(){
			return FILL;
		}
		public Shape getShape(){
			return ell;
		}
	}

}
