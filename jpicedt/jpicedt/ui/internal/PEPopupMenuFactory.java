// PEPopupMenuFactory.java --- -*- coding: iso-8859-1 -*-
// January 4, 2002 - jPicEdt, a picture editor for LaTeX.
// Copyright (C) 1999/2013 Sylvain Reynal
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
// Version: $Id: PEPopupMenuFactory.java,v 1.32 2013/03/27 06:51:06 vincentb1 Exp $
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
package jpicedt.ui.internal;

import jpicedt.JPicEdt;
import jpicedt.graphic.toolkit.*;
import jpicedt.graphic.model.*;
import jpicedt.graphic.*;
import jpicedt.graphic.event.PEMouseEvent;
import jpicedt.graphic.view.HitInfo;
import jpicedt.ui.dialog.*;
import jpicedt.ui.action.*;
import jpicedt.Localizer;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import static jpicedt.Log.*;
import static jpicedt.Localizer.*;

/**
 * A factory to create a PEPopupMenu for an EditorKit.
 * @since jPicEdt
 * @author Sylvain Reynal
 * @version $Id: PEPopupMenuFactory.java,v 1.32 2013/03/27 06:51:06 vincentb1 Exp $
 */
public class PEPopupMenuFactory implements PopupMenuFactory {

	private ActionRegistry actionMap;

	/**
	 * Create a new PEPopupMenuFactory using the given action-map to retrieve actions.
	 */
	public PEPopupMenuFactory(){
		this.actionMap = JPicEdt.getActionRegistry();
	}

	/**
	 * Create a new PEPopupMenuFactory using the given action-map to retrieve actions.
	 */
	public PEPopupMenuFactory(ActionRegistry actionMap){
		this.actionMap = actionMap; // JPicEdt.getActionRegistry()
	}

	/**
	 * @param board the popup-menu invoker component
	 * @param hi holds information on the graphic element on which the click occured.
	 */
	public JPopupMenu createPopupMenu(PECanvas board, HitInfo hi){
		PopupMenu pm = new PopupMenu(board, hi);
		return pm;
	}


	/**
	 * @param board la planche invoquant le menu contextuel
	 * @param e détient l'information sur l'événement souris correspondant au clic.
	 */
	public JPopupMenu createPopupMenu(PECanvas board, PEMouseEvent e){
		PopupMenu pm = new PopupMenu(board, e);
		return pm;
	}


	/**
	 * Describe <code>createPopupMenu</code> method here.
	 *
	 * @param board le <code>PECanvas</code> ayant invoqué le menu contextuel
	 * @param hi la valeur <code>ConvexZoneHitInfo</code> contenant l'information relative à la zone convexe
	 * sur laquelle le clic a eu lieu.
	 * @return le <code>JPopupMenu</code> créé
	 * @since jPicEdt 1.6
	 */
	public JPopupMenu createPopupMenu(PECanvas board, ConvexZoneHitInfo hi){
		PopupMenu pm = new PopupMenu(board, hi);
		return pm;
	}

	/**
	 * <p>This class implements a popup-menu activable from a right-click on the associated PECanvas.</p>
	 * <p>This is a context-sensitive popup-menu, which depends on the object being clicked and its "selected"
	 * state.
	 * @since PicEdt 1.2.a
	 */
	public class PopupMenu extends JPopupMenu {

		ActionLocalizer localizer;
		ActionDispatcher actionDispatcher;
		PECanvas canvas;
		HitInfo hi;
		ConvexZoneHitInfo czHi;
		PEMouseEvent e;

		public PicPoint getPicPoint(){
			if(e  == null) return null;
			PicPoint hitPoint = e.getPicPoint();
			hitPoint = canvas.getGrid().nearestNeighbour(hitPoint,hitPoint);

			return hitPoint;
		}

		public void show(Component c, int x, int y){
			super.show(c,x,y);
			if (DEBUG) debug("x="+x+",y="+y);
		}

		public PopupMenu(PECanvas canvas, HitInfo hi){

			if (DEBUG) debug("hi="+hi);

			this.canvas = canvas;
			this.hi = hi;
			if(hi != null)
				this.e = hi.getMouseEvent();
			else
				this.e = null;
			this.czHi = null;

			make();
		}

		public PopupMenu(PECanvas canvas, PEMouseEvent e){

			if (DEBUG) debug("e="+e);

			this.canvas = canvas;
			this.hi = null;
			this.e = e;
			this.czHi = null;

			make();
		}

		private void make(){

			this.actionDispatcher = new DefaultActionDispatcher(canvas);
			this.localizer = jpicedt.Localizer.currentLocalizer().getActionLocalizer();
			if (actionMap==null)
				actionMap = JPicEdt.getActionRegistry();

			JMenuItem menuItem;
			PEAction action;


			// compute size of active selection:
			int selectionSize=canvas.getSelectionSize();

			// target=clicked element if it's selected, or selected element if selection-size==1.
			// otherwise no target
			Element target = null;
			String lbl = null;

			if (selectionSize == 0){
				target = null;
			}
			else if (hi != null){ // click on an Element in a single- or multiple-element selection
				if (hi instanceof HitInfo.Composite){
					target = hi.getTarget();
					if (!canvas.isSelected(target)){
						if (selectionSize==1){
							target = canvas.selection().next();
							lbl = " "+localize("misc.Target")+": ";
							lbl += target.getName();
						}
						else {
							target=null;
						}
					}
					else {
						Element child = ((HitInfo.Composite)hi).getClickedChild();
						lbl = " "+localize("misc.Target")+": ";
						lbl += child.getName() + " @ "+target.getName();
					}
				}
				else {
					target = hi.getTarget(); // target = clicked element
					if (!canvas.isSelected(target)){
						if (selectionSize==1){
							target = canvas.selection().next();
							lbl = " "+localize("misc.Target")+": ";
							lbl += target.getName();
						}
						else{
							target=null;
						}
					}
					else {
						lbl = " "+localize("misc.Target")+": ";
						lbl += target.getName();
						if (target.getParent() instanceof PicGroup)
							lbl += " @ "+target.getParent().getName();
					}
				}
			}
			else if (selectionSize == 1){
				target = canvas.selection().next();
				lbl = " "+localize("misc.Target")+": ";
				lbl += target.getName();
				if (target.getParent() instanceof PicGroup)
					lbl += " @ "+target.getParent().getName();
			}


			// menu label:
			if (lbl!=null){
				add(new JLabel(lbl, SwingConstants.RIGHT));
				addSeparator();
			}

			// add Element specific actions
			if (target instanceof ActionFactory){
				ArrayList<PEAction> actionArray = ((ActionFactory)target).createActions(actionDispatcher,localizer,hi);
				if (actionArray != null){
					for (PEAction a: actionArray){
						if (a==null)
							addSeparator();
						else
							add(a);
					}
				}
				addSeparator();
			}

			// group selection
			if (selectionSize > 1){
				add(actionMap.getAction(EditorKit.GroupAction.KEY));
				// join curves
				if (canvas.getEditorKit().getSelectionHandler().containsClass(PicMultiCurveConvertable.class))
					add(actionMap.getAction(EditorKit.JoinCurvesAction.KEY));
				addSeparator();
			}

			// disposition
			if (selectionSize > 0){
				menuItem=add(actionMap.getAction(EditorKit.EditDispositionAction.TOBACK));
				if (selectionSize == 1)
					menuItem.setEnabled(!target.getParent().isToBack(target));
				else
					menuItem.setEnabled(selectionSize>0);

				menuItem=add(actionMap.getAction(EditorKit.EditDispositionAction.BACKWARD));
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MULTIPLY,0));
				menuItem.setEnabled(selectionSize == 1 && !target.getParent().isToBack(target));

				menuItem=add(actionMap.getAction(EditorKit.EditDispositionAction.FORWARD));
				menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH,0));
				menuItem.setEnabled(selectionSize == 1 && !target.getParent().isToFront(target));

				menuItem=add(actionMap.getAction(EditorKit.EditDispositionAction.TOFRONT));
				if (selectionSize == 1)
					menuItem.setEnabled(!target.getParent().isToFront(target));
				else
					menuItem.setEnabled(selectionSize>0);
				addSeparator();
			}


			// get always added:
			menuItem = createMenu("menu.Fragments");
			Component[] cc = jpicedt.ui.util.Fragments.createMenu(this).getMenuComponents();
			for(int i=0; i<cc.length; i++){
				menuItem.add(cc[i]);
			}
			add(menuItem);
			add(actionMap.getAction(JPicEdt.ViewLaTeXFileAction.KEY));
			add(actionMap.getAction(EditorKit.EditBoundingBoxAction.KEY));
		}


		/**
		 * Crée une nouvelle instance de <code>PopupMenu</code>.
		 *
		 * @param canvas la toile <code>PECanvas</code> courante du déclenchement du menu contextuel
		 * @param hi l'information <code>ConvexZoneHitInfo</code> sur la frappe souris ayant déclenché le menu
		 * contextuel.
		 */
		public PopupMenu(PECanvas canvas, ConvexZoneHitInfo hi){

			if (DEBUG) debug("hi="+hi);

			JMenuItem menuItem;
			PEAction action;

			this.canvas = canvas;
			this.czHi = hi;
			this.hi = null;
			this.actionDispatcher = new DefaultActionDispatcher(canvas);
			this.localizer = jpicedt.Localizer.currentLocalizer().getActionLocalizer();
			if (actionMap==null)
				actionMap = JPicEdt.getActionRegistry();

			// compute size of active selection:
			ConvexZoneSelectionHandler selectionHandler = canvas.getEditorKit()
				.getConvexZoneSelectionHandler();
			int selectionSize=selectionHandler.size();

			// target=clicked element if it's selected, or selected element if selection-size==1.
			// otherwise no target
			ConvexZoneGroup target = null;
			String lbl = null;

			if (selectionSize == 0){
				target = null;
			}
			else if (hi != null){ // click on a ConvexZone in a single- or multiple-convexeZone selection
				target = hi.getTarget();
			}
			else if (selectionSize == 1){
				target = new ConvexZoneGroup(selectionHandler);
			}
			if(target != null){
				lbl = " "+localize("misc.Target") + ": "
					+ localize("misc.ConvexZone");
			}


			// menu label:
			if (lbl!=null){
				add(new JLabel(lbl, SwingConstants.RIGHT));
				addSeparator();
			}

			// add ConvexZone specific actions
			if (target instanceof ConvexZoneActionFactory){
				ArrayList<PEConvexZoneAction> actionArray = ((ConvexZoneActionFactory)target).createActions(actionDispatcher,localizer,hi);
				if (actionArray != null){
					for (PEConvexZoneAction a: actionArray){
						if (a==null)
							addSeparator();
						else
							add(a);
					}
				}
				addSeparator();
			}

			// get always added:
			add(actionMap.getAction(JPicEdt.ViewLaTeXFileAction.KEY));
			add(actionMap.getAction(EditorKit.EditBoundingBoxAction.KEY));
		}

		/**
		 * <p>Overriden from JPopupMenu so as to take localization into account.</p>
		 * <p>Example: suppose we add an action whose name (ie value associated with key Action.NAME) is
		 * "GroupAction". Then:<ul>
	     * <li>menuitem's text gets constructed from localized "GroupAction" in the adequate locale resource
	     * file (res/i18n_xx.properties)</li>
		 * <li>menuitem's mnemonic gets constructed from "GroupAction_menuitem_mnemonic" in the same resource
		 * file, IF IT'S FOUND.</li>
		 * <li>menuitem's accelerator gets constructed from "GroupAction_menuitem_accelerator" in the same
		 * resource file, IF IT'S FOUND.</li>
		 * </ul>
		 */
		public JMenuItem add(Action a){

			JMenuItem mi = super.add(a);
			//mi.setText((String)a.getValue(Action.NAME));
			//mi.setMnemonic(Localizer.currentLocalizer().get(name + "_menuitem_mnemonic").charAt(0));
			KeyStroke ks =  (KeyStroke)a.getValue(Action.ACCELERATOR_KEY);
			if (ks != null) mi.setAccelerator(ks);
			mi.setIcon(null);
			return mi;
		}

		/**
		 * Creates a <code>PEMenu</code> from the given String, using JPicEdt's default
		 * <code>ActionLocalizer</code> to fetch the associated label (ie "name") and mnemonic
		 * ("name.mnemonic").
		 * @param name the name of the menu, used to determine:
		 * Example: createMenu("File") create a menu with label "File" and mnemonic = first char of
		 * "File.mnemonic"
		 *
		 */
		private PEMenu createMenu(String name) throws MissingResourceException {

			PEMenu menu = new PEMenu(localizer.getActionName(name));
			menu.setMnemonic(localizer.getActionMnemonic(name).intValue());
			return menu;
		}

	} // PopupMenu

} // PEPopupMenuFactory
