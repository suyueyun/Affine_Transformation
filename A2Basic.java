public class A2Basic {
	public static void main(String[] args) {
	    DrawingModel model = new DrawingModel();
		View view = new View(model);
		model.addView(view);
	}

}
