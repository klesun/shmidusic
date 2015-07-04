package Storyspace;

import Gui.ImageStorage;
import Gui.Settings;
import Main.MajesticWindow;
import Model.*;
import Storyspace.Image.ImagePanel;
import Storyspace.Staff.StaffPanel;
import Storyspace.Article.Article;
import Stuff.Tools.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Storyspace extends JPanel implements IComponentModel {

	private MajesticWindow window = null;

	private List<StoryspaceScroll> childScrollList = new ArrayList<>();

	final private AbstractHandler handler;
	final private Helper modelHelper = new Helper(this);
	final private Settings settings = new Settings(this);
	final private ImageStorage imageStorage = new ImageStorage(this);

	public Storyspace(MajesticWindow window) {
		this.window = window;
		setLayout(null);
		setFocusable(true);

		handler = new StoryspaceHandler(this);
		addKeyListener(handler);
		addMouseMotionListener(handler);
		addMouseListener(handler);

		this.setBackground(Color.DARK_GRAY);
	}

	public StoryspaceScroll addModelChild(IStoryspacePanel child) {
		StoryspaceScroll scroll = new StoryspaceScroll(child, this);
		childScrollList.add(scroll);
		this.add(scroll);

		/** @ommented for debug */
//		this.validate();
		child.requestFocus();

		return scroll;
	}

	public void removeModelChild(IStoryspacePanel child) {
		this.remove(child.getScroll());
		childScrollList.remove(child.getScroll());
		repaint();
	}

	private void clearChildList() {
		this.removeAll();
		this.childScrollList.clear();
	}

	// getters

	public MajesticWindow getWindow() { return this.window; }

	// overriding IModel

	@Override
	public IComponentModel getModelParent() { return null; } // Storyspace is root parent
	@Override
	public StoryspaceScroll getFocusedChild() { // i think, i don't get awt philosophy...
		Component focused = window.getFocusOwner();
		if (focused instanceof IModel) {
			IModel model = (IModel)focused;
			while (model != null) {
				if (childScrollList.contains(model)) {
					return (StoryspaceScroll)model;
				}
				model = model.getModelParent();
			}
		}

		return null;
	}
	@Override
	public AbstractHandler getHandler() { return this.handler; }
	@Override
	public Helper getModelHelper() {
		return modelHelper;
	}

	public Settings getSettings() { return this.settings; }
	public ImageStorage getImageStorage() { return this.imageStorage; }

	@Override
	public void getJsonRepresentation(JSONObject dict) {
		dict.put("childBlockList", new JSONArray(childScrollList.stream().map(child -> {
			JSONObject childJs = Helper.getJsonRepresentation(child.content);
			childJs.put("scroll", Helper.getJsonRepresentation(child));
			childJs.put("className", child.content.getClass().getSimpleName());
			return childJs;
		}).toArray()));
	}
	@Override
	public Storyspace reconstructFromJson(JSONObject jsObject) throws JSONException {
		clearChildList();
		JSONArray childBlockList = jsObject.getJSONArray("childBlockList");
		for (int i = 0; i < childBlockList.length(); ++i) {
			JSONObject childJs = childBlockList.getJSONObject(i);
			IStoryspacePanel child = makeChildByClassName(childJs.getString("className"));
			child.getScroll().reconstructFromJson(childJs.getJSONObject("scroll"));
			child.reconstructFromJson(childJs);

			// StaffPanels take ~10 mib; Articles - ~1 mib
//			Logger.logMemory("reconstructed " + child.getClass().getSimpleName() + " [" + child.getScroll().toString() + "]");
		}
		return this;
	}

	// private methods

	private static Class<?extends IStoryspacePanel>[] childClasses = new Class[]{Article.class, ImagePanel.class, StaffPanel.class};

	private IStoryspacePanel makeChildByClassName(String className) {
		for (int i = 0; i < childClasses.length; ++i) {
			if (childClasses[i].getSimpleName().equals(className)) {
				try {
					return childClasses[i].getDeclaredConstructor(getClass()).newInstance(this);
				} catch (Exception e) {
					childClasses[i].getSimpleName();
					System.out.println(e.getMessage());
					e.printStackTrace(); Runtime.getRuntime().halt(666);
				}
			}
		}

		Logger.fatal("Invalid className, Storyspace denies this child [" + className + "]");
		return null;
	}

	public List<StoryspaceScroll> getChildScrollList() {
		return childScrollList;
	}

	/** @unused kinda */
	public List<Article> getArticles() {
		return getChildScrollList().stream()
				.filter(scroll -> scroll.content instanceof Article)
				.map(scroll -> Article.class.cast(scroll.content))
				.collect(Collectors.toList());
	}

	// event handles

	public void pushToFront(StoryspaceScroll scroll) {
		setComponentZOrder(scroll, 0);
		repaint();
	}

	public void scale(Combo combo) {
		int sign = combo.getSign();
		double factor = sign == -1 ? 0.75 : 1 / 0.75;
		for (StoryspaceScroll scroll: childScrollList) {

			int width = (int) (scroll.getWidth() * factor);
			int height = (int) (scroll.getHeight() * factor);
			int x = (int) (scroll.getX() * factor);
			int y = (int) (scroll.getY() * factor);

			scroll.setSize(width, height);
			scroll.setLocation(x, y);
			scroll.validate();

			// TODO: child.scale(combo)
		}
	}

	public StaffPanel addMusicBlock() {
		StaffPanel obj = new StaffPanel(this);
		this.revalidate();
		return obj;
	}
	public void addTextBlock() { new Article(this); }
	public void addImageBlock() { new ImagePanel(this); }
}
