package fr.m1m2.advancedEval;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CRectangle;
import fr.lri.swingstates.canvas.CShape;
import fr.lri.swingstates.canvas.CText;
import fr.lri.swingstates.canvas.Canvas;

public class Trial {

	protected boolean practice = false;
	protected int block;
	protected int trial;
	protected String visualVariable;
	protected String objectCount;
	protected int errors = 0;

	protected Experiment experiment;

	protected CExtensionalTag instructions = new CExtensionalTag() {
	};

	protected CExtensionalTag shapes = new CExtensionalTag() {
	};

	protected CExtensionalTag target = new CExtensionalTag() {
	};

	protected CExtensionalTag placeholders = new CExtensionalTag() {
	};

	protected CExtensionalTag notes = new CExtensionalTag() {
	};

	int x = 150;
	int y = 150;

	int targetIndex;
	CShape targetShape;

	public Trial(Experiment experiment, boolean practice, int block, int trial, String visualVariable,
			String objectCount) {
		this.practice = practice;
		this.block = block;
		this.trial = trial;
		this.visualVariable = visualVariable;
		this.objectCount = objectCount;
		this.experiment = experiment;
	}

	public void displayInstructions() {
		Canvas canvas = experiment.getCanvas();
		CText text1 = canvas.newText(0, 0, "A scene with multiple shapes will get displayed", Experiment.FONT);
		CText text2 = canvas.newText(0, 50, "Identify the shape that is different from all other shapes",
				Experiment.FONT);
		CText text3 = canvas.newText(0, 100, "    1. Press Space bar", Experiment.FONT);
		CText text4 = canvas.newText(0, 150, "    2. Click on the identified shape", Experiment.FONT);
		CText text5 = canvas.newText(0, 200, "Do it AS FAST AND AS ACCURATELY AS POSSIBLE", Experiment.FONT);
		CText text6 = canvas.newText(0, 350, "--> Press Enter key when ready",
				Experiment.FONT.deriveFont(Font.PLAIN, 30));
		text1.addTag(instructions);
		text2.addTag(instructions);
		text3.addTag(instructions);
		text4.addTag(instructions);
		text5.addTag(instructions);
		text6.addTag(instructions);
		double textCenterX = instructions.getCenterX();
		double textCenterY = instructions.getCenterY();
		double canvasCenterX = canvas.getWidth() / 2;
		double canvasCenterY = canvas.getHeight() / 2;
		double dx = canvasCenterX - textCenterX;
		double dy = canvasCenterY - textCenterY;
		instructions.translateBy(dx, dy);
		canvas.setAntialiased(true);

		// TODO install keyboard listener to handle user input
		// canvas.requestFocus();
	}

	public void displayEnd() {
		Canvas canvas = experiment.getCanvas();
		CText note = canvas.newText(0, 0, "Thank you for your participation!",
				Experiment.FONT.deriveFont(Font.PLAIN, 30));
		note.addTag(notes);
		centeralize(notes);
	}

	public void removeInstructions() {
		Canvas canvas = experiment.getCanvas();
		canvas.removeShapes(instructions);
	}

	public void removeShapes() {
		Canvas canvas = experiment.getCanvas();
		canvas.removeShapes(shapes);
	}

	public void removePlaceholders() {
		Canvas canvas = experiment.getCanvas();
		canvas.removeShapes(placeholders);
	}

	public void addShapes() {
		Canvas canvas = experiment.getCanvas();

		int count = getCounts(objectCount);

		int h = 40;
		int w = 120;

		Color color1 = new Color(244, 67, 54);
		Color color2 = new Color(255, 193, 7);

		// Target
		Color targetColor, otherColor;
		if (Math.random() > 0.5) {
			targetColor = color1;
			otherColor = color2;
		} else {
			targetColor = color2;
			otherColor = color1;
		}

		int targetW, targetH, otherW, otherH;
		if (Math.random() > 0.5) {
			targetW = w;
			targetH = h;
			otherW = h;
			otherH = w;
		} else {
			targetW = h;
			targetH = w;
			otherW = w;
			otherH = h;
		}

		ArrayList<CRectangle> allShapes = new ArrayList<CRectangle>();

		CRectangle targetRectangle;

		targetRectangle = canvas.newRectangle(0, 0, targetW, targetH);
		targetRectangle.setFilled(true);
		targetRectangle.setFillPaint(targetColor);
		targetRectangle.setStroke(new BasicStroke(0));
		targetRectangle.addTag(shapes);

		allShapes.add(targetRectangle);

		// Other shapes
		CRectangle rectangle;

		if (visualVariable.equals("VV1")) {

			for (int i = 0; i < count - 1; i++) {
				rectangle = canvas.newRectangle(0, 0, targetW, targetH);
				rectangle.setFilled(true);
				rectangle.setFillPaint(otherColor);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(shapes);

				allShapes.add(rectangle);
			}
		} else if (visualVariable.equals("VV2")) {

			for (int i = 0; i < count - 1; i++) {
				rectangle = canvas.newRectangle(0, 0, otherW, otherH);
				rectangle.setFilled(true);
				rectangle.setFillPaint(targetColor);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(shapes);

				allShapes.add(rectangle);
			}
		} else if (visualVariable.equals("VV1VV2")) {

			for (int i = 0; i < 2; i++) {

				rectangle = canvas.newRectangle(0, 0, targetW, targetH);
				rectangle.setFilled(true);
				rectangle.setFillPaint(otherColor);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(shapes);

				allShapes.add(rectangle);
			}

			for (int i = 0; i < 2; i++) {
				rectangle = canvas.newRectangle(0, 0, otherW, otherH);
				rectangle.setFilled(true);
				rectangle.setFillPaint(targetColor);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(shapes);

				allShapes.add(rectangle);
			}
			for (int i = 0; i < 2; i++) {
				rectangle = canvas.newRectangle(0, 0, otherW, otherH);
				rectangle.setFilled(true);
				rectangle.setFillPaint(otherColor);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(shapes);

				allShapes.add(rectangle);
			}

			for (int i = 6; i < (count - 1); i++) {

				if (Math.random() > 0.5) {
					w = targetW;
					h = targetH;
				} else {
					w = otherW;
					h = otherH;
				}

				rectangle = canvas.newRectangle(0, 0, otherW, otherH);
				rectangle.setFilled(true);

				if (w == targetW) {

					rectangle.setFillPaint(otherColor);
					rectangle.setStroke(new BasicStroke(0));
					rectangle.addTag(shapes);
				} else {
					if (Math.random() > 0.5) {
						rectangle.setFillPaint(targetColor);
					} else {
						rectangle.setFillPaint(otherColor);
					}
					rectangle.setStroke(new BasicStroke(0));
					rectangle.addTag(shapes);
				}

				allShapes.add(rectangle);
			}
		}

		Collections.shuffle(allShapes);

		int index = 0;

		for (int i = 0; i < Math.sqrt(count); i++) {
			for (int j = 0; j < Math.sqrt(count); j++) {

				allShapes.get(index).translateTo(i * x, j * y);

				if (allShapes.get(index).equals(targetRectangle)) {
					targetIndex = index;
				}

				index++;
			}
		}

		centeralize(shapes);

	}

	public void addPlaceholdres() {

		Canvas canvas = experiment.getCanvas();
		int count = getCounts(objectCount);

		CRectangle rectangle;

		int index = 0;

		for (int i = 0; i < Math.sqrt(count); i++) {
			for (int j = 0; j < Math.sqrt(count); j++) {

				rectangle = canvas.newRectangle(i * x, j * x, x, x);
				rectangle.setFilled(false);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(placeholders);

				if (index == targetIndex) {
					rectangle.addTag(target);
				}

				rectangle = canvas.newRectangle(i * x + 65, j * x + 15, 20, 120);
				rectangle.setFilled(true);
				rectangle.setFillPaint(Color.BLACK);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(placeholders);

				rectangle = canvas.newRectangle(i * x + 15, j * x + 65, 120, 20);
				rectangle.setFilled(true);
				rectangle.setFillPaint(Color.BLACK);
				rectangle.setStroke(new BasicStroke(0));
				rectangle.addTag(placeholders);

				index++;

			}
		}

		centeralize(placeholders);

	}

	public CRectangle getTarget() {
		Canvas canvas = experiment.getCanvas();
		return (CRectangle) canvas.getFirstHavingTag(target);
	}

	public int getCounts(String size) {
		if (size.equals("small")) {
			return 9;
		} else if (size.equals("medium")) {
			return 16;
		} else if (size.equals("large")) {
			return 25;
		}
		return 0;

	}

	public void centeralize(CExtensionalTag tag) {
		Canvas canvas = experiment.getCanvas();
		double centerX = tag.getCenterX();
		double centerY = tag.getCenterY();
		double canvasCenterX = canvas.getWidth() / 2;
		double canvasCenterY = canvas.getHeight() / 2;
		double dx = canvasCenterX - centerX;
		double dy = canvasCenterY - centerY;
		tag.translateBy(dx, dy);
	}

}
