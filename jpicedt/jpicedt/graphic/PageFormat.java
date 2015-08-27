// PageFormat.java --- -*- coding: iso-8859-1 -*-
// February 15, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2006 Sylvain Reynal
//
// Author: Sylvain Reynal
//         Département de Physique
//         École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
//         6, avenue du Ponceau
//         95014 CERGY CEDEX
//         FRANCE
//
// Tel : +33 130 736 245
// Fax : +33 130 736 667
// e-mail : reynal@ensea.fr
// Version: $Id: PageFormat.java,v 1.20 2013/03/27 07:00:53 vincentb1 Exp $
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
package jpicedt.graphic;

import jpicedt.graphic.toolkit.*;
import jpicedt.widgets.DecimalNumberField;
import jpicedt.Localizer;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import javax.swing.*;

import static jpicedt.Localizer.*;
import static java.lang.Math.round;
import static java.lang.Math.max;

/**
 * Size and margins data for a {@link jpicedt.graphic.PECanvas PECanvas}.
 * <p>
 * This class provides a convenient way of converting b/w millimeters (ie model) coordinates and
 * pixel coordinates. The dot-per-mm scale factor is computed from the AWT's ToolKit.
 * <p>
 * [SR:todo] adapt to fit with {@link java.awt.print#PageFormat PageFormat}.
 * @since jpicedt 1.3.2
 * @author Sylvain Reynal
 * @version $Id: PageFormat.java,v 1.20 2013/03/27 07:00:53 vincentb1 Exp $
 */
public class PageFormat {

	/** screen dot per mm ; used during coordinates translation from model to view.<br>
	 *  the effect is to render 1 cm as a "true" cm (or approximately) on the screen
	 */
	public static final double DPMM = 0.1 * round(Toolkit.getDefaultToolkit().getScreenResolution() / 2.54);

	private double widthMm, heightMm, leftMarginMm, bottomMarginMm;

	// [SR:todo] define default (A4, Letter,...)

	/** key used to retrieve parameters from Properties */
	public static final String KEY_PAGE_FORMAT = "view.page-format";

	public static final double widthMmDEFAULT = 170;
	public static final double heightMmDEFAULT = 100;
	public static final double leftMarginMmDEFAULT = 5;
	public static final double bottomMarginMmDEFAULT = 5;


	/**
	 * Construct a new PageFormat with the default values (170,100,5,5)
	 */
	public PageFormat(){
		this(null);
	}

	/**
	 * Construct a new PageFormat with values fetched from the given Properties : <br>
	 * Key = KEY_PAGE_FORMAT<br>
	 * Value = width height leftMargin bottomMargin (separated with spaces)<br>
	 * If some values aren't found (including Properties's default), the local default values are used.
	 */
	public PageFormat(Properties preferences){
		double[] parsed = parseValues(preferences);
		this.widthMm = parsed[0];
		this.heightMm = parsed[1];
		this.leftMarginMm = parsed[2];
		this.bottomMarginMm= parsed[3];
	}

	/**
	 * Return a four-element array (w,h,left,bottom) filled from the KEY_PAGE_FORMAT
	 * in the given Properties object, or with default values if not found.
	 * @param preferences if null, return default values.
	 */
	private static double[] parseValues(Properties preferences){
		double[] parsed = new double[4];
		parsed[0] = widthMmDEFAULT;
		parsed[1] = heightMmDEFAULT;
		parsed[2] = leftMarginMmDEFAULT;
		parsed[3] = bottomMarginMmDEFAULT;
		if (preferences == null) return parsed;
		String val = preferences.getProperty(KEY_PAGE_FORMAT);
		if (val == null) return parsed;
		StringTokenizer tok = new StringTokenizer(val," ");
		if (tok.hasMoreTokens()) parsed[0] = Double.parseDouble(tok.nextToken());
		if (tok.hasMoreTokens()) parsed[1] = Double.parseDouble(tok.nextToken());
		if (tok.hasMoreTokens()) parsed[2] = Double.parseDouble(tok.nextToken());
		if (tok.hasMoreTokens()) parsed[3] = Double.parseDouble(tok.nextToken());
		return parsed;
	}

	/**
	 * Construct a new PageFormat with lengths given in mm.
	 * @param widthMm page width in mm
	 * @param heightMm page height in mm
	 * @param leftMarginMm left margin in mm
	 * @param bottomMarginMm bottom margin in mm
	 */
	public PageFormat(double widthMm, double heightMm, double leftMarginMm, double bottomMarginMm){
		this.widthMm = widthMm;
		this.heightMm = heightMm;
		this.leftMarginMm = leftMarginMm;
		this.bottomMarginMm= bottomMarginMm;
	}

	/**
	 * Constructor a new PageFormat with length given in mm. Margins default to 0.
	 * @param widthMm page width in mm
	 * @param heightMm page height in mm
	 */
	public PageFormat(double widthMm, double heightMm){
		this(widthMm, heightMm, 0,0);
	}

	// ----------- mm coords --------------

	/**
	 * Return the page width in mm.
	 */
	public double getWidthMm(){
		return widthMm;
	}

	/**
	 * Set the page width in mm.
	 */
	public void setWidthMm(double w){
		widthMm = w;
	}

	/**
	 * Set the page height in mm.
	 */
	public void setHeightMm(double h){
		heightMm = h;
	}

	/**
	 * Return the page height in mm.
	 */
	public double getHeightMm(){
		return heightMm;
	}

	/**
	 * Sets the page size in millimeters
	 */
	public void setSizeMm(double w, double h){
		widthMm = w;
		heightMm = h;
	}

	/**
	 * Return the left-margin in mm
	 */
	public double getLeftMarginMm(){
		return leftMarginMm;
	}

	/**
	 * Return the bottom-margin in mm
	 */
	public double getBottomMarginMm(){
		return bottomMarginMm;
	}

	/**
	 * Set the left margin in mm
	 */
	public void setLeftMarginMm(double margin){
		leftMarginMm = margin;
	}

	/**
	 * Set the bottom margin in mm
	 */
	public void setBottomMarginMm(double margin){
		bottomMarginMm = margin;
	}

	/**
	 * Set the page margins in mm
	 */
	public void setMarginsMm(double left, double bottom){
		bottomMarginMm = bottom;
		leftMarginMm = left;
	}

	/**
	 * Returns whether the given rectangle (in mm units) fits into this page.
	 */
	public boolean isFitInto(Rectangle2D r){
		final double A = 1.0; // aka "security" factor
		boolean ok = (A*r.getMinX() > -this.getLeftMarginMm());
		ok = ok && (A*r.getMaxX() < this.getWidthMm() - this.getLeftMarginMm());
		ok = ok && (A*r.getMinY() > -this.getBottomMarginMm());
		ok = ok && (A*r.getMaxY() < this.getHeightMm() - this.getBottomMarginMm());
		return ok;
	}

	/**
	 * Adjusts this PageFormat so that it fits to the given rectangle (in mm)
	 */
	public void fitTo(Rectangle2D r){
		final double A = 1.5; // aka "security" factor
		this.leftMarginMm = -A*r.getMinX();
		if (leftMarginMm < 0) leftMarginMm = 0; // ensure (0,0) still belongs to the drawing sheet
		this.widthMm = A*(r.getMaxX() + leftMarginMm);
		if (this.widthMm < leftMarginMm) widthMm=leftMarginMm; // ibid.

		this.bottomMarginMm = -A*r.getMinY();
		if (bottomMarginMm < 0) bottomMarginMm = 0;
		this.heightMm = A*(r.getMaxY() + bottomMarginMm);
		if (heightMm < bottomMarginMm) heightMm = bottomMarginMm;
	}

	/**
	 * Adjusts this PageFormat so that it is not smaller than the given rectangle (in mm)
	 */
	public void enlargeTo(Rectangle2D r){
		final double A = 1.5; // aka "security" factor
		this.leftMarginMm = max(leftMarginMm,-A*r.getMinX());
		if (leftMarginMm < 0) leftMarginMm = 0; // ensure (0,0) still belongs to the drawing sheet
		this.widthMm = max(widthMm,A*(r.getMaxX() + leftMarginMm));
		if (this.widthMm < leftMarginMm) widthMm=leftMarginMm; // ibid.

		this.bottomMarginMm = max(bottomMarginMm,-A*r.getMinY());
		if (bottomMarginMm < 0) bottomMarginMm = 0;
		this.heightMm = max(A*(r.getMaxY() + bottomMarginMm),heightMm);
		if (heightMm < bottomMarginMm) heightMm = bottomMarginMm;
	}

	// -------------- pixels coords ------------------

	/**
	 * Return the page width in pixels.
	 * @param zoom the current zoom factor
	 */
	public int getWidthPx(double zoom){
		return (int)(DPMM * zoom * widthMm);
	}

	/**
	 * Return the page height in pixels.
	 * @param zoom the current zoom factor
	 */
	public int getHeightPx(double zoom){
		return (int)(DPMM * zoom * heightMm);
	}

	/**
	 * Return the page dimension (w,h) in pixels.
	 * @param zoom the current zoom factor
	 */
	public Dimension getSizePx(double zoom){
		return new Dimension(getWidthPx(zoom), getHeightPx(zoom));
	}


	/**
	 * Return the left margin in pixels, ie the x-coordinate of the (0,0) model origin.
	 * @param zoom the current zoom factor
	 */
	public int getXOrgPx(double zoom){
		return (int)(zoom * DPMM * leftMarginMm);
	}

	/**
	 * Return the y-coordinate in pixels (starting from the canvas's top-side) of the (0,0) model origin.
	 * @param zoom the current zoom factor
	 */
	public int getYOrgPx(double zoom){
		return (int)(zoom * DPMM * (heightMm-bottomMarginMm));
	}

	/**
	 * Return (x,y) double-precision pixel-coordinates of the (0,0) model origin.
	 * @param zoom the current zoom factor
	 */
	public PicPoint getOrgPx(double zoom){
		return new PicPoint(zoom * DPMM * leftMarginMm,
		                    zoom * DPMM * (heightMm-bottomMarginMm));
	}

	/**
	 * x' = zoom * DPMM * (x + leftMarginMm)<br>
	 * y' = zoom * DPMM * (-y + heightMm-bottomMarginMm)<br>
	 * Return the AffineTransform that translates from model to view coordinate using the
	 *         given zoom factor and this page format (ie most notably the left and bottom margins)
	 * @param zoom the current zoom factor
	 */
	public AffineTransform getModel2ViewTransform(double zoom){
		return new AffineTransform(
		           zoom*DPMM,0,
		           0,-zoom*DPMM,
		           zoom*DPMM*leftMarginMm,zoom*DPMM*(heightMm-bottomMarginMm));
	}

	/**
	 * x' = zoom * DPMM * (x + leftMarginMm)<br>
	 * y' = zoom * DPMM * (-y + heightMm-bottomMarginMm)<br>
	 * @return the AffineTransform that translate from view (or mouse) coordinates to model-coordinates
	 *         using the given zoom factor, and this page format (ie most notably the left and bottom margins)
	 * @param zoom the current zoom factor
	 */
	public AffineTransform getView2ModelTransform(double zoom){
		return new AffineTransform(
		           1/zoom/DPMM,0,
		           0,-1/zoom/DPMM,
		           -leftMarginMm,heightMm-bottomMarginMm);
	}

	/**
	 * Return a dialog for editing this PageFormat attached to the given canvas
	 * (a reference to the hosting canvas is needed so that setPageFormat() can
	 * be ultimately called when "ok" is pressed).
	 */
	public Customizer createCustomizer(PECanvas canvas){
		return new Customizer(this, canvas);
	}

	/**
	 * Return a dialog for editing the PageFormat's values stored in the given Properties.
	 */
	public static Customizer createCustomizer(Properties preferences){
		return new Customizer(preferences);
	}

	//////////////////////////////////// DIALOG BOX //////////////////////////////////

	/**
	 * a dialog box used to change a PageFormat
	 */
	public static class Customizer extends AbstractCustomizer {

		private DecimalNumberField widthMmTF, heightMmTF, leftMarginMmTF, bottomMarginMmTF;
		private Properties preferences;
		private PageFormat pageFormat;
		private PECanvas canvas;

		/**
		 * @param preferences this Properties object gets filled with the new PageFormat values if user
		 * presses ok.
		 */
		public Customizer(Properties preferences) {
			this.preferences = preferences;
			initGui();
		}

		/**
		 * @param pageFormat this PageFormat object gets filled with the new values if user
		 * presses ok.
		 */
		public Customizer(PageFormat pageFormat, PECanvas canvas) {
			this.pageFormat = pageFormat;
			this.canvas = canvas;
			initGui();
			load();
		}

		private void initGui(){
			JPanel p = new JPanel(new GridLayout(4,2,5,5));
			p.setBorder(BorderFactory.createEtchedBorder());
			p.add(new JLabel(" "+localize("misc.Width")+" (mm) :"));
			widthMmTF = new DecimalNumberField(0,10,true); // >=0
			p.add(widthMmTF);
			p.add(new JLabel(" "+localize("misc.Height")+" (mm) :"));
			heightMmTF = new DecimalNumberField(0,10,true); // >=0
			p.add(heightMmTF);
			p.add(new JLabel(" "+localize("misc.MarginLeft")+" (mm) :"));
			leftMarginMmTF = new DecimalNumberField(0,10);
			p.add(leftMarginMmTF);
			p.add(new JLabel(" "+localize("misc.MarginBottom")+" (mm) :"));
			bottomMarginMmTF = new DecimalNumberField(0,10);
			p.add(bottomMarginMmTF);
			add(p, BorderLayout.NORTH);
		}

		/**
		 * <br><b>author:</b> Sylvain Reynal
		 * @return the panel title, used e.g. for Border or Tabpane title.
		 * @since jPicEdt
		 */
		public String getTitle(){
			return localize("action.ui.PageFormat");
		}


		/**
		 * Load widgets display content with a default value,
		 * presumably from a "default preferences" file or a dedicated storage class.
		 * <br><b>author:</b> Sylvain Reynal
		 * @since jPicEdt
		 */
		public void loadDefault(){
			double[] parsed = parseValues(null);
			widthMmTF.setValue(parsed[0]);
			heightMmTF.setValue(parsed[1]);
			leftMarginMmTF.setValue(parsed[2]);
			bottomMarginMmTF.setValue(parsed[3]);
		}

		/**
		 * Load widgets value, presumably from a "preferences" file or a dedicated storage class
		 * <br><b>author:</b> Sylvain Reynal
		 * @since jPicEdt
		 */
		public void load(){
			if (preferences != null){
				double[] parsed = parseValues(preferences);
				widthMmTF.setValue(parsed[0]);
				heightMmTF.setValue(parsed[1]);
				leftMarginMmTF.setValue(parsed[2]);
				bottomMarginMmTF.setValue(parsed[3]);
			}
			else if (pageFormat != null){
				widthMmTF.setValue(pageFormat.widthMm);
				heightMmTF.setValue(pageFormat.heightMm);
				leftMarginMmTF.setValue(pageFormat.leftMarginMm);
				bottomMarginMmTF.setValue(pageFormat.bottomMarginMm);
			}
		}

		/**
		 * Store current widgets value, presumably to a file or to a dedicated storage class
		 * <br><b>author:</b> Sylvain Reynal
		 * @since jPicEdt
		 */
		public void store(){
			if (preferences != null){
				String str =  widthMmTF.getText() + " "
				              + heightMmTF.getText() + " "
				              + leftMarginMmTF.getText() + " "
				              + bottomMarginMmTF.getText();
				preferences.setProperty(KEY_PAGE_FORMAT,str);
			}
			else if (pageFormat != null){
				pageFormat.widthMm=widthMmTF.getValue();
				pageFormat.heightMm=heightMmTF.getValue();
				pageFormat.leftMarginMm=leftMarginMmTF.getValue();
				pageFormat.bottomMarginMm=bottomMarginMmTF.getValue();
				if (canvas != null)
					canvas.setPageFormat(pageFormat);
			}
		}
	}


} // PageFormat
