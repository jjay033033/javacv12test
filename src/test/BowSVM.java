package test;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_core.KeyPoint;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_ml.SVM;

public class BowSVM extends SVM{

	public BowSVM(Pointer p) {
		super(p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float predict(Mat arg0) {
		Mat mat = new Mat();
		KeyPointVector dstKeyPoints = new KeyPointVector();
		test4.sift.detect(arg0, dstKeyPoints);
		test4.bowDescriptorExtractor.compute(arg0, dstKeyPoints, mat);
		return super.predict(mat);
	}
	
	

}
