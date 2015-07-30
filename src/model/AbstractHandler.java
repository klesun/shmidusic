package model;

import blockspace.BlockSpaceHandler;
import blockspace.staff.MidianaComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

abstract public class AbstractHandler implements KeyListener, MouseListener, MouseMotionListener {

	// constants
	final public static int ctrl = KeyEvent.CTRL_MASK;
	final public static KeyEvent k = new KeyEvent(new JPanel(),0,0,0,0,'h'); // just for constants

	private IComponentModel context = null;

	private LinkedList<SimpleAction> simpleActionQueue = new LinkedList<>();
	private int simpleActionIterator = 0;

	synchronized final public void performAction(SimpleAction action) {
		if (simpleActionIterator < simpleActionQueue.size()) {
			this.simpleActionQueue = new LinkedList<>(simpleActionQueue.subList(0, simpleActionIterator));
		}
		simpleActionQueue.addLast(action);
		action.redo();
		++simpleActionIterator;
	}

	// mouse
	protected Point mouseLocation = new Point(0,0);

	public AbstractHandler(IComponentModel context) {
		this.context = context;
	}

	abstract public LinkedHashMap<Combo, ContextAction> getMyClassActionMap();

	// implemented methods
	final public void keyPressed(KeyEvent e) {
		BlockSpaceHandler bsh = getRootHandler();
		Explain result = bsh.handleKey(new Combo(e));
		if (!result.isSuccess() && !result.isImplicit()) {
			JOptionPane.showMessageDialog(getContext().getFirstAwtParent(), result.getExplanation());
		}
	}
	final public void keyTyped(KeyEvent e) {}
	final public void keyReleased(KeyEvent e) {}

	public BlockSpaceHandler getRootHandler() {
		IComponentModel rootContext = getContext();
		while (rootContext.getModelParent() != null) {
			rootContext = rootContext.getModelParent();
		}

		return (BlockSpaceHandler)rootContext.getHandler();
	}

	final public Explain handleKey(Combo combo) {
		Explain result = null;

		if (getContext().getFocusedChild() != null) {
			result = getContext().getFocusedChild().getHandler().handleKey(combo);
		}

		if ((result == null || !result.isSuccess()) && getMyClassActionMap().containsKey(combo)) {

			result = getMyClassActionMap().get(combo).redo(getContext());

			if (getContext() instanceof MidianaComponent) { // i don't like this
				MidianaComponent.class.cast(getContext()).getPanel().checkCam();
			}
			getRootHandler().getContext().getWindow().updateMenuBar();
		}

		return result != null ? result : new Explain(false, "No Action For This Combination").setImplicit(true);
	}

	public IComponentModel getContext() {
		return this.context;
	}

	//---------
	// mouse
	//--------

	// override us, please!
	public Boolean mouseDraggedFinal(ComboMouse combo) { return false; } // TODO: remove "final" from name somehow, it's ugly, they are almost abstract
	public Boolean mousePressedFinal(ComboMouse combo) { return false; }
	public Boolean mouseReleasedFinal(ComboMouse combo) { return false; }
	public Boolean mouseMovedFinal(ComboMouse combo) { return false; }

	final public Boolean mousePressed(ComboMouse combo) {
		if (mousePressedFinal(combo)) {
			this.mouseLocation = combo.getPoint();
			return true;
		} else {
			int x = Component.class.cast(getContext()).getX();
			int y = Component.class.cast(getContext()).getY();
			combo.getPoint().move(x, y);
			return (getContext().getModelParent() != null && getContext().getModelParent().getHandler().mousePressed(combo));
		}
	}

	final public Boolean mouseReleased(ComboMouse combo) {
		if (mouseReleasedFinal(combo)) {
			this.mouseLocation = combo.getPoint();
			return true;
		} else {
			int x = Component.class.cast(getContext()).getX();
			int y = Component.class.cast(getContext()).getY();
			combo.getPoint().move(x, y);
			return (getContext().getModelParent() != null && getContext().getModelParent().getHandler().mouseReleased(combo));
		}
	}

	final public Boolean mouseDragged(ComboMouse combo) {
		if (mouseDraggedFinal(combo)) {
			this.mouseLocation = combo.getPoint();
			return true;
		} else {
			return (getContext().getModelParent() != null && getContext().getModelParent().getHandler().mouseDragged(combo));
		}
	}

	final public Boolean mouseMoved(ComboMouse combo) {
		// TODO: maybe do it as in mouseDragged() if got issues
		Boolean result = mouseMovedFinal(combo) ||
			(getContext().getModelParent() != null && getContext().getModelParent().getHandler().mouseMoved(combo));
		this.mouseLocation = combo.getPoint();
		return result;
	}

	// implementing mouse listeners
	final public void mousePressed(MouseEvent e) { mousePressed(new ComboMouse(e).setOrigin(getContext())); }
	final public void mouseReleased(MouseEvent e) { mouseReleased(new ComboMouse(e).setOrigin(getContext())); }
	final public void mouseDragged(MouseEvent e) { mouseDragged(new ComboMouse(e, mouseLocation).setOrigin(getContext())); }
	final public void mouseMoved(MouseEvent e) { mouseMoved(new ComboMouse(e, mouseLocation).setOrigin(getContext())); }
	// useless for now - so i put final
	final public void mouseEntered(MouseEvent e) {}
	final public void mouseExited(MouseEvent e) {}
	final public void mouseClicked(MouseEvent e) {}
}