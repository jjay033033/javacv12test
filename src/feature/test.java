package feature;

import static org.bytedeco.javacpp.opencv_core.CV_32F;
import static org.bytedeco.javacpp.opencv_features2d.drawMatches;
import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.opencv_core.DMatchVector;
import org.bytedeco.javacpp.opencv_core.KeyPointVector;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatExpr;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_features2d;
import org.bytedeco.javacpp.opencv_features2d.DrawMatchesFlags;
import org.bytedeco.javacpp.opencv_features2d.FastFeatureDetector;
import org.bytedeco.javacpp.opencv_features2d.FlannBasedMatcher;
import org.bytedeco.javacpp.opencv_xfeatures2d.BriefDescriptorExtractor;

public class test {

	public static void main(String[] args) {
		

		Mat src1, src2;
		src1 = imread("surf/2.jpg");
		src2 = imread("surf/1.jpg");
		// vector of keyPoints
		KeyPointVector keys1 = new KeyPointVector();
		KeyPointVector keys2 = new KeyPointVector();
		// construction of the fast feature detector object
		FastFeatureDetector fast1 = FastFeatureDetector.create(); // 检测的阈值为40
		FastFeatureDetector fast2 = FastFeatureDetector.create();
		// feature point detection
		fast1.detect(src1, keys1);
		// double t;
		// t=getTickCount();
		fast2.detect(src2, keys2);
		// t=getTickCount()-t;
		// t=t*1000/getTickFrequency();
		// cout<<"KeyPoint Size:"<<keys2.size()<<endl;
		// cout<<"extract time:"<<t<<"ms"<<endl;
//		opencv_features2d.drawKeypoints(src1, keys1, src1, Scalar.all(-1), DrawMatchesFlags.DRAW_OVER_OUTIMG);
//		opencv_features2d.drawKeypoints(src2, keys2, src2, Scalar.all(-1), DrawMatchesFlags.DRAW_OVER_OUTIMG);
//		imshow("FAST feature1", src1);
//		imshow("FAST feature2", src2);
//		waitKey(0);
		// t=getTickCount();

		BriefDescriptorExtractor extractor = BriefDescriptorExtractor.create();// Run:BruteForceMatcher<
																				// L2<float>
																				// >
																				// matcher
		// ORB Extractor;//Not Run;
		// BriefDescriptorExtractor Extractor;//RUN:BruteForceMatcher< Hamming >
		// matcher
		Mat descriptors1 = new Mat(), descriptors2 = new Mat();
		extractor.compute(src1, keys1, descriptors1);
		extractor.compute(src2, keys2, descriptors2);

		// BruteForceMatcher< Hamming > matcher;
		// BruteForceMatcher< L2<float> > matcher;
		FlannBasedMatcher matcher = new FlannBasedMatcher();
		DMatchVector matches = new DMatchVector();
		descriptors1.convertTo(descriptors1, CV_32F);
		descriptors1.convertTo(descriptors2, CV_32F);
		matcher.match(descriptors1, descriptors2, matches);
		// t=getTickCount()-t;
		// t=t*1000/getTickFrequency();
		// cout<<"match time:"<<t<<"ms"<<endl;
		Mat img_matches = new Mat();
		drawMatches(src1, keys1, src2, keys2, matches, img_matches);
		imshow("draw", img_matches);
		waitKey(0);
	}

}
