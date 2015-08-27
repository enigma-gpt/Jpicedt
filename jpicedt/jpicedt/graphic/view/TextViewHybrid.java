/*
 TextViewHybrid.java - February 9, 2002 - jPicEdt, a picture editor for LaTeX.
 Copyright (C) 1999-2007 Sylvain Reynal

 Departement de Physique
 École Nationale Supérieure de l'Électronique et de ses Applications (ENSEA)
 6, avenue du Ponceau
 F-95014 CERGY CEDEX

 Tel : +33 130 736 245
 Fax : +33 130 736 667
 e-mail : reynal@ensea.fr
 jPicEdt web page : http://www.jpicedt.org

*/
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

import jpicedt.graphic.model.*; // all elements + drawing
import jpicedt.graphic.PECanvas;
import jpicedt.graphic.PicPoint;
import jpicedt.graphic.PicVector;
import jpicedt.graphic.PEToolKit;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.event.DrawingEvent;
import jpicedt.graphic.ContentType;
import jpicedt.ui.util.RunExternalCommand;

import java.awt.*;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.lang.reflect.*;

import static jpicedt.graphic.model.PicAttributeName.*;
import static jpicedt.graphic.model.StyleConstants.*;
import static jpicedt.graphic.view.ViewConstants.*;
import static jpicedt.Log.*;

/**
 * a View for rendering Text's based on bitmaps (when available) or TextLayout
 * (when image not available)
 */
public class TextViewHybrid extends LeafElementView implements Runnable {


	boolean wantToComputeLatexDimensions=true;// from preferences: do we want to run LateX to compute preferences
	boolean wantToGetBitMap=true;// from preferences: do we want to run LateX+pstoimg to get a bitmap.
	//   wantToGetBitMap=true should Imply wantToComputeLatexDimensions=true

	protected double strx, stry; // TextLayout/Image location with respect to PicText's anchor point
	//[pending] pas joli. A enlever sous peu (utilise juste dans HitInfo que je ne comprends pas, je laisse donc en attendant)

	protected TextLayout textLayout; // the TextLayout that renders the text string of this TextEditable
	// [inherited] shape; But here it's the frame box !!! (not textLayout !)
	protected AffineTransform text2ModelTr=new AffineTransform();; // maps text coordinates to Model coordinates
	protected BufferedImage image; // bitmap (if null, we rely on TextLayout)
	protected boolean areDimensionsComputed;  // has the real dimensions been read from the log file ?
	protected int fileDPI = 300 ; // Dot Per Inch of the file (for image resizing) [pending] gerer les preferences de ce parametre
	// il faut passer la valeur en DPI en argument a create_bitmap.sh

	/** pattern used for parsing log file */
	private static final Pattern LogFilePattern = Pattern.compile("JPICEDT INFO:\\s*([0-9.]*)pt,\\s*([0-9.]*)pt,\\s*([0-9.]*)pt");
	private static final String CR_LF = System.getProperty("line.separator");

	private PicPoint ptBuf = new PicPoint();

	/**
	 * construct a new View for the given PicRectangle
	 */
	public TextViewHybrid(PicText te, AttributesViewFactory f){
		super(te,f);
		areDimensionsComputed=false;
		changedUpdate(null);
	}

	public PicText getElement(){
		return (PicText)element;
	}

	/**
	 * Returns the text rotation in radians : subclassers that don't support rotating text may return 0 here.
	 * Used by TextLayout only.
	 */
	protected double getRotation(){
		//			debug(set.getAttribute(TEXT_ROTATION).toString());
		return Math.toRadians(element.getAttribute(TEXT_ROTATION).doubleValue());
	}

	/**
	 * Give notification from the model that a change occured to the text this view is responsible
	 * for rendering.<p>
	 */
	public void changedUpdate(DrawingEvent.EventType eventType){
		PicText text = (PicText)element;
		if (textLayout==null){
			// new *************************** begin (by ss & bp)
			textLayout = new TextLayout(text.getText(text.getTextMode()).length()==0 ? " " :
                    text.getText(text.getTextMode()),
			// new *************************** end (by ss & bp)
			                            DefaultViewFactory.textFont, // static field
			                            new FontRenderContext(null,false,false));
		}
		if (eventType==DrawingEvent.EventType.TEXT_CHANGE) {
			// new *************************** begin (by ss & bp)
			textLayout = new TextLayout(text.getText(text.getTextMode()).length()==0 ? " " :
                   text.getText(text.getTextMode()),
			// new *************************** end (by ss & bp)
			                            DefaultViewFactory.textFont,
			                            new FontRenderContext(null,false,false));

			// first try to create a bitmap
			image=null; // aka "reset" image => we might temporarily resort to TextLayout until the image is ready
			areDimensionsComputed=false;
			//reset dimensions to the textlayout dimensions
			text.setDimensions(textLayout.getBounds().getWidth(),textLayout.getAscent(),textLayout.getDescent());

			// new *************************** begin (by ss & bp)
			if ( wantToComputeLatexDimensions && text.getText(text.getTextMode()).length()>0){
			// new *************************** end (by ss & bp)
				// don't produce a bitmap for an empty string (LaTeX might not like it)
				// [pending] this should be made dependent on a preference's option
				new Thread(this).start();
			}
			if (image==null) super.changedUpdate(null); // ie resort to TextLayout (update all)
		}
		else {
			text.updateFrame();
			super.changedUpdate(eventType);
		}
	}

	// Thread's run method aimed at creating a bitmap asynchronously
	public void run(){
		Drawing drawing = new Drawing();
		PicText rawPicText=new PicText();
		String s=((PicText)element).getText();
		rawPicText.setText(s);
		drawing.add(rawPicText); // bug fix: we must add a CLONE of the PicText, otherwise it loses it former parent... (then pb with the view )
		drawing.setNotparsedCommands("\\newlength{\\jpicwidth}\\settowidth{\\jpicwidth}{"+s+"}"+CR_LF
		                             + "\\newlength{\\jpicheight}\\settoheight{\\jpicheight}{" +s + "}"+CR_LF
		                             + "\\newlength{\\jpicdepth}\\settodepth{\\jpicdepth}{" +s + "}"+CR_LF
		                             + "\\typeout{JPICEDT INFO: \\the\\jpicwidth, \\the\\jpicheight,  \\the\\jpicdepth }"+CR_LF);
		RunExternalCommand.Command commandToRun = RunExternalCommand.Command.BITMAP_CREATION;
		//RunExternalCommand command = new RunExternalCommand(drawing, contentType,commandToRun);
		boolean isWriteTmpTeXfile=true;
		String bitmapExt = "png"; // [pending] preferences
		String cmdLine = "{i}/unix/tetex/create_bitmap.sh {p} {f} "+bitmapExt+" "+fileDPI; // [pending] preferences
		ContentType contentType = getContainer().getContentType();
		RunExternalCommand.isGUI=false; // System.out, no dialog box // [pending] debug
		RunExternalCommand command = new RunExternalCommand(drawing, contentType,cmdLine, isWriteTmpTeXfile);
		command.run(); // synchronous in an async. thread => it's ok (anyway, we must way until the LaTeX process has completed)


		if (wantToComputeLatexDimensions){
			// load size of text:
			try {
				File logFile = new File(command.getTmpPath(), command.getTmpFilePrefix()+".log");
				BufferedReader reader=null;
				try{
					reader = new BufferedReader(new FileReader(logFile));
				}
				catch (FileNotFoundException fnfe) {
					System.out.println("Cannot find log file! "+fnfe.getMessage());
					System.out.println(logFile);
				}
				catch (IOException ioex){System.out.println("Log file IO exception"); ioex.printStackTrace(); }//utile ?
				System.out.println("Log file created! file="+logFile);
				getDimensionsFromLogFile(reader,(PicText)element);
				syncStringLocation(); // update dimensions
				syncBounds();
				syncFrame();
				SwingUtilities.invokeLater(new Thread(){
					                           public void run(){
						                           repaint(null);
					                           }
				                           });
				//repaint(null); // now that dimensions are available, we force a repaint() [pending] smart-repaint ?
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}

		if (wantToGetBitMap){
			// load image:
			try {
				File bitmapFile = new File(command.getTmpPath(), command.getTmpFilePrefix()+"."+bitmapExt);
				this.image = ImageIO.read(bitmapFile);
				System.out.println("Bitmap created! file="+bitmapFile+", width="+image.getWidth()+"pixels, height="+image.getHeight()+"pixels");
				if (image==null) return;
				syncStringLocation(); //sets strx, stry, and dimensions of text
				syncBounds();
				// update the AffineTransform that will be applied to the bitmap before displaying on screen
				PicText te = (PicText)element;
				text2ModelTr.setToIdentity(); // reset
				PicPoint anchor = te.getCtrlPt(TextEditable.P_ANCHOR,ptBuf);
				text2ModelTr.rotate(getRotation(),anchor.x, anchor.y); // rotate along P_ANCHOR !
				text2ModelTr.translate(te.getLeftX(),te.getTopY());
				text2ModelTr.scale(te.getWidth()/image.getWidth(),-(te.getHeight()+te.getDepth())/image.getHeight());
				//[pending]  should do something special to avoid dividing by 0 or setting a rescaling factor to 0 [non invertible matrix] (java will throw an exception)
				syncFrame();
				SwingUtilities.invokeLater(new Thread(){
					                           public void run(){
						                           repaint(null);
					                           }
				                           });
				//repaint(null); // now that bitmap is available, we force a repaint() [pending] smart-repaint ?
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}


	/** update strx stry = location of TextLayout's bottom-Left corner with
	 * respect to PicText's anchor-point */
	protected void syncStringLocation(){
		PicText te = (PicText)element;
		PicPoint anchor = te.getCtrlPt(TextEditable.P_ANCHOR,ptBuf);
		if (image == null){
			if (! areDimensionsComputed){
				te.setDimensions(textLayout.getBounds().getWidth(),textLayout.getAscent(),textLayout.getDescent());
			}
			strx = te.getLeftX()-anchor.x;
			stry = te.getBaseLineY()-anchor.y;
		}
		else {//image not null
			strx = te.getLeftX()-anchor.x;
			stry = te.getBottomY()-anchor.y;
		}
	}


	/**
	 * Synchronize the textLayout and the shape (=frame box, by calling
	 * syncFrame) with the model When <code>TextLayout</code> is used, this
	 * delegates to <code>getRotation()</code> where computing rotation angle
	 * is concerned, and updates the AffineTransform returned by
	 * <code>getTextToModelTransform()</code>.
	 */
	protected void syncShape(){
		PicText te = (PicText)element;

		//			textLayout = new TextLayout(te.getText().length()==0 ? " " : te.getText(),
		//			    textFont,
		//			    new FontRenderContext(null,false,false));

		text2ModelTr.setToIdentity(); // reset
		PicPoint anchor = te.getCtrlPt(TextEditable.P_ANCHOR,ptBuf);
		text2ModelTr.rotate(getRotation(),anchor.x, anchor.y); // rotate along P_ANCHOR !
		// the reference point of an image is the top-left one, but the refpoint of a text layout is on the baseline
		if (image!=null){
			text2ModelTr.translate(te.getLeftX(),te.getTopY());
			if (te.getWidth()!=0 && image.getWidth()!=0 && (te.getDepth()+te.getHeight())!=0 && image.getHeight()!=0)
				text2ModelTr.scale(te.getWidth()/image.getWidth(),-(te.getHeight()+te.getDepth())/image.getHeight());
		}
		else {
			//Hack ? Just cheating a little bit ? Ou juste ruse ?
			//we want here to use the dimensions of the textLayout instead of latex dimensions if areDimensionsComputed
			// sinon on va aligner le textlayout en fonction des parametres latex, et l'Utilisateur (qui est bien bete) ne va rien comprendre.
			double latexH=0; double latexD=0; double latexW=0;
			if (areDimensionsComputed){//store latex dimensions, and setDimensions to textLayout ones
				latexH=te.getHeight();
				latexD=te.getDepth();
				latexW=te.getWidth();
				te.setDimensions(textLayout.getBounds().getWidth(),textLayout.getAscent(),textLayout.getDescent());
			}
			text2ModelTr.translate(te.getLeftX(),te.getBaseLineY());
			if (areDimensionsComputed){//restore latex dimensions
				te.setDimensions(latexW,latexH,latexD);
			}
			//Autre possibilite= comprimer le texte pour qu'il rentre dans la boite (evite le hack ci-dessus):
			//text2ModelTr.scale(te.getWidth()/textLayout.getWidth(),-(te.getHeight()+te.getDepth())/textLayout.getHeight());
			text2ModelTr.scale(1.0,-1.0);
		}
		syncFrame();
	}

	/**
	 * synchronize frame shape and location (TextLayout only) ; this is called by syncShape(),
	 * so that subclasser might override easily when only rectangular shapes are availables.
	 */
	protected void syncFrame(){
		PicText te = (PicText)element;
		if (! te.isFramed()){return;}
		AffineTransform tr=new AffineTransform(); // maps Image coordinates to Model coordinates (see paint)
		tr.setToIdentity(); // reset
		PicPoint anchor = te.getCtrlPt(TextEditable.P_ANCHOR,ptBuf);
		tr.rotate(getRotation(),anchor.x, anchor.y); // rotate along P_ANCHOR !
		shape = tr.createTransformedShape(te.getShapeOfFrame());
	}


	protected void getDimensionsFromLogFile(BufferedReader reader,PicText text){
		if (reader == null){return;}
		String line="" ;//il rale si j'initialise pas ...
		boolean finished=false;
		while(!finished){
			try{
				line=reader.readLine();
			}
			catch (IOException ioex){
				ioex.printStackTrace();
				return;
			}
			if (line == null){
				System.out.println("Size of text not found in log file...");
				return;
			}

			System.out.println(line);
			Matcher matcher=LogFilePattern.matcher(line);

			if (line != null && matcher.find())
			{
				System.out.println("FOUND :"+line);
				finished=true;
				try{
					text.setDimensions(0.3515*Double.parseDouble(matcher.group(1)),//height, pt->mm (1pt=0.3515 mm)
					                   0.3515*Double.parseDouble(matcher.group(2)),//width
					                   0.3515*Double.parseDouble(matcher.group(3)));//depth
					areDimensionsComputed=true;
				}
				catch(NumberFormatException e){
					System.out.println("Logfile number format problem: $line"+e.getMessage());
				}
				catch(IndexOutOfBoundsException e){
					System.out.println("Logfile regexp problem: $line"+e.getMessage());
				}
			}
		}
		return;
	}

	/**
	 * Synchronizes bounding box with the model ;
	 */
	protected void syncBounds(){
		PicText te = (PicText)element;
		//[pending] Il faut tenir compte de la rotation !

		Rectangle2D latexBB=null; // BB relative to latex dimensions (including rotation) (without frame)
		Rectangle2D textLayoutBB=null; // BB relative to textLayout dimensions (including rotation) (without frame)
		Rectangle2D textBB=null; // BB of the text (including rotation), without frame

		if (areDimensionsComputed){			//compute latexBB
			Rectangle2D nonRotated = new Rectangle2D.Double(te.getLeftX(),te.getBottomY(),te.getWidth(),te.getHeight()+te.getDepth());
			AffineTransform tr=new AffineTransform(); // maps Image coordinates to Model coordinates (see paint)
			tr.setToIdentity(); // reset
			PicPoint anchor = te.getCtrlPt(TextEditable.P_ANCHOR,ptBuf);
			tr.rotate(getRotation(),anchor.x, anchor.y); // rotate along P_ANCHOR !
			latexBB = tr.createTransformedShape(nonRotated).getBounds2D();
		}

		if (image==null) { 			//compute textLayoutBB
			Rectangle2D nonRotated = textLayout.getBounds();
			textLayoutBB = text2ModelTr.createTransformedShape(nonRotated).getBounds2D();
		}

		//use textLayoutBB or latexBB or their union
		if (image!=null) textBB=latexBB;
		else {
			if (! areDimensionsComputed) textBB=textLayoutBB;
			else {
				textBB=latexBB.createUnion(textLayoutBB);
			}
		}

		//union with frame BB
		if (te.isFramed()) {
			super.syncBounds();//update bounds of the frame if necessary
			Rectangle2D.union(super.bounds,textBB,this.bounds);
		}
		else this.bounds=textBB;
	}

	/**
	 * Render the View to the given graphic context.
	 * This implementation render the interior first, then the outline.
	 */
	public void paint(Graphics2D g,Rectangle2D a){
		if (!a.intersects(getBounds())) return;
		if (image!=null){ // paint bitmap
			g.drawImage(image, text2ModelTr, null);
			// debug:
			g.setPaint(Color.red); g.draw(this.bounds);
			super.paint(g,a); // possibly paint framebox if non-null
		}
		else { // paint textlayout
			super.paint(g,a); // possibly paint framebox if non-null

			AffineTransform oldAT = g.getTransform();
			// paint text in black
			g.setPaint(Color.black);
			// from now on, we work in Y-direct (<0) coordinates to avoid inextricable problems with font being mirrored...
			g.transform(text2ModelTr); // also include rotation
			textLayout.draw(g, 0.0f,0.0f);
			//[pending] ajouter un cadre si areDimensionsComputed (wysiwyg du pauvre)
			// get back to previous transform
			g.setTransform(oldAT);
			if (DEBUG) {
				g.setPaint(Color.red);
				g.draw(bounds);
			}
		}
	}

	/**
	 * This implementation calls <code>super.hitTest</code> and returns the result if non-null
	 * (this should be a HitInfo.Point),
	 * then returns a HitInfo.Interior if the mouse-click occured inside the text bound (as defined
	 * by text layout)
	 *
	 * @return a HitInfo corresponding to the given mouse-event
	 */
	public HitInfo hitTest(PEMouseEvent e){

		// from Bitmap:
		if (image!=null){
			if (getBounds().contains(e.getPicPoint())) {
				return new HitInfo.Interior((PicText)element, e);
			}
			return null;
		}

		// from TextLayout:
		if (!getBounds().contains(e.getPicPoint())) return null;

		PicText te = (PicText)element;
		// recompute textlayout b-box, but store it in a temporary field !
		Rectangle2D tb = textLayout.getBounds();
		Shape text_bounds = text2ModelTr.createTransformedShape(tb);
		if (text_bounds.contains(e.getPicPoint())) {
			// [SR:pending] for the hitInfo to be reliable, getPicPoint() should first be transformed by
			//              inverse text2ModelTr ! (especially when rotationAngle != 0)
			TextHitInfo thi = textLayout.hitTestChar((float)(e.getPicPoint().x - strx), (float)(e.getPicPoint().y - stry)); // guaranteed to return a non-null thi
			return new HitInfo.Text((PicText)element, thi, e);
		}
		// test hit on textlayout's bounding rectangle :
		//else if (bounds.contains(e.getPicPoint())) return new HitInfo.Interior(element,e);
		return null;


	}

	/**
	 * [SR:pending] make this view implement aka TextEditableView interface (or something like it), where
	 * TextEditableView is a subinterface of View with text-editing specific capabilities.
	 *
	 * Returns the TextLayout which is responsible for painting the textual content of this element
	 */
	public TextLayout getTextLayout(){
		return textLayout;
	}

	/** Return an affine transform which translat b/w the TextLayout coordinate system and the
	 *  jpicedt.graphic.model coordinate system.
	 * [SR:pending] refactor method name to something more explanatory
	 */
	public AffineTransform getTextToModelTransform(){
		return text2ModelTr;
	}

} // TextView
